package to.go.gro.groapplet;
import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

public class GROapplet extends JApplet
{
    private boolean test;

    private FamilyChart fc;

    public GROapplet() throws HeadlessException
    {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    public void init()
    {
        try
        {
            super.init();
            tryinit();
        }
        catch (Throwable e)
        {
            handleException(e);
        }
    }

    protected void handleException(Throwable e)
    {
        try
        {
            /*
             * Print simple error message to Java Console.
             * Do this for sanity in case something bad
             * happens below that prevents any error message
             * from being displayed in the browser.
             */
            e.printStackTrace();

            /*
             * Build a StringBuffer containing the entire
             * exception message and stack dump. This includes
             * any chained exceptions.
             */
            StringBuffer sb = new StringBuffer();
            appendException(e, sb);
            Throwable cause = e.getCause();
            while (cause != null)
            {
                sb.append("caused by:\r\n");
                appendException(cause, sb);
                cause = cause.getCause();
            }

            /*
             * Make a text area (with scrollbars as needed)
             * and display the exception message.
             */
            JTextArea errorPane = new JTextArea();
            errorPane.read(new StringReader(sb.toString()), null);
            JScrollPane areaScrollPane = new JScrollPane(errorPane);
            areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            areaScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            getContentPane().removeAll();
            getContentPane().add(areaScrollPane);
        }
        catch (Throwable ignore)
        {
            System.err.println("Exception happened while processing exception.");
            System.err.println(ignore.getMessage());
            System.err.println("The original exception was:");
            System.err.println(e.getMessage());
        }
    }

    protected static void appendException(Throwable e, StringBuffer sb)
    {
        String sMsg = e.getMessage();
        if (sMsg != null)
        {
            sb.append(sMsg);
            sb.append("\r\n");
        }

        sb.append(e.getClass().getName());
        sb.append("\r\n");

        StackTraceElement[] rtr = e.getStackTrace();
        for (int i = 0; i < rtr.length; i++)
        {
            StackTraceElement stackTraceElement = rtr[i];
            sb.append("    at ");
            sb.append(stackTraceElement.toString());
            sb.append("\r\n");
        }
    }

    protected static void useOSLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ignoreAnyExceptions)
        {
        }
    }

    protected void tryinit() throws Exception
    {
        useOSLookAndFeel();

        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

        InputStream streamTree;

		if (test)
		{
			streamTree = new FileInputStream(new File("test.gro"));
		}
		else
		{
			URL url = new URL(getDocumentBase(), "?chartdata");
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.connect();
			streamTree = con.getInputStream();
		}

        readFrom(streamTree);

        JScrollPane scr = new JScrollPane(fc, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scr);
    }

    protected void readFrom(InputStream instream) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(instream, "UTF-8"));

        Map mapIdToIndi = new HashMap();

        IndiSet indis = new IndiSet();

        String sMaxWidth = br.readLine();
        double dMaxWidth = Double.parseDouble(sMaxWidth);
        int cMaxWidth = (int)Math.round(dMaxWidth);
        indis.setMaxWidth(cMaxWidth);

        String scIndi = br.readLine();
        int cIndi = Integer.parseInt(scIndi);
        for (int i = 0; i < cIndi; ++i)
        {
            String slineIndi = br.readLine();
            StringFieldizer sf = new StringFieldizer(slineIndi);
            String id = sf.nextToken();
            String name = sf.nextToken();
            String birth = sf.nextToken();
            String death = sf.nextToken();
            String sx = sf.nextToken();
            double dx = Double.parseDouble(sx);
            int x = (int)Math.round(dx);
            String sy = sf.nextToken();
            double dy = Double.parseDouble(sy);
            int y = (int)Math.round(dy);
            Indi indi = new Indi(x, y, name, birth, death);
            indis.add(indi);
            mapIdToIndi.put(id, indi);
        }

        FamiSet famis = new FamiSet();
        String scFami = br.readLine();
        int cFami = Integer.parseInt(scFami);
        for (int i = 0; i < cFami; ++i)
        {
            String slineIndi = br.readLine();
            StringFieldizer sf = new StringFieldizer(slineIndi);
            Fami fami = new Fami();
            String husb = sf.nextToken();
            fami.setHusb((Indi)mapIdToIndi.get(husb));
            String wife = sf.nextToken();
            fami.setWife((Indi)mapIdToIndi.get(wife));
            String sc = sf.nextToken();
            int c = Integer.parseInt(sc);
            for (int ic = 0; ic < c; ++ic)
            {
                String chil = sf.nextToken();
                fami.addChild((Indi)mapIdToIndi.get(chil));
            }
            famis.add(fami);
        }

        fc = new FamilyChart(indis, famis);
    }

    protected void createIndi(IndiSet indis, Map mapIdToIndi, String id, int x, int y, String name, String birth, String death)
    {
        Indi n = new Indi(x, y, name, birth, death);
        indis.add(n);
        mapIdToIndi.put(id, n);
    }

    public static void main(String[] args)
    {
        if (args.length > 0)
            System.err.println("Arguments ignored.");

        Frame f = new Frame("Paint Applet");
        f.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                e = e;
                System.exit(0);
            }
        });

        GROapplet applet = new GROapplet();
        applet.setTestMode();
        applet.init();
        f.add(applet);

        f.setSize(640, 480);
        f.setVisible(true);
    }

    private void setTestMode()
    {
    	test = true;
    }

    //    protected void processMouseMotionEvent(MouseEvent e)
    //    {
    //        if (fc.isOnIndi(e.getPoint()))
    //            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    //        else
    //            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    //        super.processMouseMotionEvent(e);
    //    }
    /* (non-Javadoc)
     * @see java.awt.Component#processMouseEvent(java.awt.event.MouseEvent)
     */
    protected void processMouseEvent(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        super.processMouseEvent(arg0);
    }

    /* (non-Javadoc)
     * @see java.awt.Component#processMouseMotionEvent(java.awt.event.MouseEvent)
     */
    protected void processMouseMotionEvent(MouseEvent arg0)
    {
        // TODO Auto-generated method stub
        super.processMouseMotionEvent(arg0);
    }

}
