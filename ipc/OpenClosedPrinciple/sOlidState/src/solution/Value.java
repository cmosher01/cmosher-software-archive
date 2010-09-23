package solution;

import java.text.ParseException;

public class Value implements ParserState
{
	@Override
	public ParserState transition(char c, StringBuilder name, StringBuilder value) throws ParseException
	{
		value.append(c);
		return this;
	}
}
