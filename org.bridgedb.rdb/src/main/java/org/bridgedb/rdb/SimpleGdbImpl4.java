package org.bridgedb.rdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
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

	final SimpleGdb.QueryLifeCycle qCrossRefs4 = new SimpleGdb.QueryLifeCycle (
			"SELECT dest.idRight, dest.codeRight, node.isPrimary FROM link AS src " +
	        "JOIN link AS dest ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
	        "JOIN datanode AS node ON node.id = dest.idRight " +
			"WHERE src.idRight = ? AND src.codeRight = ?"
		);
	final SimpleGdb.QueryLifeCycle qCrossRefsWithCode4 = new SimpleGdb.QueryLifeCycle (
			"SELECT dest.idRight, dest.codeRight, node.isPrimary FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
	        "JOIN datanode AS node ON node.id = dest.idRight " +
			"WHERE src.idRight = ? AND src.codeRight = ? AND dest.codeRight = ?"
		);

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

	@Override
	public Set<Xref> mapID (Xref idc, DataSource... resultDs) throws IDMapperException
	{
		final QueryLifeCycle pst = resultDs.length != 1 ? qCrossRefs4 : qCrossRefsWithCode4;
		Set<Xref> refs = new HashSet<Xref>();
		
		if (idc.getDataSource() == null) return refs;
		synchronized (pst) {
			try
			{
				pst.init();
				pst.setString(1, idc.getId());
				pst.setString(2, idc.getDataSource().getSystemCode());
				if (resultDs.length == 1) pst.setString(3, resultDs[0].getSystemCode());			
				
				Set<DataSource> dsFilter = new HashSet<DataSource>(Arrays.asList(resultDs));
	
				ResultSet rs = pst.executeQuery();
				while (rs.next())
				{
					DataSource ds = DataSource.getExistingBySystemCode(rs.getString(2));
					if (resultDs.length == 0 || dsFilter.contains(ds))
					{
						Xref xref = (rs.getString(3) != null && rs.getBoolean(3))
							? new Xref (rs.getString(1), ds, true) : new Xref (rs.getString(1), ds, false);
						refs.add (xref);
					}
				}
			}
			catch (SQLException e)
			{
				throw new IDMapperException (e);
			}
			finally {pst.cleanup(); }
		
			return refs;
		}
	}

}
