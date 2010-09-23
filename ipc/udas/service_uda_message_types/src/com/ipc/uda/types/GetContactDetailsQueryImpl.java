/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.manager.ContactManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible to fetch the personal contact details from the database based on the
 * contact id given
 * 
 * @author Veena Makam
 * 
 */
public class GetContactDetailsQueryImpl extends GetContactDetailsQuery implements
        ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        boolean isPersonal = false;
        ContactDetailsResultType ctDetailsResult = null;
        PersonalContactManager perCtMgr = null;
        ContactManager ctMgr = null;

        // validate input
        if (this.contactId == null || this.contactType == null)
        {
            throw new ExecutionException("contactID/contactType from UDAC is NULL");
        }

        // Check if the contact is of personal category or not
        if (this.getContactType() == DirectoryContactType.PERSONAL)
        {
            isPersonal = true;
            perCtMgr = new PersonalContactManager(this.ctx.getSecurityContext());
            if (perCtMgr == null)
            {
                Log.logger().debug(
                        "PersonalContactManager object is Null from DS for user "
                                + this.ctx.getUser().getName());
                return returnNothing();
            }
        }
        else
        {
            ctMgr = new ContactManager(this.ctx.getSecurityContext());
            if (ctMgr == null)
            {
                Log.logger().debug(
                        "ContactManager object is Null from DS for user "
                                + this.ctx.getUser().getName());
                return returnNothing();
            }
        }

        try
        {
            ContactType udaContact = new ContactType();
            if (isPersonal)
            {
                final PersonalContact dsContact = perCtMgr.getById(Integer.parseInt(this
                        .getContactId().toString()));
                if (dsContact == null)
                {
                    throw new ExecutionException("PersonalContact from database is NULL");
                }
                ctDetailsResult = new ContactDetailsResultType();

                // map DS PersonalContact and UDA ContactType object
                udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(dsContact, udaContact, this.ctx);
                ctDetailsResult.setContact(udaContact);
            }
            else
            {
                final Contact dsContact = ctMgr.getById(Integer.parseInt(this.getContactId()
                        .toString()));
                if (dsContact == null)
                {
                    throw new ExecutionException("Contact from database is NULL");
                }
                ctDetailsResult = new ContactDetailsResultType();

                // map DS Contact and UDA ContactType object
                udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(dsContact, udaContact, this.ctx);
                ctDetailsResult.setContact(udaContact);
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setContactDetailsResult(ctDetailsResult);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * Empty Execution result is returned
     * 
     * @return Optional<ExecutionResult>
     */
    private Optional<ExecutionResult> returnNothing()
    {
        return new Nothing<ExecutionResult>();
    }

}
