import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/*
 * Created on Nov 15, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Lister extends PrintWriter
{

    /**
     * @param out
     */
    public Lister(OutputStream out)
    {
        super(out);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param out
     * @param autoFlush
     */
    public Lister(OutputStream out, boolean autoFlush)
    {
        super(out, autoFlush);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param out
     */
    public Lister(Writer out)
    {
        super(out);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param out
     * @param autoFlush
     */
    public Lister(Writer out, boolean autoFlush)
    {
        super(out, autoFlush);
        // TODO Auto-generated constructor stub
    }

}
