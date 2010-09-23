/**
 * 
 */
package com.ipc.uda.service.locator;



import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;

import com.ipc.va.cti.DeviceIDImpl;
import com.ipc.va.cti.logicalDevice.AgentIDImpl;
import com.ipc.va.cti.logicalDevice.AgentState;
import com.ipc.va.cti.logicalDevice.CDILoginType;
import com.ipc.va.cti.logicalDevice.services.ejb.LogicalDeviceServicesRemote;
import com.ipc.va.cti.logicalDevice.services.ejb.LogicalDeviceServicesRemoteHome;
import com.ipc.va.cti.logicalDevice.services.extensions.SetAgentStateExtensionsImpl;
import com.ipc.va.cti.logicalDevice.services.results.SetAgentStateResult;



/**
 * @author mordarsd
 * 
 */
public class CtiServiceLocatorTest
{

    @Test
    public void testLookup() throws Exception
    {
        InitialContext ctx = null;
        Hashtable<String, String> env = null;

        LogicalDeviceServicesRemoteHome home = null;
        LogicalDeviceServicesRemote bean = null;
        try
        {
            env = new Hashtable<String, String>(5);
            env.put(Context.PROVIDER_URL,"t3://10.204.52.205:7001");
            env.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
            env.put(Context.SECURITY_PRINCIPAL,"weblogic");
            env.put(Context.SECURITY_CREDENTIALS,"weblogic");
            env.put(Context.SECURITY_AUTHENTICATION,"simple");

            ctx = new InitialContext(env);

            home = (LogicalDeviceServicesRemoteHome)ctx.lookup("com.ipc.va.cti.remote.session.LogicalDeviceServices");
            System.out.println(home);

            bean = home.create();

            SetAgentStateExtensionsImpl sase = new SetAgentStateExtensionsImpl("localhost",CDILoginType.STANDARD);

            SetAgentStateResult agentStateResult = bean.setAgentState(new DeviceIDImpl("00E0A707273F"),AgentState.LOGGED_ON,
                    new AgentIDImpl("MattB"),sase);

            System.out.println(agentStateResult.getResultStatus());

        }
        finally
        {
            if (ctx != null)
            {
                ctx.close();
            }
        }

    }
}
