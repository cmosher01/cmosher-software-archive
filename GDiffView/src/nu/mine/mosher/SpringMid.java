import javax.swing.Spring;
/*
 * Created on Sep 1, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class SpringMid extends Spring
{
    private final Spring right;
    private final double fraction;
    private final int width;
    private final boolean isRight;

    /**
     * 
     */
    public SpringMid(Spring right, double fraction, int width, int isRight)
    {
        this.right = right;
        this.fraction = fraction;
        this.width = width;
        this.isRight = isRight;
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getMaximumValue()
     */
    public int getMaximumValue()
    {
        return (int)Math.round(fraction*right.getMaximumValue() - width/2);
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getMinimumValue()
     */
    public int getMinimumValue()
    {
        return (int)Math.round(fraction*right.getMinimumValue() - width/2);
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getPreferredValue()
     */
    public int getPreferredValue()
    {
        return (int)Math.round(fraction*right.getPreferredValue() - width/2);
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getValue()
     */
    public int getValue()
    {
        return (int)Math.round(fraction*right.getValue() - width/2);
    }

    /**
     * @see javax.swing.Spring#setValue(int)
     */
    public void setValue(int val)
    {
        if (val == UNSET)
        {
            return;
        }
        throw new UnsupportedOperationException("Cannot set value on a derived spring");
    }
}
