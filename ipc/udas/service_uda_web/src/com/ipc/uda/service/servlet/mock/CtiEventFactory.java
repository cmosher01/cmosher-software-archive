/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.ipc.va.cti.ConnectionID;
import com.ipc.va.cti.ConnectionIDImpl;
import com.ipc.va.cti.CtiEvent;
import com.ipc.va.cti.DeviceID;
import com.ipc.va.cti.DeviceIDImpl;
import com.ipc.va.cti.UserData;
import com.ipc.va.cti.UserDataImpl;
import com.ipc.va.cti.logicalDevice.AgentID;
import com.ipc.va.cti.logicalDevice.AgentIDImpl;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;
import com.ipc.va.cti.monitoring.MonitorCrossRefIDImpl;
import com.ipc.va.dialog.Dialog;
import com.ipc.va.dialog.DialogInfo;

/**
 * @author mordarsd
 *
 */
public final class CtiEventFactory
{

    private static final Map<String, Class> CLASS_IMPL_MAP = new HashMap<String, Class>();
    
    
    
    static
    {
        CLASS_IMPL_MAP.put(MonitorCrossRefID.class.getName(), MonitorCrossRefIDImpl.class);
        CLASS_IMPL_MAP.put(ConnectionID.class.getName(), ConnectionIDImpl.class);
        CLASS_IMPL_MAP.put(UserData.class.getName(), UserDataImpl.class);
        CLASS_IMPL_MAP.put(DeviceID.class.getName(), DeviceIDImpl.class);
        CLASS_IMPL_MAP.put(AgentID.class.getName(), AgentIDImpl.class);
        CLASS_IMPL_MAP.put(Dialog.class.getName(), Dialog.class);
        CLASS_IMPL_MAP.put(DialogInfo.class.getName(), DialogInfo.class);
        
    }
    
    public static CtiEvent create(Map<String,Object> args)
    {
        CtiEvent event = null;
    
        String typeObj[] = (String[])args.get("type");
        
        String type = typeObj[0];
        Method[] methods = null;
        try
        {
            event = (CtiEvent)Class.forName(type).newInstance();
            // we want inherited methods as well
            methods = event.getClass().getMethods();

            CtiEventFactory.setProperties(methods, event, args);

            
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
        
        
        return event;
    }
    

    
    private static void setProperties(Method[] methods, Object target, Map<String, Object> args)
    {
        
        // iterate over all of the available methods
        for(Method method : methods)
        {
            final String name = method.getName();
            if (name.startsWith("set")) {
                char[] ckey = name.substring(3).toCharArray();
                ckey[0] = Character.toLowerCase(ckey[0]);
                String key = new String(ckey);
                if(args.containsKey(key)) 
                {
                    Object value = null;
                    
                    Class<?>[] params = method.getParameterTypes();
                    if (params.length == 1)
                    {
                        // check for known types
                        // since most of these are probably interfaces, we cannot simply locate
                        // and instantiate them via a constructor
                        String strVal = ((String[])args.get(key))[0];
                        value = CtiEventFactory.getValue(params[0], strVal);
                        
                        if (value != null)
                        {
                            try
                            {
                                method.invoke(target, value);
                            } 
                            catch(Exception e)
                            {
                              System.err.printf(e.getMessage());
                            }
                        }
                    }
                    else
                    {
                        System.err.printf(
                                "Unable to invoke setter for property: %s, too many params found in method sig: %d\n",
                                key, params.length);            
                    }
                }
                else
                {
                    System.out.println(
                            "No value found for property: " + key);
                    
                }
            }
            
        }
        
    }

    private static Object getValue(Class clazz, String str)
    {
        Object value = null;

        // just a String, return as is
        // TODO: check primitives as well...
        if (clazz == String.class)
        {
            value = str;
        }
        // is it an enum type
        else if (clazz.isEnum())
        {
            value = Enum.valueOf(clazz, str);
        }
        // is it in our class map?
        else if (CLASS_IMPL_MAP.containsKey(clazz.getName()))
        {
            Class<?> impl = CLASS_IMPL_MAP.get(clazz.getName());
            // get the constructor that takes a String
            try
            {
                if (str.startsWith("<") && str.endsWith(">"))
                {
                    JAXBContext jaxbContext = 
                        JAXBContext.newInstance(clazz.getPackage().getName(), clazz.getClassLoader());
                    final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    value = unmarshaller.unmarshal(new ByteArrayInputStream(str.getBytes()));
                    
                }
                else
                {
                    Constructor<?> ctor = impl.getConstructor(String.class);
                    value = ctor.newInstance(str);
                }
            }
            catch (Exception e)
            {
                throw new IllegalStateException(e);
            }
        }
        else
        {
            System.err.printf(
                    "Unable to getValue for Class: %s from String: %s\n", clazz, str);
        }
        
        
        return value;
    }
    
    
}
