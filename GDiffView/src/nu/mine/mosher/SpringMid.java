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

    /**
     * 
     */
    public SpringMid(Spring right, double fraction, int width)
    {
        this.right = right;
        this.fraction = fraction;
        this.width = width;
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getMaximumValue()
     */
    public int getMaximumValue()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getMinimumValue()
     */
    public int getMinimumValue()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.swing.Spring#getPreferredValue()
     */
    public int getPreferredValue()
    {
        // TODO Auto-generated method stub
        return 0;
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
