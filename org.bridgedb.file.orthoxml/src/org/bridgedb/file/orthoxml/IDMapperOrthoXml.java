package org.bridgedb.file.orthoxml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.modelmbean.XMLParseException;
import javax.xml.stream.XMLStreamException;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.bio.BioDataSource;
import org.bridgedb.impl.InternalUtils;

import sbc.orthoxml.Database;
import sbc.orthoxml.Gene;
import sbc.orthoxml.Group;
import sbc.orthoxml.ScoreDefinition;
import sbc.orthoxml.Species;
import sbc.orthoxml.io.OrthoXMLReader;

public class IDMapperOrthoXml implements IDMapper
{
	static
	{
		BridgeDb.register ("idmapper-orthoxml", new Driver());
	}
	
	/** Knows how to instantiate IDMapperText. */
	private static final class Driver implements org.bridgedb.Driver
	{
		@Override
		public IDMapper connect(String locationString) throws IDMapperException
		{
			Reader reader;
			try
			{
				reader = getReader (new URL(locationString));
			}
			catch (MalformedURLException e)
			{
				throw new IDMapperException(e);
			}
			return new IDMapperOrthoXml(reader);
		}
	}
	
	private Map<Xref, Set<Xref>> allIds = new HashMap <Xref, Set<Xref>>();
	private Set<DataSource> dataSources = new HashSet<DataSource>();
	
    private static Reader getReader(URL url) throws IDMapperException {
        try {
            InputStream inputStream = InternalUtils.getInputStream(url);
            return new InputStreamReader(inputStream);
        } catch(IOException e) {
            throw new IDMapperException(e);
        }
    }

    private static Map<String, DataSource> aliases;
    
    static
    {
    	aliases = new HashMap<String, DataSource>();
    	aliases.put ("FlyBase", BioDataSource.FLYBASE);
    	aliases.put ("Ensembl", BioDataSource.ENSEMBL_HUMAN); //TODO: change to regular Ensembl
    }
    
	private void readMappings(Reader in) throws FileNotFoundException, XMLStreamException, XMLParseException
	{
		//open the orthoXML for reading
		OrthoXMLReader reader = new OrthoXMLReader(in);
		
		// get species from reader
		for (Species species : reader.getSpecies())
		{
			System.out.printf(species.getName() + "\t");
			System.out.printf(species.getNcbiTaxId() + "\t");
		}
		System.out.printf("#");
		//read the group iteratively
		Group group;
		
		while ((group = reader.next()) != null)
		{		
			Set<Xref> groupRefs = new HashSet<Xref>();
			
			for(Gene gene : group.getNestedGenes())
			{
				Database database = gene.getDatabase();
				
				DataSource ds = aliases.get(database.getName());
				if (ds == null) ds = DataSource.getByFullName(database.getName());
				
				dataSources.add(ds);
				groupRefs.add (new Xref(gene.getGeneIdentifier(), ds));
				groupRefs.add (new Xref(gene.getProteinIdentifier(), ds));		
			}
			
			for (Xref ref : groupRefs)
			{
				allIds.put (ref, groupRefs);
			}			
		}
	}
	
	public IDMapperOrthoXml(Reader reader) throws IDMapperException
	{
		try
		{
			readMappings(reader);
		}
		catch (IOException e)
		{
			throw new IDMapperException(e);
		}
		catch (XMLStreamException e)
		{
			throw new IDMapperException(e);
		}
		catch (XMLParseException e)
		{
			throw new IDMapperException(e);
		}
	}
	
	@Override
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources)
			throws IDMapperException
	{
		Map<Xref, Set<Xref>> result = 
				InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
		return result;
	}

	@Override
	public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException
	{
		
		Set<Xref> result = new HashSet<Xref>();
		if (tgtDataSources.length == 0)
		{
			Set<Xref> toAdd = allIds.get(ref);
			if (toAdd != null) result.addAll (toAdd);
		}
		else
		{
			Set<DataSource> filter = new HashSet<DataSource>();
			for (DataSource ds : tgtDataSources) filter.add(ds);
			for (Xref dest : allIds.get(ref))
			{
				if (filter.contains(dest.getDataSource()))
					result.add(dest);
			}
		}
		return result;
	}

	@Override
	public boolean xrefExists(Xref xref) throws IDMapperException
	{
		return allIds.containsKey(xref);
	}

	@Override
	public Set<Xref> freeSearch(String text, int limit) throws IDMapperException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public IDMapperCapabilities getCapabilities()
	{
		return new Capabilities();
	}

	private class Capabilities implements IDMapperCapabilities
	{
		@Override
		public boolean isFreeSearchSupported()
		{
			return false;
		}

		@Override
		public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException
		{
			return dataSources;
		}

		@Override
		public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException
		{
			return dataSources;
		}

		@Override
		public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException
		{
			return (dataSources.contains(src) && dataSources.contains(tgt));
		}

		@Override
		public String getProperty(String key)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<String> getKeys()
		{
			// TODO Auto-generated method stub
			return Collections.emptySet();
		}
	}

	private boolean connected = true;
	
	@Override
	public void close() throws IDMapperException
	{
		connected = false;
	}

	@Override
	public boolean isConnected()
	{
		return connected;
	}

}
