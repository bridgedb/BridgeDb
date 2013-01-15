/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.tools.metadata.type;

import javax.xml.datatype.XMLGregorianCalendar;
import org.openrdf.model.Literal;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public class DateType implements MetaDataType{

    @Override
    public boolean correctType(Value value) {
        if (value instanceof Literal) {
            Literal literal = (Literal)value;
            try {
                XMLGregorianCalendar test = literal.calendarValue();
                return true;
            } catch (IllegalArgumentException e){
                return false;
            }
        } else {
            return false; 
        }
    }

    @Override
    public String getCorrectType() {
        return " A Date";
    }

  
}
