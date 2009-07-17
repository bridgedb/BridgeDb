
package org.bridgedb.webservice.biomart;

import java.util.Map;

public class Database {
    private String dbname;
    private Map<String, String> param;

    public Database(String dbname, Map<String, String> param) {
        this.dbname = dbname;
        this.param = param;
    }

    public String getName() {
        return dbname;
    }

    public Map<String, String> getParam() {
        return param;
    }
    
    public boolean visible() {
        return param.get("visible").equals("1");
    }
    
    public String displayName() {
        return param.get("displayName");
    }

    public String toString() {
        return displayName();
    }
}
