package nu.mine.mosher.unicode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Handles the Unicode list of characters excluded from composition, the
 * http://www.unicode.org/Public/UNIDATA/CompositionExclusions.txt file.
 * @author Chris Mosher
 */
public class CompositionExclusions
{
	private int[] rExclusions;
	private final File file;

    public CompositionExclusions(File file) throws IOException
    {
    	this.file = file;
    }

    public void readFromFile() throws IOException
	{
		boolean ok = false;
		BufferedReader bufreader = null;
		try
		{
			bufreader = new BufferedReader(new InputStreamReader(new FileInputStream(file)),8*1024);
            tryReadFrom(bufreader);
            ok = true;
		}
		finally
		{
			if (bufreader != null)
			{
				try
				{
					bufreader.close();
				}
				catch (Throwable ignore)
				{
				}
			}
			if (!ok)
				rExclusions = new int[0];
		}
	}

	protected void tryReadFrom(BufferedReader bufreader) throws IOException
    {
        List list = new ArrayList(100);
        String line = bufreader.readLine();
        while (line != null)
        {
        	int comment = line.indexOf('#');
        	if (comment != -1)
        	    line = line.substring(0,comment);
        
        	if (line.length() > 0)
        	{
        		int value = Integer.parseInt(line.trim(),16);
        		list.add(new Integer(value));
        	}
        
        	line = bufreader.readLine();
        }
        Collections.sort(list);
        rExclusions = new int[list.size()];
        int i = 0;
        for (Iterator ilist = list.iterator(); ilist.hasNext();)
        {
            Integer integer = (Integer)ilist.next();
        	rExclusions[i++] = integer.intValue();
        }
    }

	public boolean is(int c)
	{
		return Arrays.binarySearch(rExclusions,c) >= 0;
	}
}
