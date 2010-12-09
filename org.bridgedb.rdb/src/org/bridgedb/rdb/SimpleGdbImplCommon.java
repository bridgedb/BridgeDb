package org.bridgedb.rdb;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 * Some methods and constants that are shared between SimpleGdbImpl2 and SimpleGdbImpl3
 */
public abstract class SimpleGdbImplCommon extends SimpleGdb
{
	SimpleGdbImplCommon(String dbName, String connectionString) throws IDMapperException
	{
		super(dbName, connectionString);
		caps = new SimpleGdbCapabilities();
	}

	final SimpleGdb.QueryLifeCycle qDatasources = new SimpleGdb.QueryLifeCycle(
			"SELECT codeRight FROM link GROUP BY codeRight"
		);
	final SimpleGdb.QueryLifeCycle qInfo = new SimpleGdb.QueryLifeCycle(
			"SELECT * FROM info"
		);
	final SimpleGdb.QueryLifeCycle qXrefExists = new SimpleGdb.QueryLifeCycle(
			"SELECT id FROM " + "datanode" + " WHERE " +
			"id = ? AND code = ?"
		);
	final SimpleGdb.QueryLifeCycle qAttribute = new SimpleGdb.QueryLifeCycle(
			"SELECT attrvalue FROM attribute " +
			" WHERE id = ? AND code = ? AND attrname = ?"
		);
	final SimpleGdb.QueryLifeCycle qAllAttributes = new SimpleGdb.QueryLifeCycle(
			"SELECT attrname, attrvalue FROM attribute " +
			" WHERE id = ? AND code = ?"
		);
	final SimpleGdb.QueryLifeCycle qAttributesSet = new SimpleGdb.QueryLifeCycle(
			"SELECT attrname FROM attribute GROUP BY attrname"
		);
	final SimpleGdb.QueryLifeCycle qCrossRefs = new SimpleGdb.QueryLifeCycle (
			"SELECT dest.idRight, dest.codeRight FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
			"WHERE src.idRight = ? AND src.codeRight = ?"
		);
	final SimpleGdb.QueryLifeCycle qCrossRefsWithCode = new SimpleGdb.QueryLifeCycle (
			"SELECT dest.idRight, dest.codeRight FROM link AS src JOIN link AS dest " +
			"ON src.idLeft = dest.idLeft and src.codeLeft = dest.codeLeft " +
			"WHERE src.idRight = ? AND src.codeRight = ? AND dest.codeRight = ?"
		);
	final SimpleGdb.QueryLifeCycle qRefsByAttribute = new SimpleGdb.QueryLifeCycle (
			"SELECT datanode.id, datanode.code FROM datanode " +
			" LEFT JOIN attribute ON attribute.code = datanode.code AND attribute.id = datanode.id " +
			"WHERE attrName = ? AND attrValue = ?"
		);
	final SimpleGdb.QueryLifeCycle qFreeSearch = new SimpleGdb.QueryLifeCycle (
			"SELECT id, code FROM datanode WHERE " +
			"LOWER(ID) LIKE ?"
		);
	final SimpleGdb.QueryLifeCycle qAttributeSearch = new SimpleGdb.QueryLifeCycle (
			"SELECT id, code, attrvalue FROM attribute WHERE " +
			"attrname = ? AND LOWER(attrvalue) LIKE ?"
		);
	final SimpleGdb.QueryLifeCycle qIdSearchWithAttributes = new SimpleGdb.QueryLifeCycle (
			"SELECT id, code, attrvalue FROM attribute WHERE " +
			"attrname = ? AND LOWER(ID) LIKE ?"
		);

	/** {@inheritDoc} */
	public boolean xrefExists(Xref xref) throws IDMapperException 
	{
		if (xref.getDataSource() == null) return false;
		final QueryLifeCycle pst = qXrefExists;
		synchronized (pst) {
			try 
			{
				pst.init();
				pst.setString(1, xref.getId());
				pst.setString(2, xref.getDataSource().getSystemCode());
				ResultSet r = pst.executeQuery();
	
				while(r.next()) 
				{
					return true;
				}
			} 
			catch (SQLException e) 
			{
				throw new IDMapperException (e);
			}
			finally {pst.cleanup(); }
			return false;
		}
	}

	/**
	 * Read the info table and return as properties.
	 * @return a map where keys are column names and values are the fields in the first row.
	 * @throws IDMapperException when the database became unavailable
	 */
	Map<String, String> getInfo() throws IDMapperException
	{
		Map<String, String> result = new HashMap<String, String>();
		final QueryLifeCycle pst = qInfo;
		synchronized (pst) {
			try
			{
				pst.init();
				ResultSet rs = pst.executeQuery();
				
				if (rs.next())
				{
					ResultSetMetaData rsmd = rs.getMetaData();
					for (int i = 1; i <= rsmd.getColumnCount(); ++i)
					{
						String key = rsmd.getColumnName(i);
						String val = rs.getString(i);
						result.put (key, val);
					}
				}
			}
			catch (SQLException ex)
			{
				throw new IDMapperException (ex);
			}
			
			return result;
		}
	}


	/** {@inheritDoc} */
	public Set<Xref> mapID (Xref idc, DataSource... resultDs) throws IDMapperException
	{
		final QueryLifeCycle pst = resultDs.length != 1 ? qCrossRefs : qCrossRefsWithCode;
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
					DataSource ds = DataSource.getBySystemCode(rs.getString(2));
					if (resultDs.length == 0 || dsFilter.contains(ds))
					{
						refs.add (new Xref (rs.getString(1), ds));
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

	/** {@inheritDoc} */
	public List<Xref> getCrossRefsByAttribute(String attrName, String attrValue) throws IDMapperException {
//		Logger.log.trace("Fetching cross references by attribute: " + attrName + " = " + attrValue);
		List<Xref> refs = new ArrayList<Xref>();

		final QueryLifeCycle pst = qRefsByAttribute;
		synchronized (pst) { 
			try {
				pst.init();
				pst.setString(1, attrName);
				pst.setString(2, attrValue);
				ResultSet r = pst.executeQuery();
				while(r.next()) {
					Xref ref = new Xref(r.getString(1), DataSource.getBySystemCode(r.getString(2)));
					refs.add(ref);
				}
			} catch(SQLException e) {
				throw new IDMapperException (e);
			}
			finally {pst.cleanup(); }
	//		Logger.log.trace("End fetching cross references by attribute");
			return refs;
		}
	}

	/** {@inheritDoc} */
	public Set<Xref> freeSearch (String text, int limit) throws IDMapperException 
	{		
		Set<Xref> result = new HashSet<Xref>();
		final QueryLifeCycle pst = qFreeSearch;
		synchronized (pst) { 
			try {
				pst.init(limit);
				pst.setString(1, "%" + text.toLowerCase() + "%");
				ResultSet r = pst.executeQuery();
				while(r.next()) {
					String id = r.getString(1);
					DataSource ds = DataSource.getBySystemCode(r.getString(2));
					Xref ref = new Xref (id, ds);
					result.add (ref);
				}			
			} 
			catch (SQLException e) 
			{
				throw new IDMapperException(e);
			}
			finally {pst.cleanup(); }
			return result;
		}
	}

	/**
	 * @return a list of data sources present in this database. 
	   @throws IDMapperException when the database is unavailable
	 */
	private Set<DataSource> getDataSources() throws IDMapperException
	{
		Set<DataSource> result = new HashSet<DataSource>();
		final QueryLifeCycle pst = qDatasources;
		synchronized (pst) { 
			try
	    	{
	    	 	pst.init();
	    	 	ResultSet rs = pst.executeQuery();
	    	 	while (rs.next())
	    	 	{
	    	 		DataSource ds = DataSource.getBySystemCode(rs.getString(1)); 
	    	 		result.add (ds);
	    	 	}
	    	}
	    	catch (SQLException ignore)
	    	{
	    		throw new IDMapperException(ignore);
	    	}
			finally {pst.cleanup(); }
	    	return result;
		}
	}

	private final IDMapperCapabilities caps;

	class SimpleGdbCapabilities extends AbstractIDMapperCapabilities
	{
		/** default constructor.
		 * @throws IDMapperException when database is not available */
		public SimpleGdbCapabilities() throws IDMapperException 
		{
			super (SimpleGdbImplCommon.this.getDataSources(), true, 
					SimpleGdbImplCommon.this.getInfo());
		}
	}

	/**
	 * @return the capabilities of this gene database
	 */
	public IDMapperCapabilities getCapabilities() 
	{
		return caps;
	}

	/**
	 *
	 * @return true
	 */
	public boolean isFreeAttributeSearchSupported()
	{
		return true;
	}

	/**
	 * free text search for matching symbols.
	 * @return references that match the query
	 * @param query The text to search for
	 * @param attrType the attribute to look for, e.g. 'Symbol' or 'Description'.
	 * @param limit The number of results to limit the search to
	 * @throws IDMapperException if the mapping service is (temporarily) unavailable 
	 */
	public Map<Xref, String> freeAttributeSearch (String query, String attrType, int limit) throws IDMapperException
	{
		Map<Xref, String> result = new HashMap<Xref, String>();
		final QueryLifeCycle pst = (MATCH_ID.equals (attrType)) ? 
				qIdSearchWithAttributes : qAttributeSearch;
		synchronized (pst) { 
			try {
				pst.init(limit);
				pst.setString(1, attrType);
				pst.setString(2, "%" + query.toLowerCase() + "%");
				ResultSet r = pst.executeQuery();
	
				while(r.next()) 
				{
					String id = r.getString("id");
					String code = r.getString("code");
					String symbol = r.getString("attrValue");
					result.put(new Xref (id, DataSource.getBySystemCode(code)), symbol);
				}
			} catch (SQLException e) {
				throw new IDMapperException (e);
			}
			finally {pst.cleanup(); }
			return result;
		}
	}

	/** {@inheritDoc} */
	public Set<String> getAttributeSet() throws IDMapperException 
	{
		Set<String> result = new HashSet<String>();
		final QueryLifeCycle pst = qAttributesSet;
		synchronized (pst) { 
	    	try
	    	{
	    	 	pst.init();
	    	 	ResultSet rs = pst.executeQuery();
	    	 	while (rs.next())
	    	 	{
	    	 		result.add (rs.getString(1));
	    	 	}
	    	}
	    	catch (SQLException ignore)
	    	{
	    		throw new IDMapperException(ignore);
	    	}
			finally {pst.cleanup(); }
	    	return result;
		}
	}

}
