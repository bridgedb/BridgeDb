/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.SchemaConstants;
import java.util.HashSet;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.w3c.dom.Element;

/**
 *
 * @author Christian
 */
public class PropertyMetaData extends MetaDataBase implements MetaData{

    private final String name;      
    private final URI predicate;
    private final String objectClass;
    private Set<Value> values;

    public PropertyMetaData(Element element) throws MetaDataException {
        name = element.getAttribute("name");
        String predicateSt = element.getAttribute(SchemaConstants.PREDICATE);
        predicate = new URIImpl(predicateSt);
        objectClass = element.getAttribute(SchemaConstants.CLASS);
        values = new HashSet<Value>();
    }
    
    private PropertyMetaData(PropertyMetaData other) {
        name = other.name;
        predicate = other.predicate;
        objectClass = other.objectClass;
        values = new HashSet<Value>();
    }
    
    @Override
    public void appendToString(StringBuilder builder, int tabLevel) {
        tab(builder, tabLevel);
        builder.append("Property ");
        builder.append(name);
        newLine(builder, tabLevel + 1);
        builder.append("predicate ");
        builder.append(predicate);        
        newLine(builder, tabLevel + 1);
        builder.append("class ");
        builder.append(objectClass);        
        newLine(builder);
   }

    @Override
    public void loadValues(Resource id, Set<Statement> data) {
        values = MetaDataCollection.getBySubjectPredicate(data, id, predicate);
    }

    @Override
    public PropertyMetaData getSchemaClone() {
        return new PropertyMetaData(this);
    }

    @Override
    public boolean hasRequiredValues(RequirementLevel requirementLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCorrectTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String validityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
