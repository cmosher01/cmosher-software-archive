package com.surveysampling.mosher.testrmiclient;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import com.surveysampling.mosher.testrmi.TestRMI;

public class TestRMIClient
{
    public static void main(String[] rArg)
        throws RemoteException, MalformedURLException, NotBoundException
    {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());
        TestRMI obj = (TestRMI)Naming.lookup("//63.119.50.184/TestRMI"); 
        String message = obj.getMyString();
        System.out.println(message);
    }
}
