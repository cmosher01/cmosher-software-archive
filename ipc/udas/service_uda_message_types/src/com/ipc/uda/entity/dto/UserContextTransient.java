package com.ipc.uda.entity.dto ;

import com.ipc.uda.service.context.UserContext ;

/**
 * UserContextTransient is an interface for the DTO class that represents the state of a UserContextTransient.
 * UserContextTransient is a transient entity, requring an intermediate cache due to its high performance access needs.
This entity must be explicitly deleted when the HTTP or SIP session goes away or it will litter the cache with an unreachable instance.
 * 
 * <p>The <code>UserContextTransient</code> entity has the following fields:
 * <ul><table border>
 * <tr><th>Name</th><th>Type</th><th>Notes</th></tr>
 * <tr>
 * <td>ctx</td>
 * <td>UserContext</td>
 * <td></td>
 * </tr>
 * </table></ul>
 * 
 * @author DS Elves
 * 
 */
public interface UserContextTransient extends Comparable<UserContextTransient>
{
	/**
	 * The Constants are basically All Caps version of the field name for now.
	 *
	 */
	public static final String CTX = "ctx";

	/**
	 * Returns the id.
	 *
	 * @return id
	 */
	public String getTransientId() ;

	/**
	 * Returns the ctx.
	 *
	 * @return ctx
	 */
	public UserContext getCtx() ;

	/**
	 * Sets the ctx.
	 *
	 * @param ctx	The new ctx
	 */
	public void setCtx(UserContext ctx) ;

	/**
	 * Compares this UserContextTransient with the object anObject.
	 *
	 * @return True if the object is of type UserContextTransient and it's id is the same as this entity's id.  It returns false otherwise.
	 */
	public boolean equals(Object anObject) ;

}
