import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import chipset.InvalidMemoryLoad;
import chipset.Memory;
import disk.DiskBytes;
import disk.InvalidDiskImage;

/*
 * Created on Dec 1, 2007
 */
class Config
{
	private static final Pattern patIMPORT = Pattern.compile("import\\s+(.+?)\\s+(.+?)");
	private static final Pattern patLOAD = Pattern.compile("load\\s+(.+?)\\s+(.+?)");

	private String filename;

	public Config(final String filename)
	{
		this.filename = filename;
	}

	public void parseConfig(final Memory memory, final DiskBytes disk1, final DiskBytes disk2) throws IOException, InvalidMemoryLoad, InvalidDiskImage
	{
    	final BufferedReader cfg = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.filename))));

    	for (String s = cfg.readLine(); s != null; s = cfg.readLine())
    	{
    		int comment = s.indexOf('#');
    		if (comment >= 0)
    		{
    			s = s.substring(0,comment);
    		}
    		s = s.trim();
    		if (s.isEmpty())
    		{
    			continue;
    		}

    		Matcher matcher;
    		if ((matcher = patIMPORT.matcher(s)).matches())
    		{
    			final int addr = Integer.decode(matcher.group(1));
    			final String mem = matcher.group(2);

    			final InputStream image = new FileInputStream(new File(mem));
    	        memory.load(addr-0xD000,image);
    	        image.close();
    		}
    		else if ((matcher = patLOAD.matcher(s)).matches())
    		{
    			final int drive = Integer.decode(matcher.group(1));

    			final String nib = matcher.group(2);
    			final File fnib = new File(nib);

    			if (drive == 1)
    			{
    				disk1.load(fnib);
    			}
    			else if (drive == 2)
    			{
    				disk2.load(fnib);
    			}
    			else
    			{
        			throw new IllegalArgumentException("Error in config file: "+s);
    			}
    		}
    		else
    		{
    			throw new IllegalArgumentException("Error in config file: "+s);
    		}
    	}

    	cfg.close();
	}
}
