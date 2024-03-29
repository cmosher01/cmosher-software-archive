/*
 * Created on Sep 4, 2005
 */
package net.sourceforge.templat.lexer;

import net.sourceforge.templat.exception.TemplateParsingException;
import net.sourceforge.templat.parser.TemplateParser;

class EndIfToken implements TemplateToken
{
	@Override
	public String toString()
	{
		return "END IF";
	}

	public void parse(final TemplateParser parser, final Appendable appendTo) throws TemplateParsingException
	{
		parser.getContext().current().getValue(TemplateParser.VAR_IF); // make sure we're in an if-block
		parser.getContext().pop();
	}
}
