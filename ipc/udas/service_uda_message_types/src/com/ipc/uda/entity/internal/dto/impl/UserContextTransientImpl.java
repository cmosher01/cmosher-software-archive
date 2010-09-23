package com.ipc.uda.entity.internal.dto.impl ;

import com.ipc.ds.base.dto.TransientDTOImpl ;
import com.ipc.uda.entity.dto.UserContextTransient ;
import java.io.Serializable ;
import com.ipc.ds.notification.Publishable ;
import javax.xml.bind.annotation.XmlAccessType ;
import javax.xml.bind.annotation.XmlAccessorType ;
import javax.xml.bind.annotation.XmlRootElement ;
import javax.xml.bind.annotation.XmlType ;
import java.util.Set ;
import com.ipc.uda.service.context.UserContext ;

/**
 * UserContextTransient is a DTO class that represents the state of a UserContextTransient.
 * It is a transient entity, requring an intermediate cache due to its high performance access needs.


 * It is notifiable. Any changes to it are published to the subscribers.
 * 
 * @author DS Elves
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserContextTransient", namespace="http://dunkin.ipc.com/ds/entities")
@XmlRootElement(name = "UserContextTransient", namespace="http://dunkin.ipc.com/ds/entities")
@SuppressWarnings("serial")
public final class UserContextTransientImpl extends TransientDTOImpl implements UserContextTransient, Serializable, Publishable
{
	/**
	 * The ctx field for the entity.
	 *
	 * @see #getCtx()
	 * @see #setCtx(UserContext)
	 */
	private UserContext ctx ;


	/**
	 * Returns the ctx.
	 *
	 * @return ctx
	 */
	public UserContext getCtx()
	{
		return ctx ;
	}

	/**
	 * Sets the ctx.
	 *
	 * @param ctx	The new ctx
	 */
	public void setCtx(UserContext ctx)
	{
		this.ctx=ctx ;
		changeTrackSet.add(CTX) ;

		dirty=true ;
	}


	/**
	 * Returns the changeTrackSet.
	 *
	 * @return changeTrackSet
	 */
	public Set<String> getChangeTrackSet()
	{
		return changeTrackSet ;
	}

	/**
	 * Returns the changeTrackSet as the String of all field names delimited by the given delimiter
	 *
	 * @param delimiter The delimiter that separates the field names in the String returned
	 * @return fieldNamesString All the field names in the Change TrackSet concatenated delimited by the given delimiter.
	 */
	public  String getChangeTrackSetAsString(char delimiter)
	{
		if(getChangeTrackSet().size() == 0)
		{
		return "";
		}

		StringBuffer fieldNames = new StringBuffer() ;

		if(delimiter=='\0')
		{
			delimiter=',' ;
		}

		for(String name : getChangeTrackSet())
		{
			fieldNames.append(name+delimiter) ;
		}

		String fieldNamesString=fieldNames.toString() ;

		if(!fieldNamesString.isEmpty())
		{
			fieldNamesString=fieldNamesString.substring(0,fieldNamesString.length()-1) ;
		}

		return fieldNamesString ;
	}

	/**
	 * Clears the changeTrackSet.
	 */
	public void clearChangeTrackSet()
	{
		getChangeTrackSet().clear() ;
	}

	/**
	 * Returns the id and the ChangeTrackSet of the Entity as a comma separated String.
	 */
	@Override
	public String getEncodingForPublishing()
	{
		return getTransientId()+","+getChangeTrackSetAsString(',');
	}

	/**
	 * Compares this UserContextTransient with the object anObject.
	 *
	 * @return True if the object is of type UserContextTransient and it's id is the same as this entity's id.  It returns false otherwise.
	 */
	public boolean equals(Object anObject)
	{
		if (anObject instanceof UserContextTransient)
		{
			UserContextTransient theUserContextTransient=(UserContextTransient)anObject ;

			if( !((theUserContextTransient.getTransientId() == null) ^ (transientId == null)) )
			{
				if((theUserContextTransient.getTransientId() != null) && (transientId != null))
				{
					return (theUserContextTransient.getTransientId().equals(transientId)) ;
				}
				else
				{
					return true ;				}
			}

		}

		return false ;
	}

	/**
	 * Compares this UserContextTransient with the object anObject.
	 *
	 * @return 0 or 1 if the object is equal to or not equal to the input object respectively.
	 */
	public int compareTo(UserContextTransient theUserContextTransient)
	{
		if((theUserContextTransient.getTransientId() != null) && (transientId != null))
		{
			return transientId.compareTo(theUserContextTransient.getTransientId()) ;
		}
		else
		{
			return equals(theUserContextTransient)?0:-1;
		}
	}

}
