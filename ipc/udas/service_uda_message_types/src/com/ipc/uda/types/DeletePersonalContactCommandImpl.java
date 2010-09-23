/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalDirectory;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalDirectoryManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * This class is responsible for Deleting the Contact in the database
 * 
 * @author Veena Makam
 * 
 */
public class DeletePersonalContactCommandImpl extends DeletePersonalContactCommand implements
        ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        // get the basic security context object
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();

        // relevant manager objects
        final PersonalContactManager contactMgr = new PersonalContactManager(basicSecContext);
        if (contactMgr == null)
        {
            Log.logger().debug(
                    "PersonalContactManager is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        final PersonalDirectoryManager perDirMgr = new PersonalDirectoryManager(basicSecContext);
        if (perDirMgr == null)
        {
            Log.logger().debug(
                    "PersonalDirectoryManager is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        try
        {
            // Find the Contact object with the given contactID and delete
            final PersonalContact ctDTO = contactMgr.getById(Integer.parseInt(this.contactId
                    .toString()));

            if (ctDTO != null)
            {
                final PersonalDirectory perDir = perDirMgr.findByPersonalContacts(ctDTO);
                if (perDir == null)
                {
                    Log.logger().debug(
                            "Personal directory returned by data services for the personal contact with contact ID: "
                                    + this.contactId + " is null for the user "
                                    + this.ctx.getUser().getName());
                    return returnNothing();
                }

                // subscribe for delete events from DS
                DataServicesSubscriptionHelper.createSubscriptionsTo(PersonalContact.class
                        .getSimpleName(), ctDTO.getId(), this.ctx);

                perDir.removeFromPersonalContacts(ctDTO);

                perDirMgr.save(perDir);

                Log.logger().debug(
                        "Removed the PersonalContact with contact ID: " + this.contactId
                                + " from database");
            }
            else
            {
                Log.logger().info(
                        "Unable to Removed the PersonalContact with contact ID: " + this.contactId
                                + " from database; This PersonalContact does not exist!");
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        return new Nothing<ExecutionResult>();
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
