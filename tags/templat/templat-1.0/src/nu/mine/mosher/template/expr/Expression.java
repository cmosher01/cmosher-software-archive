/*
 * Created on Sep 6, 2005
 */
package nu.mine.mosher.template.expr;

import nu.mine.mosher.template.exception.TemplateParsingException;
import nu.mine.mosher.template.expr.exception.ExprLexingException;
import nu.mine.mosher.template.expr.exception.ExprParsingException;
import nu.mine.mosher.template.parser.context.ContextStack;



/**
 * Contains static methods to evaluate an expression within a
 * template statement.
 *
 * @author Chris Mosher
 */
public class Expression
{
	/**
	 * Evaluates the given expression, using the given context stack.
	 * @param expr expression to evaluate
	 * @param stackContext context stack
	 * @return the result of evaluating the expression
	 * @throws TemplateParsingException
	 */
	public static Object eval(final String expr, final ContextStack stackContext) throws TemplateParsingException
	{
		try
		{
			return tryEval(expr,stackContext);
		}
		catch (final Throwable e)
		{
			throw new TemplateParsingException("error occurred while evaluating expression \""+expr+"\":",e);
		}
	}

	private static Object tryEval(final String expr, final ContextStack stackContext) throws ExprLexingException, ExprParsingException, TemplateParsingException
	{
		final ExprParser parser = new ExprParser(expr,stackContext);

		return parser.parse();
	}
}
