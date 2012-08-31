/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

/**
 *
 * @author Christian
 */
public class MetaDataFactory {
 
    public static MetaData makeSpecific(RDFData input){
        if (input.hasPredicate(DescriptionMetaData.RESOURCE_TYPE)){
            return new DescriptionMetaData(input);
        }
        if (input.hasPredicate(LinkSetMetaData.RESOURCE_TYPE)){
            return new LinkSetMetaData(input);
        }
        if (input.hasPredicate(DataSetMetaData.RESOURCE_TYPE)){
            return new DataSetMetaData(input);
        }       
        //Will b=nearly certainly be invalid but last ditch effort
        return new DescriptionMetaData(input);
    }
}
