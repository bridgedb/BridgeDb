package org.bridgedb.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bridgedb.IDMapperException;
import org.bridgedb.webservice.bridgerest.BridgeRest;

public class BridgeRestParameterModel extends AbstractParameterModel implements BridgeDbParameterModel
{
	static final int PARAMETER_NUM = 3;
	
	private Object[] metadata = new Object[] {"http://webservice.bridgedb.org", "80", Collections.emptyList()};
	private Object[] values = new Object[] {"http://webservice.bridgedb.org", "80", null};
	private String[] labels = new String[] {"Base URL", "Port", "Species"};
	
	/**
	 *  1: base url
	 *  2: port
	 *  3: species: Combo
	 */
	public BridgeRestParameterModel()
	{
		refreshSpeciesList();
	}

	private void refreshSpeciesList()
	{
		try
		{
			metadata[2] = getContents(values[0] + ":" + values[1]);
		}
		catch (IDMapperException e)
		{
			metadata[2] = Collections.emptyList();
		}
	}

	@Override
	public String getConnectionString()
	{
		String url = getString(0);
		String port = getString(1);
		String species = getString(2);
		return "idmapper-bridgerest:" + url + ":" + port + "/" + species; 
	}

	@Override
	public String getName()
	{
		return "BridgeRest webservice";
	}
	
	public String toString() { return getName(); }

	@Override
	public String getHelpHtml()
	{
		return 
			"<html><h1>BridgeRest webservice" +
			"<p>Generic webservice that can map most common gene, protein and metabolite " +
			"identifiers. Mapping service is split per species, so you have to select a species " +
			"in advance, and no cross-species mapping is possible. Mapping data is derived from " +
			"Ensembl and HMDB." +
			"<p><b>maintainer:</b>Gladstone Institute / UCSF and BiGCaT / Maastricht University" +
			"<p><b>more info:</b><a href=\"http://webservice.bridgedb.org\">http://webservice.bridgedb.org</a>";
	}

	@Override
	public Category getCategory()
	{
		return Category.WEBSERVICE;
	}

	@Override
	public String getHint(int i)
	{
		return labels[i];
	}

	@Override
	public String getLabel(int i)
	{
		return labels[i];
	}

	@Override
	public int getNum()
	{
		return PARAMETER_NUM;
	}

	@Override
	public Object getMetaData(int i)
	{
		return metadata[i];
	}

	@Override
	public Object getValue(int i)
	{
		return values[i];
	}

	@Override
	public void setValue(int i, Object val)
	{
		values[i] = val;
		if (i == 0 || i == 1) refreshSpeciesList();
		fireParameterModelEvent(new ParameterModelEvent(ParameterModelEvent.Type.VALUE_CHANGED, i));
	}
	
	private static List<String> getContents(String baseUrl) throws IDMapperException
	{
		//TODO: duplicate code
		try
		{
			URL url = new URL (baseUrl.toString() + "/contents"); 
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setInstanceFollowRedirects(false);
			int response = con.getResponseCode();
			if (response < HttpURLConnection.HTTP_OK || response >= HttpURLConnection.HTTP_MULT_CHOICE ) 
				throw new IOException("HTTP response: " + con.getResponseCode() + " - " + con.getResponseMessage());
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			List<String> result = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null)
			{
				String field[] = line.split("\t");
				result.add(field[1]);
			}
			return result;
		}
		catch (IOException ex)
		{
			throw new IDMapperException ("Couldn't read contents", ex);
		}				
	}

}
