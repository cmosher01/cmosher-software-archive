import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author chrism
 */
public class WriteStringToFile
{
	/**
	 * Constructor for WriteStringToFile.
	 */
	public WriteStringToFile()
	{
		super();
	}

	public static void writeStringToFile(StringBuffer sb, File f) throws IOException
	{
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(f.getPath());
			fw.write(sb.toString());
		}
		finally
		{
			if (fw != null)
			{
				try
				{
					fw.close();
				}
				catch (Exception e)
				{
				}
			}
		}
	}

}
