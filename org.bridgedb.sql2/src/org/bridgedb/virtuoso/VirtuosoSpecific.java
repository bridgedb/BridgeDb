package org.bridgedb.virtuoso;

import org.bridgedb.sql.SQLSpecific;

/**
 *
 * @author Christian
 */
public class VirtuosoSpecific implements SQLSpecific{

    @Override
    public boolean supportsIsValid() {
        return false;
    }

    @Override
    public boolean supportsMultipleInserts() {
        return false;
    }

    @Override
    public boolean supportsTop() {
        return true;
    }

    @Override
    public boolean supportsLimit() {
        return false;
    }

    @Override
    public String getAutoIncrementCommand() {
        return "IDENTITY";
    }
    
}
