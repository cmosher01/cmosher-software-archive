/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

import java.util.Date;
import java.util.List;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.CommHistUDAImmutable;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.ds.entity.manager.CommHistUDAImmutableManager;
import com.ipc.ds.entity.manager.ContactManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.ds.entity.manager.PointOfContactManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;

/**
 * Gets the history associated with the given contact details.
 * 
 * @author Veena Makam
 * 
 */
public class GetContactHistoryQueryImpl extends GetContactHistoryQuery implements
        ExecutableWithContext
{

    ContactHistoryResultType ctHistRes;
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        try
        {
            populateCommunicationHistory();
        }
        catch (final ExecutionException lException)
        {
            throw lException;
        }

        final QueryResult qResult = new QueryResult();
        qResult.setContactHistoryResult(this.ctHistRes);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * Populates Communication History based on whether the given contact is Personal or Instance
     * contact
     * 
     * @throws ExecutionException
     */
    private void populateCommunicationHistory() throws ExecutionException
    {
        boolean isPersonalContact = false;
        List<Button> dsButtonList = null;

        // determine if personal contact
        if (this.getContactType() == DirectoryContactType.PERSONAL)
        {
            isPersonalContact = true;
        }
        final ButtonManager buttonMgr = new ButtonManager(this.ctx.getSecurityContext());

        if (buttonMgr == null)
        {
            Log.logger().debug(
                    "ButtonManager object from the DS are null for the user: "
                            + this.ctx.getUser().getName());
            throw new ExecutionException("ButtonManager object from the DS are null for the user: "
                    + this.ctx.getUser().getName());
        }

        try
        {
            if (isPersonalContact)
            {
                final PersonalContactManager perCtMgr = new PersonalContactManager(this.ctx
                        .getSecurityContext());
                final PersonalPointOfContactManager perPOCMgr = new PersonalPointOfContactManager(
                        this.ctx.getSecurityContext());

                if (perCtMgr == null || perPOCMgr == null)
                {
                    Log.logger().debug(
                            "Manager objects from the DS are null for the user: "
                                    + this.ctx.getUser().getName());
                    throw new ExecutionException(
                            "Manager objects from the DS are null for the user: "
                                    + this.ctx.getUser().getName());
                }

                // get the PersonalContact object from DS with the given contactId
                final PersonalContact dsContact = perCtMgr.getById(Integer.parseInt(this
                        .getContactId().toString()));
                if (dsContact == null)
                {
                    Log.logger().debug(
                            "PersonalContact from the DS is Null for the contact ID: "
                                    + this.getContactId());
                    throw new ExecutionException(
                            "PersonalContact from the DS is Null for the contact ID: "
                                    + this.getContactId());
                }

                // get the list of personal POCs for the given personal contact
                final List<PersonalPointOfContact> perPOCList = perPOCMgr
                        .getPersonalPointOfContactsFor(dsContact);
                if (perPOCList == null || perPOCList.isEmpty())
                {
                    Log.logger().debug(
                            "PersonalPointOfContact list from the DS is Null for the contact ID: "
                                    + this.getContactId());
                    returnNothing();
                }

                // find the history associated with all the personal POCs
                for (final PersonalPointOfContact dsPerPOC : perPOCList)
                {
                    if (dsPerPOC == null)
                    {
                        Log.logger().debug(
                                "PersonalPointOfContact from DS is null for contact ID "
                                        + this.getContactId());
                        continue;
                    }

                    // get the list of Button(s) associated with the given personal POC
                    dsButtonList = buttonMgr.findByPersonalPointOfContact(dsPerPOC);

                    if (dsButtonList == null || dsButtonList.isEmpty())
                    {
                        Log.logger().debug(
                                "There are no Button(s) associated with the given PersonalPointOfContact ID: "
                                        + dsPerPOC.getId());
                        continue;
                    }

                    // populate the communication history based on the buttonlist given
                    populateCommunicationHistory(dsButtonList, dsPerPOC.getId(), true);
                }
            }
            else
            {
                final ContactManager ctMgr = new ContactManager(this.ctx.getSecurityContext());
                final PointOfContactManager pocMgr = new PointOfContactManager(this.ctx
                        .getSecurityContext());
                if (ctMgr == null || pocMgr == null)
                {
                    Log.logger().debug(
                            "Manager objects from the DS are null for the user: "
                                    + this.ctx.getUser().getName());
                    throw new ExecutionException(
                            "Manager objects from the DS are null for the user: "
                                    + this.ctx.getUser().getName());
                }

                // get the Contact object from DS with the given contactId
                final Contact dsContact = ctMgr.getById(Integer.parseInt(this.getContactId()
                        .toString()));
                if (dsContact == null)
                {
                    Log.logger().debug(
                            "Contact from the DS is Null for the contact ID: "
                                    + this.getContactId());
                    throw new ExecutionException("Contact from the DS is Null for the contact ID: "
                            + this.getContactId());
                }

                // get the list of POCs for the given contact
                final List<PointOfContact> pocList = pocMgr.getPointOfContactsFor(dsContact);
                if (pocList == null || pocList.isEmpty())
                {
                    Log.logger().debug(
                            "PointOfContact list from the DS is Null for the contact ID: "
                                    + this.getContactId());
                    returnNothing();
                }

                // find the history associated with all the POCs
                for (final PointOfContact dsPOC : pocList)
                {
                    if (dsPOC == null)
                    {
                        Log.logger().debug(
                                "PointOfContact from DS is null for contact ID "
                                        + this.getContactId());
                        continue;
                    }

                    // get the list of Button(s) associated with the given personal POC
                    dsButtonList = buttonMgr.findByPointOfContact(dsPOC);

                    if (dsButtonList == null || dsButtonList.isEmpty())
                    {
                        Log.logger().debug(
                                "There are no Button(s) associated with the given PointOfContact ID: "
                                        + dsPOC.getId());
                        continue;
                    }
                    // populate the communication history based on the buttonlist given
                    populateCommunicationHistory(dsButtonList, dsPOC.getId(), false);
                }
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

    }

    /**
     * Populates the communication history for the given Button(s)
     * 
     * @param dsButtonList List<Button>
     * @param pocId int
     * @param isPersonalContact boolean
     * @throws ExecutionException
     */
    private void populateCommunicationHistory(final List<Button> dsButtonList, final int pocId,
            final boolean isPersonalContact) throws ExecutionException
    {
        final CommHistUDAImmutableManager commHistMgr = new CommHistUDAImmutableManager(this.ctx
                .getSecurityContext());

        if (commHistMgr == null)
        {
            Log.logger().debug(
                    "CommHistUDAImmutableManager object from the DS are null for the user: "
                            + this.ctx.getUser().getName());
            throw new ExecutionException(
                    "CommHistUDAImmutableManager object from the DS are null for the user: "
                            + this.ctx.getUser().getName());
        }

        try
        {
            // get the communication history for all the Button(s)
            for (final Button dsButton : dsButtonList)
            {
                if (dsButton == null)
                {
                    if (isPersonalContact)
                    {
                        Log.logger().debug(
                                "Button from DS is null for PersonalPointOfContact ID " + pocId);
                    }
                    else
                    {
                        Log.logger().debug("Button from DS is null for PointOfContact ID " + pocId);
                    }
                    continue;
                }

                // get the communication history list for the given button number
                final List<CommHistUDAImmutable> dsCommHistList = commHistMgr
                        .findByButtonNumberEqualTo(dsButton.getButtonNumber());

                if (dsCommHistList == null || dsCommHistList.isEmpty())
                {
                    Log.logger().debug(
                            "There is no History associated with the given Button ID: "
                                    + dsButton.getId());
                    continue;
                }

                // populate the result type
                for (final CommHistUDAImmutable dsCommHist : dsCommHistList)
                {
                    if (dsCommHist == null)
                    {
                        Log.logger().debug(
                                "CommHistUDAImmutable from DS is null for Button ID "
                                        + dsButton.getId());
                        continue;
                    }
                    if (this.ctHistRes == null)
                    {
                        this.ctHistRes = new ContactHistoryResultType();
                    }
                    final HistoryType histType = new HistoryType();
                    histType.setHistoryId(new UID("" + dsCommHist.getId()));
                    // TODO: wait for DS to fix this:
//                    int eventType = dsCommHist.getEventType();
//                    if ( != null)
//                    {
//                        histType.setCallType(CallCategoryType.fromValue(dsCommHist.getCategory()
//                                .name()));
//                    }
                    histType.setName(dsCommHist.getCLIName());
                    histType.setTime(new DateTime(new Date(dsCommHist.getStartTime())));

                    // FIXME: mapping for Notes and company attributes of history is missing
                    // in the DS entity
                    this.ctHistRes.getHistoryRecords().add(histType);
                }
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }
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
