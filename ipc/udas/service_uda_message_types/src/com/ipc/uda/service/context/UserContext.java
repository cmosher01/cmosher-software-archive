/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.context;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.security.BasicSecurityContext;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.internal.manager.base.ContactBaseManager;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.ds.entity.manager.UserManager;
import com.ipc.ds.notification.Registrar;
import com.ipc.ds.notification.Subscription;
import com.ipc.uda.entity.dto.UserContextTransient;
import com.ipc.uda.entity.manager.UserContextTransientManager;
import com.ipc.uda.event.AsyncServletResponseQueue;
import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.service.callproc.CtiCallbackHandler;
import com.ipc.uda.service.context.mbean.UserContextManagerStateHelper;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.ReconnectionTimer;
import com.ipc.uda.service.util.UdaEnvironmentException;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.Event;

// REVIEW 0 add addition authors 

/**
 * Container for objects needed across multiple connections from a user (client). This is the user's
 * "session." For now, the most important object is the event queue for the user.
 * 
 * @author mosherc
 * @author mordarsd
 *
 * 
 */
@XmlTransient
public class UserContext
{
    private static final long INACTIVITY_TIMEOUT = 30 * 1000;

    private final UserID userID;
    private final User dsUser;
    private final Contact dsContact;
    private final String userName;
    private final ReconnectionTimer timer = new ReconnectionTimer(UserContext.INACTIVITY_TIMEOUT);
    private final SecurityContext entityContext;
    private final AsyncServletResponseQueue<Event> qEvents = new AsyncServletResponseQueue<Event>();
    private final CtiCallbackHandler ctiCallbackHandler;
    private final Map<Topic, Subscription> activeSubscriptionList = new HashMap<Topic, Subscription>();
    private String personalExtension;
    
    private UserContextTransient thisTransient;

    private boolean usingMockObjects;
    
    private final Optional<CallContext> callContext;

    // REVIEW 0 add javadocs
    // REVIEW 0 refactor to allow for unit testability
    public UserContext(final UserID user) throws UdaEnvironmentException
    {
        // REVIEW 0 explicitly check for nullity
        this.userID = user;
        this.entityContext = BasicSecurityContext.GetDefaultSecurityContext();
        this.ctiCallbackHandler = new CtiCallbackHandler();
        
        addToTransientCache();
        
        setPersonalExtension();
        
        UserContextManagerStateHelper.addUserID(user);

        this.dsUser = getDsUser(user);
        this.dsContact = getDsContact(this.dsUser);
        this.userName = buildUserName(this.dsContact);
        
        this.callContext = new Optional<CallContext>(new CallContext(this));
    }

    private static User getDsUser(final UserID user)
    {
        final String username = user.getPrincipal().getName();

        User dsuser;
        try
        {
            final UserManager dsUsrMgr = new UserManager(BasicSecurityContext.GetDefaultSecurityContext());
            dsuser = dsUsrMgr.findByLoginNameEqualing(username);
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException(e);
        }
        if (dsuser == null)
        {
            throw new IllegalStateException("Cannot get user from Data Services for loginName: " + username);
        }

        return dsuser;
    }

    private static Contact getDsContact(final User user)
    {
        try
        {
            final ContactBaseManager contactBaseManager = new ContactBaseManager(BasicSecurityContext.GetDefaultSecurityContext());
            return contactBaseManager.getContactFor(user);
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException(e);
        }
    }

    public UdaPrincipal getUser()
    {
        return this.userID.getPrincipal();
    }
    
    public UserID getUserID()
    {
        return this.userID;
    }

    public SecurityContext getSecurityContext()
    {
        return this.entityContext;
    }

    public ReconnectionTimer getTimer()
    {
        return this.timer;
    }

    public AsyncServletResponseQueue<Event> getEventQueue()
    {
        return this.qEvents;
    }
    
    public void initializeCtiCallbackHandler()
    {
        this.ctiCallbackHandler.initialize(this);
    }
    
    public void addDataServiceSubscription(Topic topic, Subscription subscription)
    {
        this.activeSubscriptionList.put(topic, subscription);
    }
    
    public boolean hasSubscription(Topic topic)
    {
    	return this.activeSubscriptionList.containsKey(topic);
    }
    
    public void dispose()
    {
        Log.logger().info("disposing: " + this);
        
        synchronized(this.activeSubscriptionList)
        {
            final Registrar dsRegistrar = Registrar.GetRegistrar();
            for(Subscription sub : this.activeSubscriptionList.values())
            {
                dsRegistrar.deregisterSubscription(sub);
            }
        }
        
        this.ctiCallbackHandler.dispose();
        
        if (this.callContext.exists())
        {
            this.callContext.get().dispose();
        }
        
        this.removeFromTransientCache();
        
        UserContextManagerStateHelper.removeUserID(this.userID);
    }
    
    public void setUsingMockObjects(boolean usingMockObjects)
    {
        this.usingMockObjects = usingMockObjects;
    }
    
    public boolean isUsingMockObjects()
    {
        return this.usingMockObjects;
    }

    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof UserContext))
        {
            return false;
        }
        final UserContext that = (UserContext)object;
        return this.userID.equals(that.userID);
    }

    @Override
    public int hashCode()
    {
        return this.userID.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "UserContext [user=" + this.userID + "]";
    }
    
    public String getCurrentDeviceID()
    {
        return this.userID.getDeviceID();
    }
    
    public CallContext getCallContext()
    {
        if (!this.callContext.exists())
        {
            throw new IllegalStateException(
                    "Unable to get CallContext from UserContext - " +
                    "initCallContext() has not been invoked!");
        }
        return this.callContext.get();
    }
    
    /**
     * Returns the Users personal extension
     * 
     * @return  the Users personal extension  
     */
    public String getPersonalExtension()
    {
        return this.personalExtension;
    }
    
    /**
     * Adds the (transient) wrapper representation of this instance to the transient cache.
     */
    private void addToTransientCache()
    {
        UserContextTransientManager mgr = new UserContextTransientManager(getSecurityContext());
        
        this.thisTransient = UserContextTransientManager.NewUserContextTransient();
        
        // REVIEW 0 should not pass 'this' ref to unknown/untrusted class from within the 
        //          context of the constructor - refactor
        this.thisTransient.setCtx(this);
        try
        {
            mgr.save(this.thisTransient);
        }
        catch (StorageFailureException e)
        {
            // TODO: might want to rethrow this...
            Log.logger().info(
                    "Unable to add UserContextTransient: [" + this + "] to UserContextTransient cache!", e);
        }
        
        
    }
    
    /**
     * Removes the (transient) wrapper representation of this instance from the transient cache.
     */
    private void removeFromTransientCache()
    {
        UserContextTransientManager mgr = new UserContextTransientManager(getSecurityContext());
        try
        {
            mgr.delete(this.thisTransient);
        }
        catch (StorageFailureException e)
        {
            Log.logger().info(
                    "Unable to remove UserContextTransient: [" + this + "] from UserContextTransient cache!", e);

        }        
    }
    
    private void setPersonalExtension() throws UdaEnvironmentException
    {
        try 
        {
            final UserManager userMgr = new UserManager( this.entityContext );
            final UserCDIManager cdiMgr = new UserCDIManager( this.entityContext );
            final ResourceAORManager resAorMgr = new ResourceAORManager( this.entityContext );
            
            final User user = userMgr.findByLoginNameEqualing( this.userID.getPrincipal().getName() );
            if ( user == null )  
            {
                throw new IllegalStateException( "Unable to locate User Entity" );
            }
                
            final UserCDI userCDI = cdiMgr.getUserCDIFor( user );
            final ResourceAOR resAOR = resAorMgr.getPersonalExtensionFor( userCDI );
            
            if ( resAOR == null )
            {
                throw new IllegalStateException( "Unable to locate ResourceAOR Entity" );
            }
            
            this.personalExtension = resAOR.getResourceAOR();
            
            Log.logger().debug( "Setting personalExtension to: [" + 
                                this.personalExtension + "] for UserID: " + this.userID );
        }
        catch (Throwable t)
        {
            throw new UdaEnvironmentException( 
                    "Unable to set personalExtension for UserID: " + this.userID, t );
        }
    }

    /**
     * Gets the human-readable name for this user.
     * @return this user's name
     */
    public String getUserName()
    {
        return this.userName;
    }

    private static String buildUserName(final Contact dsContact)
    {
        return dsContact.getFirstName()+" "+dsContact.getLastName();
    }
}
