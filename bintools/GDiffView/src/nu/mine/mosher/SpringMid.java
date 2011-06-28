package nu.mine.mosher;
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
    private final double offset;

    /**
     * 
     */
    public SpringMid(Spring right, double fraction, int width, boolean isRight)
    {
        this.right = right;
        this.fraction = fraction;
        if (isRight)
        {
            this.offset = +(width/2+1);
        }
        else
        {
            this.offset = -(width/2+1);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getMaximumValue()
     */
    public int getMaximumValue()
    {
        return (int)Math.round(fraction*right.getMaximumValue() + offset);
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getMinimumValue()
     */
    public int getMinimumValue()
    {
        return (int)Math.round(fraction*right.getMinimumValue() + offset);
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getPreferredValue()
     */
    public int getPreferredValue()
    {
        return (int)Math.round(fraction*right.getPreferredValue() + offset);
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getValue()
     */
    public int getValue()
    {
        return (int)Math.round(fraction*right.getValue() + offset);
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
