package org.bridgedb.webservice.uniprot;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.junit.Assert;
import org.junit.BeforeClass;

import junit.framework.TestCase;

public class Test extends TestCase
{
	private IDMapper mapper;

	@BeforeClass
	public void setUp() throws ClassNotFoundException, IDMapperException
	{
		BioDataSource.init();
		Class.forName("org.bridgedb.webservice.uniprot.IDMapperUniprot");
		mapper = BridgeDb.connect("idmapper-uniprot:");
	}
	
	@org.junit.Test
	public void testBasic() throws IDMapperException
	{
//		DataSource acc = DataSource.getByFullName("ACC");
//		DataSource refseq = DataSource.getByFullName("P_REFSEQ_AC");
//		DataSource entrez = DataSource.getByFullName("P_ENTREZGENEID"); 
			
		DataSource acc = BioDataSource.UNIPROT;
		DataSource refseq = BioDataSource.REFSEQ;
		DataSource entrez = BioDataSource.ENTREZ_GENE;
		DataSource uniprot_id = DataSource.getByFullName ("ID");
		
		Xref ref1 = new Xref("P13368", acc);
		Set<Xref> mappings = mapper.mapID (ref1, refseq);
		Assert.assertNotNull(mappings);
		Assert.assertNotSame(0, mappings.size());
		System.out.println("" + mappings);
		for (Xref i : mappings)
		{
			System.out.println (i);
		}

		mappings = mapper.mapID (ref1, uniprot_id);
		Assert.assertNotNull(mappings);
		Assert.assertNotSame(0, mappings.size());
		System.out.println("" + mappings);
		for (Xref i : mapper.mapID (ref1, uniprot_id))
		{
			System.out.println (i);
		}

		mappings = mapper.mapID (ref1, entrez);
		Assert.assertNotNull(mappings);
		Assert.assertNotSame(0, mappings.size());
		System.out.println("" + mappings);
		for (Xref i : mapper.mapID (ref1, entrez))
		{
			System.out.println (i);
		}

		Xref insr = new Xref ("32039", entrez); 
		Set<Xref> query = new HashSet<Xref>();
		query.add (insr);
		query.add (ref1);
		
		Map<Xref, Set<Xref>> result = mapper.mapID (query, refseq);
		Assert.assertNotNull(result);
		Assert.assertNotSame(0, result.size());
		System.out.println("" + result);
		for (Xref src : result.keySet())
		{
			System.out.println(src);
			for (Xref dest : result.get(src))
			{
				System.out.println ("   " + dest);
			}
		}
		
	}
}
