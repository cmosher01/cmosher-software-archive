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
        sb.append("<html>\n");
        sb.append("<HEAD>\n");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    private void shouldBe(StringBuffer sbExpect, StringBuffer in)
    {
        in = FixMetaUTF8.removeContentTypeMeta(in);
        String s = FixMetaUTF8.addContentTypeMeta(in);
        assertTrue(s.compareToIgnoreCase(sbExpect.toString())==0);
    }
}
