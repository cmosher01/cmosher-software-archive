/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.types.MessageSerializer.InvalidXML;
//import com.ipc.uda.types.old.RecordCallCommandImpl;

/**
 * @author mosherc
 *
 */
public class MessageSerializerTest
{
    private static final MessageSerializer serializer = new MessageSerializer();

    /**
     * Test method for {@link com.ipc.uda.types.MessageSerializer#deserialize(java.io.BufferedReader)}.
     * @throws UnsupportedEncodingException 
     * @throws InvalidXML 
     */
    @Test(expected=InvalidXML.class)
    public void deserEmpty() throws UnsupportedEncodingException, InvalidXML
    {
        final String sXML = "";
        final BufferedReader readXML = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sXML.getBytes("UTF-8"))));
        MessageSerializerTest.serializer.deserialize(readXML);
    }

    /**
     * Test method for {@link com.ipc.uda.types.MessageSerializer#deserialize(java.io.BufferedReader)}.
     * @throws UnsupportedEncodingException 
     * @throws InvalidXML 
     */
    @Test(expected=InvalidXML.class)
    public void deserXMLOK() throws UnsupportedEncodingException, InvalidXML
    {
        final String sXML = "<junk></junk>";
        final BufferedReader readXML = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sXML.getBytes("UTF-8"))));
        MessageSerializerTest.serializer.deserialize(readXML);
    }

    /**
     * Test method for {@link com.ipc.uda.types.MessageSerializer#deserialize(java.io.BufferedReader)}.
     * @throws UnsupportedEncodingException 
     * @throws InvalidXML 
     */
    @Test(expected=InvalidXML.class)
    public void deserBadXML() throws UnsupportedEncodingException, InvalidXML
    {
        final String sXML = "<<junk=>&junk;-->";
        final BufferedReader readXML = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sXML.getBytes("UTF-8"))));
        MessageSerializerTest.serializer.deserialize(readXML);
    }

    /**
     * Test method for {@link com.ipc.uda.types.MessageSerializer#deserialize(java.io.BufferedReader)}.
     * @throws UnsupportedEncodingException 
     * @throws InvalidXML 
     */
    @Test
    public void deserNominal() throws UnsupportedEncodingException, InvalidXML
    {
        final String sXML = "<ipc:udaMessage xmlns:ipc=\"http://www.ipc.com/uda/types\"><ipc:udaRequest><ipc:command><ipc:releaseCall><ipc:handset>RIGHT</ipc:handset></ipc:releaseCall></ipc:command></ipc:udaRequest></ipc:udaMessage>";
        final BufferedReader readXML = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sXML.getBytes("UTF-8"))));
        Executable releaseCallCmdImpl = MessageSerializerTest.serializer.deserialize(readXML);
        assertTrue(releaseCallCmdImpl instanceof ReleaseCallCommandImpl);
    }

    /**
     * Test method for {@link com.ipc.uda.types.MessageSerializer#deserialize(java.io.BufferedReader)}.
     * @throws UnsupportedEncodingException 
     * @throws InvalidXML 
     */
    @Test(expected=InvalidXML.class)
    public void deserNoNamespaces() throws UnsupportedEncodingException, InvalidXML
    {
        final String sXML = "<ipc:uda-message xmlns:ipc=\"http://www.ipc.com/uda/types\"><uda-request><command><record-call><record>true</record></record-call><handset>RIGHT</handset></command></uda-request></ipc:uda-message>";
        final BufferedReader readXML = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sXML.getBytes("UTF-8"))));
        MessageSerializerTest.serializer.deserialize(readXML);
    }
}
