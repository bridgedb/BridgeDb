package org.bridgedb.bio;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.impl.InternalUtils;

public class EnsemblCompatibilityMapper implements IDMapper
{
	@Override
	public Map<Xref, Set<Xref>> mapID(Collection<Xref> srcXrefs, DataSource... tgtDataSources)
			throws IDMapperException
	{
		return InternalUtils.mapMultiFromSingle(this, srcXrefs, tgtDataSources);
	}

	@Override
	public Set<Xref> mapID(Xref ref, DataSource... tgtDataSources) throws IDMapperException
	{
		Set<Xref> result = new HashSet<Xref>();
		
		if (!allDataSources.contains(ref.getDataSource())) return result;
		
		Set<DataSource> dest = new HashSet<DataSource>();
		
		// either from old to new or new to old, but not both at the same time...
		if (ref.getDataSource() == BioDataSource.ENSEMBL)
			dest.addAll (oldDataSources);
		else
			dest.add(BioDataSource.ENSEMBL);	
		
		// keep only tgtDataSources that were asked for, if defined
		if (tgtDataSources != null && tgtDataSources.length > 0)
			dest.retainAll(Arrays.asList(tgtDataSources));
		
		for (DataSource ds : dest)
		{
			if (ds == ref.getDataSource()) continue;
			result.add(new Xref(ref.getId(), ds));
		}
		
		return result;
	}

	@Override
	public boolean xrefExists(Xref xref) throws IDMapperException
	{
		return true;
	}

	@Override
	public Set<Xref> freeSearch(String text, int limit) throws IDMapperException
	{
		return Collections.emptySet();
	}

	private final Set<DataSource> oldDataSources;
	private final Set<DataSource> allDataSources;
	
	public EnsemblCompatibilityMapper()
	{
		oldDataSources = new HashSet<DataSource>();
		oldDataSources.add(BioDataSource.ENSEMBL_BSUBTILIS);
		oldDataSources.add(BioDataSource.ENSEMBL_CELEGANS);
		oldDataSources.add(BioDataSource.ENSEMBL_CHICKEN);
		oldDataSources.add(BioDataSource.ENSEMBL_CHIMP);
		oldDataSources.add(BioDataSource.ENSEMBL_COW);
		oldDataSources.add(BioDataSource.ENSEMBL_DOG);
		oldDataSources.add(BioDataSource.ENSEMBL_ECOLI);
		oldDataSources.add(BioDataSource.ENSEMBL_FRUITFLY);
		oldDataSources.add(BioDataSource.ENSEMBL_HORSE);
		oldDataSources.add(BioDataSource.ENSEMBL_HUMAN);
		oldDataSources.add(BioDataSource.ENSEMBL_MOSQUITO);
		oldDataSources.add(BioDataSource.ENSEMBL_MOUSE);
		oldDataSources.add(BioDataSource.ENSEMBL_MTUBERCULOSIS);
		oldDataSources.add(BioDataSource.ENSEMBL_RAT);
		oldDataSources.add(BioDataSource.ENSEMBL_SCEREVISIAE);
		oldDataSources.add(BioDataSource.ENSEMBL_XENOPUS);
		oldDataSources.add(BioDataSource.ENSEMBL_ZEBRAFISH);
		
		allDataSources = new HashSet<DataSource>(oldDataSources);
		allDataSources.add(BioDataSource.ENSEMBL);
	}
	
	@Override
	public IDMapperCapabilities getCapabilities()
	{
		
		return new IDMapperCapabilities()
		{
			@Override
			public boolean isMappingSupported(DataSource src, DataSource tgt) throws IDMapperException
			{
				return (oldDataSources.contains(src) && tgt == BioDataSource.ENSEMBL)
						||
					(oldDataSources.contains(tgt) && src == BioDataSource.ENSEMBL);
			}
			
			@Override
			public boolean isFreeSearchSupported()
			{
				return false;
			}
			
			@Override
			public Set<DataSource> getSupportedTgtDataSources() throws IDMapperException
			{
				return allDataSources;
			}
			
			@Override
			public Set<DataSource> getSupportedSrcDataSources() throws IDMapperException
			{
				return allDataSources;
			}
			
			@Override
			public String getProperty(String key)
			{
				return null;
			}
			
			@Override
			public Set<String> getKeys()
			{
				return Collections.emptySet();
			}
		};
	}

	private boolean closed = false;
	
	@Override
	public void close() throws IDMapperException
	{
		closed = true;
	}

	@Override
	public boolean isConnected()
	{
		return !closed;
	}

	@Override
	public String toString()
	{
		return "old-ensembl-systemcode-compatbility-mapper";
	}
}
