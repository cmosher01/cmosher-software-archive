/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.Directory;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalDirectory;
import com.ipc.ds.entity.dto.Sphere;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.manager.ContactManager;
import com.ipc.ds.entity.manager.DirectoryManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalDirectoryManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

//REVIEW 0 add javadocs - done
/**
 * This class is responsible for getting the list of directories for the given user
 * 
 * @author Veena Makam
 * 
 */
public class GetDirectoryListQueryImpl extends GetDirectoryListQuery implements
        ExecutableWithContext
{
    private UserContext ctx;

    private final DirectoryListResult dirListResult = new DirectoryListResult();

    // REVIEW 0 method too long and complex - done (subdivided into smaller methods)
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        try
        {
            // populate directories and contacts
            populateDirectoryContacts();

            // REVIEW 0 Directory Groups have not been defined yet - done (marked as TODO)
            // TODO: Populate the directory groups as per review comments once they are defined

            // REVIEW 0 should be using ImmutablePersonalContactManager instead -Immutable entities
            // are views not tables, hence they do not have managers, explicit conversion from
            // PersonalContact to ImmutableContactType is done in the method addPersonalContact in
            // the same class

            // populate personal directories and contacts
            populatePersonalDirectories();

        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setDirectoryList(this.dirListResult);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    private void addContact(final Contact contact, final DirectoryListResult contacts)
    {
        try
        {
            ImmutableContactType udaContact = new ImmutableContactType();
            udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(contact, udaContact, this.ctx);
            DataServicesSubscriptionHelper.createSubscriptionsTo(Contact.class.getSimpleName(),
                    contact.getId(), this.ctx);
            contacts.getContact().add(udaContact);
        }
        catch (final Throwable e)
        {
            Log.logger().info("Error reading contact; skipping this contact", e);
        }
    }

    private void addPersonalContact(final PersonalContact contact,
            final DirectoryListResult contacts)
    {
        try
        {
            ImmutableContactType udaContact = new ImmutableContactType();
            udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(contact, udaContact, this.ctx);
            DataServicesSubscriptionHelper.createSubscriptionsTo(PersonalContact.class
                    .getSimpleName(), contact.getId(), this.ctx);
            contacts.getContact().add(udaContact);
        }
        catch (final Throwable e)
        {
            Log.logger().info("Error reading personal contact; skipping this contact", e);
        }
    }

    /**
     * Populates directories and their contacts
     * 
     * @throws ExecutionException
     * 
     */
    private void populateDirectoryContacts() throws ExecutionException
    {
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();

        try
        {
            // REVIEW 0 should be using Immutable instance ContactManager - Immutable entities are
            // views not tables, hence they do not have managers, explicit conversion from Contact
            // to ImmutableContactType is done in the method addContact in the same class
            final ContactManager ctMgr = new ContactManager(basicSecContext);

            final Sphere sphere = UDAAndDSEntityUtil.getSphere(this.ctx);
            if (sphere == null)
            {
                Log.logger().debug("Sphere is Null from DS for " + this.ctx.getUser().getName());
                throw new ExecutionException("Sphere is Null from DS for "
                        + this.ctx.getUser().getName());
            }
            final DirectoryManager dirMgr = new DirectoryManager(basicSecContext);
            if (dirMgr == null)
            {
                Log.logger().debug(
                        "DirectoryManager is Null from DS for " + this.ctx.getUser().getName());
                throw new ExecutionException("DirectoryManager is Null from DS for "
                        + this.ctx.getUser().getName());
            }
            final List<Directory> dirList = dirMgr.getDirectoryFor(sphere);
            if (dirList == null || dirList.isEmpty())
            {
                Log.logger().debug(
                        "Directory list from the database is null or empty for user: "
                                + this.ctx.getUser().getName());
            }
            else
            {
                for (final Directory dirObj : dirList)
                {
                    // REVIEW 0 remove unnecessary variable 'dirObj' - done
                    final List<Contact> ctList = ctMgr.getContactsFor(dirObj);

                    if (ctList == null || ctList.isEmpty())
                    {
                        Log.logger().debug(
                                "Contacts for the Directory ID: " + dirObj.getId()
                                        + " is NULL/Empty");
                        continue;
                    }
                    for (final Contact dsContact : ctList)
                    {
                        if (dsContact == null)
                        {
                            Log.logger().debug(
                                    "Contact is NULL for directory ID: " + dirObj.getId());
                        }
                        else
                        {
                            addContact(dsContact, this.dirListResult);
                        }
                    }
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }

    }

    /**
     * Populates personal directories and its contacts in the result
     * 
     * @throws ExecutionException
     */
    private void populatePersonalDirectories() throws ExecutionException
    {
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();
        final PersonalContactManager perCtMgr = new PersonalContactManager(basicSecContext);
        try
        {
            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            if (usrCDI == null)
            {
                Log.logger().debug(
                        "UserCDI object is Null from DS for user: " + this.ctx.getUser().getName());
                throw new ExecutionException("UserCDI object is Null from DS for user: "
                        + this.ctx.getUser().getName());
            }

            final PersonalDirectoryManager perDirMgr = new PersonalDirectoryManager(basicSecContext);
            if (perDirMgr == null)
            {
                Log.logger().debug(
                        "PersonalDirectoryManager object is Null from DS for user: "
                                + this.ctx.getUser().getName());
                throw new ExecutionException(
                        "PersonalDirectoryManager object is Null from DS for user: "
                                + this.ctx.getUser().getName());
            }
            final PersonalDirectory perDir = perDirMgr.getPersonalDirectoryFor(usrCDI);
            if (perDir == null)
            {
                Log.logger().debug(
                        "PersonalDirectory is NULL for user: " + this.ctx.getUser().getName());
            }
            else
            {
                // REVIEW 0 remove unnecessary variable 'perDir' - done
                if (perDir == null)
                {
                    Log.logger().debug(
                            "PersonalDirectory is NULL for user: " + this.ctx.getUser().getName());
                }
                else
                {
                    final List<PersonalContact> perCtList = perCtMgr.getPersonalContactsFor(perDir);
                    if (perCtList == null || perCtList.isEmpty())
                    {
                        Log.logger().debug(
                                "PersonalContact list is NULL/Empty for PersonalDirectory ID: "
                                        + perDir.getId());
                    }
                    else
                    {
                        for (final PersonalContact perContact : perCtList)
                        {
                            if (perContact == null)
                            {
                                Log.logger().debug(
                                        "PersonalContact is NULL for directory group ID: "
                                                + perDir.getId());
                            }
                            else
                            {
                                addPersonalContact(perContact, this.dirListResult);
                            }
                        }
                    }
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
    }
}
