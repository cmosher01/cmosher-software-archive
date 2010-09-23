package com.ipc.uda.entity.manager ;

import com.ipc.uda.entity.internal.manager.base.UserContextTransientBaseManager ;
import com.ipc.ds.base.security.SecurityContext ;
import com.ipc.uda.entity.dto.UserContextTransient ;

/**
 * UserContextTransientManager comprises the contract API implementation for operations over UserContextTransient entities.
 * 
 * @author DS Elves
 * 
 * @see UserContextTransient
 * 
 */
public final class UserContextTransientManager extends UserContextTransientBaseManager
{
	/**
	 * This is the primary constructor.
	 *
	 * @param context The security context to use to access entities.
	 */
	public UserContextTransientManager(SecurityContext context)
	{
		super(context) ;
	}

}
