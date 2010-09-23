/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.PersonalContact;
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
public class GetPersonalContactDetailsQueryImpl extends GetPersonalContactDetailsQuery implements
        ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();

        final PersonalContactManager contactMgr = new PersonalContactManager(basicSecContext);
        if (contactMgr == null)
        {
            Log.logger().debug(
                    "PersonalContactManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }
        PersonalContactDetailsResult ctDetailsResult = null;
        PersonalContact dsContact = null;
        try
        {
            if (this.contactId == null)
            {
                throw new ExecutionException("contactID from UDAC is NULL");
            }
            dsContact = contactMgr.getById(Integer.parseInt(this.contactId.toString()));
            if (dsContact == null)
            {
                throw new ExecutionException("PersonalContact from database is NULL");
            }
            ctDetailsResult = new PersonalContactDetailsResult();
            ContactType udaContact = new ContactType();

            udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(dsContact, udaContact, this.ctx);
            ctDetailsResult.setContact(udaContact);
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setPersonalContactDetails(ctDetailsResult);

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
