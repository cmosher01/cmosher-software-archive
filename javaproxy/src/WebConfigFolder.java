/**
 * @(#)ToolBarCaption.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE
 * SUITABILITY OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE
 * LIABLE FOR ANY DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR
 * OWN RISK. Author : Steve Yeong-Ching Hsueh
 */

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;



/**
 * WebConfigFolder, folder for configuration
 */
class WebConfigFolder implements ActionListener, ConfigInterface
{
    Frame parentFrame;

    Folder fldr;

    SetupPage sp1 = new SetupPage("Network");

    SetupPage sp2 = new SetupPage("Logging");

    TextField tf_ip, tf_port, tf_adminmail, tf_proxy_ip, tf_proxy_port;

    TextField tf_rcvtimeout, tf_sendtimeout, tf_maxconnection, tf_cache_max, tf_cache_exp;

    TextField tf_accesslog, tf_errorlog;

    CheckboxGroup logginglevel = new CheckboxGroup();

    Checkbox[] levels = new Checkbox[3];

    Checkbox ckbox_spc = null;

    //    List mime, authdir, authuser, alias;
    Button addauthdir, delauthdir, addauthuser, delauthuser;

    Button addmime, delmime, addalias, delalias;

    String inifile = "Jproxy.properties";

    String newuser = "";

    String newmime = "";

    String newalias = "";

    String newacl = "";

    int i, listsize;

    static String header = "Properties file for Java Proxy Server.\n#Use \'/\' as separator for both DOS and Unix";

    Properties httpconfig = null;



    /**
     * constructor
     */
    WebConfigFolder(Frame p, String title)
    {
        String s = title;
        title = s;

        parentFrame = p;
        fldr = new Folder(p,this,"Server Configuration");
        fldr.setBounds(new Rectangle(100,100,500,320));
        httpconfig = new Properties();

        try
        {
            FileInputStream fs = new FileInputStream(inifile);
            httpconfig.load(fs);
            fs.close();
        }
        catch (IOException e)
        {
            System.out.println("IO Error: " + e);
            return;
        }

        // setup page 1, Network
        fldr.addPage(sp1);
        sp1.setLayout(null);
        sp1.add(new BorderBox("TCP/IP",15,8,460,98));

        Label label_ip = new Label(" IP   :"); // ip label
        sp1.add(label_ip); // ip label added
        label_ip.setBounds(20,25,30,10); // ip label sized & located
        tf_ip = new TextField(httpconfig.getProperty("network.ip"),15); // ip
                                                                        // textfield
        sp1.add(tf_ip); // ip textfield added
        tf_ip.setBounds(95,23,110,18); // ip textfield sized & located

        Label label_port = new Label(" PORT :");
        sp1.add(label_port);
        label_port.setBounds(20,50,50,10);
        tf_port = new TextField(httpconfig.getProperty("network.port"),6);
        sp1.add(tf_port);
        tf_port.setBounds(95,48,110,18);

        Label label_adminmail = new Label("Note: leave proxy ip/port blank if not using proxy.");
        sp1.add(label_adminmail);
        label_adminmail.setBounds(20,75,280,18);

        Label label_proxy_ip = new Label(" Proxy Server :");
        sp1.add(label_proxy_ip);
        label_proxy_ip.setBounds(230,25,100,18);
        tf_proxy_ip = new TextField(httpconfig.getProperty("network.proxy.ip"),50);
        sp1.add(tf_proxy_ip);
        tf_proxy_ip.setBounds(330,23,130,18);

        Label label_proxy_port = new Label(" Proxy Port :");
        sp1.add(label_proxy_port);
        label_proxy_port.setBounds(230,50,90,18);
        tf_proxy_port = new TextField(httpconfig.getProperty("network.proxy.port"),50);
        sp1.add(tf_proxy_port);
        tf_proxy_port.setBounds(330,48,130,18);



        sp1.add(new BorderBox("Connections",15,117,460,97));
        Label label_receivetimeout = new Label(" Receive TimeOut :");
        sp1.add(label_receivetimeout);
        label_receivetimeout.setBounds(20,141,110,10);
        tf_rcvtimeout = new TextField(httpconfig.getProperty("network.receive_timeout"),3);
        sp1.add(tf_rcvtimeout);
        tf_rcvtimeout.setBounds(140,139,40,18);

        Label label_sendtimeout = new Label(" Send TimeOut :");
        sp1.add(label_sendtimeout);
        label_sendtimeout.setBounds(20,165,90,10);
        tf_sendtimeout = new TextField(httpconfig.getProperty("network.send_timeout"),3);
        sp1.add(tf_sendtimeout);
        tf_sendtimeout.setBounds(140,163,40,18);

        Label label_maxconnections = new Label(" Number of Threads :");
        sp1.add(label_maxconnections);
        label_maxconnections.setBounds(20,189,120,10);
        tf_maxconnection = new TextField(httpconfig.getProperty("network.max_connections"),2);
        sp1.add(tf_maxconnection);
        tf_maxconnection.setBounds(140,187,40,18);


        Label label_cache_num = new Label(" Cached URLs :");
        sp1.add(label_cache_num);
        label_cache_num.setBounds(230,141,100,18);
        tf_cache_max = new TextField(httpconfig.getProperty("network.cache.max"),50);
        sp1.add(tf_cache_max);
        tf_cache_max.setBounds(360,139,100,18);

        Label label_cache_exp = new Label(" Cache Expires (hrs) :");
        sp1.add(label_cache_exp);
        label_cache_exp.setBounds(230,165,120,18);
        tf_cache_exp = new TextField(httpconfig.getProperty("network.cache.expire"),50);
        sp1.add(tf_cache_exp);
        tf_cache_exp.setBounds(360,163,100,18);


        String showcontent_string = httpconfig.getProperty("network.showcontent");
        boolean spc = false;
        if (showcontent_string != null && showcontent_string.equals("true"))
            spc = true;
        else
            spc = false;
        ckbox_spc = new Checkbox(" Show Printable Contents ?",spc);
        sp1.add(ckbox_spc);
        ckbox_spc.setBounds(230,187,180,18);



        // seup page 2, Logging
        fldr.addPage(sp2);
        sp2.setLayout(null);
        sp2.add(new BorderBox("Logging",15,8,460,196));

        Label label_accesslog = new Label(" Access Log   :"); // access log
                                                              // label
        sp2.add(label_accesslog); // access log label added
        label_accesslog.setBounds(20,30,80,15); // access log label sized &
                                                // located
        tf_accesslog = new TextField(httpconfig.getProperty("log.access"),60); // access
                                                                               // log
                                                                               // textfield
        sp2.add(tf_accesslog); // access log textfield added
        tf_accesslog.setBounds(110,28,200,18); // access log textfield sized &
                                               // located

        Label label_errorlog = new Label(" Error Log   :"); // error log label
        sp2.add(label_errorlog); // error log label added
        label_errorlog.setBounds(20,60,80,15); // error log label sized &
                                               // located
        tf_errorlog = new TextField(httpconfig.getProperty("log.error"),60); // error
                                                                             // log
                                                                             // textfield
        sp2.add(tf_errorlog); // error log textfield added
        tf_errorlog.setBounds(110,60,200,18); // error log textfield sized &
                                              // located
        Label label_logginglevel = new Label(" Logging Level :"); // logging
                                                                  // level label
        sp2.add(label_logginglevel); // logging level label added
        label_logginglevel.setBounds(20,90,90,15); // logging level label sized
                                                   // & located

        String log_level = httpconfig.getProperty("log.loglevel");
        if (log_level == null || log_level.equals(""))
            log_level = "0";
        levels[0] = new Checkbox("Minimal",logginglevel,(log_level.equals("0") ? true : false)); // log
                                                                                                 // detail
        levels[0].setBounds(110,90,80,20);
        sp2.add(levels[0]);
        levels[1] = new Checkbox("Normal",logginglevel,(log_level.equals("1") ? true : false)); // log
                                                                                                // only
                                                                                                // warning
                                                                                                // and
                                                                                                // fatal
        levels[1].setBounds(110,110,80,20);
        sp2.add(levels[1]);
        levels[2] = new Checkbox("Detail",logginglevel,(log_level.equals("2") ? true : false)); // log
                                                                                                // only
                                                                                                // fatal
        levels[2].setBounds(110,130,80,20);
        sp2.add(levels[2]);


        fldr.setVisible(true);

    }


    /**
     * event handler for action events
     */
//    public void actionPerformed(ActionEvent actevt)
//    {
//        FileDialog fd = new FileDialog(parentFrame,"Find File",FileDialog.LOAD);
//        Object actcomp = actevt.getSource();
//        String targetdir = "";
//        String targetuser = "";
//        int selecteditemnum;
//
//        Object evtsrc = actevt.getSource();
//        int evtid = actevt.getID();
//        String actcmd = actevt.getActionCommand();
//
//    }

    /**
     * update configuration
     */
    public void updateConfiguration()
    {

        FileOutputStream os = null;
        Checkbox cb = null;

        // Network
        httpconfig.put("network.ip",tf_ip.getText());
        httpconfig.put("network.port",tf_port.getText());
        httpconfig.put("network.proxy.ip",tf_proxy_ip.getText());
        httpconfig.put("network.proxy.port",tf_proxy_port.getText());

        httpconfig.put("network.receive_timeout",tf_rcvtimeout.getText());
        httpconfig.put("network.send_timeout",tf_sendtimeout.getText());
        httpconfig.put("network.max_connections",tf_maxconnection.getText());
        httpconfig.put("network.cache.max",tf_cache_max.getText());
        httpconfig.put("network.cache.expire",tf_cache_exp.getText());
        httpconfig.put("network.showcontent",ckbox_spc.getState() ? "true" : "false");

        // Log
        httpconfig.put("log.access",tf_accesslog.getText());
        httpconfig.put("log.error",tf_errorlog.getText());


        cb = logginglevel.getSelectedCheckbox();
        for (i = 0; i < levels.length; i++)
        {
            if (cb == levels[i])
                httpconfig.put("log.loglevel",i + "");
        }

        try
        {
            os = new FileOutputStream(inifile);
            httpconfig.save(os,header);
            os.close();
        }
        catch (IOException e)
        {
            return;
        } // need prompt error?
        finally
        {
        }


        ((ServerInterface)parentFrame).re_initialization();
    }

}

