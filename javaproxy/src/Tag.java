/**
 * @(#)Tag.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS
 * SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES
 * THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK. Author :
 * Steve Yeong-Ching Hsueh
 */

import java.awt.Rectangle;



/**
 * Tag
 */
class Tag
{
    private String name;

    private Rectangle position_size;



    /**
     * constructor
     */
    public Tag()
    {
        this("  ");
    }

    /**
     * constructor
     */
    public Tag(String tagname)
    {
        name = tagname;
    }

    /**
     * get name of this tag
     */
    public String getName()
    {
        return name;
    }

    /**
     * set position size
     */
    public void setPositionSize(Rectangle rect)
    {
        position_size = rect;
    }

    /**
     * get positiion size
     */
    public Rectangle getPositionSize()
    {
        return position_size;
    }

}