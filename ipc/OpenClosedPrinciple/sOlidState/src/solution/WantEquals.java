package solution;

import java.text.ParseException;

public class WantEquals implements ParserState
{
	@Override
	public ParserState transition(char c, StringBuilder name, StringBuilder value) throws ParseException
	{
		if (Character.isWhitespace(c))
		{
			return this;
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
