/**
 * @(#)MessageWindow.java	April 3, 1998
 *
 * I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF THIS SOFTWARE, EITHER
 * EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY DAMAGES THIS
 * SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 *
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PrintJob;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * MessageWindow shows message in a frame
 */
class MessageWindow extends Frame implements ActionListener
{
    final static int MAX_ROW_NUM =800;  // max number of lines in the window
    final static int TOLERANCE   = 10;  // extra line allowed
    private TextArea MessageArea;       // TextArea object
    MenuBar mb;
    Menu    file_menu;
    MenuItem mi_save  = new MenuItem("Save");
    MenuItem mi_print = new MenuItem("Print");
    MenuItem mi_exit  = new MenuItem("Exit");
    int  pcount        = 0; // print job counter
    int  font_height   = 0; // system font's height
    Font cur_font      = null;
    String default_savename = "";
    String default_fileext  = ".msg";
    Graphics pg = null;

    /**
     * constructor
     */
    MessageWindow(String Title, String savefile) {
        this(Title);
        default_savename = savefile;
    }

    /**
     * constructor
     */
    MessageWindow(String Title) {
        super(Title);

        mb = new MenuBar();
        this.setMenuBar(mb);
        file_menu = new Menu("File", false);
        file_menu.add(mi_save);
        mi_save.addActionListener(this);
        file_menu.add(mi_print);
        mi_print.addActionListener(this);
        file_menu.addSeparator();
        file_menu.add(mi_exit);
        mi_exit.addActionListener(this);
        mb.add(file_menu);

        setLayout(new BorderLayout());
        MessageArea = new TextArea("", 10, 20);
        MessageArea.setEditable(false);
        add("Center", MessageArea);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK );

    }

    public void setSaveEnabled(boolean mode) {
        mi_save.setEnabled(mode);
    }

    public void setPrintEnabled(boolean mode) {
        mi_print.setEnabled(mode);
    }


    public boolean addMessage(String msg) {
        int newlineposition = 0;
        String Text;

        Text = MessageArea.getText();
        StringTokenizer st = new StringTokenizer(Text, "\n");

        if(st.countTokens() > MAX_ROW_NUM + TOLERANCE) {
            //System.out.println("tokens = " + st.countTokens());
            for(int j=0; j<TOLERANCE; j++) {
                newlineposition = Text.indexOf(10, newlineposition) + 1;
            }
            MessageArea.replaceRange("", 0, newlineposition);
        }

        MessageArea.append(msg); // append the message to textarea too

        return true;
    } // end of addMessage


    public void processWindowEvent(WindowEvent winevt) {

        // check if the event is widow_destroy
        if( winevt.getID() == Event.WINDOW_DESTROY ) {
            this.dispose();
        }
        super.processWindowEvent(winevt);  // parent class need to handle the event too

    } // end of processWindowEvent


    private void save_content() {
        FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
        fd.setFile(default_savename);
        fd.setVisible(true);


        String path = fd.getDirectory();
        String file = fd.getFile();

        if(path == null || file == null) return ;

        String pathname = path + file;

        System.out.println("Saving  " + pathname);
        try {
            FileOutputStream target = new FileOutputStream(pathname);
            String content = MessageArea.getText();
            byte [] buf = new byte[content.length()];
            buf = content.getBytes("8859_1");
            target.write(buf);
            target.flush();
            target.close();
            System.out.println(content);
        }
        catch (IOException e) {
            System.out.println("Failed: " + e);
        }
        finally {

        }

    }

    private void print_content() {

        if(pcount++ > 99) pcount = 0;

        PrintJob pj = getToolkit().getPrintJob(this, "server print_" + pcount, null);

        if(pj != null) {
            pg = pj.getGraphics();
        }
        else return;



        /*
        pg.setFont(cur_font); // ok
        int x = 0;
        int y = 0;

        String fcontent    = MessageArea.getText();
        StringTokenizer st = new StringTokenizer(fcontent, "\n");

        while (st.hasMoreTokens()) {
              pg.drawString(st.nextToken(), x, y);
              y += font_height;
        }
        */
        printAll(pg);
        pg.dispose();
        pj.end();

        System.gc(); // force garbage collection
    }


    public void paint (Graphics g) {
        if(cur_font == null) cur_font = g.getFont();
        if(font_height == 0)
            font_height = g.getFontMetrics(g.getFont()).getHeight();
        super.paint(g);
    }

    public void actionPerformed(ActionEvent actevt) {

        Object evtsrc = actevt.getSource();

        if(evtsrc == mi_save ) save_content();
        else if(evtsrc == mi_print ) print_content();
        else if(evtsrc == mi_exit  ) {
            // a trick here, calls processWindowEvent
            processWindowEvent(new WindowEvent(this, Event.WINDOW_DESTROY));
        }

    }


}
