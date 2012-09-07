/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 *
 * @author Christian
 */
interface MetaDataClass {

    public String schema();
    
    void appendToSchema(StringBuilder builder, int tabLevel);
    
    void loadValues(Resource id, Set<Statement> data);
    
    MetaDataClass getSchemaClone();

}
