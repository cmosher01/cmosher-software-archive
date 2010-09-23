/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.Directory;
import com.ipc.ds.entity.dto.Sphere;
import com.ipc.ds.entity.manager.ContactManager;
import com.ipc.ds.entity.manager.DirectoryManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible for fetching the contact list belonging to the Instance directory from
 * DS
 * 
 * @author Veena Makam
 * 
 */
public class GetInstanceDirectoryListQueryImpl extends GetInstanceDirectoryListQuery implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        final SecurityContext basicSecContext = this.ctx.getSecurityContext();

        InstanceDirectoryListResult dlResultObj = null;
        List<Contact> contactList = null;
        try
        {
            final ContactManager contactMgr = new ContactManager(basicSecContext);
            final DirectoryManager dirMgr = new DirectoryManager(basicSecContext);
            if (contactMgr == null || dirMgr == null)
            {
                Log.logger().debug(
                        "Manager object is Null from DS for user " + this.ctx.getUser().getName());
                return returnNothing();
            }

            final Sphere sphere = UDAAndDSEntityUtil.getSphere(this.ctx);
            if (sphere == null)
            {
                Log.logger().debug("Sphere is Null from DS for " + this.ctx.getUser().getName());
                return returnNothing();
            }

            final List<Directory> dsDirList = dirMgr.getDirectoryFor(sphere);
            if (dsDirList == null || dsDirList.isEmpty())
            {
                Log.logger().debug(
                        "Directory list is Null from DS for " + this.ctx.getUser().getName());
                return returnNothing();
            }
            final Directory instanceDir = dsDirList.get(0);
            if (instanceDir == null)
            {
                Log.logger().debug("Directory is Null from DS for " + this.ctx.getUser().getName());
                return returnNothing();
            }
            contactList = contactMgr.getContactsFor(instanceDir);

            if (contactList == null || contactList.isEmpty())
            {
                throw new ExecutionException(
                        "Instance contact list from the database is empty or NULL");
            }
            dlResultObj = new InstanceDirectoryListResult();
            for (final Contact dsContact : contactList)
            {
                ContactType dirContact = new ContactType();

                dirContact = UDAAndDSEntityUtil.mapDSToUDAContact(dsContact, dirContact, this.ctx);
                DataServicesSubscriptionHelper.createSubscriptionsTo(Contact.class.getSimpleName(),
                        dsContact.getId(), this.ctx);
                dlResultObj.getContact().add(dirContact);
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setInstanceDirectoryList(dlResultObj);

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
