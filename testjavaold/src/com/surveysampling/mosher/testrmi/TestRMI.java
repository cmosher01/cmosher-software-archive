package com.surveysampling.mosher.testrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TestRMI extends Remote
{
    String getMyString() throws RemoteException;
}
