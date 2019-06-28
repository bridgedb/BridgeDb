package org.bridgedb.rdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

class SimpleGdbImpl4 extends SimpleGdbImplCommon {

    private static final int GDB_COMPAT_VERSION = 4; //Preferred schema version

    /**
     * Opens a connection to the Gene Database located in the given file.
     * A new instance of this class is created automatically.
     * @param dbName The file containing the Gene Database.
     * @param con An existing java SQL connection
     * @param props PROP_RECREATE if you want to create a new database (possibly overwriting an existing one)
     * 	or PROP_NONE if you want to connect read-only
     * @throws IDMapperException when the database could not be created or connected to
     */

    public SimpleGdbImpl4(String dbName, String connectionString) throws IDMapperException
    {
        super(dbName, connectionString);
        checkSchemaVersion();
    }
    /**
     * look at the info table of the current database to determine the schema version.
     * @throws IDMapperException when looking up the schema version failed
     */
    private void checkSchemaVersion() throws IDMapperException
    {
        int version = 0;
        try
        {
            ResultSet r = getConnection().createStatement().executeQuery("SELECT schemaversion FROM info");
            if(r.next()) version = r.getInt(1);
        }
        catch (SQLException e)
        {
            //Ignore, older db's don't even have schema version
        }
        if(version != GDB_COMPAT_VERSION)
        {
            throw new IDMapperException ("Implementation and schema version mismatch");
        }
    }
    /** {@inheritDoc} */
    public Set<String> getAttributes(Xref ref, String attrname)
            throws IDMapperException
    {
        Set<String> result = new HashSet<String>();
        final QueryLifeCycle pst = qAttribute;
        synchronized (pst)
        {
            try {
                pst.init();
                pst.setString (1, ref.getId());
                pst.setString (2, ref.getDataSource().getSystemCode());
                //pst.setBoolean(3, ref.isPrimary());
                pst.setString (3, attrname);

                ResultSet r = pst.executeQuery();
                if (r.next())
                {
                    result.add (r.getString(1));
                }
                return result;
            }
            catch(SQLException e) { throw new IDMapperException (e); } // Database unavailable
            finally {pst.cleanup();
            }
        }
    }
    /** {@inheritDoc} */
    public Map<String, Set<String>> getAttributes(Xref ref)
            throws IDMapperException
    {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        final QueryLifeCycle pst = qAllAttributes;
        synchronized (pst)
        {
            try {
                pst.init();
                pst.setString (1, ref.getId());
                pst.setString (2, ref.getDataSource().getSystemCode());
                //pst.setBoolean(3, ref.isPrimary());
                ResultSet r = pst.executeQuery();
                while (r.next())
                {
                    String key = r.getString(1);
                    String value = r.getString(2);
                    if (result.containsKey (key))
                    {
                        result.get(key).add (value);
                    }
                    else
                    {
                        Set<String> valueSet = new HashSet<String>();
                        valueSet.add (value);
                        result.put (key, valueSet);
                    }
                }
                return result;
            } catch	(SQLException e) { throw new IDMapperException ("Xref:" + ref, e); } // Database unavailable
            finally {pst.cleanup(); }
        }
    }
}
