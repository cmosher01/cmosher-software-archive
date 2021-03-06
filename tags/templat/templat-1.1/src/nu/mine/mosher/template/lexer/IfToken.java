/*
 * Created on Sep 4, 2005
 */
package nu.mine.mosher.template.lexer;

import nu.mine.mosher.template.exception.TemplateParsingException;
import nu.mine.mosher.template.expr.Expression;
import nu.mine.mosher.template.parser.TemplateParser;
import nu.mine.mosher.template.parser.context.TemplateParserContext;

class IfToken implements TemplateToken
{
	private final String tag;

	/**
	 * @param tag
	 */
	public IfToken(final String tag)
	{
		this.tag = tag.trim();
	}

	@Override
	public String toString()
	{
		return "IF: "+this.tag;
	}

	public void parse(final TemplateParser parser, final Appendable appendTo) throws TemplateParsingException
	{
		try
		{
			tryParse(parser);
		}
		catch (final TemplateParsingException e)
		{
			throw e;
		}
		catch (final Throwable e)
		{
			throw new TemplateParsingException(e);
		}
	}

	private void tryParse(final TemplateParser parser) throws TemplateParsingException, ClassCastException
	{
		final boolean bCond;
		if (parser.getContext().isEverEqual(TemplateParser.VAR_IF,false))
		{
			bCond = false;
		}
		else
		{
			final Object objectCond = Expression.eval(this.tag,parser.getContext());
			bCond = (Boolean)objectCond; // could throw ClassCastException
		}

		final TemplateParserContext ctxNew = new TemplateParserContext();
		ctxNew.addVariable(TemplateParser.VAR_IF,bCond);

		parser.getContext().push(ctxNew);
	}
}
