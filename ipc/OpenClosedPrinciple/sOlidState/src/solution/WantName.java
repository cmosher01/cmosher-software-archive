package solution;

import java.text.ParseException;

public class WantName implements ParserState
{
	@Override
	public ParserState transition(char c, StringBuilder name, StringBuilder value) throws ParseException
	{
		if (Character.isLetterOrDigit(c))
		{
			name.append(c);
			return new Name();
		}
		else
		{
			throw new ParseException("syntax error", 0);
		}
	}
}
