/*
 * Created on April 13, 2004
 */
package nu.mine.mosher.util.text.exception;

/**
 * Indicates that the field contained a quotation
 * mark (U+0022) in an illegal position. This can
 * occur in two cases:
 * <ol>
 * <li>a quote in the middle of a non-quoted field,
 * for example, test"test</li>
 * <li>a single quote in the middle of a quoted field,
 * for example "test"test"</li>
 * </ol>
 * The only valid uses of a quotation mark are:
 * <ol>
 * <li>at the beginning and end of the field, for
 * example, "test"</li>
 * <li>in a quoted field, doubled, for example
 * "test""test" (where it indicated one literal quote)</li>
 * </ol>
 */
public class IllegalQuoteException extends Exception
{
    private static final String MSG = "Illegal quotation mark";

    /**
     * Initializes an <code>IllegalQuoteException</code> with a
     * generic, English error message.
     */
    public IllegalQuoteException()
    {
        super(MSG);
    }

    /**
     * Initializes an <code>IllegalQuoteException</code> with a
     * generic, English error message and a given chained <code>Exception</code>.
     * @param cause the chained <code>Throwable</code>
     */
    public IllegalQuoteException(Throwable cause)
    {
        super(MSG,cause);
    }

    /**
     * Initializes an <code>IllegalQuoteException</code> with a
     * generic, English error message incorporating the given
     * piece of source string.
     * @param near some context from the source buffer, intended to
     * provide additional information to the end user about where the
     * error occurred.
     */
    public IllegalQuoteException(String near)
    {
        super(MSG+" near: "+near);
    }

    /**
     * Initializes an <code>IllegalQuoteException</code> with a
     * generic, English error message incorporating the given
     * piece of source string, and a given chained <code>Exception</code>.
     * @param near some context from the source buffer, intended to
     * provide additional information to the end user about where the
     * error occurred.
     * @param cause the chained <code>Throwable</code>
     */
    public IllegalQuoteException(String near, Throwable cause)
    {
        super(MSG+" near: "+near,cause);
    }
}
