package com.mediamania.prototype;

import java.util.Iterator;
import java.util.Set;

import javax.jdo.Transaction;

import com.mediamania.MediaManiaApp;

public class GetActorRoles extends MediaManiaApp {
private String              actorName;

public static void main(String[] args)
{
    GetActorRoles actorRoles = new GetActorRoles(args[0]);
    actorRoles.getRoles();
}

public GetActorRoles(String actor)
{
    actorName = actor;
}

public void getRoles()
{
    Transaction tx = pm.currentTransaction();
    tx.begin();
    Actor actor = PrototypeQueries.getActor(pm, actorName);
    if( actor == null ){
        System.out.print("There is no actor named ");
        System.out.println(actorName);
        tx.rollback();
        return;		
    }
    Set roles = actor.getRoles();
    Iterator iter = roles.iterator();
    while( iter.hasNext() ){
        Role role = (Role) iter.next();
        String roleName = role.getName();
        Movie movie = role.getMovie();
        String title = movie.getTitle();
        System.out.print(title);
        System.out.print(";");
        System.out.println(roleName);		
    }
    tx.commit();
}

public void execute() {
}

}