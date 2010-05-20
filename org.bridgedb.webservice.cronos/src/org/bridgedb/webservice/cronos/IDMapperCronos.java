package org.bridgedb.webservice.cronos;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.bio.BioDataSource;
import org.bridgedb.impl.InternalUtils;
import org.bridgedb.webservice.IDMapperWebservice;
import org.bridgedb.AbstractIDMapperCapabilities;
import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

public class IDMapperCronos extends IDMapperWebservice
{
	private final CronosWS port;
/**
 * 		organism		| organism3Letter
		------------------------------------------
		Homo sapiens		| hsa
		Mus musculus		| mmu
		Rattus norvegicus	| rno
		Bos taurus		| bta
		Canis familiaris	| cfa
		Drosophila melanogaster	| dme

 */
	private static final String[] allowedSpecies = {
		"hsa", "mmu", "rno", "bta", "cfa", "dme"
	};
	
	private final String speciesCode; // e.g.: "hsa"
	
	
/*
 * 
|-----------------------------------------------|
| Integer value	| Query/Target-Type	        | 
|-----------------------------------------------|
|   1		| Gene Name			|
|   2		| Protein Name			|	
|   3		| RefSeq			|
|   4		| UniProt			|
|   5		| Ensembl/FlyBase Gene ID 	|
|   6		| Ensembl/FlyBase Transcript ID	|
|   7		| Ensembl/FlyBase Protein ID	|
|   8		| GI				|
|   9		| GeneID			|
|  10		| EMBL				|
|  11		| PIR				|
|  12		| DBSNP				|
|  13		| UniSTS			|
|  14		| HGNC				|
|  17		| MfunGD			|
|  18		| MGI				|
-------------------------------------------------

|-------------------------------------------------------------------------------|
| Integer value	| Query/Target-Type	| Integer value	| Query/Target-Type	|
|-------------------------------------------------------------------------------|
| 200		| affy_hc_g110		| 500		| affy_mg_u74a		|
| 210		| affy_hg_u133_plus_2	| 510		| affy_mg_u74av2	|
| 220		| affy_hg_u133a_2	| 520		| affy_mg_u74b		|
| 230		| affy_hg_u133a		| 530		| affy_mg_u74bv2	|
| 240		| affy_hg_u133b		| 540		| affy_mg_u74c		|
| 250		| affy_u133_x3p		| 550		| affy_mg_u74cv2	|
| 260		| affy_hg_u95a		| 560		| affy_moe430a		|
| 270		| affy_hg_u95av2	| 570		| affy_moe430b		|
| 280		| affy_hg_u95b		| 580		| affy_mouse430_2	|
| 290		| affy_hg_u95c		| 590		| affy_mouse430a_2	|
| 300		| affy_hg_u95d		| 600		| affy_mu11ksuba	|
| 310		| affy_hg_u95e		| 610		| agilentprobe		|
| 320		| affy_hg_focus		|---------------------------------------|	
| 330		| affy_hugenefl		|	
| 		| 			|		
| 350		| agilentcgh		|	
| 360		| agilentprobe		|	
----------------------------------------|

 */
	private static Map<Integer, DataSource> intMap = new HashMap<Integer, DataSource>();
	private static Map<DataSource, Integer> invMap = new HashMap<DataSource, Integer>();
	
	static {
		intMap.put (3, BioDataSource.REFSEQ);
		intMap.put (4, BioDataSource.UNIPROT);
		intMap.put (5, BioDataSource.ENSEMBL_HUMAN);
		intMap.put (8, BioDataSource.GENBANK);
		intMap.put (9, BioDataSource.ENTREZ_GENE);
		intMap.put (10, BioDataSource.EMBL);
		intMap.put (14, BioDataSource.HUGO);
		intMap.put (18, BioDataSource.MGI);
		intMap.put (220, BioDataSource.AFFY);

		invMap.put (BioDataSource.REFSEQ, 3);
		invMap.put (BioDataSource.UNIPROT, 4);
		invMap.put (BioDataSource.ENSEMBL_HUMAN, 5);
		invMap.put (BioDataSource.ENSEMBL, 5);
		invMap.put (BioDataSource.GENBANK, 8);
		invMap.put (BioDataSource.ENTREZ_GENE, 9);
		invMap.put (BioDataSource.EMBL, 10);
		invMap.put (BioDataSource.HUGO, 14);
		invMap.put (BioDataSource.MGI, 18);
		invMap.put (BioDataSource.AFFY, 220);
	}

	IDMapperCronos(String speciesCode) throws IDMapperException
	{
		this.speciesCode = speciesCode;
		if (!Arrays.asList(allowedSpecies).contains(speciesCode))
		{
			throw new IDMapperException(speciesCode + " is not allowed as Species Code, must be e.g. 'hsa'");
		}
		
		Map<String, String> info = new HashMap<String, String>();
		info.put("SpeciesCode", speciesCode);
		try 
		{ 	// Call Web Service Operation

			//CronosWSServiceLocator locator = new CronosWSServiceLocator();
			CronosWSService service = new CronosWSService();
			port = service.getCronosWSPort(); //locator.getCronosWSPort();
			//info.put("WSPortAddress", locator.getCronosWSPortAddress());
			//info.put("WSDDServiceName", locator.getCronosWSPortWSDDServiceName());
			//info.put("WSDLDocumentLocation", "" + locator.getWSDLDocumentLocation());
			info.put("WSPortAddress", service.WSDL_LOCATION.toString());
			info.put("WSDDServiceName", service.getServiceName().toString());
			info.put("WSDLDocumentLocation", service.WSDL_LOCATION.toString());
		}
		catch (Exception ex)
		{
			throw new IDMapperException (ex);
		}
		
		caps = new CronosCapabilities(info);
	}
	
    static {
		BridgeDb.register ("idmapper-cronos", new Driver());
	}

	private final static class Driver implements org.bridgedb.Driver 
	{
		public IDMapper connect(String locationString) throws IDMapperException 
		{
			return new IDMapperCronos(locationString);
		}
	}

    private boolean isConnected = true;
    /**
     * {@inheritDoc}
     */
    public void close() throws IDMapperException { isConnected = false; }
    /**
     * {@inheritDoc}
     */
    public boolean isConnected() { return isConnected; }

  
	public Set<Xref> freeSearch(String text, int limit)
			throws IDMapperException {
		throw new UnsupportedOperationException("Free search not supported by CRONOS");
	}

	private final CronosCapabilities caps;
	
	private class CronosCapabilities extends AbstractIDMapperCapabilities 
	{
		public CronosCapabilities(Map<String, String> info) 
		{
			super(getSupportedDataSources(), false, info);
		}
	}
	
	private Set<DataSource> getSupportedDataSources()
	{
		return invMap.keySet();
	}
	
	public IDMapperCapabilities getCapabilities() 
	{
		return caps;
	}

	@Override public Set<Xref> mapID(Xref srcXref,
			DataSource... tgtDataSources) throws IDMapperException 
	{
		Set<Xref> result = new HashSet<Xref>();
		Set<DataSource> tgts = new HashSet<DataSource>();
		Integer srcDs = invMap.get(srcXref.getDataSource());
		if (srcDs == null) return result; // we can't map unknown DataSource
		
		if (tgtDataSources.length > 0)
			for (DataSource ds : tgtDataSources) tgts.add(ds);
		else
			tgts = getSupportedDataSources();
		
		try {
			for (DataSource tgtDs : tgts)
			{
				Integer tgtInt = invMap.get(tgtDs);
				if (tgtInt != null)
				{
					String ids = port.cronosWS(srcXref.getId(), speciesCode, srcDs, tgtInt);
					if (!ids.equals(""))
						for (String id : ids.split(";"))
							result.add (new Xref (id, tgtDs));
				}
			}
		} catch (Exception e) {
			throw new IDMapperException(e);
		}
		return result;
	}
	
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> src, DataSource... tgt) 
		throws IDMapperException
	{
		return InternalUtils.mapMultiFromSingle(this, src, tgt);
	}

	public boolean xrefExists(Xref xref) throws IDMapperException 
	{
		Set<Xref> result = mapID(xref);
		return result.size() > 0;
	}
}
