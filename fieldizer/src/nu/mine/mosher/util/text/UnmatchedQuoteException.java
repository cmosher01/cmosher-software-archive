/*
 * Created on Apr 12, 2004
 */
package nu.mine.mosher.util.text;

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

    public UnmatchedQuoteException()
    {
        super(MSG);
    }

    public UnmatchedQuoteException(Throwable cause)
    {
        super(MSG,cause);
    }

    public UnmatchedQuoteException(String near)
    {
        super(MSG+" near: "+near);
    }

    public UnmatchedQuoteException(String near, Throwable cause)
    {
        super(MSG+" near: "+near,cause);
    }
}
