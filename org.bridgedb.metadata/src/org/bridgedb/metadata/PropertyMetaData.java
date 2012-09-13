/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.SchemaConstants;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.type.*;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Christian
 */
public class PropertyMetaData extends MetaDataBase implements MetaData, LeafMetaData{

    public static RequirementLevel ALLWAYS_WARN_LEVEL = RequirementLevel.SHOULD;

    private final URI predicate;
    private final MetaDataType metaDataType;
    private final RequirementLevel requirementLevel;
    private Set<Value> values;
    private final Set<PropertyMetaData> parents = new HashSet<PropertyMetaData>();

    public PropertyMetaData(Element element) throws MetaDataException {
        super(element);
        String predicateSt = element.getAttribute(SchemaConstants.PREDICATE);
        predicate = new URIImpl(predicateSt);
        String objectClass = element.getAttribute(SchemaConstants.CLASS);
        metaDataType = getMetaDataType(objectClass, element);
        String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
        requirementLevel = RequirementLevel.parse(requirementLevelSt);
        values = new HashSet<Value>();
    }
    
    private PropertyMetaData(PropertyMetaData other) {
        super(other.name);
        predicate = other.predicate;
        metaDataType = other.metaDataType;
        requirementLevel = other.requirementLevel;
        values = new HashSet<Value>();
    }
    
    @Override
    public void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        setupValues(id, parent);
        values = new HashSet<Value>();
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id) && statement.getPredicate().equals(predicate)){
                 iterator.remove();
                 rawRDF.add(statement);
                 values.add(statement.getObject());
            }
        }  
    }

    private MetaDataType getMetaDataType(String objectClass, Element element) throws MetaDataException{
        if (SchemaConstants.CLASS_ALLOWED_URIS.equalsIgnoreCase(objectClass)){
            return new AllowedUriType(element);
        }
        if (SchemaConstants.CLASS_ALLOWED_VALUES.equalsIgnoreCase(objectClass)){
            return new AllowedValueType(element);
        }
        if (SchemaConstants.CLASS_DATE.equalsIgnoreCase(objectClass)){
            return new DateType();
        }
        if (SchemaConstants.CLASS_INTEGER.equalsIgnoreCase(objectClass)){
            return new IntegerType();
        }
        if (SchemaConstants.CLASS_STRING.equalsIgnoreCase(objectClass)){
            return new StringType();
        }
        if (SchemaConstants.CLASS_URI.equalsIgnoreCase(objectClass)){
            return new UriType();
        }
        throw new MetaDataException ("Unexpected " + SchemaConstants.CLASS + " " + objectClass);
    }
    
    @Override
    void appendShowAll(StringBuilder builder, RequirementLevel forceLevel, int tabLevel) {
        if (values.isEmpty() && requirementLevel.compareTo(forceLevel) > 0) { 
            //No value and low enough level not too care
            return; 
        } 
        tab(builder, tabLevel);
        builder.append(id);
        builder.append(" Property ");
        builder.append(name);
        if (values.isEmpty()){
            builder.append(" MISSING!  Set with ");
            builder.append(predicate);
        } else if (values.size() == 1){
            builder.append(" == ");
            builder.append(values.iterator().next());
        } else {
            for (Value value: values){
                newLine(builder, tabLevel + 1);
                builder.append(value);
            }
        }
        newLine(builder);
    }

    @Override
    public void appendSchema(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Property ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("predicate ");
        builder.append(predicate);        
        newLine(builder, tabLevel + 1);
        builder.append("class ");
        builder.append(metaDataType);        
        newLine(builder, tabLevel + 1);
        builder.append("Requirement Level ");
        builder.append(requirementLevel);        
        newLine(builder);
    }

    @Override
    public PropertyMetaData getSchemaClone() {
        return new PropertyMetaData(this);
    }

    @Override
    public boolean hasRequiredValues(RequirementLevel forceLevel) {
        if (values.isEmpty()){
            //Is the level so low that is does not matter
            return (requirementLevel.compareTo(forceLevel) > 0);          
        } else {
            return true;
        }
    }

    @Override
    boolean hasValues() {
        return !values.isEmpty();
    }
    
    @Override
    public boolean hasCorrectTypes() {
        for (Value value: values){
            if (!metaDataType.correctType(value)){
                return false;
            }
        }
        //If no incorrect values return true. Even if there are No values.
        return true;
    }

    @Override
    public void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        if (values.isEmpty()){
            if (requirementLevel.compareTo(forceLevel) <= 0){
                tab(builder, tabLevel);
                builder.append("ERROR: ");
                builder.append(id );
                builder.append(":");
                builder.append(name);
                builder.append(" is missing. ");
                newLine(builder, tabLevel + 1);
                builder.append("Please add a statment with the predicate ");
                builder.append(predicate);
                newLine(builder);
            } else if (includeWarnings && requirementLevel.compareTo(ALLWAYS_WARN_LEVEL) <= 0){
                tab(builder, tabLevel);
                builder.append("Warning: ");
                builder.append(id );
                builder.append(":");
                builder.append(name);
                builder.append(" is missing. ");
                newLine(builder, tabLevel + 1);
                builder.append("This has a RequirementLevel of ");
                builder.append(requirementLevel);
                newLine(builder);
            }
        } else if (!hasCorrectTypes()){
            tab(builder, tabLevel);
            builder.append("ERROR: Incorrect type for ");
            builder.append(id );
            builder.append(":");
            builder.append(name);            
            for (Value value: values){
                if (!metaDataType.correctType(value)){
                    newLine(builder, tabLevel + 1);
                    builder.append("Expected ");
                    builder.append(metaDataType.getCorrectType());
                    newLine(builder, tabLevel + 1);
                    builder.append(" Found ");
                    builder.append(value);
                    builder.append(" Which is a  ");
                    builder.append(value.getClass());
                }
            }
            newLine(builder);
        }
    }

    @Override
    public boolean allStatementsUsed() {
        return true;
    }
    
    @Override
    void appendUnusedStatements(StringBuilder builder) {
        //rawRDF here is used
    }

    @Override
    public Set<Value> getValuesByPredicate(URI predicate) {
        if (this.predicate.equals(predicate)){
            return values;
        } else {
            return null;
        }
    }

    @Override
    PropertyMetaData getLeafByPredicate(URI predicate) {
        if (this.predicate.equals( predicate)){
            return this;
        }
        return null;
    }

    @Override
    Set<? extends LeafMetaData> getLeaves() {
        HashSet<PropertyMetaData> results = new HashSet<PropertyMetaData>();
        results.add(this);
        return results;
    }

    @Override
    public URI getPredicate() {
        return predicate;
    }

    @Override
    public void addParent(LeafMetaData parentLeaf) {
        parents.add((PropertyMetaData)parentLeaf);
    }



}
