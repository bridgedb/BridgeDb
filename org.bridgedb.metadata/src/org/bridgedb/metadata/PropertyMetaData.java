/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.SchemaConstants;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.metadata.constants.RdfConstants;
import org.bridgedb.metadata.constants.XsdConstants;
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

    private final URI predicate;
    private final MetaDataType metaDataType;
    private final Set<Value> values = new HashSet<Value>();
    private final Set<PropertyMetaData> parents = new HashSet<PropertyMetaData>();
    private final Set<Statement> rawRDF = new HashSet<Statement>();
    private final boolean specifiedProperty;
    
    public PropertyMetaData(Element element) throws MetaDataException {
        super(element);
        String predicateSt = element.getAttribute(SchemaConstants.PREDICATE);
        predicate = new URIImpl(predicateSt);
        String objectClass = element.getAttribute(SchemaConstants.CLASS);
        metaDataType = getMetaDataType(objectClass, element);
        String requirementLevelSt = element.getAttribute(SchemaConstants.REQUIREMENT_LEVEL);
        specifiedProperty = true;
    }
    
    public static PropertyMetaData getUnspecifiedProperty(URI predicate){
        PropertyMetaData result = new PropertyMetaData(predicate);
        return result;
    }
    
    public static PropertyMetaData getTypeProperty(){
        PropertyMetaData result = new PropertyMetaData();
        return result;
    }

    private PropertyMetaData(PropertyMetaData other) {
        super(other);
        predicate = other.predicate;
        metaDataType = other.metaDataType;
        specifiedProperty = true;
    }
    
    private PropertyMetaData(URI predicate){
        super(predicate.getLocalName());
        this.predicate = predicate;
        metaDataType = null;
        specifiedProperty = false;
    }
    
    private PropertyMetaData(){
        super("Type");
        this.predicate = RdfConstants.TYPE_URI;
        metaDataType = new UriType();
        specifiedProperty = true;
    }

    @Override
    public void loadValues(Resource id, Set<Statement> data, MetaData parent) {
        setupValues(id, parent);
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
        if (objectClass.startsWith(XsdConstants.PREFIX)){
            return new XsdType(objectClass);
        }
        throw new MetaDataException ("Unexpected " + SchemaConstants.CLASS + " " + objectClass);
    }
    
    @Override
    void appendShowAll(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        //builder.append(id);
        if (specifiedProperty){
            builder.append("Property ");
            builder.append(name);
        } else {
            builder.append("RawRDF ");            
            builder.append(predicate);
        }
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
        if (specifiedProperty){
            builder.append("class ");
            builder.append(metaDataType);        
            newLine(builder);
        } else {
            builder.append("Unspecified RDF found in the data. ");
            newLine(builder);
        }
    }

    @Override
    public PropertyMetaData getSchemaClone() {
        return new PropertyMetaData(this);
    }

    @Override
    public boolean hasRequiredValues() {
        if (values.isEmpty()){
            if (specifiedProperty){
                return false;          
            } else {
                return true;
            }
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
        if (specifiedProperty) {
            for (Value value: values){
                if (!metaDataType.correctType(value)){
                    return false;
                }
            }
        }
        //If no incorrect values return true. Even if there are No values.
        return true;
    }

    @Override
    public void appendValidityReport(StringBuilder builder, boolean includeWarnings, int tabLevel) {
        if (specifiedProperty) {
            if (values.isEmpty()){
                appendEmptyReport(builder, tabLevel);
            } else if (!hasCorrectTypes()){
                appendIncorrectTypeReport(builder, tabLevel);
            } else {
                //Ok so nothing to append
            }
        } else {
            appendUnspecifiedReport(builder, includeWarnings, tabLevel);            
        }
    }

    private void appendEmptyReport(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("ERROR: ");
        builder.append(id );
        builder.append(":");
        builder.append(name);
        builder.append(" is missing. ");
        newLine(builder, tabLevel + 1);
        builder.append("Please add a statement with the predicate ");
        builder.append(predicate);
        newLine(builder);
        addDocumentationLink(builder, tabLevel);
   }
    
    private void appendIncorrectTypeReport(StringBuilder builder, int tabLevel) {
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
        addDocumentationLink(builder, tabLevel);
    }
    
    private void appendUnspecifiedReport(StringBuilder builder, boolean includeWarnings, int tabLevel) {
        if (includeWarnings){
            tab(builder, tabLevel);
            builder.append("INFO: ");
            builder.append(id);
            builder.append(" has an extra Predicate ");
            builder.append(predicate);
            newLine(builder);
        }
    }
    
    @Override
    public boolean allStatementsUsed() {
        return specifiedProperty;
    }
    
    @Override
    void appendUnusedStatements(StringBuilder builder) {
        if (!specifiedProperty){
            for (Statement statement: rawRDF){
                builder.append(statement);
                newLine(builder);
            }
        }
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
    Set<PropertyMetaData> getLeaves() {
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
        if (parentLeaf == null){
            return;
        }
        if (parentLeaf instanceof PropertyMetaData){
            PropertyMetaData pmd = (PropertyMetaData)parentLeaf;
            values.addAll(pmd.values);
        } else {
            throw new UnsupportedOperationException("Unexpected LeafMetaData type of " + parentLeaf.getClass());
        }
    }

    @Override
    public Set<Statement> getRDF() {
        return rawRDF;
    }


}
