package com.mediamania;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public abstract class MediaManiaApp {
    protected PersistenceManagerFactory pmf;
    protected PersistenceManager        pm;
    protected Transaction               tx;

    protected MediaManiaApp()
    {
        try {
            InputStream propertyStream = new FileInputStream("jdo.properties");
            Properties jdoproperties = new Properties();
            jdoproperties.load(propertyStream);
            jdoproperties.putAll(getPropertyOverrides());
            pmf = JDOHelper.getPersistenceManagerFactory(jdoproperties);
            pm = pmf.getPersistenceManager();
            tx = pm.currentTransaction();
        } catch(Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
    }
    protected static Map getPropertyOverrides()
    {
        return new HashMap();
    }    
    public PersistenceManager getPersistenceManager()
    {
        return pm;
    }
    public abstract void execute();

    public void executeTransaction()
    {
        try {
            tx.begin();
            execute();
            tx.commit();
        } catch (Throwable exception){
            exception.printStackTrace(System.err);
            if (tx.isActive()) tx.rollback();
        }
    }
}