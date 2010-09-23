/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;



import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserUDA;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.ds.entity.manager.UserManager;
import com.ipc.ds.entity.manager.UserUDAManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.EntityHelper;



public class GetUserConfigurationQueryImpl extends GetUserConfigurationQuery implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        UserConfigurationResultType resType = null; // do we need this?

        try
        {
            final SecurityContext basicSecContext = this.ctx.getSecurityContext();
            final UserManager dsUsrMgr = new UserManager(basicSecContext);
            final User user = dsUsrMgr.findByLoginNameEqualing(this.ctx.getUser().getName());
            if (user != null)
            {
                // for a given username there is only one User associated;
                // TODO other-wise do we need to iterate through the list?

                resType = new UserConfigurationResultType();

                resType.setUser(getUserType(user));

                final UserCDIManager dsCDIMgr = new UserCDIManager(basicSecContext);
                UserCDI usrCDI = dsCDIMgr.getUserCDIFor(user);
                if(usrCDI!= null )
                {
                    resType.setUserCdi(EntityHelper.fromDSUserCDI(usrCDI));
                }
                else
                {
                    Log.logger().debug(" usrCDI from DS is Null for thefor the given user"
                            + this.ctx.getUser().getName());
                }

                final UserUDAManager dsUsrUDAMgr = new UserUDAManager(basicSecContext);
                UserUDA userUDA=dsUsrUDAMgr.getUserUDAFor(user);
                if (userUDA != null )
                {
                    resType.setUserUda(getUDAUserDataType(userUDA));
                }
                else
                {
                    Log.logger().debug(" userUDA from DS is NULL for the given user"
                            + this.ctx.getUser().getName());
                }
            }
            else
            {
                Log.logger().debug(" usersList from DS is NULL or of Size Zero for the given user"
                        + this.ctx.getUser().getName());
            }

        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        if (resType != null)
        {
            final QueryResult qr = new QueryResult();
            qr.setUserConfigurationResult(resType);
            return new Optional<ExecutionResult>(qr);
        }

        return new Nothing<ExecutionResult>();
    }

    /**
     * Get getUserType
     * 
     * @input User (as per DS User Entity type)
     * @return UserType (USerType as defined in UDA Types schema )
     */
    private UserType getUserType(User user)
    {
        final UserType userType = new UserType();
        userType.setLoginName(user.getLoginName());
        userType.setPassword(user.getPassword());
        return userType;
    }

    /**
     * Get getUDAUserDataType
     * 
     * @input UserUDA
     * @return UserDataType
     */
    private UserUdaType getUDAUserDataType(UserUDA userUDA)
    {
        final UserUdaType userDataType = new UserUdaType();

        // TODO: this needs to be fixed (update DS with the correct data type: boolean)
        // userDataType.setActivityWindowDisplayable(userUDA.getActivityWindowDisplayable());

        userDataType.setActivityWindowDockable(userUDA.getActivityWindowDockable());
        userDataType.setActivityWindowDockLocation(userUDA.getActivityWindowDockLocation());
        userDataType.setActivityWindowHoldCallSwitchBehavior(userUDA
                .getActivityWindowHoldCallSwitchBehavior());
        userDataType.setActivityWindowMaxDisplay(userUDA.getActivityWindowMaxDisplay());
        userDataType.setDefaultApplication(userUDA.getDefaultApplication());
        userDataType.setDisplayIncomingInSystemTray(userUDA.getDisplayIncomingInSystemTray());
        userDataType.setDisplayMainFormOnStartup(userUDA.getDisplayMainFormOnStartup());
        userDataType.setEnableUITearOffs(userUDA.getEnableUITearOffs());
        userDataType.setQuickFinderHideDelay(userUDA.getQuickFinderHideDelay());
        userDataType.setQuickFinderHideShortCutKeys(userUDA.getQuickFinderHideShortCutKeys());
        userDataType.setQuickFinderLaunchShortCutKeys(userUDA.getQuickFinderLaunchShortCutKeys());
        userDataType.setQuickFinderMaxDisplay(userUDA.getQuickFinderMaxDisplay());

        return userDataType;
    }
}
