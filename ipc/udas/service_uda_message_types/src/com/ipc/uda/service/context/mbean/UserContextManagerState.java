/**
 * 
 */
package com.ipc.uda.service.context.mbean;

import java.util.ArrayList;
import java.util.List;

import com.ipc.uda.service.context.UserID;

/**
 * @author mordarsd
 *
 */
public class UserContextManagerState implements UserContextManagerStateMBean
{

    private final List<UserID> userIDList;
    
    public UserContextManagerState()
    {
        this.userIDList = new ArrayList<UserID>();
    }
    
    /* (non-Javadoc)
     * @see com.ipc.uda.service.context.mbean.UserContextManagerStateMBean#addUserID(com.ipc.uda.service.context.UserID)
     */
    @Override
    public void addUserID(UserID userID)
    {
        this.userIDList.add(userID);
    }

    /* (non-Javadoc)
     * @see com.ipc.uda.service.context.mbean.UserContextManagerStateMBean#removeUserID(com.ipc.uda.service.context.UserID)
     */
    @Override
    public void removeUserID(UserID userID)
    {
        this.userIDList.remove(userID);
    }

    @Override
    public List<UserID> getUserIDList()
    {
        return this.userIDList;
    }
    
    @Override
    public String getUserIDListAsString()
    {
        return this.userIDList.toString();
    }
    
    @Override
    public int getNumUserIDs()
    {
        return this.userIDList.size();
    }
}
