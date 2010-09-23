/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Directory;
import com.ipc.ds.entity.dto.DirectoryGroup;
import com.ipc.ds.entity.dto.PersonalDirectory;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.manager.DirectoryGroupManager;
import com.ipc.ds.entity.manager.DirectoryManager;
import com.ipc.ds.entity.manager.PersonalDirectoryManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

public class GetDirectoryNameListQueryImpl extends GetDirectoryNameListQuery implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();
        final DirectoryManager dirMgr = new DirectoryManager(basicSecContext);
        final DirectoryNameListResult dirNameListResult = new DirectoryNameListResult();
        if (dirMgr == null)
        {
            Log.logger().debug(
                    "DirectoryManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            returnNothing();
        }
        try
        {
            final List<Directory> dirList = dirMgr.getAll();
            if (dirList == null || dirList.isEmpty())
            {
                Log.logger().debug(
                        "Directory list from the database is null or empty for user: "
                                + this.ctx.getUser().getName());
                returnNothing();
            }
            for (final Directory directory : dirList)
            {
                final Directory dirObj = directory;
                if (dirObj == null)
                {
                    Log.logger().debug(
                            "Directory is NULL for user: " + this.ctx.getUser().getName());
                }
                else
                {
                    final CategoryType catType = new CategoryType();
                    catType.setDirectoryID(new UID("" + dirObj.getId()));
                    catType.setName(dirObj.getName());
                    catType.setDirectoryType(DirectoryContactType.INSTANCE);
                    dirNameListResult.getCategory().add(catType);
                }
            }

            final DirectoryGroupManager dirGrpMgr = new DirectoryGroupManager(basicSecContext);
            if (dirGrpMgr == null)
            {
                Log.logger().debug(
                        "DirectoryGroupManager object is Null from DS for user "
                                + this.ctx.getUser().getName());
                returnNothing();
            }

            final List<DirectoryGroup> dirGrpList = dirGrpMgr.getAll();
            if (dirGrpList == null || dirGrpList.isEmpty())
            {
                Log.logger().debug(
                        "DirectoryGroup list is NULL/Empty for user: "
                                + this.ctx.getUser().getName());
            }

            for (final DirectoryGroup directoryGroup : dirGrpList)
            {
                final DirectoryGroup dirGrp = directoryGroup;
                if (dirGrp == null)
                {
                    Log.logger().debug(
                            "DirectoryGroup is NULL for user: " + this.ctx.getUser().getName());
                }
                else
                {
                    final CategoryType catType = new CategoryType();
                    catType.setDirectoryID(new UID("" + dirGrp.getId()));
                    catType.setName(dirGrp.getName());
                    catType.setDirectoryType(DirectoryContactType.INSTANCE);
                    dirNameListResult.getCategory().add(catType);
                }
            }

            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            if (usrCDI == null)
            {
                Log.logger().debug(
                        "UserCDI object is Null from DS for user: " + this.ctx.getUser().getName());
                returnNothing();
            }

            final PersonalDirectoryManager perDirMgr = new PersonalDirectoryManager(basicSecContext);
            if (perDirMgr == null)
            {
                Log.logger().debug(
                        "PersonalDirectoryManager object is Null from DS for user "
                                + this.ctx.getUser().getName());
                returnNothing();
            }

            final PersonalDirectory perDir = perDirMgr.getPersonalDirectoryFor(usrCDI);
            if (perDir == null)
            {
                Log.logger().debug(
                        "PersonalDirectory is NULL for user: " + this.ctx.getUser().getName());
                returnNothing();
            }
            else
            {
                final CategoryType catType = new CategoryType();
                catType.setDirectoryID(new UID("" + perDir.getId()));
                catType.setName(perDir.getName());
                catType.setDirectoryType(DirectoryContactType.PERSONAL);
                dirNameListResult.getCategory().add(catType);
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setDirectoryNameList(dirNameListResult);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    private Optional<ExecutionResult> returnNothing()
    {
        return new Nothing<ExecutionResult>();
    }
}
