/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.manager.ButtonManager;
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
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author Veena Makam
 * 
 */
public class GetContactDetailsFromButtonPageQueryImpl extends GetContactDetailsFromButtonPageQuery
        implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final SecurityContext basSecCtx = this.ctx.getSecurityContext();
        boolean buttonIsPersonalContact = false;

        // Get the required manager objects
        final ButtonManager buttonMgr = new ButtonManager(basSecCtx);
        if (buttonMgr == null)
        {
            Log.logger()
                    .debug(
                            "ButtonManager object is Null from DS for user "
                                    + this.ctx.getUser().getName());
            return returnNothing();
        }

        final PointOfContactManager pocMgr = new PointOfContactManager(basSecCtx);
        if (pocMgr == null)
        {
            Log.logger().debug(
                    "PointOfContactManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        final PersonalPointOfContactManager perPOCMgr = new PersonalPointOfContactManager(basSecCtx);
        if (perPOCMgr == null)
        {
            Log.logger().debug(
                    "PersonalPointOfContactManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        final ContactManager ctMgr = new ContactManager(basSecCtx);
        if (ctMgr == null)
        {
            Log.logger().debug(
                    "ContactManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        final PersonalContactManager perCTMgr = new PersonalContactManager(basSecCtx);
        if (perCTMgr == null)
        {
            Log.logger().debug(
                    "PersonalContactManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }

        PersonalPointOfContact perPOCDS = null;
        PointOfContact pocDS = null;
        PersonalContactDetailsResult perCTDetailsResult = null;
        InstanceContactDetailsResult insCTDetailsResult = null;

        try
        {
            // Get Button object based on the input Id and in turn contact
            final Button buttonDS = buttonMgr.getById(Integer.parseInt(this.buttonId.toString()));
            perPOCDS = perPOCMgr.getPersonalPointOfContactFor(buttonDS);
            ContactType udaContact = new ContactType();

            // button contact is personal point of contact
            if (perPOCDS != null)
            {
                buttonIsPersonalContact = true;
                final List<PersonalContact> perCtList = perCTMgr
                        .findByPersonalPointOfContacts(perPOCDS);

                udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(perCtList.get(0), udaContact,
                        this.ctx);
                perCTDetailsResult = new PersonalContactDetailsResult();
                perCTDetailsResult.setContact(udaContact);
            }
            else
            {
                // button contact is instance point of contact
                buttonIsPersonalContact = false;
                pocDS = pocMgr.getPointOfContactFor(buttonDS);

                if (pocDS == null)
                {
                    Log.logger().debug(
                            "PointOfContact and PersonalPointOfContact not present for the given button ID: "
                                    + this.buttonId.toString() + " in the database");
                }
                else
                {
                    final List<Contact> ctList = ctMgr.findByPointOfContacts(pocDS);
                    udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(ctList.get(0), udaContact,
                            this.ctx);
                    insCTDetailsResult = new InstanceContactDetailsResult();
                    insCTDetailsResult.setContact(udaContact);
                }
            }

            final QueryResult qResult = new QueryResult();
            // set the appropriate result based on the contact category type - instance/personal
            if (buttonIsPersonalContact)
            {
                qResult.setPersonalContactDetails(perCTDetailsResult);
            }
            else
            {
                qResult.setInstanceContactDetails(insCTDetailsResult);
            }
            return new Optional<ExecutionResult>(qResult);
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }
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
