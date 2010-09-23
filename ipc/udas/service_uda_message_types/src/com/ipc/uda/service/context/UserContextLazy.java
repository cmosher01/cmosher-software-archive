package com.ipc.uda.service.context;

import com.ipc.uda.service.util.UdaEnvironmentException;


/**
 * Does lazy initialization of a UserContext object.
 * Since the constructor for UserContext could be time-consuming,
 * we may want to avoid calling it until necessary.
 * @author mosherc
 */
class UserContextLazy
{
    private final UserID userID;
    private UserContext ctx;

    public UserContextLazy(final UserID userID)
    {
        this.userID = userID;
    }

    public synchronized UserContext get()
    {
        if (this.ctx == null)
        {
            this.ctx = new UserContext(this.userID);
        }
        return this.ctx;
    }
}
