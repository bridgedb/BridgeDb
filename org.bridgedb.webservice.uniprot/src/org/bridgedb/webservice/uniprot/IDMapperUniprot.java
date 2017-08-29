package org.bridgedb.webservice.uniprot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.impl.InternalUtils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Code based on documentation of Uniprot webservice found here: 
 * http://www.uniprot.org/faq/28#id_mapping_examples
 */
public class IDMapperUniprot implements IDMapper
{
	private static final String DEFAULT_BASE_URL = "http://www.uniprot.org";
	private final String baseURL;
	
	static 
	{
		BridgeDb.register ("idmapper-uniprot", new Driver());
	}

	private static class Driver implements org.bridgedb.Driver {
		private Driver() { } // prevent outside instantiation

		public IDMapper connect(String location) throws IDMapperException 
		{
			String baseURL = DEFAULT_BASE_URL;

			Map<String, String> info = 
				InternalUtils.parseLocation(location);
			
			if (info.containsKey("BASE"))
			{
				baseURL = info.get("BASE");
			}
			return new IDMapperUniprot(baseURL);
		}
	}
	
	
	private IDMapperUniprot(String baseURL)
	{
		this.baseURL = baseURL;
	}


	private boolean closed = false;
	
	@Override
	public void close() throws IDMapperException
	{
		closed = true;
	}


	@Override
	public Set<Xref> freeSearch(String text, int limit) throws IDMapperException
	{
		if (closed) throw new IDMapperException ("Already closed");
		// TODO Auto-generated method stub
		return null;
	}

	private class UniprotCapabilities extends AbstractIDMapperCapabilities
	{
			/** default constructor.
			 * @throws IDMapperException when database is not available */
			public UniprotCapabilities()
			{
					super (null, false, null);
			}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException {
			return IDMapperUniprot.this.aliasses.values();
		}

		/** {@inheritDoc} */
		public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException {
			return IDMapperUniprot.this.aliasses.values();
		}

	}

	@Override
	public IDMapperCapabilities getCapabilities()
	{
		return new UniprotCapabilities();
	}


	@Override
	public boolean isConnected()
	{
		return !closed;
	}

	public String[] runMappingQuery (String from, String to, String query) throws IDMapperException
	{
		String response = runQuery("mapping", new NameValuePair[] {
					   new NameValuePair("from", from),
					   new NameValuePair("to", to),
					   new NameValuePair("format", "tab"),
					   new NameValuePair("query", query),
		  		});
		return response.split("\n");
	}
	
	@Override
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources)
			throws IDMapperException
	{
		if (closed) throw new IDMapperException ("Already closed");
		
		Map <Xref, Set<Xref>> result = new HashMap<Xref, Set<Xref>>();
		
		Map <DataSource, Set<Xref>> groupedByDataSource = InternalUtils.groupByDataSource(srcXrefs);
		
		for (DataSource srcDs : groupedByDataSource.keySet())
		{
			String srcFullName = aliasses.inverse().get(srcDs);
			if (!FROM_DATABASES.contains(srcFullName)) throw new IDMapperException ("DataSource " + srcFullName + " not supported");
			
			if (tgtDataSources.length == 0) throw new IDMapperException("Must specify at least one data source");
			for (DataSource ds : tgtDataSources)
			{
				String destFullName = aliasses.inverse().get(ds);
				if (!TO_DATABASES.contains(destFullName)) throw new IDMapperException ("DataSource " + destFullName + " not supported");
				
				String query = InternalUtils.joinIds (groupedByDataSource.get(srcDs), " ");
				
				String[] lines = runMappingQuery(srcFullName, destFullName, query);
				// skip header row				
				for (int i = 1; i < lines.length; ++i)
				{
					String[] fields = lines[i].split("\t");
					Xref src = new Xref(fields[0], srcDs);
					Xref dest = new Xref(fields[1], ds);
					InternalUtils.multiMapPut(result, src, dest);
				}
			}
		}
		return result;
	}


	@Override
	public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException
	{
		if (closed) throw new IDMapperException ("Already closed");
		
		Set<Xref> result = new HashSet<Xref>();
		String from = aliasses.inverse().get(ref.getDataSource());
		
		if (!FROM_DATABASES.contains(from)) throw new IDMapperException ("DataSource " + ref.getDataSource() + " not supported");
		
		if (tgtDataSources.length == 0) throw new IDMapperException("Must specify at least one data source");
		
		for (DataSource ds : tgtDataSources)
		{
			String to = aliasses.inverse().get(ds);
			if (!TO_DATABASES.contains(to)) throw new IDMapperException ("DataSource " + ds + " not supported");
			
			String[] lines = runMappingQuery(from, to, ref.getId());
			
			// skip header row
			for (int i = 1; i < lines.length; ++i)
			{
				String[] fields = lines[i].split("\t");
				result.add (new Xref(fields[1], ds));
			}
		}
		
		return result;
	}

	static final Set<String> FROM_DATABASES;
	static final Set<String> TO_DATABASES;

	private static BiMap<String, DataSource> aliasses = HashBiMap.create();

	private static void alias (String name)
	{
		aliasses.put(name, DataSource.getByFullName(name));
	}

	private static void alias (String name, DataSource ds)
	{
		aliasses.put(name, ds);
	}
	
	static
	{
		alias("ACC", BioDataSource.UNIPROT); // according to doc, ACC and ID are only allowed in "to" queries, 
		alias("ID"); // but in practice, it means that they must occur in either from or to.
		alias("UPARC");
		alias("NF50");
		alias("NF90");
		alias("NF100");
		alias("EMBL_ID");
		alias("EMBL");
		alias("PIR");
		alias("UNIGENE_ID", BioDataSource.UNIGENE);
		alias("P_ENTREZGENEID", BioDataSource.ENTREZ_GENE);
		alias("P_GI");
		alias("P_IPI");
		alias("P_REFSEQ_AC", BioDataSource.REFSEQ);
		alias("PDB_ID");
		alias("DISPROT_ID");
		alias("HSSP_ID");
		alias("DIP_ID");
		alias("MINT_ID");
		alias("MEROPS_ID");
		alias("PEROXIBASE_ID");
		alias("PPTASEDB_ID");
		alias("REBASE_ID");
		alias("TCDB_ID");
		alias("AARHUS_GHENT_2DPAGE_ID");
		alias("ECO2DBASE_ID");
		alias("WORLD_2DPAGE_ID");
		alias("ENSEMBL_ID", BioDataSource.ENSEMBL);
		alias("ENSEMBL_PRO_ID");
		alias("ENSEMBL_TRS_ID");
		alias("GENOMEREVIEWS_ID");
		alias("KEGG_ID", BioDataSource.KEGG_GENES);
		alias("TIGR_ID", BioDataSource.TIGR);
		alias("UCSC_ID");
		alias("VECTORBASE_ID");
		alias("AGD_ID");
		alias("ARACHNOSERVER_ID");
		alias("CGD");
		alias("CONOSERVER_ID");
		alias("CYGD_ID");
		alias("DICTYBASE_ID");
		alias("ECHOBASE_ID");
		alias("ECOGENE_ID");
		alias("EUHCVDB_ID");
		alias("EUPATHDB_ID");
		alias("FLYBASE_ID", BioDataSource.FLYBASE);
		alias("GENECARDS_ID");
		alias("GENEDB_SPOMBE_ID");
		alias("GENEFARM_ID");
		alias("GENOLIST_ID");
		alias("H_INVDB_ID");
		alias("HGNC_ID");
		alias("HPA_ID");
		alias("LEGIOLIST_ID");
		alias("LEPROMA_ID");
		alias("MAIZEGDB_ID");
		alias("MIM_ID");
		alias("MGI_ID");
		alias("NMPDR");
		alias("ORPHANET_ID");
		alias("PHARMGKB_ID");
		alias("PSEUDOCAP_ID");
		alias("RGD_ID", BioDataSource.RGD);
		alias("SGD_ID", BioDataSource.SGD);
		alias("TAIR_ID", BioDataSource.TAIR);
		alias("TUBERCULIST_ID");
		alias("WORMBASE_ID", BioDataSource.WORMBASE);
		alias("WORMPEP_ID");
		alias("XENBASE_ID");
		alias("ZFIN_ID", BioDataSource.ZFIN);
		alias("EGGNOG_ID");
		alias("HOGENOM_ID");
		alias("HOVERGEN_ID");
		alias("OMA_ID");
		alias("ORTHODB_ID");
		alias("PROTCLUSTDB_ID");
		alias("BIOCYC_ID");
		alias("REACTOME_ID");
		alias("CLEANEX_ID");
		alias("GERMONLINE_ID");
		alias("DRUGBANK_ID");
		alias("NEXTBIO_ID");
		
		FROM_DATABASES = new HashSet<String>(aliasses.keySet());
		TO_DATABASES = new HashSet<String>(aliasses.keySet());
	}

	/**
		@param tool is "mapping".
	 * @throws IDMapperException 
	*/
	public String runQuery(String tool, NameValuePair[] params) throws IDMapperException
	{
		HttpClient client = new HttpClient();
		String location = baseURL + '/' + tool + '/';
		HttpMethod method = new PostMethod(location);
		((PostMethod) method).addParameters(params);		
		method.setFollowRedirects(false);

		try
		{
			int status = client.executeMethod(method);
	
			if (status == HttpStatus.SC_MOVED_TEMPORARILY)
			{
				location = method.getResponseHeader("Location").getValue();
				method.releaseConnection();
				method = new GetMethod(location);
				status = client.executeMethod(method);
			}	

			while (true)
			{
				int wait = 0;
				Header header = method.getResponseHeader("Retry-After");
				if (header != null)
					wait = Integer.valueOf(header.getValue());
				if (wait == 0)
					break;
				Thread.sleep(wait * 1000);
				method.releaseConnection();
				method = new GetMethod(location);
				status = client.executeMethod(method);
			}
	
			if (status == HttpStatus.SC_OK)
				return method.getResponseBodyAsString();
			else
				throw new IDMapperException("Failed, got " + method.getStatusLine() + " for " + method.getURI());
		}
		catch (HttpException e)
		{
			throw new IDMapperException("Could not read from http connection", e);
		}
		catch (IOException e)
		{
			throw new IDMapperException("IO Error", e);
		}
		catch (InterruptedException e)
		{
			throw new IDMapperException("Interrupted while waiting for response", e);
		}
		finally
		{
			method.releaseConnection();
		}
	}
	
	
	@Override
	public boolean xrefExists(Xref xref) throws IDMapperException
	{
		if (closed) throw new IDMapperException ("Already closed");
		// TODO Auto-generated method stub
		return false;
	}

}
