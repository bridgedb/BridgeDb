/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public abstract class MetaDataBase implements MetaData{
    
    static final String CLEAR_REPORT = "No issues found";

    Set<Statement> rawRDF;
    Resource id;
    MetaData parent;
    
    MetaDataBase(){
        rawRDF = new HashSet<Statement>();
    }
    
    abstract void loadValues(Resource id, Set<Statement> data, MetaData parent);

    void setupValues(Resource id, MetaData parent){
        this.id = id;
        this.parent = parent;
    }
    
    public synchronized String toString(){
         StringBuilder builder = new StringBuilder();
         appendToString(builder, 0);
         return builder.toString();
    }
    
    public String Schema(){
        StringBuilder builder = new StringBuilder();
        appendSchema(builder, 0);
        return builder.toString();
    }

    abstract void appendSchema(StringBuilder builder, int tabLevel);
    
    abstract void appendToString(StringBuilder builder, int tabLevel);
    
    final void tab(StringBuilder builder, int tab){
        for (int i = 0; i < tab; i++){
            builder.append("\t");
        }
    }
    
    final void newLine(StringBuilder builder){
        builder.append("\n");
    }

    final void newLine(StringBuilder builder, int tab){
        builder.append("\n");
        for (int i = 0; i < tab; i++){
            builder.append("\t");
        }        
    }

    abstract MetaDataBase getSchemaClone();

    public String validityReport(RequirementLevel forceLevel, boolean includeWarnings) {
         StringBuilder builder = new StringBuilder();
         appendValidityReport(builder, forceLevel, includeWarnings, 0);
         return builder.toString();
    }

    abstract void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel);

}
