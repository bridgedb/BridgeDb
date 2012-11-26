/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata.type;

import java.util.HashMap;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.constants.XMLSchemaConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Christian
 */
public enum XsdType implements MetaDataType{
    
    ANY_TYPE(null,"anyType"),
        ALL_COMPLEX_TYPES(ANY_TYPE, "all complex types"),
            ANY_SIMPLE_TYPE(ALL_COMPLEX_TYPES, "anySimpleType"),
                ANY_ATOMIC_TYPE(ANY_SIMPLE_TYPE, "anyAtomicType"),
                    ANY_URI(ANY_ATOMIC_TYPE, "anyURI"),
                    BASE64BINARY(ANY_ATOMIC_TYPE, "base64Binary "),
                    BOOLEAN(ANY_ATOMIC_TYPE, "boolean"),
                    DATE(ANY_ATOMIC_TYPE, "date"),
                    DATE_TIME(ANY_ATOMIC_TYPE, "dateTime"),
                        DATE_TIME_STAMP(DATE_TIME, "dateTimeStamp"),
                    DECIMAL(ANY_ATOMIC_TYPE, "decimal"),
                        INTEGER(DECIMAL, "integer"),
                                LONG(INTEGER, "long"),
                                    INT(LONG, "int"),
                                        SHORT(INT, "short"),
                                            BYTE(SHORT, "byte"),
                            NON_NEGATIVE_INTEGER(INTEGER, "nonNegativeInteger"),
                                POSITIVE_INTEGER(NON_NEGATIVE_INTEGER, "positiveInteger"),
                                UNSIGNED_LONG(NON_NEGATIVE_INTEGER, "unsignedLong"),
                                    UNSIGNED_INT(UNSIGNED_LONG, "unsignedInt"),
                                        UNSIGNED_SHORT(UNSIGNED_INT, "unsignedShort"),
                                            UNISGNED_BYTE(UNSIGNED_SHORT, "unsignedByte"),
                            NON_POSITIVE_INTEGER(INTEGER, "nonPositiveInteger"),
                                NEGATIVE_INTEGER(NON_POSITIVE_INTEGER, "negativeInteger"),
                    DOUBLE(ANY_ATOMIC_TYPE, "double"),
                    DURATION(ANY_ATOMIC_TYPE, "duration"),
                        DAY_TIME_DURATION(DURATION, "dayTimeDuration"),
                        YEAR_MONTH_DURATION(DURATION, "yearMonthDuration"),
                    FLOAT(ANY_ATOMIC_TYPE, "float"),
                    G_DAY(ANY_ATOMIC_TYPE, "gDay"),
                    G_MONTH(ANY_ATOMIC_TYPE, "gMonth"),
                    G_MONTH_DAY(ANY_ATOMIC_TYPE, "gMonthDay"),
                    G_YEAR(ANY_ATOMIC_TYPE, "gYear"),
                    G_YEAR_MONTH(ANY_ATOMIC_TYPE, "gYearMonth"),
                    HEX_BINARY(ANY_ATOMIC_TYPE, "hexBinary"),
                    NOTATION(ANY_ATOMIC_TYPE, "NOTATION "),
                    PRECISION_DECIMAL(ANY_ATOMIC_TYPE, "precisionDecimal"),
                    Q_NAME(ANY_ATOMIC_TYPE, "QName"),
                    STRING(ANY_ATOMIC_TYPE, "string"),
                        NORMALIZED_STRING(STRING, "normalizedString "),
                            TOKEN(NORMALIZED_STRING, "token"),
                                LANGUAGE(TOKEN, "language"),
                                NAME(TOKEN, "Name"),
                                    N_C_NAME(NAME, "NCName"),
                                        ENTITY(N_C_NAME, "ENTITY"),
                                        ID(N_C_NAME, "ID"),
                                        IDREF(N_C_NAME, "IDREF"),
                                NMTOKEN(TOKEN, "NMTOKEN"),
                    TIME(ANY_ATOMIC_TYPE, "time"),
                ENTITIES(ANY_SIMPLE_TYPE, "ENTITIES"),
                IDREFS(ANY_SIMPLE_TYPE, "IDREFS"),
                NMTOKENS(ANY_SIMPLE_TYPE, "NMTOKENS");
    
    private String type;
    private XsdType superType;
    private static HashMap<String,XsdType> register;
    
    private XsdType(XsdType theSuper, String theType){
        type = XMLSchemaConstants.PREFIX + theType;
        superType = theSuper;
        getRegister().put(type, this);
    }
    
    public static XsdType getByType(URI literalType) throws MetaDataException {
        return getByType(literalType.stringValue());
    }
    
    public static XsdType getByType(String type) throws MetaDataException{
        XsdType result = getRegister().get(type);
        if (result != null){
            return result;
        }
        throw new MetaDataException("No XSD type known for " + type);
    }
    
    private static HashMap<String,XsdType> getRegister(){
        if (register == null) {
            register = new HashMap<String,XsdType>();
        }
        return register;
    }

    @Override
    public boolean correctType(Value value) throws MetaDataException {
        if (value instanceof Literal){
            Literal literal = (Literal)value;
            URI literalType = literal.getDatatype();
            if (literalType == null){
                return false;
            }
            if (literalType.equals(type)){
                return true;
            }
            XsdType other = getByType(literalType);
            return sameOrSubType(other);
        }
        return false;
    }

    private boolean sameOrSubType(XsdType other) {
        if (this.equals(other)){
            return true;
        }
        if (other.superType == null){
            return false;
        }
        return sameOrSubType(other.superType);
    }

    @Override
    public String getCorrectType() {
        return type;
    }


    
    
}
