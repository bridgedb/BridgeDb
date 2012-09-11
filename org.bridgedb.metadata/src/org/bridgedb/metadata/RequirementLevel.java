/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

/**
 * W3C RFC 2119 Key Words
 * @author Christian
 */
public enum RequirementLevel {
    TECHNICAL_MUST, MUST, SHOULD, MAY; //Negative requirement not programmed, SHOULD_NOT, MUST_NOT;
    
    public static RequirementLevel parse(String text) throws MetaDataException{
        for (RequirementLevel requirementLevel: RequirementLevel.values()){
            if (text.equalsIgnoreCase(requirementLevel.name())) {
                return requirementLevel;
            }
        }
        throw new MetaDataException("Unexpected RequirementLevel text "+ text);
    }
}
