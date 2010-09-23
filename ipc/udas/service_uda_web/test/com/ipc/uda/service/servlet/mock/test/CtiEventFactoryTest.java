/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.ipc.uda.service.servlet.mock.CtiEventFactory;
import com.ipc.va.cti.CtiEvent;
import com.ipc.va.cti.lineStatus.events.LineStatusEvent;

/**
 * @author mordarsd
 *
 */
public class CtiEventFactoryTest
{

    /**
     * Test method for {@link com.ipc.uda.service.servlet.mock.CtiEventFactory#create(java.util.Map)}.
     */
    @Test
    public void testCreateMockMonitorEvent()
    {
        
        CtiEvent event = null;
        
        Map<String,Object> args = new HashMap<String,Object>(3);
        args.put("type", new String[] {"com.ipc.uda.service.servlet.mock.event.monitoring.MockMonitorEvent"});
        args.put("notifyToURL", new String[] {"foo"});
        args.put("monitorCrossRefID", new String[] {"123"});
        
        event = CtiEventFactory.create(args);
        
        System.out.println(event);
        
    }
    
    @Test
    public void testCreateMockLineStatusEvent()
    {
        CtiEvent event = null;
        
        final String dialog = "<dialog xmlns=\"urn:ietf:params:xml:ns:dialog-info\"><appearance xmlns=\"urn:ietf:params:xml:ns:sa-dialog-info\">1</appearance><state>trying</state></dialog>";
        final String dialogInfo = "<dialog-info xmlns=\"urn:ietf:params:xml:ns:dialog-info\" entity=\"derek\"></dialog-info>";
        
        Map<String,Object> args = new HashMap<String,Object>(3);
        args.put("type", new String[] {"com.ipc.uda.service.servlet.mock.event.linestatus.MockLineStatusEvent"});
        args.put("dialog", new String[] {dialog});
        args.put("dialogInfo", new String[] {dialogInfo});
        
        event = CtiEventFactory.create(args);
        
        LineStatusEvent lsEvent = (LineStatusEvent)event;
        System.out.println(lsEvent.getDialog().getAppearance());
        
    }

}
