package com.ipc.uda.service.wsagent.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ipc.diag.wsagent.xml.LogMessageDumpType;
import com.ipc.diag.wsagent.xml.ObjectFactory;
import com.ipc.diag.wsagent.xml.TestParamsType;
import com.ipc.diag.wsagent.xml.TestType;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class DiagTestWebComponent
{
    private static final String BASE_URL = "http://localhost:7001/uda/diag";
    private static final GenericType<JAXBElement<TestType>> GENERIC_TYPE_LOGBUFFER = new GenericType<JAXBElement<TestType>>()
    {
    };
    private static final GenericType<String> GENERIC_TYPE_STRING = new GenericType<String>()
    {
    };

    private Client client = Client.create();

    /** Resets log buffer messageDump */
    @Before
    @Ignore
    public void setUp()
    {
        WebResource resource = this.client.resource(BASE_URL + "");
        TestType messageDump = new TestType();
        TestParamsType testp = new TestParamsType();
        testp.setName("name");
//        testp.setParams("param");
        messageDump.setJobId(1);
        messageDump.getTest().add(testp);
        
       //resource.type("application/xml").put(new ObjectFactory().createTest(messageDump) );
    }
    
    @Test
    public void testGetVersion()
    {
        WebResource resource = this.client.resource(BASE_URL + "/test");
        //resource.type("text/plain").put(WebResource.class,"bb");
        resource.path("/test").type(MediaType.APPLICATION_XML);

		//assertEquals("Expected Client Response Code", 200, clientResponse
				//.getStatus()) ;
		
       // JAXBElement<TestType> response = resource.type(MediaType.APPLICATION_XML).get(GENERIC_TYPE_LOGBUFFER);
      
       
        //assertEquals("", response.getValue());
    }
    
   

   
}
