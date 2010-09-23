/**
 * 
 */
package com.ipc.uda.service.util.jmx;



import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ipc.uda.service.util.UdaEnvironmentException;
import com.ipc.uda.service.util.logging.Log;



/**
 * @author mordarsd
 * 
 */
public final class JMXUtil
{

    private static final String INSTANCE_NAME;
    private static String listenAddress;
    private static int listenPort;
    private static String adminListenAddress;
    private static int adminListenPort;
    private static String nodeManagerUsername;
    private static String nodeManagerPassword;
    private static String domainName;
    private static ObjectName serverRuntimeObjectName;
    private static ObjectName serverObjectName;
    private static ObjectName securityConfigurationObjectName;
    private static MBeanServer runtimeMBeanServer;
    private static MBeanServer domainRuntimeMBeanServer;
    private static MBeanServer localMBeanServer;

    static
    {
        INSTANCE_NAME = System.getProperty("weblogic.Name");
        if (JMXUtil.INSTANCE_NAME == null)
        {
            throw new IllegalStateException("Cannot find WebLogic Name system property. (Maybe we are not running in a WebLogic container?");
        }

        Hashtable<String, String> attrs = new Hashtable<String, String>();
        // Note: Weblogic uses capitalized Type & Name attributes
        attrs.put("Type","ServerRuntime");
        attrs.put("Name",INSTANCE_NAME);

        try
        {
            serverRuntimeObjectName = new ObjectName("com.bea",attrs);
        }
        catch (MalformedObjectNameException e)
        {
            throw new IllegalStateException(e);
        }

        attrs.put("Type","Server");
        // preserve the instance name
        try
        {
            serverObjectName = new ObjectName("com.bea",attrs);
        }
        catch (MalformedObjectNameException e)
        {
            throw new IllegalStateException(e);
        }

        // need to obtain the Server mbean's "Parent" attribute
        MBeanServer rtServer;
        try
        {
            rtServer = getLocalRuntimeMBeanServer();
            Object res = rtServer.getAttribute(serverObjectName,"Parent");
            if (res instanceof ObjectName)
            {
                domainName = ((ObjectName)res).getKeyProperty("Name");
                System.out.println("DomainName: " + domainName);
            }

        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }

        if (domainName != null)
        {
            attrs.put("Type","SecurityConfiguration");
            attrs.put("Name",domainName);
            try
            {
                securityConfigurationObjectName = new ObjectName("com.bea",attrs);
            }
            catch (MalformedObjectNameException e)
            {
                throw new IllegalStateException(e);
            }
        }

    }

    /**
     * 
     * @param domainNamex
     * @param name
     * @param type
     * @param className
     * @param conn
     * @throws IOException 
     * @throws JMException 
     */
    public static void registerMBean(String domainNamex, String name, String type, String className, MBeanServerConnection conn) throws IOException, JMException
    {

        if (conn == null)
        {
            throw new IllegalArgumentException("Unable to unregister MBean - MBeanServerConnection was null!!");
        }

        ObjectName objName = ObjectNameFactory.create(domainNamex,name,type);
        if (!conn.isRegistered(objName))
        {
            System.out.println("Registering MBean: " + objName);
            conn.createMBean(className,objName);
        }

    }

    /**
     * 
     * @param domainNamex
     * @param name
     * @param type
     * @param conn
     * @return
     * @throws Exception
     */
    public static boolean unregisterMBean(String domainNamex, String name, String type, MBeanServerConnection conn) throws IOException, JMException
    {

        if (conn == null)
        {
            throw new IllegalArgumentException("Unable to unregister MBean - MBeanServerConnection was null!!");
        }

        boolean res = false;

        ObjectName objName = ObjectNameFactory.create(domainNamex,name,type);

        if (conn.isRegistered(objName))
        {
            conn.unregisterMBean(objName);
            System.out.println("UnRegistering MBean: " + objName);
        }

        return res;
    }

    public static MBeanServerConnection getMBeanServerConnection(String hostName, int port, String jndiName)
    {

        JMXConnector connector = null;
        MBeanServerConnection conn = null;
        try
        {
            String protocol = "t3";
            String jndiroot = "/jndi/";

            JMXServiceURL serviceURL = new JMXServiceURL(protocol,hostName,port,jndiroot + jndiName);

            String userName = getNodeManagerUsername();
            String password = getNodeManagerPassword();

            Hashtable<String, Object> h = new Hashtable<String, Object>();
            h.put(Context.SECURITY_PRINCIPAL,userName);
            h.put(Context.SECURITY_CREDENTIALS,password);
            h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,"weblogic.management.remote");
            h.put("jmx.remote.x.request.waiting.timeout",new Long(10000));
            connector = JMXConnectorFactory.connect(serviceURL,h);
            conn = connector.getMBeanServerConnection();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 
     * @return
     * @throws IOException 
     */
    public static MBeanServerConnection getAdminPlatformMBeanServerConnection() throws IOException
    {
        MBeanServerConnection conn = null;

        String host = getAdminListenAddress();
        int port = 7091;// 9000;

        JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi");
        JMXConnector c = JMXConnectorFactory.connect(u);
        conn = c.getMBeanServerConnection();

        return conn;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public static MBeanServerConnection getAdminMBeanServerConnection()
    {
        MBeanServerConnection conn = null;
        String host = getAdminListenAddress();
        int port = getAdminListenPort();

        conn = getMBeanServerConnection(host,port,"weblogic.management.mbeanservers.domainruntime");

        return conn;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public static synchronized MBeanServer _getDomainRuntimeMBeanServer()
    {
        if (domainRuntimeMBeanServer == null)
        {
            Properties props = new Properties();

            String userName = getNodeManagerUsername();
            String password = getNodeManagerPassword();

            // Temporary HACK!
            String userNameOverride = System.getProperty("wl.username");
            String passwordOverride = System.getProperty("wl.password");

            // Uncomment for all other deployments for now...
            userNameOverride = "weblogic";
            passwordOverride = "password";

            props.put(Context.SECURITY_PRINCIPAL,userNameOverride != null ? userNameOverride : userName);
            props.put(Context.SECURITY_CREDENTIALS,passwordOverride != null ? passwordOverride : password);
            // props.put( Context.SECURITY_AUTHENTICATION, "none" );
            String providerUrl = "t3://" + getAdminListenAddress() + ":" + getAdminListenPort();
            props.put(Context.PROVIDER_URL,providerUrl);

            domainRuntimeMBeanServer = getMBeanServer("weblogic.management.server",props);
        }

        return domainRuntimeMBeanServer;
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public static synchronized MBeanServer getRuntimeMBeanServer()
    {
        if (JMXUtil.runtimeMBeanServer == null)
        {
            JMXUtil.runtimeMBeanServer = getMBeanServer("weblogic.management.server",null);
        }
        if (JMXUtil.runtimeMBeanServer == null)
        {
            throw new UdaEnvironmentException("cannot find MBeanServer: weblogic.management.server");
        }
        return JMXUtil.runtimeMBeanServer;
    }

    /**
     * 
     * @param jndiName
     * @param props
     * @return
     */
    private static MBeanServer getMBeanServer(String jndiName, Properties props)
    {
        Log.logger().debug("getMBeanServer() with InitialContext Properties: " + props);
        MBeanServer server = null;
        InitialContext ctx = null;

        try
        {
            if (props != null)
            {
                ctx = new InitialContext(props);
            }
            else
            {
                ctx = new InitialContext();
            }

            server = (MBeanServer)ctx.lookup(jndiName);
        }
        catch (NamingException ne)
        {
            Log.logger().debug("Unable to locate MBeanServer with JNDI name: " + jndiName, ne);
        }
        finally
        {
            if (ctx != null)
            {
                try
                {
                    ctx.close();
                }
                catch (NamingException ignored)
                {
                    ignored.printStackTrace();
                }
            }
        }
        return server;

    }

    /**
     * 
     * @return
     */
    public static synchronized MBeanServer getLocalRuntimeMBeanServer()
    {
        if (JMXUtil.localMBeanServer == null)
        {

            // First, try the top-level MBeanServer
            JMXUtil.localMBeanServer = getMBeanServer("java:comp/jmx/runtime",null);
            if (JMXUtil.localMBeanServer == null)
            {
                JMXUtil.localMBeanServer = getMBeanServer("java:comp/env/jmx/runtime",null);
            }
        }

        if (JMXUtil.localMBeanServer == null)
        {
            throw new UdaEnvironmentException("Cannot find any MBean Server");
        }
        return JMXUtil.localMBeanServer;
    }

    /**
     * 
     * @return
     */
    public static synchronized String getAdminListenAddress()
    {
        if (adminListenAddress == null)
        {

            // 
            MBeanServer mbeanServer = null;
            Object portObj = null;
            try
            {
                mbeanServer = getLocalRuntimeMBeanServer();

                portObj = mbeanServer.getAttribute(serverRuntimeObjectName,"AdminServerHost");
                if (portObj != null)
                {
                    adminListenAddress = portObj.toString();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        return adminListenAddress;

    }

    /**
     * 
     * @return
     */
    public static synchronized int getAdminListenPort()
    {
        if (adminListenPort <= 0)
        {
            // AdminServerListenPort
            MBeanServer mbeanServer = null;
            Object portObj = null;
            try
            {
                mbeanServer = getLocalRuntimeMBeanServer();

                portObj = mbeanServer.getAttribute(serverRuntimeObjectName,"AdminServerListenPort");
                if ((portObj != null) && (portObj instanceof Integer))
                {
                    adminListenPort = ((Integer)portObj).intValue();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        return adminListenPort;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Runtime instance helper methods
    // /////////////////////////////////////////////////////////////////////////

    /**
	 * 
	 */
    public static synchronized int getInstanceListenPort()
    {
        if (listenPort <= 0)
        {
            MBeanServer mbeanServer = null;
            Object portObj = null;
            try
            {
                mbeanServer = getLocalRuntimeMBeanServer();

                portObj = mbeanServer.getAttribute(serverRuntimeObjectName,"ListenPort");
                if ((portObj != null) && (portObj instanceof Integer))
                {
                    listenPort = ((Integer)portObj).intValue();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return listenPort;
    }

    /**
     * 
     * @return
     */
    public static synchronized String getInstanceListenAddress()
    {
        if (listenAddress == null)
        {

            MBeanServer mbeanServer = null;
            Object portObj = null;
            try
            {
                mbeanServer = getLocalRuntimeMBeanServer();

                portObj = mbeanServer.getAttribute(serverRuntimeObjectName,"ListenAddress");
                if (portObj != null)
                {
                    listenAddress = portObj.toString();
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return listenAddress;
    }
    
    /**
     * 
     * @return
     */
    public static String getInstanceListenIP()
    {
        String listenIP = JMXUtil.getInstanceListenAddress();
        if (listenIP.indexOf('/') != -1)
        {
            String parts[] = listenIP.split("/");
            if (parts.length == 2)
            {
                listenIP = parts[1];
            }
            else
            {
                listenIP = parts[0];
            }
        }
        return listenIP;
    }

    /**
     * 
     * @return
     */
    private static synchronized String getNodeManagerUsername()
    {
        if (nodeManagerUsername == null)
        {
            MBeanServer rtMbServer = getLocalRuntimeMBeanServer();
            try
            {
                nodeManagerUsername = (String)rtMbServer.getAttribute(securityConfigurationObjectName,"NodeManagerUsername");
                System.out.println("NodeManagerUsername: " + nodeManagerUsername);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return nodeManagerUsername;
    }

    /**
     * 
     * @return
     */
    private static synchronized String getNodeManagerPassword()
    {
        return nodeManagerPassword;
    }
}
