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
        sb.append("<META http-equiv=\"Content-Type\" \ncontent=\"text/html; charset=windows-1252\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sb.append("<html>");
        sb.append("<HEAD>\");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\");

        sb.append("</head>\");
        sb.append("</html>\");


        System.out.println(sb);
        sb = FixMetaUTF8.removeContentTypeMeta(sb);
        System.out.println(sb);
        String s = FixMetaUTF8.addContentTypeMeta(sb);
        System.out.println(s);
    }
}
