
import org.bridgedb.impl.InternalUtils;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * TODO: incomplete
 * The idea of this script is to download a fresh version of metabocards before running Hmdb2Gdb on it
 */

class DownloadMetabocards
{
	public void run() throws MalformedURLException, IOException
	{
		URL url = new URL("http://www.hmdb.ca/public/downloads/current/metabocards.zip");
		InputStream is = InternalUtils.getInputStream(url);
		
	}

	public static void main (String[] args) throws MalformedURLException, IOException
	{
		new DownloadMetabocards().run();	
	}

}
