/**
 * @(#)ProxyConnection.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE
 * SUITABILITY OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE
 * LIABLE FOR ANY DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR
 * OWN RISK. Author : Steve Yeong-Ching Hsueh
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;



/**
 * ProxyConnection handles connection from client and opens a new connection to
 * the remote server
 */
public class ProxyConnection extends Thread
{

    static final String _CRLF = "\r\n";

    static final String _CRLF2 = "\r\n\r\n";

    static final String _LF = "\n";

    static final String _LF2 = "\n\n";

    public static final int _LOG_LEVEL_MINIMAL = 2;

    public static final int _LOG_LEVEL_NORMAL = 1;

    public static final int _LOG_LEVEL_MAXIMAL = 0;

    Properties httpconfig = null;

    int _RECV_TIMEOUT = 15 * 1000; // 15 second

    Socket sock_c, sock_s;

    Object console, parent;

    static final boolean debug_mode = false;

    DebugTracer tracer = new DebugTracer();

    Hashtable MIMETypes = null;

    HTTPRequestHeader c_header = null;

    HTTPResponseHeader s_header = null;

    ProxyCachePool cachepool = null;

    ProxyCache pc = null;

    public String proxy_addr = null;

    public int proxy_port;

    boolean using_proxy = false;

    boolean using_cache = false;

    boolean show_content = false;



    /**
     * constructor
     */
    ProxyConnection(Object c, Object p, Socket s)
    {
        console = c;
        parent = p;
        sock_c = s;

        setPriority(NORM_PRIORITY + 1);

        httpconfig = ((ServerInterface)console).getHTTPConfiguration();
        cachepool = ((ServerInterface)console).getProxyCachePool();

        using_cache = cachepool.isCacheEnabled();

        _RECV_TIMEOUT = Integer.parseInt(httpconfig.getProperty("network.receive_timeout")) * 1000;

        proxy_addr = httpconfig.getProperty("network.proxy.ip");
        if (proxy_addr != null)
            proxy_addr = proxy_addr.trim();
        try
        {
            proxy_port = Integer.parseInt(httpconfig.getProperty("network.proxy.port"));
        }
        catch (NumberFormatException ne)
        {
            proxy_port = 0;
        }

        String showcontent_str = httpconfig.getProperty("network.showcontent");
        if (showcontent_str != null)
            show_content = showcontent_str.equals("true") ? true : false;

        if (proxy_addr != null && !proxy_addr.equals("") && proxy_port >= 0)
            using_proxy = true;

        try
        {
            sock_c.setSoTimeout(_RECV_TIMEOUT); // set socket time out
            start();
        }
        catch (SocketException se)
        {
            System.out.println(se);
        }
        catch (OutOfMemoryError oe)
        {
            Runtime r = Runtime.getRuntime();
            System.out.println("Out of Memory!!\nFree memory is: " + r.freeMemory());
            System.out.println("Total memory is: " + r.totalMemory());
            System.gc();
            try
            {
                Thread.sleep(5000L);
            }
            catch (InterruptedException ie)
            {
            }
        }


    } // end of ProxyConnection(Object p, Socket s )

    /**
     * close socket connection
     */
    private boolean closeSock(Socket s)
    {
        try
        {
            s.close();
        }
        catch (IOException err)
        {
            return false;
        }
        return true;
    }

    /**
     * run
     */
    public void run()
    {
        StringBuffer sb = new StringBuffer();
        String key, value;
        InputStream in_c = null; // input from client socket
        InputStream in_s = null; // input from server socket
        OutputStream out_c = null; // output to client socket
        OutputStream out_s = null; // output to server socket

        try
        {
            in_c = sock_c.getInputStream();
            out_c = sock_c.getOutputStream();
        }
        catch (IOException ie)
        {
            ((ServerInterface)console).logError(tracer.getSource(),ie.toString());
            closeSock(sock_c);
            return;
        }

        /***********************************************************************
         * Read Client Request From Socket InputStream in_c
         **********************************************************************/
        byte onebyte[] = new byte[1];
        boolean EOH = false; // flag End Of Header
        try
        {

            while (!EOH)
            {
                if (in_c.read(onebyte) <= 0)
                {
                    EOH = true;
                    continue;
                }
                else
                    sb.append(new String(onebyte,"8859_1"));

                if (sb.toString().endsWith(_CRLF2) || sb.toString().endsWith(_LF2))
                {
                    EOH = true;
                }
            }

        }
        catch (IOException ie)
        {
            ((ServerInterface)console).logError(tracer.getSource(),ie.toString());
            closeSock(sock_c);
            return;
        }

        // System.out.println("\nClient Request: \n" + sb.toString()); // debug
        ((ServerInterface)console).showClientRequest(sb.toString()); // show
                                                                     // message


        c_header = new HTTPRequestHeader(sb.toString());
        String server_adpt = ""; // address and port
        String server_addr = "";
        String doc = "/";
        int server_port = 80;
        int loc = 0;
        if (c_header.URI == null || !c_header.URI.startsWith("http://"))
        {
            closeSock(sock_c);
            return;
        }

        ((ServerInterface)console).logAccess(_LOG_LEVEL_NORMAL,tracer.getSource(),c_header.getPrimeHeader());
        ((ServerInterface)console).logAccess(_LOG_LEVEL_MINIMAL,tracer.getSource(),sb.toString());

        if ((loc = c_header.URI.indexOf("/",8)) <= 0)
        {
            server_adpt = c_header.URI.substring(7);
        }
        else
        {
            server_adpt = c_header.URI.substring(7,loc);
            doc = c_header.URI.substring(loc);
        }

        if ((loc = server_adpt.indexOf(":")) <= 0)
        {
            server_addr = server_adpt;
            server_port = 80;
        }
        else
        {
            server_addr = server_adpt.substring(0,loc);
            server_port = Integer.parseInt(server_adpt.substring(loc + 1));
        }

        // TODO: lookup server_addr in hosts file
        String hostIP = ((Jproxy)(((ProxyServer)(this.parent)).console)).hosts.lookupHost(server_addr);
        if (hostIP.length() > 0)
        {
            server_addr = hostIP;
            this.using_proxy = false;
        }

        System.out.println(server_adpt);
        System.out.println(server_addr + " ,, " + server_port);
        System.out.println(doc);

        /***********************************************************************
         * Check if request is in cache(for GET only)
         **********************************************************************/
        if (using_cache && c_header.Method.equals("GET"))
        {

            pc = cachepool.getCache(c_header.URI);
            if (pc == null)
            {
                // Not in Cache
            }
            else
            {
                // get header and content from cache
                // then send it back to client
                byte header[] = pc.getHeader();
                byte content[] = pc.getContent();
                try
                {
                    out_c.write(header); //, 0, header.length);
                    //System.out.println("Using Cache " + c_header.URI);
                    //System.out.println("Header:["+ new String(header)+"] " +
                    // content.length);
                    if (content != null)
                    {
                        out_c.write(content); //, 0, content.length);
                    }
                    out_c.flush();
                }
                catch (IOException e)
                {
                    System.out.println(e.toString());
                }
                closeSock(sock_c); // close connection to client
                return;
            }
        }

        /***********************************************************************
         * Open a socket connection to remote server
         **********************************************************************/
        byte resp[] = new byte[2048];
        int content_len = 0;
        int total_len = 0;
        int len = 0;

        content_len = c_header.getContentLength(); // get content length from
                                                   // client request

        EOH = false; // flag End Of Header
        try
        {

            if (using_proxy)
            { // connect to another proxy server if using proxy
                sock_s = new Socket(proxy_addr,proxy_port);
                sock_s.setSoTimeout(_RECV_TIMEOUT); // set socket time out
                in_s = sock_s.getInputStream();
                out_s = sock_s.getOutputStream();
            }
            else
            { // connect directly to remote web server

                // open a socket to connect to server
                sock_s = new Socket(server_addr,server_port);
                sock_s.setSoTimeout(_RECV_TIMEOUT); // set socket time out
                // System.out.println("connected to : " + server_addr + " ,, " +
                // server_port );
                in_s = sock_s.getInputStream();
                out_s = sock_s.getOutputStream();

                // create the message to send to server
                sb = new StringBuffer();
                sb.append(c_header.Method).append(" ").append(doc).append(" ").append(c_header.Version).append(_CRLF);
                Map ht = c_header.getHeaderFields();
                for (Iterator enu = ht.keySet().iterator(); enu.hasNext();)
                {
                    key = (String)enu.next();
                    value = (String)ht.get(key);
                    if (key.equalsIgnoreCase("Proxy-Connection"))
                        continue; // bypass the proxy connection row
                    sb.append(key).append(": ").append(value).append(_CRLF);
                }
                sb.append(_CRLF);
            }
            // System.out.println("sending : \n" + sb.toString());

            // write header message to server
            out_s.write(sb.toString().getBytes(),0,sb.toString().length());
            out_s.flush();

            // write content body to server if there is any
            if (content_len > 0)
            {
                while (content_len > total_len)
                {
                    if ((len = in_c.read(resp)) <= 0)
                        break;
                    total_len += len;

                    // determine if need to show content
                    if (show_content)
                    {
                        if (byteArrayOperator.isPrintable(resp,len))
                        {
                            ((ServerInterface)console).showClientRequest(new String(resp,0,len));
                        }
                    }

                    out_s.write(resp,0,len);
                    out_s.flush();
                }
            }


            sb = new StringBuffer();


            /*******************************************************************
             * Read Server Response Header
             ******************************************************************/
            while (!EOH)
            {
                if (in_s.read(onebyte) < 0)
                {
                    EOH = true;
                    continue;
                }
                else
                    sb.append(new String(onebyte,"8859_1"));

                if (sb.toString().endsWith(_CRLF2) || sb.toString().endsWith(_LF2))
                {
                    EOH = true;
                }
            }


            // System.out.println("\nserver response \n" + sb.toString());
            ((ServerInterface)console).showServerResponse(sb.toString());

            // send server response header
            out_c.write(sb.toString().getBytes(),0,sb.toString().length());
            out_c.flush();

            // read server response body
            s_header = new HTTPResponseHeader(sb.toString());

            // logging
            ((ServerInterface)console).logAccess(_LOG_LEVEL_NORMAL,tracer.getSource(),s_header.getPrimeHeader());
            ((ServerInterface)console).logAccess(_LOG_LEVEL_MINIMAL,tracer.getSource(),sb.toString());

            content_len = 0;
            total_len = 0;
            len = 0;

            /*******************************************************************
             * Read Server Response Content
             ******************************************************************/
            content_len = s_header.getContentLength();
            byte resp_content[] = null;
            if (content_len > 0)
            {
                resp_content = new byte[content_len];
                int last_byte = 0;

                while (content_len > total_len)
                {
                    if ((len = in_s.read(resp)) <= 0)
                        break;

                    if (using_cache || show_content)
                    { // save content if using cache
                        byteArrayOperator.copy(resp_content,total_len,resp,len);
                    }

                    total_len += len;
                    out_c.write(resp,0,len);
                    out_c.flush();
                }

            }
            else
            { // if content length is not set

                StringBuffer resp_sb = new StringBuffer();
                while ((len = in_s.read(resp)) >= 0)
                {
                    out_c.write(resp,0,len);
                    out_c.flush();

                    if (using_cache || show_content)
                    { // save content if using cache
                        resp_sb.append(new String(resp,0,len,"8859_2"));
                    }

                }

                if (using_cache || show_content)
                    resp_content = resp_sb.toString().getBytes("8859_2");
            }

            // determine if need to show content
            if (show_content)
            {
                if (byteArrayOperator.isPrintable(resp_content))
                {
                    ((ServerInterface)console).showServerResponse(new String(resp_content));
                }
            }

            /*******************************************************************
             * Add Content to Cache
             ******************************************************************/
            if (using_cache && c_header.Method.equals("GET"))
            {

                // add url, header, content to cachepool
                cachepool.setCache(c_header.URI,s_header.toString(),resp_content);
            }

        }
        catch (SocketException se)
        {
            ((ServerInterface)console).logError("Error: SocketException, ",c_header.URI);
            ((ServerInterface)console).logError(tracer.getSource(),se.toString());
            System.out.println(c_header.URI + " " + se);
        }
        catch (IOException ie)
        {
            ((ServerInterface)console).logError("Error: IOException, ",c_header.URI);
            ((ServerInterface)console).logError(tracer.getSource(),ie.toString());
            // ie.printStackTrace();
            System.out.println(c_header.URI + " " + ie);
        }

        closeSock(sock_c); // close connection to client

    } // end of run()



} // end of ProxyConnection
