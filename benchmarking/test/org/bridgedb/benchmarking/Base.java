package org.bridgedb.benchmarking;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.AttributeMapper;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

import junit.framework.TestCase;

public class Base extends TestCase 
{
	static final Xref NONEXISTENT = new Xref("123", DataSource.getBySystemCode("??"));
	
	/**
	 * Basic check for a mapper.
	 * Test that ...
	 * <ul>
	 * <li> connect method works
	 * <li> capabilities are defined
	 * <li> methods return non-null values when required
	 * <li> mapID works for at least one pair
	 * <li> close() and isConnected() are consistent
	 * </ul>
	 * 
	 * @param connectString a connectionstring for BridgeDb.connect
	 * @param from first Xref of a mapping pair
	 * @param to second Xref a mapping pair
	 */
	protected void basicMapperTest (String connectString, Xref from, Xref to) throws IDMapperException
	{
		IDMapper mapper = BridgeDb.connect (connectString);
		assertNotNull(mapper);
		
		// check that xrefexists doesn't lead to exception
		assertFalse (mapper.xrefExists(NONEXISTENT));
		assertTrue (mapper.xrefExists(from));
		assertTrue (mapper.xrefExists(to));
		
		IDMapperCapabilities caps = mapper.getCapabilities();
		assertNotNull (caps);
		
		// test capabilities
		assertTrue (caps.getSupportedSrcDataSources().size() > 0);
		assertTrue (caps.getSupportedSrcDataSources().contains(
				from.getDataSource()));
		assertTrue (caps.getSupportedTgtDataSources().size() > 0);
		assertTrue (caps.getSupportedTgtDataSources().contains(
				to.getDataSource()));

		assertNotNull (caps.getKeys());
		// each property should be a non-null value.
		// note that there may be zero properties, in which this 
		// test is irrelevant
		for (String key : caps.getKeys())
		{	
			assertNotNull (caps.getProperty(key));
		}
		
		assertTrue (caps.isMappingSupported(from.getDataSource(), to.getDataSource()));
		// should return false for non-sensical mapping
		assertFalse (caps.isMappingSupported(
				DataSource.getBySystemCode("?!"),
				DataSource.getBySystemCode("!?")));

		if (caps.isFreeSearchSupported())
		{
			assertNotNull (mapper.freeSearch("p53", 100));
			// search for id of existent should return existent
			assertTrue (mapper.freeSearch(from.getId(), 100).contains(from));
		}
		else
		{
			// if free search is not supported then UnsupportedOperatinException should
			// be thrown.
			try
			{
				mapper.freeSearch("p53", 100);
				fail ("Expected UnsupportedOperationException");
			}
			catch (UnsupportedOperationException ex) { /* good. */ }
		}
		
		// test mapping single id
		Set<Xref> result2 = mapper.mapID(from);
		assertTrue (result2.contains(to));
		assertNotNull (mapper.mapID(NONEXISTENT));
		assertTrue (mapper.mapID(from, to.getDataSource()).contains(to));
				
		try {
			mapper.mapID((Xref)null);
		} catch (NullPointerException e)
		{ /* OK, expected NPE here */ }
		
		// test mapping id in set
		Set<Xref> fromSet = new HashSet<Xref>();
		fromSet.add(from);
		Map<Xref, Set<Xref>> result = mapper.mapID(fromSet, to.getDataSource());
		assertNotNull (result);
		assertTrue (result.containsKey(from));
		assertTrue (result.get(from).contains(to));

		try {
			mapper.mapID((Set<Xref>)null);
		} catch (NullPointerException e)
		{ /* OK, expected NPE here */ }

		// test attributes
		if (mapper instanceof AttributeMapper)
		{
			AttributeMapper attributes = (AttributeMapper)mapper;
			
			assertNotNull (attributes.getAttributeSet());
			assertNotNull (attributes.getAttributes(NONEXISTENT));
			assertNotNull (attributes.getAttributes(NONEXISTENT, "Symbol"));
			
			if (caps.isFreeSearchSupported())
			{
				assertNotNull (attributes.freeAttributeSearch("p53", "Symbol", 100));
			}
			else
			{
				try
				{
					attributes.freeAttributeSearch("p53", "Symbol", 100);
					fail ("Expected UnsupportedOperationException");
				}
				catch (UnsupportedOperationException ex) { /* good. */ }
			}
		}

		// test close method
		assertTrue (mapper.isConnected());
		mapper.close();
		assertFalse (mapper.isConnected());
	}
}
