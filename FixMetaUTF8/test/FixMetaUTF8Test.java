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
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");
        String s = FixMetaUTF8.fixMeta(sb);
    }
}
