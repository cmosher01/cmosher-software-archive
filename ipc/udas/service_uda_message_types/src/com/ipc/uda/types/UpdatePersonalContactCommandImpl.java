/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.exception.EntityDoesNotExistException;
import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.InvalidEntityException;
import com.ipc.ds.base.exception.RepeatedSaveInTransactionException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.exception.UninitializedEntityException;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.internal.manager.base.PersonalPointOfContactBaseManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * This class is responsible for updating the Contact to the database
 * 
 * @author Veena Makam
 * 
 */

public class UpdatePersonalContactCommandImpl extends UpdatePersonalContactCommand implements
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
                    "PersonalContactManager is null for user: " + this.ctx.getUser().getName());
            throw new ExecutionException("PersonalContactManager from DS is NULL");
        }

        // contactType = ContactType object on UDA (to be added to Database)
        final ContactType contactType = this.getContact();
        if (contactType == null)
        {
            throw new ExecutionException("ContactType from UDAC is NULL");
        }

        try
        {
            // contactDTO = PersonalContact entity on DS
            final PersonalContact contactDTO = contactMgr.getById(Integer.parseInt((contactType
                    .getContactId().toString())));

            if (contactDTO != null)
            {
                updateDSContactDTO(contactType, contactDTO);
                DataServicesSubscriptionHelper.createSubscriptionsTo(PersonalContact.class
                        .getSimpleName(), this.getContact().getContactId().asDataServicesID(), ctx);
                contactMgr.save(contactDTO);
            }
            else
            {
                // log it? send back an event?
                Log.logger().info(
                        "Unable to perform UPDATE on PersonalContact: "
                                + contactType.getContactId()
                                + "; PersonalContact no longer exists!");
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

    private void deleteDeltas(final List<PointOfContactType> udaPocList,
            final List<PersonalPointOfContact> dsPocList, final PersonalPointOfContactManager pocMgr)
            throws StorageFailureException, InvalidContextException, EntityDoesNotExistException
    {
        for (final PersonalPointOfContact dsPoc : dsPocList)
        {

            boolean delete = true;

            // Check if the uda POC list no longer contains this DS POC equivalent
            // If it no longer exists in the UDA List, delete it from DS
            // Unfortunately we must take a brute force approach here becuase the Lists
            // contain two different types - so we must check by ID's
            for (final PointOfContactType udaPoc : udaPocList)
            {
                if (udaPoc.getContactId() != null
                        && (udaPoc.getContactId().intValue() == dsPoc.getId()))
                {
                    delete = false;
                    break;
                }
            }

            if (delete)
            {
                pocMgr.delete(dsPoc);
            }
        }
    }

    /**
     * Updates DS Contact object
     * 
     * @param contactType ContactType
     * @param contactDTO Contact
     * @return PersonalContact contactDTO
     * @throws ExecutionException
     */
    private void updateDSContactDTO(final ContactType contactType, final PersonalContact contactDTO)
            throws StorageFailureException, InvalidContextException, EntityDoesNotExistException,
            RepeatedSaveInTransactionException, InvalidEntityException,
            UninitializedEntityException
    {

        contactDTO.setBusinessGroup(contactType.getBusinessGroup());
        contactDTO.setCompany(contactType.getCompanyName());
        contactDTO.setFirstName(contactType.getFirstName());
        contactDTO.setLastName(contactType.getLastName());
        contactDTO.setTitle(contactType.getTitle());

        final List<PointOfContactType> udaPocList = contactType.getPointsOfContact()
                .getPointOfContact();

        final PersonalPointOfContactManager pocMgr = new PersonalPointOfContactManager(this.ctx
                .getSecurityContext());

        // get a list of whats in the database
        final List<PersonalPointOfContact> dsPocList = pocMgr
                .getPersonalPointOfContactsFor(contactDTO);

        this.deleteDeltas(udaPocList, dsPocList, pocMgr);

        for (final PointOfContactType udaPoc : udaPocList)
        {
            this.updatePersonalPointOfContact(pocMgr, udaPoc, contactDTO);
        }

    }

    private void updatePersonalPointOfContact(final PersonalPointOfContactManager mgr,
            final PointOfContactType udaPoc, final PersonalContact dsContact)
            throws StorageFailureException, InvalidContextException, EntityDoesNotExistException,
            RepeatedSaveInTransactionException, InvalidEntityException,
            UninitializedEntityException
    {

        PersonalPointOfContact dsPoc = null;
        boolean isNew = false;

        // get the DS PointOfContact
        if (udaPoc.getContactId() != null)
        {
            dsPoc = mgr.getById(udaPoc.getContactId().intValue());
        }
        // No ID exists, attempt to create a new one!
        else
        {
            dsPoc = PersonalPointOfContactBaseManager.NewPersonalPointOfContact();
            isNew = true;

        }

        if (dsPoc != null)
        {
            dsPoc.setData(udaPoc.getData());

            dsPoc.setMediaType(PersonalPointOfContact.EnumMediaType.valueOf(udaPoc.getMediaType()
                    .value()));
            dsPoc.setShortDescriptor(udaPoc.getDescriptor());

            // This doesn't exist in the UDA PoC
            // dsPoc.setPOCType(EnumPOCType.valueOf())

            mgr.save(dsPoc);

            // Note: we need to do this here, because we cannot add the new PersonalContact
            // until after they have been persisted.
            if (isNew)
            {
                dsContact.addToPersonalPointOfContacts(dsPoc);
            }

        }
        else
        {
            Log.logger().info(
                    "Unable to find (DS) PersonalPointOfContact for "
                            + "UDA PointOfContactType with id: " + udaPoc.getContactId());
        }
    }
}
