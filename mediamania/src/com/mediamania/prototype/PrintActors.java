package com.mediamania.prototype;

import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.Transaction;

import com.mediamania.MediaManiaApp;

public class PrintActors extends MediaManiaApp {

public static void main(String[] args)
{
    PrintActors actors = new PrintActors();
    actors.print();
}

public void print()
{
    Transaction tx = pm.currentTransaction();
    tx.begin();
    Extent extent = pm.getExtent(Actor.class, true);
    Iterator iter = extent.iterator();
    while( iter.hasNext() ){
        Actor actor = (Actor) iter.next();
        System.out.println(actor.getName());
    }
    extent.close(iter);
    tx.commit();
}

public void execute() {
}

}