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
    public void testSimple()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<html>\n");
        sbExpect.append("<HEAD>\n");
        sbExpect.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n");
        sbExpect.append("</head>\n");
        sbExpect.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    public void testMissing()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<html>\n");
        sbExpect.append("<HEAD>\n");
        sbExpect.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sbExpect.append("</head>\n");
        sbExpect.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    public void testMissingButOtherMeta()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<META http-equiv=\"Junk\" content=\"stuff\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<html>\n");
        sbExpect.append("<HEAD>\n");
        sbExpect.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sbExpect.append("<META http-equiv=\"Junk\" content=\"stuff\">\n");
        sbExpect.append("</head>\n");
        sbExpect.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    public void testMetaBefore()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<META http-equiv=\"Junk\" content=\"stuff\">\n");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<html>\n");
        sbExpect.append("<HEAD>\n");
        sbExpect.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sbExpect.append("<META http-equiv=\"Junk\" content=\"stuff\">\n\n");
        sbExpect.append("</head>\n");
        sbExpect.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    public void testMetaAfter()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">\n");
        sb.append("<META http-equiv=\"Junk\" content=\"stuff\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<html>\n");
        sbExpect.append("<HEAD>\n");
        sbExpect.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n\n");
        sbExpect.append("<META http-equiv=\"Junk\" content=\"stuff\">\n");
        sbExpect.append("</head>\n");
        sbExpect.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    public void testTwoLines()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<META http-equiv=\"Content-Type\" \ncontent=\"text/html; charset=windows-1252\">\n");
        sb.append("</head>\n");
        sb.append("</html>\n");

        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<html>\n");
        sbExpect.append("<HEAD>\n");
        sbExpect.append("<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sbExpect.append("\n");
        sbExpect.append("</head>\n");
        sbExpect.append("</html>\n");

        shouldBe(sbExpect,sb);
    }

    public void testNoQuotes()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" + 
                "<!-- saved from url=(0062)http://ntjp:8080/epanCustomerOrder/customerOrder.jsp?id=jackie -->\n" + 
                "<HTML><HEAD><TITLE>Customer Order Form</TITLE>\n" + 
                "<META http-equiv=Content-Type content=\"text/html; charset=iso-8859-1\">\n" + 
                "<META content=\"MSHTML 6.00.2800.1400\" name=GENERATOR></HEAD>\n" + 
                "<BODY>");
        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" + 
                "<!-- saved from url=(0062)http://ntjp:8080/epanCustomerOrder/customerOrder.jsp?id=jackie -->\n" + 
                "<HTML><HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><TITLE>Customer Order Form</TITLE>\n" + 
                "\n" + 
                "<META content=\"MSHTML 6.00.2800.1400\" name=GENERATOR></HEAD>\n" + 
                "<BODY>");

        shouldBe(sbExpect,sb);
    }

    public void testBodyOnly()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<body>...</body></html>");
        StringBuffer sbExpect = new StringBuffer();
        sbExpect.append("<HTML>\n<HEAD>\n<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n</HEAD>\n" + 
                "<body>...</body></html>");

        shouldBe(sbExpect,sb);
    }

    private void shouldBe(StringBuffer sbExpect, StringBuffer in)
    {
        System.out.println(in);
        System.out.println("----------------------");
        in = FixMetaUTF8.removeContentTypeMeta(in);
        System.out.println(in);
        System.out.println("----------------------");
        String s = FixMetaUTF8.addContentTypeMeta(in);
        System.out.println(s);
        System.out.println("-----------------------------------------------------------------------------");
        assertEquals(sbExpect.toString(), s);
    }
}
