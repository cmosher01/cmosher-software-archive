package com.surveysampling.mosher.testrmi;

import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationID;
import java.rmi.MarshalledObject;

public class TestRMIImpl extends Activatable implements TestRMI
{
    public TestRMIImpl(ActivationID id, MarshalledObject data) throws RemoteException
    {
        super(id,0);
        MarshalledObject unused = data; data = unused;
    }

    public String getMyString() throws RemoteException
    {
/*		System.out.println("Threads:");
		Thread thread[] = findAllThreads();
		for (int i = 0; i < thread.length; ++i)
		{
			Thread t = thread[i];
			System.out.println(t.getName());
		}
*/
        return "My string.";
    }

	public static Thread[] findAllThreads()
	{
		ThreadGroup group = Thread.currentThread().getThreadGroup();

		ThreadGroup topGroup = group;

		// traverse the ThreadGroup tree to the top
		while (group != null)
		{
			topGroup = group;
			group = group.getParent();
		}

		// Create a destination array that is about
		// twice as big as needed to be very confident
		// that none are clipped.
		int estimatedSize = topGroup.activeCount() * 2;
		Thread[] slackList = new Thread[estimatedSize];

		// Load the thread references into the oversized
		// array. The actual number of threads loaded 
		// is returned.
		int actualSize = topGroup.enumerate(slackList);

		// copy into a list that is the exact size
		Thread[] list = new Thread[actualSize];
		System.arraycopy(slackList, 0, list, 0, actualSize);

		return list;
	}
}
