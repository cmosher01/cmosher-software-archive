package problem;

import java.text.ParseException;

public class PropertiesFileParser
{
	private final StringBuilder name = new StringBuilder();
	private final StringBuilder value = new StringBuilder();
	private static enum STATE { WANT_NAME, NAME, WANT_EQUALS, WANT_VALUE, VALUE, END };
	private STATE state = STATE.WANT_NAME;
	// ...
	void transition(char c) throws ParseException
	{
		switch (state)
		{
			case WANT_NAME:
				if (Character.isLetterOrDigit(c))
				{
					name.append(c);
					state = STATE.NAME;
				}
				else
				{
					throw new ParseException("syntax error", 0);
				}
			break;
			case NAME:
				if (Character.isLetterOrDigit(c))
				{
					name.append(c);
				}
				else if (Character.isWhitespace(c))
				{
					state = STATE.WANT_EQUALS;
				}
				else if (c == '=')
				{
					state = STATE.WANT_VALUE;
				}
				else
				{
					throw new ParseException("syntax error", 0);
				}
			break;
			case WANT_EQUALS:
				if (Character.isWhitespace(c))
				{
				}
				else if (c == '=')
				{
					state = STATE.WANT_VALUE;
				}
				else
				{
					throw new ParseException("syntax error", 0);
				}
			break;
			case WANT_VALUE:
				if (Character.isWhitespace(c))
				{
				}
				else
				{
					value.append(c);
					state = STATE.VALUE;
				}
			break;
			case VALUE:
				value.append(c);
			break;
		}
	}
}
