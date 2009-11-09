package org.bridgedb.benchmarking;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.Xref;

public class Utils 
{

	public static Set<Xref> translateDs(Set<Xref> in, DataSource base)
	{
		Set<Xref> result = new HashSet<Xref>();
		for (Xref ref : in)
		{
			result.add (translateDs(ref, base));
		}
		return result;
	}

	public static Xref translateDs (Xref in, DataSource base)
	{
		return new Xref (in.getId(), base);		
	}

	public static boolean safeEquals (Object a, Object b)
	{
		return a == null ? b == null : a.equals(b);
	}

	public static void printRefSet(PrintWriter writer, Set<Xref> dests)
	{
		boolean first = true;
		if (dests != null) for (Xref dest : dests)
		{
			if (!first) writer.print (" /// ");
			writer.print (dest);
			first = false;
		}
	}

}
