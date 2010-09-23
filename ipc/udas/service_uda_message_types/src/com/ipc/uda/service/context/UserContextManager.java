package com.ipc.uda.service.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.ipc.uda.event.notification.ds.Topic;
import com.ipc.uda.service.callproc.CallConnectionManager;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.LifecycleUtil;
import com.ipc.va.cti.monitoring.MonitorCrossRefID;



/**
 * Manages user contexts (sessions). Use {@link UserContextManager#getInstance getInstance} to get the singleton
 * instance of this class. Handles creation of new contexts, or removal of contexts. Also provides a method to
 * remove inactive sessions. The singleton instance of this class is thread-safe.
 * 
 * @author mosherc
 */
public class UserContextManager
{
    private static final UserContextManager instance = new UserContextManager();

    /**
     * Gets the singleton instance of {@link UserContextManager}
     * @return singleton instance
     */
    public static UserContextManager getInstance()
    {
        return UserContextManager.instance;
    }

    private final ConcurrentMap<UserID,UserContextLazy> userCtxMap = new ConcurrentHashMap<UserID,UserContextLazy>();
    private final ConcurrentMap<String,UserContext> monXRefIdUserCtxMap = new ConcurrentHashMap<String,UserContext>();
    

    private final CallConnectionManager mgrConnection = new CallConnectionManager();


    private UserContextManager()
    {
        // this is a singleton
    }

    /**
     * Removes any inactive sessions from this manager.
     */
    public void removeInactiveSessions()
    {
        final Iterator<UserContextLazy> it = this.userCtxMap.values().iterator();
        while (it.hasNext())
        {
            final UserContextLazy session = it.next();
            if (!session.get().getTimer().isActive())
            {
                Log.logger().info("UDAS session timed out for user: "+session.get().getUser());
                try 
                {
                    LifecycleUtil.logOff(session.get(), false);
                }
                catch (Throwable t)
                {
                    Log.logger().info("Error logging off user: "+session.get().getUser(),t);
                }
                
                session.get().dispose();
                it.remove();
            }
        }
    }

    /**
     * Gets a pre-existing context for the given user, or creates a new one
     * if it does not exist.
     * @param forUser the user to get the context for
     * @return the user's context
     */
    public UserContext getOrCreateContext(final UserID forUser)
    {
        this.userCtxMap.putIfAbsent(forUser, new UserContextLazy(forUser));
        return this.userCtxMap.get(forUser).get();
    }

    /**
     * Gets the {@link UserContext} for a given user (identified by {@link UserID}).
     * @param forUser the {@link UserID} (name and device)
     * @return the user's context, if it exists
     */
    public Optional<UserContext> getUserContext(final UserID forUser)
    {
        if (this.userCtxMap.containsKey(forUser))
        {
            return new Optional<UserContext>(this.userCtxMap.get(forUser).get());
        }
        return new Nothing<UserContext>();
    }
    
    
    /**
     * Gets the {@link UserContext} for the user subscribed using the given
     * {@link MonitorCrossRefID}.
     * @param monXrefID user's {@link MonitorCrossRefID}
     * @return the user's context, if it exists
     */
    public Optional<UserContext> getUserContext(final MonitorCrossRefID monXrefID)
    {
        if (this.monXRefIdUserCtxMap.containsKey(monXrefID.getMonitorCrossRefId()))
        {
            return new Optional<UserContext>(this.monXRefIdUserCtxMap.get(monXrefID.getMonitorCrossRefId()));
        }
        return new Nothing<UserContext>();
    }
    
    /**
     * Adds a mapping of the given {@link MonitorCrossRefID} to the given
     * {@link UserContext} to this manager.
     * @param monXrefID the {@link MonitorCrossRefID}
     * @param userCtx the user
     */
    public void setUserContext(final MonitorCrossRefID monXrefID, final UserContext userCtx)
    {
        Log.logger().debug(
                "UserContextManager.setUserContext(): UserContext=" + userCtx + 
                ", MonitorCrossRefID=" + monXrefID.getMonitorCrossRefId());
        
        this.monXRefIdUserCtxMap.put(monXrefID.getMonitorCrossRefId(), userCtx);
    }

    /**
     * Removes any context for the given user. If no context exists,
     * this method simply returns.
     * @param forUser the user to remove the context for
     */
    public void removeContext(final UserID forUser)
    {
        this.userCtxMap.remove(forUser).get().dispose();
    }
    
    /**
     * Removes the given {@link MonitorCrossRefID} mapping.
     * @param monXrefID {@link MonitorCrossRefID} to remove
     */
    public void removeContext(final MonitorCrossRefID monXrefID)
    {
        this.monXRefIdUserCtxMap.remove(monXrefID.getMonitorCrossRefId());
    }

    /**
     * Returns a live reference (not a copy) of this manager's
     * {@link CallConnectionManager}.
     * @return this manager's {@link CallConnectionManager}
     */
    public CallConnectionManager getConnectionManager()
    {
        return this.mgrConnection;
    }
    
    /**
     * Returns a List of UserContext's that subscribe to the passed in Topic
     * 
     * @param topic	The DataServices topic
     * @return (a copy of) the {@link List} of all subscribers
     */
    public List<UserContext> getDataServicesSubscribers(Topic topic)
    {
    	List<UserContext> subscriberList = new ArrayList<UserContext>();
    	for(UserContextLazy userCtx : this.userCtxMap.values())
    	{
    		if (userCtx.get().hasSubscription(topic))
    		{
    			subscriberList.add(userCtx.get());
    		}
    	}
    	return subscriberList;
    }
}
