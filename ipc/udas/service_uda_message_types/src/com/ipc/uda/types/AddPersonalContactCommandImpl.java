/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import javax.xml.bind.annotation.XmlTransient;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.internal.manager.base.PersonalContactBaseManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.uda.event.ExecutableResultQueue;
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
 * This class is responsible for adding the new Contact to the database
 * 
 * @author Veena Makam
 * 
 */
public class AddPersonalContactCommandImpl extends AddPersonalContactCommand implements
        ExecutableWithContext
{
    @XmlTransient
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        final SecurityContext secCtx = this.ctx.getSecurityContext();

        ContactType contactType = this.getContact();

        try
        {
            PersonalContact contactDTO = PersonalContactBaseManager.NewPersonalContact();
            final PersonalContactManager perCtMgr = new PersonalContactManager(secCtx);
            final UID uid = this.getContact().getContactId();
            boolean ctAddFromPersonalDir = false;

            if (uid != null)
            {
                PersonalContact existingDSContact = null;
                try
                {
                    existingDSContact = perCtMgr.getById(Integer.parseInt(uid.toString()));
                    if (existingDSContact != null)
                    {
                        ctAddFromPersonalDir = false;
                    }
                    else
                    {
                        ctAddFromPersonalDir = true;
                    }
                }
                catch (final NumberFormatException nfException)
                {
                    ctAddFromPersonalDir = true;
                }
            }

            // map the fields from UDA ContactType to DS Contact entity and save the newly created
            // PersonalContact
            contactDTO = UDAAndDSEntityUtil.mapUDAToDSContact(contactType, contactDTO, this.ctx,
                    ctAddFromPersonalDir);
            Log.logger().debug(
                    "New contact added to the database with contact ID: " + contactDTO.getId());

            // Add personal contact can happen either by copying instance contact to personal
            // directory or by create a personal contact freshly. In the former case POC and the
            // preferred POC are not sent by the client, in order to update the POC and preferred
            // POC of the newly added contact, contactType is updated to get the POC and preferred POC
            // before sending an event to the client
            contactType = UDAAndDSEntityUtil.mapDSToUDAContact(contactDTO, contactType, this.ctx);

            final PersonalDirectoryItemAddedEvent perDirAddEve = new PersonalDirectoryItemAddedEvent();
            perDirAddEve.setContact(contactType);
            final Event event = new Event();
            event.setPersonalDirectoryItemAdded(perDirAddEve);
            ExecutableResultQueue.<Event> send(event, this.ctx.getUser(), this.ctx.getUserID()
                    .getDeviceID());

            DataServicesSubscriptionHelper.createSubscriptionsTo(PersonalContact.class
                    .getSimpleName(), contactDTO.getId(), this.ctx);
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
}
