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

    /**
     * 
     */
    public SpringMid(Spring right, double fraction, int width)
    {
        super();
        // TODO Auto-generated constructor stub
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
        // TODO Auto-generated method stub
        return 0;
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
