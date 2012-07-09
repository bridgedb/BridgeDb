package org.bridgedb.mysql;

import org.bridgedb.sql.SQLSpecific;

/**
 *
 * @author Christian
 */
public class MySQLSpecific implements SQLSpecific{

    @Override
    public boolean supportsIsValid() {
        return true;
    }

    @Override
    public boolean supportsMultipleInserts() {
        return true;
    }

    @Override
    public boolean supportsTop() {
        return false;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String getAutoIncrementCommand() {
        return "AUTO_INCREMENT";
    }
    
}
