/**
 * @(#)AboutDialog.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Properties;



/**
 * This class shows the About dialog box The dialog box display information
 * about the system as well as the Java VM.
 */
class AboutDialog extends Dialog implements ActionListener
{

    public static final String _JAVA_VERSION = "java.version";

    public static final String _JAVA_VENDOR = "java.vendor";

    public static final String _USER_TIMEZONE = "user.timezone";

    public static final String _OS_ARCH = "os.arch";

    public static final String _OS_NAME = "os.name";

    public static final String _OS_VERSION = "os.version";

    public static final String _USER_NAME = "user.name";

    public static final String _USER_HOME = "user.home";

    public static final String _USER_DIR = "user.dir";

    public static final String _VERSION = "Version 0.1 Beta";

    Button ok;

    Properties p = System.getProperties();

    static Enumeration property_keys;

    static String system_properties = null;

    MultiLineLabel system_info;

    String key;



    public AboutDialog(Frame parent)
    {
        super(parent,"About Java HTTP Proxy Server",true);
        this.setLayout(new BorderLayout(30,30));


        ok = new Button("OK"); // create 'OK' button
        ok.addActionListener(this); // add this to actionlistener list



        if (system_properties == null)
        {
            system_properties = " \nSystem Information:\n \n";
            system_properties += ("Java Version" + " : " + "JDK " + p.getProperty(_JAVA_VERSION) + "\n");
            system_properties += ("Java Vendor" + " : " + p.getProperty(_JAVA_VENDOR) + "\n");
            system_properties += ("Local Time Zone " + " : " + p.getProperty(_USER_TIMEZONE) + "\n");
            system_properties += ("Hardware Architecture" + " : " + p.getProperty(_OS_ARCH) + "\n");
            system_properties += ("Operating System" + " : " + p.getProperty(_OS_NAME) + "\n");
            system_properties += ("OS Version" + " : " + p.getProperty(_OS_VERSION) + "\n");
            system_properties += ("User Name" + " : " + p.getProperty(_USER_NAME) + "\n");
            system_properties += ("User Home Directory" + " : " + p.getProperty(_USER_HOME) + "\n");
            system_properties += ("Current Directory" + " : " + p.getProperty(_USER_DIR) + "\n");
            system_properties += " \nJava HTTP Proxy Server, " + _VERSION + "\nby Steve Y.C. Hsueh,  April 1998";
        } // end of if

        //  this.add("North", new Label("System Information:"));
        system_info = new MultiLineLabel(system_properties,10,10);
        this.add("Center",system_info);

        Panel pnl = new Panel();
        pnl.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
        pnl.add(ok);

        //System.out.println(system_properties);
        this.add("South",pnl);
        setSize(300,350);

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }


    /**
     * event handler for action events
     */
    public void actionPerformed(ActionEvent evt)
    {

        if ((evt.getActionCommand()).equals("OK"))
        {
            this.dispose();
        }

    }

    /**
     * event handler for window event
     */
    public void processWindowEvent(WindowEvent winevt)
    {

        // check if the event is widow_destroy
        if (winevt.getID() == Event.WINDOW_DESTROY)
            this.dispose();

    }


} // end of class AboutDialog
