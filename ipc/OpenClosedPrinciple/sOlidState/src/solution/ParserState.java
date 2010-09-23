package solution;

import java.text.ParseException;

public interface ParserState
{
	ParserState transition(char c, StringBuilder name, StringBuilder value) throws ParseException;
}
