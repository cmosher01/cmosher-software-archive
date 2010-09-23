package smcsolution;
import java.io.*;
public class PropertiesFileActions
{
	private static final StringBuilder name = new StringBuilder();
	private static final StringBuilder value = new StringBuilder();
	private static char c;
	public static void main(String... args) throws ParseException, IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			PropertiesFileParser s = new PropertiesFileParser();
			name.setLength(0);
			value.setLength(0);
			for (int i = 0; i < line.length(); ++i)
			{
	            c = (char)line.charAt(i);
	    	    if (c == '=') s.Eq();
	    	    else if (Character.isWhitespace(c)) s.Ws();
	    	    else if (Character.isLetterOrDigit(c)) s.X();
	    	    else s.Oth();
	        }
			System.out.println(name+"="+value);
		}
    }

	protected void append_name()
    {
    	name.append(c);
    }
    protected void append_value()
    {
    	value.append(c);
    }
}
