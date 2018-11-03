import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IoStatParser
{
	static class Stat
	{
		String name;
		int value;
	}

	static final Map<String,List<Stat>> map = new HashMap<String,List<Stat>>();

	static void accumulate(String table, String stats)
	{
		map.get(table);
	}

	public static void main(String[] args) throws IOException
	{
		final Pattern pattern = Pattern.compile("^Table \'\\([^\']*\\)\'\\.\\(.*\\)$");

		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));

		for (String sIn = in.readLine(); sIn != null; sIn = in.readLine())
		{
			final Matcher matcher = pattern.matcher(sIn.trim());
			if (matcher.matches())
			{
				accumulate(matcher.group(1).trim(),matcher.group(2).trim());
			}
		}
	}
}
