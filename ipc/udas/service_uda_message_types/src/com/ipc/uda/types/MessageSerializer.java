/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.Returnable;
import com.ipc.uda.service.util.logging.Log;



/**
 * Serializes and deserializes between UdaMessageType objects and uda-message XML.
 * Objects of this class are thread-safe (they have no internal state).
 * 
 * @author mordarsd
 * @author mosherc
 * 
 */
public class MessageSerializer
{
    /**
     * Indicates that invalid XML was attempted to
     * be deserialized.
     * @author mosherc
     */
    public static class InvalidXML extends Exception
    {
        private InvalidXML() { /* */ }
        private InvalidXML(Throwable e) { super(e); }
    }





    /**
     * Reads XML from the given {@link BufferedReader} and deserializes it. It assumes
     * that the XML is in <code>udaMessage</code> format, and furthermore that it contains
     * either a <code>udaCommand</code> or <code>udaQuery</code>. It then constructs the
     * corresponding concrete implementation of {@link ExecutableWithContext}.
     * @param in the {@link BufferedReader} to read XML from
     * @return the corresponding concrete implementation of {@link Executable}
     * @throws InvalidXML if the XML is invalid, or if any other exceptions occur
     */
    @SuppressWarnings("synthetic-access")
    public ExecutableWithContext deserialize(final BufferedReader in) throws InvalidXML
    {
        try
        {
            return tryDeserialize(in);
        }
        catch (final Throwable e)
        {
            // Assume all exceptions are user errors.
            throw new InvalidXML(e);
        }
    }



    private ExecutableWithContext tryDeserialize(final BufferedReader in) throws JAXBException, ClassCastException, ParserConfigurationException, SAXException
    {
        final SAXSource source = createSaxParser(in);

        final Unmarshaller unmarshaller = createUnmarshaller();

        final JAXBElement<UdaMessageType> req = unmarshaller.unmarshal(source,UdaMessageType.class);

        if (Log.logger().isDebugEnabled())
        {
            final Marshaller marshaller = getJAXBContext().createMarshaller();
            final StringWriter message = new StringWriter();
            marshaller.marshal(req,message);
            Log.logger().debug(message.toString());
        }

        return getExecutable(req);
    }



    private SAXSource createSaxParser(final BufferedReader in) throws ParserConfigurationException, SAXException
    {
        final SAXParserFactory spf = SAXParserFactory.newInstance(SAXParserFactoryImpl.class.getName(),UdaMessageType.class.getClassLoader());
        spf.setNamespaceAware(true);
        spf.setValidating(false);

        final SAXParser saxParser = spf.newSAXParser();

        final XMLReader xmlReader = saxParser.getXMLReader();

        return new SAXSource(xmlReader, new InputSource(in));
    }



    private Unmarshaller createUnmarshaller() throws JAXBException
    {
        final Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();

        unmarshaller.setEventHandler(new ValidationEventHandler()
        {
            /**
             * Throws an exception if the event is of severity "error" or "fatal"
             * (when there is a problem trying to unmarshal the XML)
             */
            @Override
            public boolean handleEvent(ValidationEvent event)
            {
                final int sev = event.getSeverity();
                if (sev == ValidationEvent.FATAL_ERROR || sev == ValidationEvent.ERROR)
                {
                    throw new IllegalStateException(new JAXBException(event.getMessage()));
                }
                return true;
            }
        });

        return unmarshaller;
    }

    private ExecutableWithContext getExecutable(final JAXBElement<UdaMessageType> req) throws JAXBException
    {
        final UdaRequest udaRequest = (UdaRequest)req.getValue().getUdaRequest();
        if (udaRequest == null)
        {
            throw new JAXBException("Could not parse request XML.");
        }
        return udaRequest.getExecutable();
    }





    /**
     * Takes the given {@link Returnable}, wraps it in a {@link UdaResponseType},
     * wraps that in a {@link UdaMessageType},
     * and marshals that to the given {@link BufferedWriter}.
     * 
     * @param ret the {@link Returnable} to serialize
     * @param out the {@link BufferedWriter} to serialize <code>ret</code> to
     */
    public void serialize(final Returnable ret, final BufferedWriter out)
    {
        try
        {
            trySerialize(ret,out);
        }
        catch (final Throwable e)
        {
            // Assume all exceptions are programming errors.
            throw new IllegalStateException(e);
        }
    }

    private void trySerialize(final Returnable ret, final BufferedWriter out) throws JAXBException
    {
        final JAXBElement<UdaMessageType> resp = createJAXBElement(ret);

        final Marshaller marshaller = getJAXBContext().createMarshaller();

        marshaller.marshal(resp,out);

        if (Log.logger().isDebugEnabled())
        {
            final StringWriter message = new StringWriter();
            marshaller.marshal(resp,message);
            Log.logger().debug(message.toString());
        }
    }

    private JAXBElement<UdaMessageType> createJAXBElement(final Returnable ret)
    {
        final ObjectFactory factoryObject = new ObjectFactory();

        // create the UdaResponseType wrapper
        final UdaResponse resp = (UdaResponse)factoryObject.createUdaResponseType();
        resp.setReturnable(ret);

        final UdaMessageType msg = factoryObject.createUdaMessageType();
        msg.setUdaResponse(resp);

        return factoryObject.createUdaMessage(msg);
    }





    private JAXBContext getJAXBContext() throws JAXBException
    {
        return getJAXBContextForClass(UdaMessageType.class);
    }

    @SuppressWarnings("unchecked")
    private JAXBContext getJAXBContextForClass(final Class cls) throws JAXBException
    {
        return JAXBContext.newInstance(cls.getPackage().getName(),cls.getClassLoader());
    }
}
