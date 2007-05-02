/*
 * Created on Sep 4, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.surveysampling.bulkemailer;

import java.io.File;

import junit.framework.TestCase;

/**
 * @author paul
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class BulkEmailerTestCase extends TestCase
{

    /**
     * 
     */
    public BulkEmailerTestCase()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public BulkEmailerTestCase(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    /**
     * DOESN'T WORK ANYMORE
     * @throws Exception 
     */
    public void test() throws Exception
    {
    	File directory = new File("C:\\Documents and Settings\\paul\\My Documents\\projects\\surveymessage\\testcases");
    	String[] filenames = directory.list();
    	
    	for (int j = 0; j < 5; j++)
    	{
    	
    	for (int i = 0; i < filenames.length; i++)
    	{
    		try
    		{
//				BulkEmailerClient.sendFile(new File(directory.getAbsolutePath() + File.separator + filenames[i]));
    		}
    		catch (Throwable t)
    		{
    			System.out.println(filenames[i] + "failed.");
    			t.printStackTrace();
    		}
    		
    	}}
    }

}
