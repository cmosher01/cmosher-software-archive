package solution;

import java.text.ParseException;

public class Name implements ParserState
{
	@Override
	public ParserState transition(char c, StringBuilder name, StringBuilder value) throws ParseException
	{
		if (Character.isLetterOrDigit(c))
		{
			name.append(c);
			return this;
		}
		else if (Character.isWhitespace(c))
		{
			return new WantEquals();
		}
		else if (c == '=')
		{
			return new WantValue();
		}
		else
		{
			throw new ParseException("syntax error", 0);
		}
	}
}
