package config;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import util.Util;

/*
 * Created on Feb 8, 2008
 */
public class CmdLineArgs
{
	private final List<String> args;

	private boolean gui = true;
	private String config = "ja2.cfg";

	public CmdLineArgs(final String... args)
	{
		this.args = Collections.<String>unmodifiableList(Arrays.<String>asList(args));
		parse();
	}

	private void parse() throws IllegalArgumentException
	{
		for (final String arg : this.args)
		{
			if (arg.startsWith("--"))
			{
				parseArg(arg.substring(2));
			}
			else
			{
				throw new IllegalArgumentException(arg);
			}
		}
	}

	private void parseArg(final String arg) throws IllegalArgumentException
	{
		final StringTokenizer tok = new StringTokenizer(arg,"=");
		final String opt = Util.nextTok(tok);
		final String val = Util.nextTok(tok);

		if (opt.equals("config"))
		{
			this.config = val;
		}
		else if (opt.equals("gui"))
		{
			this.gui = true;
		}
		else if (opt.equals("cli"))
		{
			this.gui = false;
		}
		else
		{
			throw new IllegalArgumentException(arg);
		}
	}

	public String getConfig()
	{
		return this.config;
	}

	public boolean isGUI()
	{
		return this.gui;
	}
}
