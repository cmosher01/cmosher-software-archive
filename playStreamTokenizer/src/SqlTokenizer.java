import java.io.BufferedReader;
import java.io.IOException;
import java.io.StreamTokenizer;


/**
 * Parses a SQL source file; WARNING: does not handle "--" EOL comments.
 * It really doesn't do a very good job; I'm just using it to find the
 * CREATE PROCDURE XXX statement at the beginning of a file.
 * 
 * @author Chris Mosher
 */
public class SqlTokenizer
{
	private final StreamTokenizer tok;

	public SqlTokenizer(final BufferedReader sql)
	{
		this.tok = new StreamTokenizer(sql);
		this.tok.wordChars('.', '.');
		this.tok.wordChars('_', '_');
		this.tok.wordChars('@', '@');
		this.tok.ordinaryChars('\"','\"');
	}

	public String nextToken() throws IOException
	{
		this.tok.nextToken();
		if (this.tok.ttype == StreamTokenizer.TT_EOF)
		{
			return "";
		}
		String s;
		switch (tok.ttype)
		{
			case StreamTokenizer.TT_WORD:
			s = tok.sval;
			break;
			case StreamTokenizer.TT_NUMBER:
			s = Double.toString(tok.nval);
			break;
			case '\'':
			s = "\'"+tok.sval+"\'";
			break;
			default:
			s = new String(new char[]
			{ (char) tok.ttype });
			break;
		}
		return s;
	}
}
