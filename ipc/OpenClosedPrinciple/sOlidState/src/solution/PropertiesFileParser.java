package solution;

import java.text.ParseException;

/*
 *	For each input line
 *		PropertiesFileParser p = new PropertiesFileParser();
 *		for each character c
 *			p.transition(c);
 */
public class PropertiesFileParser
{
	private ParserState state = new WantName();
	private final StringBuilder name = new StringBuilder();
	private final StringBuilder value = new StringBuilder();
	// ...
	public void transition(char c) throws ParseException
	{
		state = state.transition(c,name,value);
	}
}
