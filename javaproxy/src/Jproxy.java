/**
 * @(#)Jproxy.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY OF
 * THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;



/**
 * Jproxy , core of the Java Proxy server. It instantiates the server frame,
 * reads configurations, and handles events.
 */
class Jproxy extends Frame implements ServerInterface, ActionListener,
        WindowListener, MouseListener
{
    // Constant Variables Here
    static final String _ADMIN = "Admin";

    static final String _START_SERVER = "Start Server";

    static final String _SHUTDOWN_SERVER = "Stop  Server";

    static final String _EXIT = "Exit";

    static final String _VIEW = "View";

    static final String _SERVER_RESPONSES = "Server Responses";

    static final String _CLIENT_REQUESTS = "Client Requests";

    static final String _SYSTEM_LOGS = "System Logs";

    static final String _VACCESS_LOG = "Access Log";

    static final String _VERROR_LOG = "Error  Log";

    static final String _SETUP = "Setup";

    static final String _SETUP_PROXY_SERVER = "Proxy";

    static final String _HELP = "Help";

    static final String _ABOUT = "About";

    static final String _HELP_FILE = "readme.1st";

    static final int _BUTTON_WIDTH = 50;

    static final int _BUTTON_HEIGHT = 25;

    static final String _FS = "\u001c";

    public static final int _LOG_LEVEL_MINIMAL = 2;

    public static final int _LOG_LEVEL_NORMAL = 1;

    public static final int _LOG_LEVEL_MAXIMAL = 0;

    static int log_level = 0;

    ImageButton ibstart, ibstop, ibexit;

    ImageButton ibviewresp, ibviewreq;

    Image img1, img2, img3, img4, img5;

    MenuBar menubar;

    Menu admin_menu, view_menu, setup_menu, help_menu;

    ProxyServer proxyserver = null;

    AboutDialog about;

    MessageWindow serverrespwin = null;

    MessageWindow clientreqwin = null;

    MessageWindow access_log_win = null;

    MessageWindow error_log_win = null;

    Image bkgroundimg, offscrimg;

    MediaTracker tracker = new MediaTracker(this);

    WebConfigFolder webconfigfolder;

    String inifile = "Jproxy.properties";

    int i;

    ToolBarCaption tbcpt;

    FileViewer helpviewer = null;

    DebugTracer tracer = new DebugTracer();

    Properties httpconfig = null;

    ProxyCachePool cachepool = null;

    // Menu Items
    MenuItem mi_start_server = new MenuItem(_START_SERVER);

    MenuItem mi_stop_server = new MenuItem(_SHUTDOWN_SERVER);

    MenuItem mi_exit = new MenuItem(_EXIT);

    MenuItem mi_view_httpmsgs = new MenuItem(_SERVER_RESPONSES);

    MenuItem mi_view_gamemsgs = new MenuItem(_CLIENT_REQUESTS);

    MenuItem mi_view_access_log = new MenuItem(_VACCESS_LOG);

    MenuItem mi_view_error_log = new MenuItem(_VERROR_LOG);

    MenuItem mi_setup_http = new MenuItem(_SETUP_PROXY_SERVER);

    MenuItem mi_help_help = new MenuItem(_HELP);

    MenuItem mi_help_about = new MenuItem(_ABOUT);

    public String host_addr = null;

    public int host_port;

    public int cache_max = 0;

    public int cache_expire = 0;

    public String access_log_name;

    public String error_log_name;

    static Log access_log = null;

    static Log error_log = null;

    ProxyCachePool cache_pool = null;



    /**
     * construnctor
     */
    Jproxy(int width, int height)
    {
        super("Java HTTP Proxy Server");

        // read background image
        bkgroundimg = Toolkit.getDefaultToolkit().getImage(
                "./svr_imgs/bkground.jpg");
        img1 = Toolkit.getDefaultToolkit().getImage("./svr_imgs/start.gif");
        img2 = Toolkit.getDefaultToolkit().getImage("./svr_imgs/stop.gif");
        img3 = Toolkit.getDefaultToolkit().getImage("./svr_imgs/close.gif");
        img4 = Toolkit.getDefaultToolkit().getImage("./svr_imgs/view1.gif");
        img5 = Toolkit.getDefaultToolkit().getImage("./svr_imgs/view2.gif");

        tracker.addImage(bkgroundimg,0);
        tracker.addImage(img1,1);
        tracker.addImage(img2,2);
        tracker.addImage(img3,3);
        tracker.addImage(img4,4);
        tracker.addImage(img5,5);

        try
        { // run media tracker to wait for images
            tracker.waitForID(0);
            tracker.waitForID(1);
            tracker.waitForID(2);
            tracker.waitForID(3);
            tracker.waitForID(4);
            tracker.waitForID(5);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(0);
        }


        ibstart = new ImageButton("start",img1);
        ibstop = new ImageButton("stop",img2);
        ibexit = new ImageButton("exit",img3);
        ibviewresp = new ImageButton("view1",img4);
        ibviewreq = new ImageButton("view2",img5);

        this.resize(width,height);

        setLayout(new UpperLineLayout());

        setResizable(false);

        add(ibstart);
        add(ibstop);
        add(ibviewresp);
        add(ibviewreq);
        add(ibexit);

        ibstart.addActionListener(this);
        ibstart.addMouseListener(this);
        ibstop.addActionListener(this);
        ibstop.addMouseListener(this);

        ibexit.addActionListener(this);
        ibexit.addMouseListener(this);
        ibviewresp.addActionListener(this);
        ibviewresp.addMouseListener(this);
        ibviewreq.addActionListener(this);
        ibviewreq.addMouseListener(this);

        ibstop.setEnabled(false);

        // read from configuration file and do initialization
        initialization();

        // menu
        menubar = new MenuBar();
        this.setMenuBar(menubar);

        // add admin_menu to menubar
        admin_menu = new Menu(_ADMIN,false);

        admin_menu.add(mi_start_server);
        mi_start_server.addActionListener(this);

        admin_menu.add(mi_stop_server);
        mi_stop_server.addActionListener(this);
        mi_stop_server.setEnabled(false);

        admin_menu.addSeparator();
        admin_menu.add(mi_exit);
        mi_exit.addActionListener(this);
        menubar.add(admin_menu);

        // add view_menu to menubar
        view_menu = new Menu(_VIEW,false);
        view_menu.add(mi_view_httpmsgs);
        mi_view_httpmsgs.addActionListener(this);
        view_menu.add(mi_view_gamemsgs);
        mi_view_gamemsgs.addActionListener(this);
        view_menu.addSeparator();

        // add 'logs' to second layer under view_menu
        Menu logs = new Menu(_SYSTEM_LOGS);
        logs.add(mi_view_access_log);
        mi_view_access_log.addActionListener(this);
        logs.add(mi_view_error_log);
        mi_view_error_log.addActionListener(this);
        view_menu.add(logs);
        menubar.add(view_menu);

        // add setup_menu to menubar
        setup_menu = new Menu(_SETUP,false);
        setup_menu.add(mi_setup_http);
        mi_setup_http.addActionListener(this);
        menubar.add(setup_menu);


        // add help_menu to menubar
        help_menu = new Menu(_HELP,false);
        help_menu.add(mi_help_help);
        mi_help_help.addActionListener(this);
        help_menu.addSeparator();
        help_menu.add(mi_help_about);
        mi_help_about.addActionListener(this);
        menubar.add(help_menu);

        menubar.setHelpMenu(help_menu);
        setMenuBar(menubar);

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);


        tbcpt = new ToolBarCaption(this," ");

    } // end of Jproxy


    private boolean initialization()
    {
        httpconfig = new Properties();

        try
        {
            FileInputStream fs = new FileInputStream(inifile);
            httpconfig.load(fs);
            fs.close();
        }
        catch (IOException e)
        {
            return false;
        }

        host_addr = httpconfig.getProperty("network.ip");
        host_port = Integer.parseInt(httpconfig.getProperty("network.port"));

        String cacheMaxString = httpconfig.getProperty("network.cache.max");
        String cacheExpString = httpconfig.getProperty("network.cache.expire");
        if (cacheMaxString == null || cacheMaxString.equals(""))
            cacheMaxString = "0";
        if (cacheExpString == null || cacheExpString.equals(""))
            cacheExpString = "0";
        try
        {
            cache_max = Integer.parseInt(cacheMaxString);
            cache_expire = Integer.parseInt(cacheExpString);
        }
        catch (NumberFormatException nfe)
        {
            nfe.printStackTrace();
            System.out
                    .println("Error: Bad Number Format for cache!. Cache Dieabled.");
            cache_max = 0;
            cache_expire = 0;
        }
        if (cache_max > 800)
            cache_max = 800;
        if (cache_max < 0)
            cache_max = 0;
        if (cache_expire > 720)
            cache_expire = 720;
        if (cache_expire < 0)
            cache_expire = 0;
        System.out.println("cache size: " + cache_max + " ,, " + "expires : "
                + cache_expire);

        cachepool = new ProxyCachePool(cache_max,cache_expire);

        access_log_name = (String)httpconfig.getProperty("log.access");
        error_log_name = (String)httpconfig.getProperty("log.error");
        log_level = Integer.parseInt((String)httpconfig
                .getProperty("log.loglevel"));

        access_log = new Log(access_log_name);
        error_log = new Log(error_log_name);

        logAccess(_LOG_LEVEL_MAXIMAL,tracer.getSource(),"Console Initialized");
        logError(tracer.getSource(),"Console Initialized");

        return true;
    }

    /**
     * reinitialize when server is restarted
     */
    public boolean re_initialization()
    {
        httpconfig = new Properties();

        try
        {
            FileInputStream fs = new FileInputStream(inifile);
            httpconfig.load(fs);
            fs.close();
        }
        catch (IOException e)
        {
            return false;
        }

        host_addr = (String)httpconfig.getProperty("network.ip");
        host_port = Integer.parseInt((String)httpconfig
                .getProperty("network.port"));

        access_log_name = (String)httpconfig.getProperty("log.access");
        error_log_name = (String)httpconfig.getProperty("log.error");
        log_level = Integer.parseInt((String)httpconfig
                .getProperty("log.loglevel"));

        logAccess(_LOG_LEVEL_MAXIMAL,tracer.getSource(),
                "Console Re-Initialized");

        return true;

    }

    /**
     * handle http setup
     */
    private void handleHTTPSetup()
    {
        webconfigfolder = new WebConfigFolder(this,
                "HTTP Proxy Server Configuration");
    }

    /**
     * get http configuration
     */
    public Properties getHTTPConfiguration()
    {
        return httpconfig;
    }

    /**
     * get proxy cache pool
     */
    public ProxyCachePool getProxyCachePool()
    {
        return cachepool;
    }

    /**
     * show server response in server response window
     */
    public void showServerResponse(String msg)
    {
        if (serverrespwin != null)
            serverrespwin.addMessage(msg);
    }

    /**
     * show client request in client request window
     */
    public void showClientRequest(String msg)
    {
        if (clientreqwin != null)
            clientreqwin.addMessage(msg);
    }

    /**
     * update graphic
     */
    public void update(Graphics g)
    {
        paint(g);
    }

    // the following functions are not used for jproxy
    public void updateHTTPCounter()
    {
    }

    public void drawHTTPChart(Graphics g, Rectangle r, int xn, int yn)
    {
    }

    public void drawGameChart(Graphics g, Rectangle r, int xn, int yn)
    {
    }

    private void drawPolyBar(Graphics g, int xs[], int ys[], int points, int baseY, Color ic, Color bc)
    {
    }

    private void drawPoly3DBar(Graphics g, int xs[], int ys[], int points, int baseY, Color ic, Color bc)
    {
    }

    public void paint(Graphics g)
    {

        Dimension d = size();
        Insets in = insets();
        int client_area_width = d.width - in.right - in.left;
        int client_area_height = d.height - in.bottom - in.top;
        int client_area_x = in.left;
        int client_area_y = in.top;
        int img_width = bkgroundimg.getWidth(this);
        int img_height = bkgroundimg.getHeight(this);

        if (offscrimg == null)
            offscrimg = createImage(size().width,size().height);
        Graphics og = offscrimg.getGraphics();

        if (bkgroundimg != null)
        {
            if (img_width <= 0 || img_height <= 0)
            {
                img_width = bkgroundimg.getWidth(this);
                img_height = bkgroundimg.getHeight(this);
            }

            if (img_width > 0 && img_height > 0)
            {
                og.drawImage(bkgroundimg,0,0,this);
                for (int i = 0; i <= client_area_width / img_width + 1; i++)
                    for (int j = 0; j <= client_area_height / img_height + 1; j++)
                        if (i + j > 0)
                        {
                            og.copyArea(0,0,img_width,img_height,i * img_width,
                                    j * img_height);
                        }
            }
        }
        og.setColor(Color.white);
        og.drawLine(0,0,client_area_width,0);
        g.drawImage(offscrimg,client_area_x,client_area_y,this);
        og.dispose();

        super.paint(g);
    }


    /*
     * public void confirmWebStartSuccess() { ImageDialog dlg = new
     * ImageDialog(this, "Success", null, "Web Server Started!");
     * dlg.setBounds(350, 250, 160, 60); dlg.setVisible(true); } public void
     * confirmWebStopSuccess() { ImageDialog dlg = new ImageDialog(this,
     * "Success", null, "Web Server Stopped!"); dlg.setBounds(350, 250, 160,
     * 60); dlg.setVisible(true); } public void confirmWebStartFailure() {
     * ImageDialog dlg = new ImageDialog(this, "Failed", null, "Web Server Not
     * Started!"); dlg.setBounds(350, 250, 180, 60); dlg.setVisible(true); }
     * public void confirmWebStopFailure() { ImageDialog dlg = new
     * ImageDialog(this, "Failed", null, "Web Server Already Stopped!");
     * dlg.setBounds(350, 250, 200, 60); dlg.setVisible(true); }
     */


    /**
     * start proxy server
     */
    public boolean startProxyServer(boolean doconfirm)
    {


        logAccess(_LOG_LEVEL_MAXIMAL,tracer.getSource(),"starting server on "
                + host_addr + " port: " + host_port);
        if (proxyserver != null && proxyserver.isServerRunning())
            return false;

        proxyserver = new ProxyServer(this,host_addr,host_port);
        if (!proxyserver.startServer())
        {
            logAccess(_LOG_LEVEL_MAXIMAL,tracer.getSource(),
                    "Error: failed to start server.");
            proxyserver = null;
            System.gc();
        }
        else
        {
            mi_start_server.setEnabled(false); // disable start menu item
            ibstart.setEnabled(false); // disable start button
            mi_stop_server.setEnabled(true); // enable stop menu item
            ibstop.setEnabled(true); // enable stop button
            logAccess(_LOG_LEVEL_MAXIMAL,tracer.getSource(),
                    "Server started successfully.");
        }
        return true;
    }

    /**
     * stop proxy server
     */
    public boolean stopProxyServer(boolean doconfirm)
    {

        if (proxyserver == null || !proxyserver.isServerRunning())
        {
            return false;
        }
        proxyserver.stopServer();
        proxyserver.serverExit();
        proxyserver = null;
        System.gc();
        try
        {
            Thread.sleep(300);
        }
        catch (InterruptedException ie)
        {
        }
        mi_start_server.setEnabled(true); // enable start menu item
        ibstart.setEnabled(true); // enable start button
        mi_stop_server.setEnabled(false); // disable stop menu item
        ibstop.setEnabled(false); // disable stop button
        logAccess(_LOG_LEVEL_MAXIMAL,tracer.getSource(),"Web server stopped. ");
        return true;
    }

    /**
     * show server response window
     */
    public boolean showResp()
    {
        if (serverrespwin != null)
            return false; // window exists already
        serverrespwin = new MessageWindow("Server Responses","response.msg");
        serverrespwin.setBounds(new Rectangle(150,100,300,400));
        serverrespwin.setVisible(true);
        ibviewresp.setEnabled(false);
        mi_view_httpmsgs.setEnabled(false);
        serverrespwin.addWindowListener(this);
        return true;
    }

    /**
     * show client request window
     */
    public boolean showReq()
    {
        if (clientreqwin != null)
            return false; // window exists already
        clientreqwin = new MessageWindow("Client Requests","request.msg");
        clientreqwin.setBounds(new Rectangle(150,100,300,400));
        clientreqwin.setVisible(true);
        ibviewreq.setEnabled(false);
        mi_view_gamemsgs.setEnabled(false);
        clientreqwin.addWindowListener(this);
        return true;
    }


    /**
     * show access log window
     */
    public boolean showAccessLog()
    {
        if (access_log_win != null)
            return false; // window exists already
        access_log_win = new MessageWindow("Access Log");
        access_log_win.setSaveEnabled(false);
        access_log_win.setBounds(new Rectangle(150,100,300,400));
        access_log_win.setVisible(true);
        mi_view_access_log.setEnabled(false);
        access_log_win.addWindowListener(this);
        access_log_win.addMessage(FileUtil.readFile(access_log_name));
        return true;
    }

    /**
     * show error log window
     */
    public boolean showErrorLog()
    {
        if (error_log_win != null)
            return false; // window exists already
        error_log_win = new MessageWindow("Error Log");
        error_log_win.setSaveEnabled(false);
        error_log_win.setBounds(new Rectangle(150,100,300,400));
        error_log_win.setVisible(true);
        mi_view_error_log.setEnabled(false);
        error_log_win.addWindowListener(this);
        error_log_win.addMessage(FileUtil.readFile(error_log_name));
        return true;
    }

    /**
     * add access info to log
     */
    public synchronized void logAccess(int level, String source, String message)
    {
        if (log_level >= level)
            access_log.addMessage(source,message);
    }

    /**
     * add error to error log
     */
    public synchronized void logError(String source, String message)
    {
        error_log.addMessage(source,message);
    }

    /**
     * show about dialog box
     */
    public void showAbout()
    {
        about = new AboutDialog(this);
        about.move(150,150);
        about.setVisible(true);
    }

    /**
     * show help windows
     */
    public void showHelp()
    {
        if (helpviewer == null)
        {
            helpviewer = new FileViewer(_HELP_FILE);
            return;
        }
        else
        {
            if (!helpviewer.isVisible())
            {
                helpviewer.setVisible(true);
                helpviewer.toFront();
            }
        }
    }

    /**
     * action event handler
     */
    public void actionPerformed(ActionEvent actevt)
    {

        Object evtsrc = actevt.getSource();
        String actcmd = actevt.getActionCommand();

        // image button events
        if (evtsrc instanceof ImageButton)
        {
            if (evtsrc == ibstart)
                startProxyServer(false);
            else if (evtsrc == ibstop)
                stopProxyServer(false);
            else if (evtsrc == ibexit)
            {
                System.exit(0);
            }
            else if (evtsrc == ibviewresp)
                showResp();
            else if (evtsrc == ibviewreq)
                showReq();
            return;
        }

        // menu item events
        if (evtsrc instanceof MenuItem)
        {
            if (actcmd.equals(_START_SERVER))
                startProxyServer(false);
            else if (actcmd.equals(_SHUTDOWN_SERVER))
                stopProxyServer(false);
            else if (actcmd.equals(_EXIT))
                System.exit(0);
            else if (actcmd.equals(_SERVER_RESPONSES))
                showResp();
            else if (actcmd.equals(_CLIENT_REQUESTS))
                showReq();
            else if (actcmd.equals(_VACCESS_LOG))
                showAccessLog();
            else if (actcmd.equals(_VERROR_LOG))
                showErrorLog();
            else if (actcmd.equals(_SETUP_PROXY_SERVER))
                handleHTTPSetup();
            else if (actcmd.equals(_HELP))
                showHelp();
            else if (actcmd.equals(_ABOUT))
                showAbout();
        }
    }


    /**
     * main
     */
    public static void main(String argv[])
    {
        Jproxy sc = new Jproxy(450,300);
        sc.pack();
        sc.move(150,150);
        sc.validate();
        sc.setVisible(true);
    } // end of main(String argv[])


    /**
     * event handler
     */
    public void processWindowEvent(WindowEvent winevt)
    {
        if (winevt.getID() == Event.WINDOW_DESTROY)
            System.exit(0);
    }

    /**
     * event handler
     */
    public void windowOpened(WindowEvent winevt)
    {
    }

    /**
     * event handler
     */
    public void windowClosing(WindowEvent winevt)
    {
        if (winevt.getWindow() == serverrespwin)
        {
            serverrespwin = null;
            ibviewresp.setEnabled(true);
            mi_view_httpmsgs.setEnabled(true);
        }
        if (winevt.getWindow() == clientreqwin)
        {
            clientreqwin = null;
            ibviewreq.setEnabled(true);
            mi_view_gamemsgs.setEnabled(true);
        }
        if (winevt.getWindow() == access_log_win)
        {
            access_log_win = null;
            mi_view_access_log.setEnabled(true);
        }
        if (winevt.getWindow() == error_log_win)
        {
            error_log_win = null;
            mi_view_error_log.setEnabled(true);
        }

        System.gc(); // force garbage collection
    }

    // various window event handlers
    public void windowClosed(WindowEvent winevt)
    {
    }

    public void windowIconified(WindowEvent winevt)
    {
    }

    public void windowDeiconified(WindowEvent winevt)
    {
    }

    public void windowActivated(WindowEvent winevt)
    {
    }

    public void windowDeactivated(WindowEvent winevt)
    {
    }



    /**
     * mouseClicked event handler
     */
    public void mouseClicked(MouseEvent e)
    {
        if (tbcpt != null)
            tbcpt.hide();
    }

    /**
     * mouseEntered event handler
     */
    public void mouseEntered(MouseEvent e)
    {

        Component comp = e.getComponent();
        Point p = comp.getLocationOnScreen();
        int cx = p.x + 10;
        int cy = p.y + 30;

        // if( tbcpt.isShowing()) return;
        if (comp == ibstart)
            tbcpt.setCaption("Start Server");
        else if (comp == ibstop)
            tbcpt.setCaption("Stop Server");
        else if (comp == ibexit)
            tbcpt.setCaption("Exit");
        else if (comp == ibviewresp)
            tbcpt.setCaption("View Responses");
        else if (comp == ibviewreq)
            tbcpt.setCaption("View Requests");

        tbcpt.setBounds(cx,cy,15,15);
        tbcpt.show();

    }

    /**
     * mouseExited event handler
     */
    public void mouseExited(MouseEvent e)
    {

        if (tbcpt != null)
            tbcpt.hide();

    }

    // mouse event handlers
    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }


} // end of Jproxy


