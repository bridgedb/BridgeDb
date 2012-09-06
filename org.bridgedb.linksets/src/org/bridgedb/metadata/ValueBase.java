/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bridgedb.linkset.constants.HasURI;
import org.bridgedb.metadata.RequirementLevel;
import org.bridgedb.utils.Reporter;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.sail.memory.model.CalendarMemLiteral;

/**
 *
 * @author Christian
 */
public abstract class ValueBase implements MetaPart{
    
    protected final String name;
    protected URI predicate;
    protected final Class type;
    protected final RequirementLevel level;
    private static RequirementLevel ALLWAYS_WARN_LEVEL = RequirementLevel.SHOULD;
    
    public ValueBase(String name, URI predicate, Class type, RequirementLevel level){
        this.name = name;
        this.type = type;
        this.level = level;
        this.predicate = predicate;
    }
    
    abstract boolean hasValue();
    
    public String toString(){
        return (this.getClass() + " : " + name + "\n\ttype: " + type + "\n\tlevel: " + level + "\n\tpredicate: " + predicate);
    }

    boolean correctType (Value value){
        if (type.isAssignableFrom(value.getClass())){
            return true;
        }
        if (Value.class.isAssignableFrom(type)){
            Reporter.report(type + " != " + value.getClass());
            return false;
        }
        if (type == String.class){
            String temp = value.stringValue();
            if (temp != null && !temp.isEmpty()) return true;
            Reporter.report("EXpected a none empty String but found " + value.stringValue());
            return false;
        } else if (type == Date.class){
            if (value instanceof Literal) {
                Literal literal = (Literal)value;
                try {
                    XMLGregorianCalendar test = literal.calendarValue();
                    return true;
                } catch (IllegalArgumentException e){
                    Reporter.report(type + " != " + value.getClass() + " ~ " + e);
                    return false;
                }
            } else {
                Reporter.report(type + " != " + value.getClass()+ " not Literal");
                return false; 
            }
        } else if (type == Integer.class){
            if (value instanceof Literal) {
                Literal literal = (Literal)value;
                try {
                    BigInteger test = literal.integerValue();
                    return true;
                } catch (NumberFormatException e){
                    Reporter.report(type + " != " + value.getClass() + " ~ " + e);
                    return false;
                }
            } else {
                Reporter.report(type + " != " + value.getClass()+ " not Literal");
                return false; 
            }
        } else if (HasURI.class.isAssignableFrom(type)) {
            return HasURIChecker.legalValue(value, type);
        } else {
            throw new UnsupportedOperationException("Checking of type " + type + " Not yet implemented");
        }
    }

    public void appendValidityReport(StringBuilder builder, MetaData parent, RequirementLevel forceLevel, 
            boolean includeWarnings) {
        if (hasValue()){
            if (!hasCorrectTypes()){
                appendFormatReport(builder);
            } else {
                return; //YEAH all ok
            }
        } else if (level.compareTo(forceLevel) <= 0){
            builder.append("ERROR in ");
            builder.append(parent.id);
            builder.append("\n\t");
            builder.append(name);
            builder.append(" not found. \n\tPlease add a statment with the predicate ");
            builder.append(predicate);
            builder.append(".\n");
        } else {
            if (includeWarnings){
                if (level.compareTo(ALLWAYS_WARN_LEVEL) <= 0){
                    builder.append("\tWARNING in  ");
                    builder.append(parent.id);
                    builder.append("\n\t\t");
                    builder.append(name);
                    builder.append(" not found and is listed as a ");
                    builder.append(level);
                    builder.append("\n");                
                }
            }
        }
    }

    abstract void appendFormatReport (StringBuilder builder);

 }
