/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.event.notification.ds;



import com.ipc.uda.event.notification.ds.NotificationListener.Action;



/**
 * Represents a Data Service Topic
 * 
 * @author mordarsd
 * 
 */
public final class Topic
{

    private final String subsystem;
    private final String entityName;
    private final int entityID;
    private final Action action;

    /**
     * 
     * @param subsystem
     * @param entityName
     * @param entityID
     * @param action
     */
    public Topic(final String subsystem, final String entityName, final int entityID, final Action action)
    {
    	if ((subsystem == null) || (entityName == null) || (action == null))
    	{
    		throw new IllegalArgumentException(
    				"Unable to instantiate an instance of Topic: " +
    				"either subsystem, entityName or action was null!");
    	}
    	
        this.subsystem = subsystem;
        this.entityName = entityName;
        this.entityID = entityID;
        this.action = action;
    }

    /**
     * @return the subsystem
     */
    public String getSubsystem()
    {
        return this.subsystem;
    }

    /**
     * @return the entityName
     */
    public String getEntityName()
    {
        return this.entityName;
    }

    /**
     * @return the entityID
     */
    public int getEntityID()
    {
        return this.entityID;
    }

    /**
     * @return the action
     */
    public Action getAction()
    {
        return this.action;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.subsystem + "." + this.entityName + "." + this.entityID + "." + this.action;
    }

    
    
    
    @Override
	public int hashCode() 
    {
		final int PRIME = 31;

		int result = 1;

		result = PRIME * result + this.action.hashCode();
		result = PRIME * result + this.entityID;
		result = PRIME * result + this.entityName.hashCode();
		result = PRIME * result + this.subsystem.hashCode();

		return result;
	}

	@Override
	public boolean equals(final Object obj) 
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		final Topic other = (Topic) obj;

		if (!this.action.equals(other.action)) return false;
		if (this.entityID != other.entityID) return false;
		if (!this.entityName.equals(other.entityName)) return false;
		if (!this.subsystem.equals(other.subsystem)) return false;
		
		return true;
	}

	/**
     * Creates a Topic from a String
     * 
     * @param topicStr
     * @return Topic The Topic
     */
    public static Topic valueOf(final String topicStr)
    {
        final String[] parts = topicStr.split("[.]");
        if (parts.length != 4)
        {
            throw new IllegalArgumentException(
                "Unable to parse Topic from String: \"" + topicStr + "\"; number of tokens: " + 
                parts.length);
        }

        final int entityID = Integer.parseInt(parts[2]);
        final Action action = Action.valueOf(parts[3]);
        return new Topic(parts[0],parts[1],entityID,action);
    }

}
