// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.tools.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.rdf.constants.XMLSchemaConstants;
import org.bridgedb.tools.metadata.constants.OwlConstants;
import org.bridgedb.tools.metadata.constants.RdfsConstants;
import org.bridgedb.tools.metadata.constants.SchemaConstants;
import org.bridgedb.tools.metadata.type.LiteralType;
import org.bridgedb.tools.metadata.type.MetaDataType;
import org.bridgedb.tools.metadata.type.StringType;
import org.bridgedb.tools.metadata.type.UriType;
import org.bridgedb.tools.metadata.type.XsdType;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

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
    private final int cardinality;
    
    public PropertyMetaData(URI predicate, String type, RequirementLevel requirementLevel, String objectClass) 
            throws BridgeDBException{
        this(predicate, type, NO_CARDINALITY, requirementLevel, objectClass);
    }
    
    public PropertyMetaData(URI predicate, String type, int cardinality, RequirementLevel requirementLevel, 
            String objectClass) throws BridgeDBException{
        super(predicate.getLocalName(), type, requirementLevel);
        this.predicate = predicate;
        metaDataType = getMetaDataType(objectClass);
        this.cardinality = cardinality;
    }

    public static PropertyMetaData getUnspecifiedProperty(URI predicate, String type){
        PropertyMetaData result = new PropertyMetaData(predicate, type);
        return result;
    }
    
    public static PropertyMetaData getTypeProperty(Resource id, String type){
        PropertyMetaData result = new PropertyMetaData(id, type);
        return result;
    }

   public static PropertyMetaData getTypeProperty(String type){
        PropertyMetaData result = new PropertyMetaData(type);
        return result;
    }

   private PropertyMetaData(Resource id, PropertyMetaData other) {
        super(id, other);
        setupValues(id);
        predicate = other.predicate;
        metaDataType = other.metaDataType;
        this.cardinality = other.cardinality;
    }
    
    private PropertyMetaData(URI predicate, String type){
        super(predicate.getLocalName(), type, RequirementLevel.UNSPECIFIED);
        this.predicate = predicate;
        metaDataType = null;
        this.cardinality = this.NO_CARDINALITY;
    }
    
    private PropertyMetaData(Resource id, String type){
        super(id, "Type", type, RequirementLevel.MUST);
        this.predicate = RdfConstants.TYPE_URI;
        metaDataType = new UriType();
        this.cardinality = this.NO_CARDINALITY;
    }

    private PropertyMetaData(String type){
        super("Type", type, RequirementLevel.MUST);
        this.predicate = RdfConstants.TYPE_URI;
        metaDataType = new UriType();
        this.cardinality = this.NO_CARDINALITY;
    }

    @Override
    public void loadValues(Set<Statement> data, Set<String> errors) {
        for (Iterator<Statement> iterator = data.iterator(); iterator.hasNext();) {
            Statement statement = iterator.next();
            if (statement.getSubject().equals(id) && statement.getPredicate().equals(predicate)){
                if (!statement.getPredicate().equals(VoidConstants.SUBSET)){
                    iterator.remove();
                }
                rawRDF.add(statement);
                values.add(statement.getObject());
            }
        }  
    }

    private MetaDataType getMetaDataType(String objectClass) throws BridgeDBException{
        //if (SchemaConstants.CLASS_ALLOWED_URIS.equalsIgnoreCase(objectClass)){
        //    return new AllowedUriType(element);
        //}
        //if (SchemaConstants.CLASS_ALLOWED_VALUES.equalsIgnoreCase(objectClass)){
        //    return new AllowedValueType(element);
        //}
        //if (SchemaConstants.CLASS_DATE.equalsIgnoreCase(objectClass)){
        //    return new DateType();
        // }
        //if (SchemaConstants.CLASS_INTEGER.equalsIgnoreCase(objectClass)){
        //    return new IntegerType();
        //}
        //if (SchemaConstants.CLASS_STRING.equalsIgnoreCase(objectClass)){
        //    return new StringType();
        //}
        if (OwlConstants.THING.equalsIgnoreCase(objectClass)){
            return new UriType();
        }
        if (RdfsConstants.LITERAL.equalsIgnoreCase(objectClass)){
            return new LiteralType();
        }
        if (objectClass.startsWith("xsd:")){
            objectClass = XMLSchemaConstants.PREFIX + objectClass.substring(4);
        }
        if (objectClass.equalsIgnoreCase(XMLSchemaConstants.STRING.stringValue())){
            return new StringType();
        }
        if (objectClass.startsWith(XMLSchemaConstants.PREFIX)){
            return XsdType.getByType(objectClass);
        }
        throw new BridgeDBException ("Unexpected " + SchemaConstants.CLASS + " " + objectClass);
    }
    
    @Override
    void appendShowAll(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        //builder.append(id);
        if (requirementLevel == RequirementLevel.UNSPECIFIED){
            builder.append("RawRDF ");            
            builder.append(predicate);
        } else {
            builder.append("Property ");
            builder.append(name);
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
        if (requirementLevel == RequirementLevel.UNSPECIFIED){
            builder.append("Unspecified RDF found in the data. ");
        } else {
            builder.append("class ");
            builder.append(metaDataType.getCorrectType());        
        }
        newLine(builder, tabLevel + 1);
        builder.append("RequirementLevel ");
        builder.append(requirementLevel);        
        if (this.cardinality != NO_CARDINALITY){
            newLine(builder, tabLevel + 1);
            builder.append("Cardinality ");
            builder.append(cardinality);        
        }
        newLine(builder);
    }

    @Override
    public PropertyMetaData getSchemaClone(Resource id, MetaDataCollection collection) {
        return new PropertyMetaData(id, this);
    }

    @Override
    public boolean hasRequiredValues() {
        if (requirementLevel != RequirementLevel.MUST){
            return true;
        } else if (values.isEmpty()){
            return false;          
        } else if (this.cardinality == NO_CARDINALITY) {
            return true;
        } else {
            return values.size() == this.cardinality;
        }
    }

    @Override
    boolean hasValues() {
        return !values.isEmpty();
    }
    
    @Override
    public boolean hasCorrectTypes() throws BridgeDBException {
        if (requirementLevel != RequirementLevel.UNSPECIFIED){
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
    public void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, 
    int tabLevel) throws BridgeDBException {
        if (requirementLevel == RequirementLevel.UNSPECIFIED){
            appendUnspecifiedReport(builder, includeWarnings, tabLevel);            
        } else if (!hasCorrectTypes()){
            appendIncorrectTypeReport(builder, tabLevel, includeWarnings);
        } else if (checkAllpresent && values.isEmpty()){
            appendEmptyReport(builder, tabLevel, includeWarnings);
        } else if (this.cardinality != NO_CARDINALITY && this.cardinality != values.size()){
            appendIncorrectCardinalityReport(builder, tabLevel, includeWarnings);
        } else {
            //Ok so nothing to append
        }
    }
    
    private void appendEmptyReport(StringBuilder builder, int tabLevel, boolean includeWarnings) {
        if (requirementLevel == RequirementLevel.MUST){
            tab(builder, tabLevel);
            builder.append("ERROR: ");
            builder.append(name);
            builder.append(" is Missing. ");
            newLine(builder, tabLevel + 1);
            builder.append("Please add a statement with the predicate ");
            builder.append(predicate);
            newLine(builder);
            addDocumentationLink(builder, tabLevel);
        } else if (requirementLevel == RequirementLevel.SHOULD && includeWarnings){
            tab(builder, tabLevel);
            builder.append("WARNING: ");
            builder.append(name);
            builder.append(" is missing. ");
            newLine(builder, tabLevel + 1);
            builder.append("Please consider adding a statement with the predicate ");
            builder.append(predicate);
            newLine(builder);
            addDocumentationLink(builder, tabLevel);
        }
    }
    
    private void appendIncorrectCardinalityReport(StringBuilder builder, int tabLevel, boolean includeWarnings) {
        if (requirementLevel == RequirementLevel.IGNORE && !includeWarnings) { return; }
        tab(builder, tabLevel);
        if (requirementLevel == RequirementLevel.IGNORE){
            builder.append("WARNING: Incorrect cardinaility for ");            
        } else {
            builder.append("ERROR: Incorrect cardinaility for ");
        }
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("Expected ");
        builder.append(cardinality);
        builder.append(" values but Found ");
        builder.append(values.size());
        newLine(builder, tabLevel + 1);
        if (this.cardinality > values.size()) {
            builder.append("Please add statement(s) with the predicate ");
        } else {
            builder.append("Please remove statement(s) with the predicate ");            
        }
        builder.append(predicate);
        newLine(builder);
        addDocumentationLink(builder, tabLevel);
    }

    private void appendIncorrectTypeReport(StringBuilder builder, int tabLevel, boolean includeWarnings) throws BridgeDBException {
        if (requirementLevel == RequirementLevel.IGNORE && !includeWarnings) { return; }
        tab(builder, tabLevel);
        if (requirementLevel == RequirementLevel.IGNORE){
            builder.append("WARNING: Incorrect type for ");            
        } else {
            builder.append("ERROR: Incorrect type for ");
        }
        builder.append(name);
        for (Value value: values){
            if (!metaDataType.correctType(value)){
                newLine(builder, tabLevel + 1);
                builder.append("Expected ");
                builder.append(metaDataType.getCorrectType());
                newLine(builder, tabLevel + 1);
                builder.append("Found ");
                builder.append(value);
                builder.append(" Which is a  ");
                addClass(builder,value);
                newLine(builder, tabLevel + 1);
                builder.append("Please check statement with the predicate ");            
                builder.append(predicate);
            }
        }
        newLine(builder);
        addDocumentationLink(builder, tabLevel);
    }
    
    private void addClass(StringBuilder builder, Value value) {
        if (value instanceof URI){
            builder.append("URI.");
        } else if (value instanceof Literal){
            Literal literal = (Literal)value;
            URI type = literal.getDatatype();
            if (type != null){
                builder.append(type.getLocalName());
            } else {
                builder.append("Literal.");
            }
        } else if (value instanceof Literal){
            builder.append("Literal");
        } else {
            builder.append(value.getClass());
        }
    }

    private void appendUnspecifiedReport(StringBuilder builder, boolean includeWarnings, int tabLevel) {
        if (includeWarnings){
            tab(builder, tabLevel);
            builder.append("INFO: extra Predicate ");
            builder.append(predicate);
            builder.append(" found.");
            newLine(builder);
        }
    }
    
    @Override
    public boolean allStatementsUsed() {
        return requirementLevel != RequirementLevel.UNSPECIFIED;

    }
    
    @Override
    void appendUnusedStatements(StringBuilder builder) {
        if (requirementLevel == RequirementLevel.UNSPECIFIED){
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
    public Set<ResourceMetaData> getResoucresByPredicate(URI predicate){
        return null;
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
    public void addParent(LeafMetaData parentLeaf, Set<String> errors) {
        if (parentLeaf == null){
            return;
        }
        if (parentLeaf instanceof PropertyMetaData){
            if (values.isEmpty()){
                PropertyMetaData pmd = (PropertyMetaData)parentLeaf;
                values.addAll(pmd.values);
            }
        } else {
            errors.add("Unexpected LeafMetaData type of " + parentLeaf.getClass());
        }
    }

    @Override
    public Set<Statement> getRDF() {
        return rawRDF;
    }

}
