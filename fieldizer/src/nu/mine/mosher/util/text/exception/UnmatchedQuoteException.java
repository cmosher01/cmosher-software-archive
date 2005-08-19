/*
 * Created on Apr 12, 2004
 */
package nu.mine.mosher.util.text.exception;

/**
 * Indicates that a field started with a quotation
 * mark (U+0022) and was otherwise valid, but did
 * not end with a quotation mark. For example:
 * <ul>
 * <li>ok,"testbad</li>
 * </ul>
 * The field "testbad contains an unmatched quote.
 */
public class UnmatchedQuoteException extends Exception
{
    private static final String MSG = "Unmatched quotation mark";

    /**
     * Initializes an <code>UnmatchedQuoteException</code> with a
     * generic, English error message.
     */
    public UnmatchedQuoteException()
    {
        super(MSG);
    }

    /**
     * Initializes an <code>UnmatchedQuoteException</code> with a
     * generic, English error message and a given chained <code>Exception</code>.
     * @param cause the chained <code>Throwable</code>
     */
    public UnmatchedQuoteException(Throwable cause)
    {
        super(MSG,cause);
    }

    /**
     * Initializes an <code>UnmatchedQuoteException</code> with a
     * generic, English error message incorporating the given
     * piece of source string.
     * @param near some context from the source buffer, intended to
     * provide additional information to the end user about where the
     * error occurred.
     */
    public UnmatchedQuoteException(String near)
    {
        super(MSG+" near: "+near);
    }

    /**
     * Initializes an <code>UnmatchedQuoteException</code> with a
     * generic, English error message incorporating the given
     * piece of source string, and a given chained <code>Exception</code>.
     * @param near some context from the source buffer, intended to
     * provide additional information to the end user about where the
     * error occurred.
     * @param cause the chained <code>Throwable</code>
     */
    public UnmatchedQuoteException(String near, Throwable cause)
    {
        super(MSG+" near: "+near,cause);
    }
}
