package org.bridgedb.examples;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.bridgedb.webservice.cronos.CronosWS;
import org.bridgedb.webservice.cronos.CronosWSServiceLocator;
import org.json.JSONException;

import synergizer.SynergizerClient;

/*
 * This example shows you would map an identifier with the Cronos and 
 * Synergizer service directly (without using BridgeDb). This is a 
 * Counter - example to illustrate the advantage of the uniform interface
 * that BridgeDb provides. 
 * 
 * The uniform interface of BridgeDb makes it easy 
 * to deal with multiple identifier mapping services. 
 * In this script specialized code has to be written for each webservice.
 * It is less extensible: more code has to be added to support a third 
 * webservice.
 * 
 * Note that you first need to run Apache Axis wsdl2java on the Cronos wsdl
 * (http://mips.helmholtz-muenchen.de/CronosWSService/CronosWS?WSDL) to generate
 * the required classes. You also have to include synergizer-client.jar
 */
public class CounterExampleWithoutBridgedb
{
	public void mapCronos(String id) throws ServiceException, IOException
	{
		// This example shows how to map an identifier
		// using the Cronos service directly
		// Call Web Service Operation
		CronosWSServiceLocator locator = new CronosWSServiceLocator();
		CronosWS port = locator.getCronosWSPort();
		
		String organismCode = "hsa";
		int source = 9; //Entrez Gene
		int target = 5; //Ensembl
		String result = port.cronosWS(id, organismCode, source, target);
		if (!result.equals("")) 
		{
			//The ids in the result are separated with ;
			for (String rid : result.split(";"))
			{
				System.out.println("  " + rid);
			}
		}
	}
	
	public void mapSynergizer(String id) throws IOException, JSONException
	{
		SynergizerClient client = new SynergizerClient(new URL(SynergizerClient.defaultServiceURLString));
		
        Set<String> query = new HashSet<String>();
        query.add (id);
        
        SynergizerClient.TranslateResult res;
        res = client.translate("ensembl", "Homo sapiens", "entrezgene", "ensembl_gene_id", query);
         
        Set<String> output = res.translationMap().get(id);
         
         for (String dest : output)
         {
         	System.out.println ("  " + dest);
         }
	}
	
	public static void main(String args[]) throws ServiceException, IOException, JSONException
	{
		CounterExampleWithoutBridgedb main = new CounterExampleWithoutBridgedb();
		
		String id = "3643";
		main.mapCronos(id);
		main.mapSynergizer(id);
	}
}
