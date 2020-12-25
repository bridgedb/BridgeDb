package buildsystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Measure
{
	private File dest;
	
	public Measure(String fileName)
	{
		dest = new File (new File (System.getProperty("user.home"), "output"), fileName);
	}
	
	public void add(String key, String value, String unit)
	{
		try
		{
			DateFormat fmt = new SimpleDateFormat("dd MMM yy HH:mm z");
			// open file for appending.
			FileWriter fw = new FileWriter (dest, true);
			fw.write(fmt.format (new Date()) + "\t" + 
					key + "\t" + 
					value + "\t" + 
					unit + "\n");
			fw.close();			
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
