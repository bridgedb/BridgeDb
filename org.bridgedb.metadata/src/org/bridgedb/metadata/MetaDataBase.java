/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

/**
 *
 * @author Christian
 */
public abstract class MetaDataBase implements MetaData{
    
    public synchronized String toString(){
         StringBuilder builder = new StringBuilder();
         appendToString(builder, 0);
         return builder.toString();
    }
    
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

}
