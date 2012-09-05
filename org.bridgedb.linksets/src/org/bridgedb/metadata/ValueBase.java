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
public abstract class ValueBase {
    
    protected final String name;
    protected URI predicate;
    protected final Class type;
    protected final RequirementLevel level;
    protected List<ValueBase> alternatives;
    private static RequirementLevel ALLWAYS_WARN_LEVEL = RequirementLevel.SHOULD;
   // protected ValueBase requirementDepency
    
    public ValueBase(String name, URI predicate, Class type, RequirementLevel level){
        this.name = name;
        this.type = type;
        this.level = level;
        this.predicate = predicate;
    }
    
    public abstract boolean multipleValuesAllowed();
    
    public void addAlternative(ValueBase alternative){
        if (alternatives == null){
            alternatives = new ArrayList<ValueBase>();
        }
        alternatives.add(alternative);
    }

    abstract void addValue(Value value);
   
    public boolean hasValue(boolean exceptAlternatives){
        if (hasValue()) { return true; }
        if (!exceptAlternatives) { return false; }
        if (alternatives == null) { return false; }
        for (ValueBase alternative: alternatives){
            if (alternative.hasValue()){
                return true;
            }
        }
        return false;
    }
    
    abstract boolean hasValue();
    
    public String toString(){
        return ("ValueBase : " + name + "\n\ttype: " + type + "\n\tlevel: " + level + "\n\tpredicate: " + predicate);
    }

    abstract boolean correctType();
    
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
            return (temp != null && !temp.isEmpty());
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

    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean exceptAlternatives, 
            boolean includeWarnings) {
        if (hasValue()){
            if (!correctType()){
                appendFormatReport(builder);
            } else {
                return; //YEAH all ok
            }
        } else if (level.compareTo(forceLevel) <= 0){
            if (hasValue(exceptAlternatives)){
                if (includeWarnings){
                    builder.append("\tWARNING: " + name + " not found but an alternative is available.\n");
                }
            } else {
                builder.append("ERROR: " + name + " not found. \n"
                        + "\tPlease add a statment with the predicate " + predicate + ".\n");
            }
        } else {
            if (includeWarnings){
                if (level.compareTo(ALLWAYS_WARN_LEVEL) <= 0){
                    if (hasValue(exceptAlternatives)){
                        return; //Alterntive below forceLevel so fine
                    } else {
                        builder.append("\tWARNING: " + name + " not found and is listed as a " + level + "\n");                
                    }
                }
            }
        }
    }

    abstract void appendFormatReport (StringBuilder builder);

    abstract void show(StringBuilder builder);
 }
