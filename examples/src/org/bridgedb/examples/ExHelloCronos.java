package org.bridgedb.examples;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.bridgedb.webservice.cronos.CronosWS;
import org.bridgedb.webservice.cronos.CronosWSServiceLocator;

/**
 * This example shows you would map an identifier with the Cronos service
 * directly (without using BridgeDb).
 * 
 * Note that you first need to run Apache Axis wsdl2java on the Cronos wsdl
 * (http://mips.helmholtz-muenchen.de/CronosWSService/CronosWS?WSDL) to generate
 * the required classes.
 */
public class ExHelloCronos
{
	public static void main(String args[]) throws ServiceException, RemoteException
	{
		// This example shows how to map an identifier
		// using the Cronos service directly
		// Call Web Service Operation
		CronosWSServiceLocator locator = new CronosWSServiceLocator();
		CronosWS port = locator.getCronosWSPort();
		
		String organismCode = "hsa";
		String id = "3643";
		int source = 9; //Entrez Gene
		int target = 5; //Ensembl
		String result = port.cronosWS(id, organismCode, source, target);
		if (!result.equals("")) 
		{
			//The ids in the result are separated with ;
			for (String rid : result.split(";"))
			{
				System.out.println(rid);
			}
		}
	}
}
