package org.bridgedb.rdb;

import java.io.File;

import org.bridgedb.Base;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

public class TestPgdb extends Base
{	
	static final String GDB_HUMAN = 
		System.getProperty ("user.home") + File.separator + 
		"PathVisio-Data/gene databases/Hs_Derby_20081119.pgdb";

	public void testBase() throws IDMapperException, ClassNotFoundException
	{
		Xref insr1 = new Xref ("ENSG00000171105", DataSource.getBySystemCode("En"));
		Xref insr2 = new Xref ("3643", DataSource.getBySystemCode("L"));
		Class.forName ("org.bridgedb.rdb.IDMapperRdb");
		
		assertTrue (new File(GDB_HUMAN).exists());
		basicMapperTest ("idmapper-pgdb:" + GDB_HUMAN, insr1, insr2);
	}	

}
