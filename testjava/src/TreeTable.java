import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TreeTable
{
    private static int s;
    int m = ++s;
    boolean mAtom = true;
    final List mList = new ArrayList();

    void add(TreeTable child)
    {
        mList.add(child);
        mAtom = false;
    }

    void getName(StringBuffer s)
    {
        if (mAtom)
            s.append("atom");
        else
        {
            if (m%2==0)
//                s.append("i<br/>n<br/>t<br/>e<br/>r<br/>s<br/>e<br/>c<br/>t<br/>i<br/>o<br/>n<br/>");
                s.append("intersection");
            else
//                s.append("u<br/>n<br/>i<br/>o<br/>n<br/>");
                s.append("union");
        }

        s.append("#");

        s.append(m);
    }

    void buildTable(StringBuffer s)
    {
        s.append("<table border=\"1\">\r\n");
        buildRows(s,0,0,getDepth(),false);
        s.append("</table>\r\n");
    }

    void buildRows(StringBuffer s, int lev, int d, int maxd, boolean putInExistingRow)
    {
        if (!putInExistingRow)
        {
            s.append("<tr>");
        }
        if (mAtom)
        {
            if (d > 0)
            {
                s.append("<td colspan=\"");
                s.append(d);
                s.append("\">");
s.append("c"+d);
                s.append("&nbsp;");
                s.append("</td>");
            }
            s.append("<td>");
            getName(s);
            s.append("</td>");
            s.append("</tr>\r\n");
        }
        else
        {
            s.append("<td rowspan=\"");
            s.append(getLeafCount());
            s.append("\" colspan=\"");
            s.append(maxd-getDepth()-lev+1);
            s.append("\">");
s.append("c"+(maxd-getDepth()-lev+1));
s.append(" r"+getLeafCount());
            getName(s);
            s.append("</td>");
            boolean first = true;
            for (Iterator i = mList.iterator(); i.hasNext();)
            {
                TreeTable t = (TreeTable)i.next();
                t.buildRows(s,lev+1,getDepth()-1,maxd,first);
                first = false;
            }
        }
    }

    private int getDepth()
    {
        int d = 0;

        if (!mAtom)
        {
            for (Iterator i = mList.iterator(); i.hasNext();)
            {
                TreeTable t = (TreeTable)i.next();
                int td = t.getDepth()+1;
                if (td > d)
                    d = td;
            }
        }

        return d;
    }

    private int getLeafCount()
    {
        int c = 1;

        if (!mAtom)
        {
            c = 0;
            for (Iterator i = mList.iterator(); i.hasNext();)
            {
                TreeTable t = (TreeTable)i.next();
                c += t.getLeafCount();
            }
        }

        return c;
    }

    public static void main(String[] rArg) throws IOException
    {
        TreeTable t1 = new TreeTable();
        TreeTable t2 = new TreeTable();
        TreeTable t3 = new TreeTable();
        TreeTable t4 = new TreeTable();
        TreeTable t5 = new TreeTable();
        TreeTable t6 = new TreeTable();
        TreeTable t7 = new TreeTable();
        TreeTable t8 = new TreeTable();
        TreeTable t9 = new TreeTable();
        TreeTable t10 = new TreeTable();
        TreeTable t11 = new TreeTable();
        TreeTable t12 = new TreeTable();
        TreeTable t13 = new TreeTable();
        TreeTable t14 = new TreeTable();
        TreeTable t15 = new TreeTable();
        TreeTable t16 = new TreeTable();
        TreeTable t17 = new TreeTable();
        TreeTable t18 = new TreeTable();
        TreeTable t19 = new TreeTable();
        TreeTable t20 = new TreeTable();
        TreeTable t21 = new TreeTable();
        TreeTable t22 = new TreeTable();

        t1.add(t2);
        t1.add(t3);
        t1.add(t4);

        t3.add(t5);
        t3.add(t6);

        t8.add(t9);
        t8.add(t10);
        t8.add(t11);

        t10.add(t12);
        t10.add(t13);

        t14.add(t1);
        t14.add(t7);
        t14.add(t8);
        t14.add(t15);
        t14.add(t16);
        t14.add(t17);
        t14.add(t20);

        t18.add(t14);
        t18.add(t19);

        t20.add(t21);
        t20.add(t22);

        StringBuffer s = new StringBuffer(1024);
        s.append("<html>\r\n");
        s.append("<body>\r\n");
        t18.buildTable(s);
        s.append("</body>\r\n");
        s.append("</html>\r\n");

        File f = new File("c:\\temp\\test.html");
        WriteStringToFile.writeStringToFile(s,f);
    }
}
