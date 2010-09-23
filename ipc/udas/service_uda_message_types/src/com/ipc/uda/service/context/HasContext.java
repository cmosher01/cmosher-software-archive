package com.ipc.uda.service.context;

/**
 * Classes that implement this interface will have a {@link UserContext}
 * associated with them. The {@link HasContext#setUserContext(UserContext) setUserContext}
 * method allows consumers to set the {@link UserContext} to be used
 * by the object.
 * 
 * @author mosherc
 */
public interface HasContext
{
    /**
     * Sets the {@link UserContext} for the execution of this executable.
     * Must be called before calling execute.
     * @param ctx the {@link UserContext} to set
     */
    void setUserContext(UserContext ctx);
}
