/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.Directory;
import com.ipc.ds.entity.dto.DirectoryCategory;
import com.ipc.ds.entity.dto.DirectoryTree;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalDirectory;
import com.ipc.ds.entity.manager.ContactManager;
import com.ipc.ds.entity.manager.DirectoryCategoryManager;
import com.ipc.ds.entity.manager.DirectoryManager;
import com.ipc.ds.entity.manager.DirectoryTreeManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalDirectoryManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

public class GetDirectoryCategoryContentsQueryImpl extends GetDirectoryCategoryContentsQuery
        implements ExecutableWithContext
{
    DirectoryCategoryContentsType dirCatContents;
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();
        final DirectoryManager dirMgr = new DirectoryManager(basicSecContext);

        // validate input
        if (this.getCategoryName() == null || this.getDirectoryID() == null)
        {
            Log.logger().debug("DirectoryID/CategoryName input from UDAC is NULL");
            returnNothing();
        }
        if (dirMgr == null)
        {
            Log.logger().debug(
                    "DirectoryManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            returnNothing();
        }
        try
        {
            // populate Instance DirectoryCategoryContents
            if (this.getDirectoryType() != DirectoryContactType.PERSONAL)
            {
                final Directory instanceDir = dirMgr.getById(Integer.parseInt(this.getDirectoryID()
                        .toString()));
                if (instanceDir != null)
                {
                    populateDirectoryCatContents(instanceDir);
                }
                else
                {
                    Log.logger().debug(
                            "Directory is NULL from DS for the input DirectoryID: "
                                    + this.getDirectoryID().toString());
                    returnNothing();
                }
            }
            else
            // populate Personal DirectoryCategoryContents
            {
                final PersonalDirectoryManager perDirMgr = new PersonalDirectoryManager(
                        basicSecContext);
                if (perDirMgr == null)
                {
                    Log.logger().debug(
                            "PersonalDirectoryManager object is Null from DS for user "
                                    + this.ctx.getUser().getName());
                    returnNothing();
                }
                final PersonalDirectory perDir = perDirMgr.getById(Integer.parseInt(this
                        .getDirectoryID().toString()));
                if (perDir != null)
                {
                    populateDirectoryCatContents(perDir);
                }
                else if (perDir == null)
                {
                    Log.logger().debug(
                            "PersonalDirectory is NULL from DS for the input DirectoryID: "
                                    + this.getDirectoryID().toString());
                    returnNothing();
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }

        final QueryResult qResult = new QueryResult();
        qResult.setDirectoryCategoryContents(this.dirCatContents);

        return new Optional<ExecutionResult>(qResult);
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * Populates DirectoryCategoryContents for Directory
     * 
     * @param instanceDir Directory
     * @throws ExecutionException
     */
    private void populateDirectoryCatContents(final Directory instanceDir)
            throws ExecutionException
    {
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();
        final DirectoryTreeManager dirTreeMgr = new DirectoryTreeManager(basicSecContext);
        this.dirCatContents = new DirectoryCategoryContentsType();
        if (dirTreeMgr == null)
        {
            Log.logger().debug(
                    "DirectoryTreeManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            returnNothing();
        }

        try
        {
            final DirectoryTree dirTree = dirTreeMgr.getDirectoryTreeFor(instanceDir);

            if (dirTree == null)
            {
                Log.logger().debug(
                        "DirectoryCategoryManager object is Null from DS for user "
                                + this.ctx.getUser().getName());
            }
            else
            {
                final DirectoryCategoryManager dirCatMgr = new DirectoryCategoryManager(
                        basicSecContext);
                if (dirCatMgr == null)
                {
                    Log.logger().debug(
                            "DirectoryCategoryManager object is Null from DS for user "
                                    + this.ctx.getUser().getName());
                    returnNothing();
                }

                DirectoryCategory dirCat = dirCatMgr.getDirectoryCategoryFor(dirTree);
                if (dirCat == null)
                {
                    Log.logger()
                            .debug(
                                    "DirectoryCategory is NULL for the directoryTree ID "
                                            + dirTree.getId());
                    returnNothing();
                }
                else
                {
                    if (dirCat.getName() != null)
                    {
                        if (dirCat.getName().equals(this.getCategoryName()))
                        {
                            CategoryType catType = new CategoryType();
                            catType.setDirectoryID(new UID("" + dirTree.getId()));
                            catType.setName(dirCat.getName());
                            catType.setDirectoryType(DirectoryContactType.INSTANCE);
                            this.dirCatContents.getCategories().add(catType);
                            final ContactManager ctMgr = new ContactManager(basicSecContext);
                            if (ctMgr == null)
                            {
                                Log.logger().debug(
                                        "ContactManager object is Null from DS for user "
                                                + this.ctx.getUser().getName());
                                returnNothing();
                            }
                            final List<Contact> ctList = ctMgr.getContactsFor(dirTree);
                            if (ctList == null || ctList.isEmpty())
                            {
                                Log.logger().debug(
                                        "Contact list is NULL/Empty for Directory ID: "
                                                + dirTree.getId());
                            }
                            else
                            {
                                for (final Contact contact : ctList)
                                {
                                    final Contact dsContact = contact;
                                    if (dsContact == null)
                                    {
                                        Log.logger().debug(
                                                "Contact is NULL for DirectoryTree ID "
                                                        + dirTree.getId());
                                        continue;
                                    }
                                    ImmutableContactType udaContact = new ImmutableContactType();
                                    udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(dsContact,
                                            udaContact, this.ctx);
                                    this.dirCatContents.getContacts().add(udaContact);
                                }
                            }

                            final List<DirectoryTree> subDirTreeList = dirTreeMgr
                                    .getDirectoryTreeSelvesFor(dirTree);
                            if (subDirTreeList == null || subDirTreeList.isEmpty())
                            {
                                Log.logger().debug(
                                        "DirectoryTree list is NULL/Empty for Directory ID: "
                                                + dirTree.getId());
                            }

                            for (final DirectoryTree directoryTree2 : subDirTreeList)
                            {
                                final DirectoryTree subDirTree = directoryTree2;
                                if (subDirTree == null)
                                {
                                    Log.logger().debug(
                                            "DirectoryTree subtree is NULL for DirectoryTree ID: "
                                                    + dirTree.getId());
                                    continue;
                                }
                                dirCat = dirCatMgr.getDirectoryCategoryFor(subDirTree);
                                catType = new CategoryType();
                                catType.setDirectoryID(new UID("" + subDirTree.getId()));
                                catType.setName(dirCat.getName());

                                catType.setDirectoryType(DirectoryContactType.INSTANCE);
                                this.dirCatContents.getCategories().add(catType);

                                final List<Contact> subTreeCtList = ctMgr
                                        .getContactsFor(subDirTree);
                                if (subTreeCtList == null || subTreeCtList.isEmpty())
                                {
                                    Log.logger().debug(
                                            "Contact list is NULL/Empty for Directory ID: "
                                                    + subDirTree.getId());
                                }
                                else
                                {
                                    for (final Contact contact : subTreeCtList)
                                    {
                                        final Contact subTreeDSContact = contact;
                                        if (subTreeDSContact == null)
                                        {
                                            Log.logger().debug(
                                                    "Contact is NULL for DirectoryTree ID: "
                                                            + subDirTree.getId());
                                            continue;
                                        }
                                        ImmutableContactType udaContact = new ImmutableContactType();
                                        udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(
                                                subTreeDSContact, udaContact, this.ctx);
                                        this.dirCatContents.getContacts().add(udaContact);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        catch (final Throwable lEx)
        {
            throw new ExecutionException(lEx);
        }

    }

    /**
     * Populates DirectoryCategoryContents for PersonalDirectory
     * 
     * @param perDir PersonalDirectory
     * @throws ExecutionException
     */
    private void populateDirectoryCatContents(final PersonalDirectory perDir)
            throws ExecutionException
    {
        this.dirCatContents = new DirectoryCategoryContentsType();
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();
        final CategoryType catType = new CategoryType();
        catType.setDirectoryID(new UID("" + perDir.getId()));
        catType.setName(perDir.getName());
        catType.setDirectoryType(DirectoryContactType.PERSONAL);
        this.dirCatContents.getCategories().add(catType);
        final PersonalContactManager perCtMgr = new PersonalContactManager(basicSecContext);
        if (perCtMgr == null)
        {
            Log.logger().debug(
                    "PersonalContactManager object is Null from DS for user "
                            + this.ctx.getUser().getName());
            returnNothing();
        }
        try
        {
            final List<PersonalContact> perCtList = perCtMgr.getPersonalContactsFor(perDir);
            if (perCtList == null || perCtList.isEmpty())
            {
                Log.logger().debug(
                        "PersonalContact list is NULL/Empty for Directory ID: " + perDir.getId());
            }
            else
            {
                for (final PersonalContact personalContact : perCtList)
                {
                    final PersonalContact perContact = personalContact;
                    if (perContact == null)
                    {
                        Log.logger().debug(
                                "PersonalContact is NULL for personal directory ID: "
                                        + perDir.getId());
                        continue;
                    }
                    ImmutableContactType udaContact = new ImmutableContactType();
                    udaContact = UDAAndDSEntityUtil.mapDSToUDAContact(perContact, udaContact,
                            this.ctx);
                    this.dirCatContents.getContacts().add(udaContact);
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }

    }

    private Optional<ExecutionResult> returnNothing()
    {
        return new Nothing<ExecutionResult>();
    }
}
