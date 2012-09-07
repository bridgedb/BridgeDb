/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

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
public class Property extends MetaDataBase implements MetaDataClass{

    private final String name;      
    private final URI predicate;
    private final String objectClass;
    private Set<Value> values;

    public Property(Element element) throws MetaDataException {
        name = element.getAttribute("name");
        String predicateSt = element.getAttribute(Schema.PREDICATE);
        predicate = new URIImpl(predicateSt);
        objectClass = element.getAttribute(Schema.CLASS);
        values = new HashSet<Value>();
    }
    
    private Property(Property other) {
        name = other.name;
        predicate = other.predicate;
        objectClass = other.objectClass;
        values = new HashSet<Value>();
    }
    
    @Override
    public void appendToSchema(StringBuilder builder, int tabLevel) {
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
    public MetaDataClass getSchemaClone() {
        return new Property(this);
    }
    
}
