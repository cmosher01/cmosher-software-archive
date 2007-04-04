/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.oldexpr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class Lexer
{
	private final PushbackReader reader;

	public Lexer(final Reader reader)
	{
		this.reader = new PushbackReader(new BufferedReader(reader));
	}

	public Token getNextToken() throws IOException, ParseException
	{
		while (true)
		{
			final int t = this.reader.read();
			if (Character.isWhitespace(t))
			{
				// ignore
			}
			else if (t == '.')
			{
				return new DotToken();
			}
			else if (t == ',')
			{
				return new CommaToken();
			}
			else if (t == '(')
			{
				return new OpenParenToken();
			}
			else if (t == ')')
			{
				return new CloseParenToken();
			}
			else if (Character.isDigit(t) || t == '#' || t == '-' || t == '+')
			{
				final StringBuilder sbNumber = new StringBuilder();
				if (t != '+')
				{
					sbNumber.append((char)t);
				}

				final int t2 = this.reader.read();
				while (Character.isDigit(t2) || t2 == 'x')
				{
					sbNumber.append((char)t2);
				}
				this.reader.unread(t2);

				return new NumberToken(sbNumber.toString());
			}
			else if (Character.isJavaIdentifierStart(t))
			{
				int t2 = t;
				final StringBuilder sbIdent = new StringBuilder();
				while (Character.isJavaIdentifierPart(t2))
				{
					sbIdent.append((char)t2);
					t2 = this.reader.read();
				}
				this.reader.unread(t2);

				return new IdentToken(sbIdent.toString());
			}
			else if (t == -1)
			{
				return new EOFToken();
			}
			else
			{
				throw new ParseException();
			}
		}
	}
}
