/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.ContactCategoryType;
import com.ipc.uda.types.ContactType;
import com.ipc.uda.types.QueryResult;
import com.ipc.uda.types.UID;

/**
 * This class is responsible for fetching the contact list of personal directory for a particular
 * user
 * 
 * @author Veena Makam
 * 
 */
/*
public class GetPersonalDirectoryFolderQueryImpl extends GetPersonalDirectoryFolderQuery implements
        ExecutableWithContext
{
    private static final long serialVersionUID = -5926529090900369281L;

    private static final String personalDirCategoryType = "PERSONAL";

    private UserContext ctx;

    public GetPersonalDirectoryFolderQueryImpl()
    {

    }

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        SecurityContext basicSecContext = this.ctx.getSecurityContext();

        PersonalContactManager contactMgr = new PersonalContactManager(basicSecContext);
        PersonalDirectoryListResult dlResultObj = null;
        List<PersonalContact> contactList = new ArrayList<PersonalContact>();
        try
        {
            contactList = contactMgr.getAll();
            dlResultObj = new PersonalDirectoryListResult();
            for (Iterator<PersonalContact> iterator = contactList.iterator(); iterator.hasNext();)
            {
                PersonalContact dsContact = iterator.next();

                ContactType dirContact = new ContactType();

                dirContact = this.mapUDAToDSContact(dsContact, dirContact);
                dlResultObj.getContact().add(dirContact);
            }
        }
        catch (final Throwable e)
        {
            Log.logger().debug("Unable to get personal contact list from the database", e);
            throw new ExecutionException(e);
        }
        final QueryResult qResult = new QueryResult();
        qResult.setPersonalDirectoryList(dlResultObj);

        return new Optional<ExecutionResult>(qResult);

    }

    private ContactType mapUDAToDSContact(PersonalContact dsContact, ContactType dirContact)
    {
        // mapping between the Contact object of DS to contact object of UDA
        // Directory

        dirContact.setCompanyName(dsContact.getCompany());
        dirContact.setContactId(new UID("" + dsContact.getId()));
        ContactCategoryType contactType = ContactCategoryType.valueOf(personalDirCategoryType);
        dirContact.setContactType(contactType);
        dirContact.setFirstName(dsContact.getFirstName());
        dirContact.setLastName(dsContact.getLastName());
        return dirContact;
    }
}
*/
