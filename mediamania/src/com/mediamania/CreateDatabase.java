package com.mediamania;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

public class CreateDatabase {

public static void main(String[] args)
{
//    CreateDatabase createDB = new CreateDatabase();
//    createDB.create();
    create();
}

public static void create()
{
    try {
        InputStream propertyStream = new FileInputStream("jdo.properties");
        Properties jdoproperties = new Properties();
        jdoproperties.load(propertyStream);
        jdoproperties.put("com.sun.jdori.option.ConnectionCreate", "true");
        PersistenceManagerFactory pmf =
            JDOHelper.getPersistenceManagerFactory(jdoproperties);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        tx.begin();
        tx.commit();
        pm.close();
    } catch(Exception e) {
        System.err.println("Exception creating the database");
        System.err.println(e);
        System.exit(-1);
    }
}
}