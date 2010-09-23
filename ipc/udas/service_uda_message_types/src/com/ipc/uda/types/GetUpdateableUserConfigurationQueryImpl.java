/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserUDA;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.ds.entity.manager.UserUDAManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.EntityHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for getting the user configuration details from the database
 * 
 * @author Bhavya Bhat
 * 
 */
public class GetUpdateableUserConfigurationQueryImpl extends GetUpdateableUserConfigurationQuery
        implements ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        UpdateableUserConfigurationResultType resType = null;
        try
        {
            final SecurityContext basicSecContext = this.ctx.getSecurityContext();
            final User user = UDAAndDSEntityUtil.getUser(this.ctx);
            // for a given username there is only one User associated;
            // TODO other-wise do we need to iterate through the list?

            resType = new UpdateableUserConfigurationResultType();

            // resType.setUser(getUserType(user));

            final UserCDIManager dsCDIMgr = new UserCDIManager(basicSecContext);
            final UserCDI usrCDI = dsCDIMgr.getUserCDIFor(user);
            if (usrCDI != null)
            {
                resType.setUserCDI(EntityHelper.fromDSUserCDI(usrCDI));
            }
            else
            {
                Log.logger().debug(
                        " usrCDI from DS is Null for thefor the given user"
                                + this.ctx.getUser().getName());
            }

            final UserUDAManager dsUsrUDAMgr = new UserUDAManager(basicSecContext);
            final UserUDA userUDA = dsUsrUDAMgr.getUserUDAFor(user);
            if (userUDA != null)
            {
                resType.setUserUDA(getUDAUserDataType(userUDA));
            }
            else
            {
                Log.logger().debug(
                        " userUDA from DS is NULL for the given user"
                                + this.ctx.getUser().getName());
            }

            // NOTE:
            // Why are we mapping a Turret Entity?? DEM 5/5/10
            // ///////////////////////////////////////////////////
            // final UserTurretManager dsTurretMgr = new UserTurretManager(basicSecContext);
            // UserTurret userTurret = dsTurretMgr.getUserTurretFor(user);
            // if (userTurret != null)
            // {
            // resType.setUserTurret(getTurretUserDataType(userTurret));
            // }

            DataServicesSubscriptionHelper.createSubscriptionsTo(User.class.getSimpleName(), user.getId(), this.ctx);

        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        if (resType != null)
        {
            final QueryResult qr = new QueryResult();
            qr.setUpdateableUserConfigurationResult(resType);
            return new Optional<ExecutionResult>(qr);
        }

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }


    /**
     * Get getUDAUserDataType
     * 
     * @input UserUDA
     * @return UserDataType
     */
    private UserUdaType getUDAUserDataType(final UserUDA userUDA)
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

    // private UserTurretType getTurretUserDataType(UserTurret userTurret)
    // {
    // UserTurretType userTurretType = new UserTurretType();
    //
    // userTurretType.setBtnCliFontfmLatin(userTurret.getBtnCliFontfmLatin());
    // userTurretType.setBtnCliFontszLatin(userTurret.getBtnCliFontszLatin());
    // userTurretType.setBtnCliFontwtLatin(userTurret.getBtnCliFontwtLatin());
    // userTurretType.setBtnDesFontfmLatin(userTurret.getBtnDesFontfmLatin());
    // userTurretType.setBtnDesFontszLatin(userTurret.getBtnDesFontszLatin());
    // userTurretType.setBtnDesFontwtLatin(userTurret.getBtnDesFontwtLatin());
    // userTurretType.setBtnLabelWrap(userTurret.getBtnLblWrap());
    // userTurretType.setCallRinger(userTurret.getCallRinger());
    // userTurretType.setDateDisplayFormat(userTurret.getDateDisplayFormat());
    // userTurretType.setFastPagingSpeed(userTurret.getFastPagingSpeed());
    // userTurretType.setLowPriRing(userTurret.getLowPriRing());
    // userTurretType.setMsgLineRing(userTurret.getMsgLineRing());
    // userTurretType.setMsgRinger(userTurret.getMsgRinger());
    // userTurretType.setProgBarHeldGran(userTurret.getProgBarHeldGranularity());
    // userTurretType.setProgBarHeldMaxSec(userTurret.getProgBarHeldMaxSeconds());
    // userTurretType.setProgBarHeldShown(userTurret.getProgBarHeldShown());
    // userTurretType.setProgBarIncGran(userTurret.getProgBarIncGranularity());
    // userTurretType.setProgBarIncMaxSec(userTurret.getProgBarIncMaxSeconds());
    // userTurretType.setProgBarIncShown(userTurret.getProgBarIncShown());
    // userTurretType.setRingerVol(userTurret.getRingerVol());
    //
    // return userTurretType;
    // }

}
