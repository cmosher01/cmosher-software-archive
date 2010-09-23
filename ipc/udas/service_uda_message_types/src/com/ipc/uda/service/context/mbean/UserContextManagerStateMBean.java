/**
 * 
 */
package com.ipc.uda.service.context.mbean;

import java.util.List;

import com.ipc.uda.service.context.UserID;

/**
 * @author mordarsd
 *
 */
public interface UserContextManagerStateMBean
{

    public void addUserID(UserID userID);
    
    public void removeUserID(UserID userID);
    
    public List<UserID> getUserIDList();
    
    public String getUserIDListAsString();
    
    public int getNumUserIDs();
}
