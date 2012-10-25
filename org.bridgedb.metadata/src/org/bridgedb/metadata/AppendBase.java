/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

/**
 *
 * @author Christian
 */
public abstract class AppendBase implements MetaData{
    
    public static final String CLEAR_REPORT = "No issues found";
    static final boolean CHECK_ALL_PRESENT = true;
    
    @Override
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

    @Override
    public String Schema(){
        StringBuilder builder = new StringBuilder();
        appendSchema(builder, 0);
        return builder.toString();
    }

    abstract void appendSchema(StringBuilder builder, int tabLevel);
    
    abstract void appendShowAll(StringBuilder builder, int tabLevel);
    
    @Override
    public String validityReport(boolean includeWarnings) {
         StringBuilder builder = new StringBuilder();
         appendValidityReport(builder, CHECK_ALL_PRESENT, includeWarnings, 0);
         return builder.toString();
    }
    
    public void validate() throws MetaDataException {
        String report = this.validityReport(false);
        if (report.contains("ERROR")){
            throw new MetaDataException(report);
        }
    }

   abstract void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, int tabLevel);

    @Override
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
