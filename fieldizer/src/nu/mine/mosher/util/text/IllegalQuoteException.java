/*
 * Created on Apr 13, 2004
 */
package nu.mine.mosher.util.text;

/**
 * Indicates that the field contained a quotation
 * mark (U+0022) in an illegal position. This can
 * occur in two cases:
 * <ol>
 * <li>a quote in the middle of an unquoted field,
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

    public IllegalQuoteException()
    {
        super(MSG);
    }

    public IllegalQuoteException(Throwable cause)
    {
        super(MSG,cause);
    }

    public IllegalQuoteException(String near)
    {
        super(MSG+" near: "+near);
    }

    public IllegalQuoteException(String near, Throwable cause)
    {
        super(MSG+" near: "+near,cause);
    }
}
