/**
 * @(#)FileViewer.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;



/**
 * FileViewer display file content in a frame window
 */
public class FileViewer extends Frame
{

    /**
     * constructor
     */
    public FileViewer(String filename)
    {
        super("Content of " + filename);
        File f = new File(filename);
        byte[] buffer = null;

        try
        {
            int fsize = (int)f.length();
            buffer = new byte[fsize];
            int bytes_read = 0;

            FileInputStream in = new FileInputStream(f);
            while (bytes_read < fsize)
                bytes_read += in.read(buffer,bytes_read,fsize - bytes_read);
        }
        catch (IOException e)
        {
        }

        TextArea ta = new TextArea(new String(buffer),30,42);
        ta.setEditable(false);
        add("Center",ta);
        pack();
        show();
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    }

    /**
     * event handler for window events
     */
    public void processWindowEvent(WindowEvent winevt)
    {
        if (winevt.getID() == Event.WINDOW_DESTROY)
            this.setVisible(false);
    }

}