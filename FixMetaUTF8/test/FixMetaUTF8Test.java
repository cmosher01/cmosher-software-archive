import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
/*
 * Created on Sep 29, 2004
 */


/**
 * TODO
 * 
 * @author Chris
 */
public class FixMetaUTF8Test extends TestCase
{
    public void testSimple() throws IOException
    {
        String s = FixMetaUTF8.fix(new File("simple.html"));
    }
}
