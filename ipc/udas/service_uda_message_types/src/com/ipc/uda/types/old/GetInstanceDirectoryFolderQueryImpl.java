/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.manager.ContactManager;
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
 * This class is responsible for fetching the contact list belonging to the Instance directory from
 * DS
 * 
 * @author Veena Makam
 * 
 */
/*
public class GetInstanceDirectoryFolderQueryImpl extends GetInstanceDirectoryFolderQuery implements
        ExecutableWithContext
{

    private static final long serialVersionUID = -283320010147938153L;

    private static final String contactCategoryTypeStr = "INSTANCE";

    private static final HashMap<Integer, String> contactCategoryTypeMap = new HashMap<Integer, String>();

    private UserContext ctx;

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        SecurityContext basicSecContext = this.ctx.getSecurityContext();

        ContactManager contactMgr = new ContactManager(basicSecContext);
        InstanceDirectoryListResult dlResultObj = null;
        List<Contact> contactList = new ArrayList<Contact>();
        try
        {
            contactList = contactMgr.getAll();
            dlResultObj = new InstanceDirectoryListResult();
            for (Iterator<Contact> iterator = contactList.iterator(); iterator.hasNext();)
            {
                Contact dsContact = iterator.next();

                ContactType dirContact = new ContactType();

                dirContact = this.mapUDAToDSContact(dsContact, dirContact);
                dlResultObj.getContact().add(dirContact);
            }

        }
        catch (final Throwable e)
        {
            // Log.logger().debug("Unable to get Instance contact list from the database", e);
            throw new ExecutionException(e);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setInstanceDirectoryList(dlResultObj);

        return new Optional<ExecutionResult>(qResult);
    }

    private ContactType mapUDAToDSContact(Contact dsContact, ContactType dirContact)
    {
        // mapping between the Contact object of DS to contact object of UDA
        // Directory

        dirContact.setCompanyName(dsContact.getCompany());
        dirContact.setContactId(new UID("" + dsContact.getId()));
        ContactCategoryType contactType = ContactCategoryType
                .valueOf(GetInstanceDirectoryFolderQueryImpl.contactCategoryTypeStr);
        dirContact.setContactType(contactType);
        dirContact.setFirstName(dsContact.getFirstName());
        dirContact.setLastName(dsContact.getLastName());
        return dirContact;
    }
}
*/
