package com.surveysampling.mosher.testrmi;

import java.net.MalformedURLException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationGroup;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.UnknownGroupException;
import java.util.Properties;

public class TestRMISetup
{
    public TestRMISetup() {}

    public static void main(String args[])
        throws RemoteException, MalformedURLException, UnknownGroupException, ActivationException
    {
        if (System.getSecurityManager() == null)
            System.setSecurityManager(new RMISecurityManager());

        //String policy = "c:\\policy";

        // The "location" String specifies a URL from where the class
        // definition will come when this object is requested (activated).
        String location = "http://63.119.50.184/testrmi/";
        System.setProperty("java.rmi.server.codebase",location);

        Properties props = new Properties(); 
        //props.put("java.security.policy",policy);
        ActivationGroupDesc.CommandEnvironment ace = null; 
        ActivationGroupDesc exampleGroup = new ActivationGroupDesc(props,ace);

        // Once the ActivationGroupDesc has been created, register it 
        // with the activation system to obtain its ID
        ActivationGroupID agi = ActivationGroup.getSystem().registerGroup(exampleGroup);


        // Create the rest of the parameters that will be passed to
        // the ActivationDesc constructor
        MarshalledObject data = null;

        // The location argument to the ActivationDesc constructor will be used 
        // to uniquely identify this class; it's location is relative to the  
        // URL-formatted String, location.
        ActivationDesc desc = new ActivationDesc(agi,"com.surveysampling.mosher.testrmi.TestRMIImpl",location,data);

        // Register with rmid
        TestRMI mri = (TestRMI)Activatable.register(desc);

        // Bind the stub to a name in the registry running on 1099
        Naming.rebind("TestRMI",mri);
    }

/*
To set this whole system up:
first, stop any rmid or rmiregistry already running, before building

compile the server sources:
javac -classpath c:\projects\java TestRMI.java (creates TestRMI.class)
javac -classpath c:\projects\java TestRMIImpl.java (needs TestRMI.class) (creates TestRMIImpl.class)
rmic -v1.2 -classpath c:\projects\java -d c:\projects\java com.surveysampling.mosher.testrmi.TestRMIImpl
  (needs TestRMIImpl.class, TestRMI.class) (creates TestRMIImpl_Stub.class)
javac -classpath c:\projects\java TestRMISetup.java (needs TestRMI.class) (creates TestRMISetup.class)

deploy the server:
copy to web folder: TestRMI.class, TestRMIImpl_Stub.class

compile the client:
javac -classpath c:\projects\java TestRMIClient.java (needs TestRMI.class) (creates TestRMIClient.class)

these two commands are in a different shell, or are run int he background:
[in shell 1, or background]
    set CLASSPATH=
    rmiregistry  (note: be sure to run the one from the latest jre, not vcafe's)
[in shell 2, or background]
    rmid -J-Djava.security.policy=c:\policy (needs policy file)

this registers the server in the rmid and rmiregistry:
java -classpath c:\projects\java -Djava.security.policy=c:\policy com.surveysampling.mosher.testrmi.TestRMISetup
    (needs policy file, TestRMISetup.class,TestRMI.class,TestRMIImpl.class,TestRMIImpl_Stub.class, and (from web folder:
    TestRMIImpl_Stub.class,TestRMI.class)

[on client:]
    deploy the client:
    copy to client: TestRMI.class, TestRMIClient.class

    run the client:
    java -Djava.security.policy=policy com.surveysampling.mosher.testrmiclient.TestRMIClient
      (needs policy file, and access to web server)


here is a policy file that grants everything to everyone:
grant
{
	permission java.security.AllPermission;
};

here is another policy file that I was able to get to work:
grant
{
    permission java.net.SocketPermission "*:1024-65535", "connect,accept";
    permission java.net.SocketPermission "*:80", "connect";
    permission com.sun.rmi.rmid.ExecOptionPermission "-Djava.security.policy=c:\\policy";
};
grant codebase "file:///c:/projects/java/"
{
    permission java.util.PropertyPermission "java.rmi.server.codebase", "write";
};

or use this as the (server and client) user's .java.policy file:
grant
{
    permission java.net.SocketPermission "*:1024-65535", "connect,accept";
    permission java.net.SocketPermission "*:80", "connect";
    permission java.util.PropertyPermission "java.rmi.server.codebase", "write";
};
and then you can omit all -Djava.security.policy= switches from the above commands
*/
}
