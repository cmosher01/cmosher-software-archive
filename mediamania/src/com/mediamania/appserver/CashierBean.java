package com.mediamania.appserver;

import java.util.Date;
import java.util.Iterator;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.mediamania.store.Customer;
import com.mediamania.store.MediaItem;
import com.mediamania.store.Purchase;
import com.mediamania.store.Rental;
import com.mediamania.store.RentalItem;
import com.mediamania.store.StoreQueries;

public class CashierBean implements javax.ejb.SessionBean {
    private javax.ejb.SessionContext context;
    private PersistenceManagerFactory pmf;
    private PersistenceManager pm;
    private String pmfName = "java:comp/env/jdo/MediaManiaPMF";

    /**
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(javax.ejb.SessionContext aContext) {
        context = aContext;
        try {
            Context ic = new InitialContext();
            pmf = (PersistenceManagerFactory)ic.lookup(pmfName);
        } catch (NamingException ex) {
            throw new EJBException("setSessionContext", ex);
        }
    }
    
    public void ejbActivate() {
    }
    public void ejbPassivate() {
    }
    public void ejbRemove() {
    }
    public void ejbCreate() {
    }
    
    public void checkout(
        final java.lang.String lastName, 
        final java.lang.String firstName, 
        final java.util.Collection rentals, 
        final java.util.Collection purchases) 
            throws java.rmi.RemoteException {
        PersistenceManager pm = pmf.getPersistenceManager();
        Customer customer = StoreQueries.getCustomer(pm, firstName, lastName);
        Iterator it = rentals.iterator();
        while (it.hasNext()) {
            RentalValueObject rvo = (RentalValueObject)it.next();
            RentalItem ri = StoreQueries.getRentalItem
                (pm, rvo.serialNumber);
            Rental rental = new Rental(customer, new Date(), ri);
            customer.addTransaction(rental);
            customer.addRental(rental);
        }
        it = purchases.iterator();
        while (it.hasNext()) {
            PurchaseValueObject pvo = (PurchaseValueObject)it.next();
            MediaItem mediaItem = StoreQueries.getMediaItem(
                pm, pvo.title, pvo.format);
            Purchase purchase = new Purchase(customer, new Date(), mediaItem);
            customer.addTransaction(purchase);
        }
        pm.close();
    }
}