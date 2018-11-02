import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

/*
 * Created on Sep 23, 2005
 */



/**
 * Adapted from Dragon Book (Compilers: Principles, Techniques, and Tools) section 2.5.
 * Figs. 2.22 and 2.24.
 *
 * <pre>
 *
 * start: expr eol eof
 * expr: term rest
 * rest: + term { print('+') } rest
 *     | - term { print('-') } rest
 *     | e
 * term: digit { print(digit) }
 *
 * </pre>
 *
 * @author Chris Mosher
 */
public class InfixToPostfix
{
	public static void main(final String[] args) throws IOException, ParseException
	{
		final InfixToPostfix parser = new InfixToPostfix(
			new InputStreamReader(new FileInputStream(FileDescriptor.in)),
			new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));

		parser.parse();
	}



	private final Reader in;
	private final Writer out;
	private int lookahead;
	private int pos;



	private InfixToPostfix(final Reader reader, final Writer writer)
	{
		this.in = reader;
		this.out = writer;
	}



	private void parse() throws IOException, ParseException
	{
		try
		{
			shift();
			start();
		}
		finally
		{
			this.out.write("\n");
			this.out.flush();
		}
	}



	private void start() throws IOException, ParseException
	{
		expr();
		eol();
		eof();
	}

	private void expr() throws IOException, ParseException
	{
		term();
		rest();
	}

	private void rest() throws IOException, ParseException
	{
		final int c = this.lookahead;
		if (c == '+' || c == '-')
		{
			match(c);
			term();
			emit(c);
			rest();
		}
		else
		{
			empty();
		}
	}

	private void term() throws IOException, ParseException
	{
		final int c = this.lookahead;
		if (Character.isDigit(c))
		{
			match(c);
			emit(c);
		}
		else
		{
			error("a digit",charToString(c));
		}
	}

	private void eol() throws IOException, ParseException
	{
//		match('\r');
		match('\n');
	}

	private void eof() throws IOException, ParseException
	{
		match(-1);
	}



	private void empty()
	{
		// match nothing; no error
	}

	private void error(final String expected, final String actual) throws ParseException
	{
		System.err.println();
		throw new ParseException(expected,actual,this.pos);
	}



	private void match(final int expected) throws IOException, ParseException
	{
		if (this.lookahead != expected)
		{
			error(charToString(expected),charToString(this.lookahead));
		}

		shift();
	}

	private void shift() throws IOException
	{
		this.lookahead = this.in.read();
		++this.pos;
	}

	private void emit(final int c) throws IOException
	{
		this.out.write(c);
	}



	private static String charToString(final int c)
	{
		if (c == -1)
		{
			return "EOF";
		}
		if (c == '\r')
		{
			return "CR";
		}
		if (c == '\n')
		{
			return "LF";
		}
		return new String(new char[] {(char)c});
	}
}
