/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

/**
 *
 * @author Christian
 */
public abstract class AppendBase {
    
    static final String CLEAR_REPORT = "No issues found";
    
    public synchronized String toString(){
         StringBuilder builder = new StringBuilder();
         appendShowAll(builder, 0);
         return builder.toString();
    }
    
    String showAll() {
         StringBuilder builder = new StringBuilder();
         appendShowAll(builder, 0);
         return builder.toString();
    }

    public String Schema(){
        StringBuilder builder = new StringBuilder();
        appendSchema(builder, 0);
        return builder.toString();
    }

    abstract void appendSchema(StringBuilder builder, int tabLevel);
    
    abstract void appendShowAll(StringBuilder builder, int tabLevel);
    
    public String validityReport(boolean includeWarnings) {
         StringBuilder builder = new StringBuilder();
         appendValidityReport(builder, includeWarnings, 0);
         if (builder.length() > 0){
            return builder.toString();
         } else {
            return CLEAR_REPORT; 
         }
    }

    abstract void appendValidityReport(StringBuilder builder, boolean includeWarnings, int tabLevel);

    public String unusedStatements(){
        StringBuilder builder = new StringBuilder();
        appendUnusedStatements(builder);
        return builder.toString();        
    }
    
    abstract void appendUnusedStatements(StringBuilder builder);

    static public void tab(StringBuilder builder, int tab){
        for (int i = 0; i < tab; i++){
            builder.append("\t");
        }
    }
    
    static public void newLine(StringBuilder builder){
        builder.append("\n");
    }

    static public void newLine(StringBuilder builder, int tab){
        builder.append("\n");
        for (int i = 0; i < tab; i++){
            builder.append("\t");
        }        
    }

}
