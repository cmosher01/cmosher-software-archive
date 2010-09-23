package solution;

import java.text.ParseException;

public class WantValue implements ParserState
{
	@Override
	public ParserState transition(char c, StringBuilder name, StringBuilder value) throws ParseException
	{
		if (Character.isWhitespace(c))
		{
			return this;
		}
		else
		{
			value.append(c);
			return new Value();
		}
	}
}
