/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

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
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * This class is responsible for updating the user configuration to database
 * 
 * @author Bhavya Bhat
 * 
 */
public class UpdateUserConfigurationCommandImpl extends UpdateUserConfigurationCommand implements
        ExecutableWithContext
{
    //public final static Logger logger = Log.logger();
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        // create the basic security context
        SecurityContext securityCtx = this.ctx.getSecurityContext();
        // create the new instance of UserManager
        UserManager userManager = new UserManager(securityCtx);
        // create a new instance of UserCDIManager
        UserCDIManager userCDIManager = new UserCDIManager(securityCtx);
        // create a new instance of UserUDAManager
        UserUDAManager userUDAManager = new UserUDAManager(securityCtx);

        // UserType userType = this.getUser();
        UserCdiType userCdiType = this.getUserCDI();
        UserUdaType userUdaType = this.getUserUDA();

        UserCDI userCDIDTO = null;
        UserUDA userUDADTO = null;
        // User userDTO = null;
        try
        {
            User user = userManager.findByLoginNameEqualing(ctx.getUser().getName());

            if (user != null)
            {
                   UserCDI userCDI = userCDIManager.getUserCDIFor(user); //List<UserCDI> userCDIList = userCDIManager.getUserCDIFor(user);
                UserUDA userUDA = userUDAManager.getUserUDAFor(user);//List<UserUDA> userUDAList = userUDAManager.getUserUDAFor(user);

                // userDTO = updateDSUserDTO(user, userType);

                if (userCDI != null )//&& userCDIList.size() > 0)
                {
                    userCDIDTO = updateDSUserCDIDTO(userCDI, userCdiType);
                }
                else
                {
                    throw new ExecutionException("The userCDIList is NULL");
                   
                }

                if (userUDA != null )//&& userUDAList.size() > 0)
                {
                    userUDADTO = updateDSUserUDADTO(userUDA, userUdaType);
                }
                else
                {
                    throw new ExecutionException("The userUDAList is null");
                }

            }
            else
            {
                throw new ExecutionException("The userList from DS is NULL");
            }

            // update userCDI and userDTO using the corresponding managers in DS.
            // userManager.save(userDTO);
            if (userCDIDTO != null)
            {
                userCDIManager.save(userCDIDTO);
            }
            else
            {
                throw new ExecutionException("The userCDIDTO is null");
            }

            if (userUDADTO != null)
            {
                userUDAManager.save(userUDADTO);
            }
            else
            {
                throw new ExecutionException("The userUDADTO is null");
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }

    /*
     * public User updateDSUserDTO(User userDTO, UserType userType) {
     * userDTO.setLoginName(userType.getLoginName()); //Do we need to update login name and password
     * userDTO.setPassword(userType.getPassword()); return userDTO; }
     */

    /**
     * 
     * @param userCDIDTO UserCDI
     * @param userCdiType UserCdiType
     * @return userCDIDTO UserCDI
     */
    public UserCDI updateDSUserCDIDTO(UserCDI userCDIDTO, UserCdiType userCdiType)
    {
        // update userCDI.floatBtnRows and userCDI.fixedBtnRows
        userCDIDTO.setFloatBtnRows(userCdiType.getFloatBtnRows());
        userCDIDTO.setFixedBtnRows(userCdiType.getFixedBtnRows());

        return userCDIDTO;
    }

    /**
     * 
     * @param userUDADTO UserUDA
     * @param userUdaType UserUdaType
     * @return userUDADTO UserUDA
     */
    public UserUDA updateDSUserUDADTO(UserUDA userUDADTO, UserUdaType userUdaType)
    {
        // update userUDA.defaultApplication and userUDA.enableUITearOffs
        userUDADTO.setDefaultApplication(userUdaType.getDefaultApplication());
        userUDADTO.setEnableUITearOffs(userUdaType.isEnableUITearOffs());

        return userUDADTO;
    }

}
