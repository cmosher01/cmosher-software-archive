/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.CommunicationHistory;
import com.ipc.ds.entity.dto.CommunicationHistoryNote;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.ContactPointOfContactImmutable;
import com.ipc.ds.entity.dto.Enterprise;
import com.ipc.ds.entity.dto.Instance;
import com.ipc.ds.entity.dto.MFUBucket;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalContactPersonalPointOfContactImmutable;
import com.ipc.ds.entity.dto.PersonalDirectory;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.dto.PreferredPOC;
import com.ipc.ds.entity.dto.PreferredPersonalPOC;
import com.ipc.ds.entity.dto.Sphere;
import com.ipc.ds.entity.dto.User;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserUDA;
import com.ipc.ds.entity.dto.Zone;
import com.ipc.ds.entity.internal.manager.base.PersonalPointOfContactBaseManager;
import com.ipc.ds.entity.internal.manager.base.PreferredPersonalPOCBaseManager;
import com.ipc.ds.entity.manager.CommunicationHistoryNoteManager;
import com.ipc.ds.entity.manager.ContactManager;
import com.ipc.ds.entity.manager.ContactPointOfContactImmutableManager;
import com.ipc.ds.entity.manager.EnterpriseManager;
import com.ipc.ds.entity.manager.InstanceManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalContactPersonalPointOfContactImmutableManager;
import com.ipc.ds.entity.manager.PersonalDirectoryManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.ds.entity.manager.PointOfContactManager;
import com.ipc.ds.entity.manager.PreferredPOCManager;
import com.ipc.ds.entity.manager.PreferredPersonalPOCManager;
import com.ipc.ds.entity.manager.SphereManager;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.ds.entity.manager.UserManager;
import com.ipc.ds.entity.manager.UserUDAManager;
import com.ipc.ds.entity.manager.ZoneManager;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.CallCategoryType;
import com.ipc.uda.types.CenterlineMessageEvent;
import com.ipc.uda.types.ContactCategoryType;
import com.ipc.uda.types.ContactType;
import com.ipc.uda.types.DateTime;
import com.ipc.uda.types.DirectoryContactType;
import com.ipc.uda.types.EnumMediaType;
import com.ipc.uda.types.Event;
import com.ipc.uda.types.HistoryType;
import com.ipc.uda.types.ImmutableContactType;
import com.ipc.uda.types.MfuPointOfContactType;
import com.ipc.uda.types.PocMediaTypeType;
import com.ipc.uda.types.PocTypeType;
import com.ipc.uda.types.PointOfContactType;
import com.ipc.uda.types.PreferredPointOfContactType;
import com.ipc.uda.types.UID;
import com.ipc.uda.types.ContactType.PointsOfContact;

/**
 * @author Veena Makam
 * 
 */
public class UDAAndDSEntityUtil
{
    /**
     * This method is responsible for getting the Enterprise associated with the user
     * 
     * @param ctx UserContext
     * @return Enterprise
     * @throws Exception
     */
    public static Enterprise getEnterpriseForUser(final UserContext ctx) throws Exception
    {
        final SecurityContext basicSecContext = ctx.getSecurityContext();
        final User user = UDAAndDSEntityUtil.getUser(ctx);
        final EnterpriseManager dsEntpMgr = new EnterpriseManager(basicSecContext);
        final Enterprise dsEnterprise = dsEntpMgr.findByUsers(user);
        if (dsEnterprise == null)
        {
            throw new ExecutionException("Enterprise from DS is NULL for the given user"
                    + ctx.getUser().getName());
        }
        return dsEnterprise;
    }

    /**
     * Returns personal contacts of the given user
     * 
     * @param userAor (not used)
     * @param ctx user's context
     * @return List<ContactType> udaContactList
     * @throws ExecutionException if anything goes wrong
     */
    public static List<ContactType> getPersonalContacts(final String userAor, final UserContext ctx)
            throws ExecutionException
    {
        List<ContactType> udaContactList = null;

        udaContactList = new ArrayList<ContactType>();
        final SecurityContext secCtx = ctx.getSecurityContext();
        final PersonalDirectoryManager pdMgr = new PersonalDirectoryManager(secCtx);
        final PersonalContactManager contactMgr = new PersonalContactManager(secCtx);
        final PersonalPointOfContactManager ppocMgr = new PersonalPointOfContactManager(secCtx);

        try
        {
            final UserCDI userCDI = UDAAndDSEntityUtil.getUserCDI(ctx);
            final PersonalDirectory pd = pdMgr.getPersonalDirectoryFor(userCDI);
            final List<PersonalContact> contactList = contactMgr.getPersonalContactsFor(pd);

            // For each PersonalContact
            for (final PersonalContact contact : contactList)
            {
                // first, attempt to subscribe for events for this
                // PersonalContact
                DataServicesSubscriptionHelper.createSubscriptionsTo(PersonalContact.class
                        .getSimpleName(), contact.getId(), ctx);

                final ContactType udaContact = new ContactType();
                udaContact.setPointsOfContact(new PointsOfContact());

                udaContact.setContactId(new UID(String.valueOf(contact.getId())));
                udaContact.setFirstName(contact.getFirstName());
                udaContact.setLastName(contact.getLastName());
                udaContact.setCompanyName(contact.getCompany());
                udaContact.setTitle(contact.getTitle());
                udaContact.setContactType(ContactCategoryType.PERSONAL);
                if (contact.getLocale() != null)
                {
                    udaContact.setCountry(contact.getLocale().toString());
                }
                udaContact.setBusinessGroup(contact.getBusinessGroup());

                final List<PersonalPointOfContact> pcpocList = ppocMgr
                        .getPersonalPointOfContactsFor(contact);

                // Get all of the PersonalPointsOfContact
                for (final PersonalPointOfContact pcpoc : pcpocList)
                {
                    final PointOfContactType udaPoc = new PointOfContactType();
                    udaPoc.setContactId(pcpoc.getId());
                    udaPoc.setData(pcpoc.getData());
                    udaPoc.setDescriptor(pcpoc.getShortDescriptor());
                    if (pcpoc.getMediaType() != null)
                    {
                        udaPoc.setMediaType(EnumMediaType
                                .fromValue(pcpoc.getMediaType().toString()));
                    }
                    // need to revisit - doesn't exist in PersonalPointOfContact
                    // Entity
                    // Defaulting to false
                    udaPoc.setDefault(false);

                    udaContact.getPointsOfContact().getPointOfContact().add(udaPoc);

                }

                udaContactList.add(udaContact);
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(
                    "Unable to get personal contacts from the DS for the user: "
                            + ctx.getUser().getName());
        }

        return udaContactList;
    }

    /**
     * Returns the Sphere object for the given user
     * 
     * @param userCtx UserContext
     * @return Sphere sphObj
     * @throws Exception
     */
    public static Sphere getSphere(final UserContext userCtx) throws Exception
    {
        Sphere sphObj = null;
        final SphereManager spMgr = new SphereManager(userCtx.getSecurityContext());
        if (spMgr == null)
        {
            Log.logger().debug(
                    "SphereManager object is Null from DS for user " + userCtx.getUser().getName());
            throw new Exception("SphereManager object is Null from DS for user "
                    + userCtx.getUser().getName());
        }

        final User userUDA = UDAAndDSEntityUtil.getUser(userCtx);
        if (userUDA == null)
        {
            Log.logger().debug(
                    "User object is Null from DS for user " + userCtx.getUser().getName());
            throw new Exception("User object is Null from DS for user "
                    + userCtx.getUser().getName());
        }
        sphObj = spMgr.getSphereFor(userUDA);

        return sphObj;
    }

    /**
     * Returns the User object for the specified user
     * 
     * @param userCtx UserContext
     * @return User usrUDA
     * @throws Exception
     */
    public static User getUser(final UserContext userCtx) throws Exception
    {
        User usrUDA = null;
        final SecurityContext secCtx = userCtx.getSecurityContext();
        final UserManager dsUsrMgr = new UserManager(secCtx);
        if (dsUsrMgr == null)
        {
            Log.logger().debug("UserManager is Null from DS for user " + userCtx.getUser());
            throw new Exception("UserManager is Null from DS for user " + userCtx.getUser());
        }
        else
        {
            usrUDA = dsUsrMgr.findByLoginNameEqualing(userCtx.getUser().getName());
            if (usrUDA == null)
            {
                Log.logger().debug("User is Null/Empty from DS for user " + userCtx.getUser());
                throw new Exception("User is Null/Empty from DS for user " + userCtx.getUser());
            }
        }

        return usrUDA;
    }

    /**
     * Returns the UserCDI object for the current user
     * 
     * @return UserCDI usrCDI
     * @throws Exception
     */
    public static UserCDI getUserCDI(final UserContext userCtx) throws Exception
    {
        UserCDI usrCDI = null;
        final User userUDA = UDAAndDSEntityUtil.getUser(userCtx);
        final UserCDIManager dsCDIMgr = new UserCDIManager(userCtx.getSecurityContext());
        if (dsCDIMgr == null)
        {
            Log.logger().debug("UserCDIManager is Null from DS for user " + userCtx.getUser());
            throw new Exception("UserCDIManager is Null from DS for user " + userCtx.getUser());
        }
        else
        {
            usrCDI = dsCDIMgr.getUserCDIFor(userUDA);
            if (usrCDI == null)
            {
                throw new Exception("UserCDI is null for user: " + userCtx.getUserName());
            }
        }
        return usrCDI;
    }

    /**
     * Returns the UserUDA object for the given user
     * 
     * @param userCtx
     * @return {@link UserUDA} DS entity
     * @throws Exception
     */
    // TODO maybe move this method in UserContext class
    public static UserUDA getUserUDA(final UserContext userCtx) throws Exception
    {
        UserUDA usrUDA = null;
        final UserUDAManager usrUDAMgr = new UserUDAManager(userCtx.getSecurityContext());
        final User user = UDAAndDSEntityUtil.getUser(userCtx);

        if (user == null)
        {
            Log.logger().debug(
                    "User object is Null from DS for user " + userCtx.getUser().getName());
            throw new Exception("User object is Null from DS for user "
                    + userCtx.getUser().getName());
        }
        else
        {
            usrUDA = usrUDAMgr.getUserUDAFor(user);
        }

        return usrUDA;
    }

    /**
     * This method is responsible for getting the zone associated with the user
     * 
     * @param ctx UserContext
     * @return Zone
     * @throws Exception
     */

    public static Zone GetZoneForTheUser(final UserContext ctx) throws Exception
    {
        final SecurityContext basicSecContext = ctx.getSecurityContext();

        final Enterprise dsEnterprise = UDAAndDSEntityUtil.getEnterpriseForUser(ctx);
        final InstanceManager dsInstMgr = new InstanceManager(basicSecContext);
        final List<Instance> lstInstances = dsInstMgr.getInstancesFor(dsEnterprise);
        if (lstInstances == null || lstInstances.size() == 0)
        {
            throw new ExecutionException("lstInstances from DS is NULL for the given user"
                    + ctx.getUser().getName());
        }
        final Instance instance = lstInstances.get(0);

        final ZoneManager dsZoneMgr = new ZoneManager(basicSecContext);
        final List<Zone> lstZones = dsZoneMgr.getZonesFor(instance);
        if (lstZones == null || lstZones.size() == 0)
        {
            throw new ExecutionException(
                    "lstZones from DS is NULL or of Size Zero for the given user"
                            + ctx.getUser().getName());
        }
        final Zone zone = lstZones.get(0);
        return zone;
    }

    /**
     * Maps the DS CommunicationHistory object with the UDA HistoryType
     * 
     * @param commHistDS CommunicationHistory
     * @param udaCommHist HistoryType
     * @param userContext UserContext
     * @return HistoryType udaCommHist
     * @throws ExecutionException
     */
    public static HistoryType mapDSToUDACommHist(final CommunicationHistory commHistDS,
            final HistoryType udaCommHist, final UserContext userContext) throws ExecutionException
    {
        final SecurityContext basSecCtx = userContext.getSecurityContext();
        final CommunicationHistoryNoteManager commHistNoteMgr = new CommunicationHistoryNoteManager(
                basSecCtx);
        if (commHistNoteMgr == null)
        {
            Log.logger().debug(
                    "CommunicationHistoryNoteManager object is Null from DS for user "
                            + userContext.getUser().getName());
            throw new ExecutionException(
                    "CommunicationHistoryNoteManager object is Null from DS for user "
                            + userContext.getUser().getName());
        }

        try
        {
            final List<CommunicationHistoryNote> commHistNoteLst = commHistNoteMgr
                    .getCommunicationHistoryNotesFor(commHistDS);
            if (commHistNoteLst == null || commHistNoteLst.isEmpty())
            {
                Log.logger().debug(
                        "CommunicationHistoryNote list is NULL/empty for the CommunicationHistory ID: "
                                + commHistDS.getId());
                throw new Exception(
                        "CommunicationHistoryNote list is NULL/empty for the CommunicationHistory ID: "
                                + commHistDS.getId());
            }
            else
            {
                final CommunicationHistoryNote commHistNote = commHistNoteLst.get(0);
                // Contact ctDS =
                // ctMgr.getById(Integer.parseInt(this.buttonId.toString()));
                udaCommHist.setTime(new DateTime(
                        new Date(((long) commHistDS.getStartTime()) * 1000)));
                udaCommHist.setHistoryId(new UID(commHistDS.getId() + ""));
                udaCommHist.setNotes(commHistNote.getNote());
                udaCommHist.setName(commHistDS.getCLIName());
                // udaCommHist.setCompany(commHistDS.getCompany());

                udaCommHist.setCallType(CallCategoryType.valueOf(commHistDS.getEventType().name()));
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException("Unable to get CommunicationHistory for Button");
        }
        return udaCommHist;
    }

    /**
     * Maps the Contact object of DS with UDA ContactType
     * 
     * @param dsContact Contact
     * @param udaContact ContactType
     * @param context Object
     * @return ContactType udaContact
     * @throws ExecutionException
     */
    public static ContactType mapDSToUDAContact(final Contact dsContact,
            final ContactType udaContact, final UserContext context) throws ExecutionException
    {
        // mapping between the Contact object of DS to contact object of UDA
        // Directory

        udaContact.setCompanyName(dsContact.getCompany());
        udaContact.setContactId(new UID("" + dsContact.getId()));
        udaContact.setFirstName(dsContact.getFirstName());
        udaContact.setLastName(dsContact.getLastName());
        udaContact.setContactCategories(ContactCategoryType.INSTANCE.value());
        udaContact.setContactType(ContactCategoryType.INSTANCE);
        udaContact.setUserAor("");

        final PointOfContactManager pocMgr = new PointOfContactManager(context.getSecurityContext());

        if (pocMgr == null)
        {
            Log.logger()
                    .debug(
                            "PointOfContactManager is Null from DS for user "
                                    + context.getUser().getName());
            throw new ExecutionException("PointOfContactManager is Null from DS for user "
                    + context.getUser().getName());
        }
        List<PointOfContact> pocDSList = null;
        try
        {
            pocDSList = pocMgr.getPointOfContactsFor(dsContact);
            if (pocDSList == null || pocDSList.isEmpty())
            {
                Log.logger().debug(
                        "PointOfContact from the database is null or empty for contact ID: "
                                + dsContact.getId());
            }
            else
            {
                ContactType.PointsOfContact udaPoC = null;
                final PreferredPOCManager prePOCMgr = new PreferredPOCManager(context
                        .getSecurityContext());
                final PreferredPOC prePerPOC = prePOCMgr.getPreferredPOCForContactId(dsContact
                        .getId());
                for (final PointOfContact pointOfContact : pocDSList)
                {
                    final PointOfContact pocDS = pointOfContact;
                    if (pocDS != null)
                    {
                        if (udaContact.getPointsOfContact() == null)
                        {
                            udaContact
                                    .setPointsOfContact(udaPoC = new ContactType.PointsOfContact());
                        }

                        final PointOfContactType udaPOCType = new PointOfContactType();

                        // map DS PointOfContact with UDA PointOfContact
                        udaPOCType.setContactId(dsContact.getId());
                        udaPOCType.setData(pocDS.getData());
                        if (pocDS.getId() == prePerPOC.getId())
                        {
                            udaPOCType.setDefault(true);
                        }
                        else
                        {
                            udaPOCType.setDefault(false);
                        }
                        udaPOCType.setDescriptor(pocDS.getShortDescriptor());
                        if (pocDS.getMediaType() != null)
                        {
                            udaPOCType.setMediaType(EnumMediaType.fromValue(pocDS.getMediaType()
                                    .name()));
                            if (pocDS.getMediaType().name().equalsIgnoreCase(
                                    EnumMediaType.ICM.toString()))
                            {
                                if (pocDS.getData() != null)
                                {
                                    udaContact.setUserAor(pocDS.getData());
                                }
                                else
                                {
                                    Log.logger().debug(
                                            "POC Data is NULL for contact ID: "
                                                    + udaPOCType.getContactId() + " for user: "
                                                    + context.getUserName());
                                }
                            }

                        }
                        udaPoC.getPointOfContact().add(udaPOCType);
                    }
                }
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        return udaContact;
    }

    /**
     * Maps the Contact entity object with the UDA ImmutableContactType object
     * 
     * @param dsContact Contact
     * @param udaContact ImmutableContactType
     * @param userContext UserContext
     * @throws ExecutionException
     */
    public static ImmutableContactType mapDSToUDAContact(final Contact dsContact,
            ImmutableContactType udaContact, final UserContext userContext)
            throws ExecutionException
    {
        final SecurityContext basicSecContext = userContext.getSecurityContext();

        // map DS personal contact to ImmutableContactType object
        udaContact = EntityHelper.fromDSContactToImmutableContact(dsContact);
        udaContact.setBusinessGroup(dsContact.getBusinessGroup());
        udaContact.setCompanyName(dsContact.getCompany());
        udaContact.setContactId(new UID("" + dsContact.getId()));
        udaContact.setFirstName(dsContact.getFirstName());
        udaContact.setLastName(dsContact.getLastName());
        udaContact.setContactType(DirectoryContactType.INSTANCE);

        try
        {
            final PreferredPointOfContactType udaPrePOC = new PreferredPointOfContactType();
            final ContactPointOfContactImmutableManager pocImmMgr = new ContactPointOfContactImmutableManager(
                    basicSecContext);
            final List<ContactPointOfContactImmutable> pocImmList = pocImmMgr
                    .findByContactIdEqualTo(dsContact.getId());
            if (pocImmList == null || pocImmList.isEmpty())
            {
                Log.logger().debug(
                        "ContactPointOfContactImmutable list object is Null from DS for contactID "
                                + dsContact.getId() + " for user "
                                + userContext.getUser().getName());
            }
            else
            {
                final ContactPointOfContactImmutable dsPOC = pocImmList.get(0);
                if (dsPOC != null)
                {
                    if (dsPOC.getMediaType() != null)
                    {
                        udaPrePOC.setMediaType(PocMediaTypeType.fromValue(dsPOC.getMediaType()
                                .name()));
                    }

                    if (dsPOC.getPOCType() != null)
                    {
                        udaPrePOC.setPointOfContactType(PocTypeType.fromValue(dsPOC.getPOCType()
                                .name()));
                    }
                    udaPrePOC.getDescriptor().add(dsPOC.getShortDescriptor());
                    udaPrePOC.setData(dsPOC.getData());
                    udaContact.setPreferredPointOfContact(udaPrePOC);
                    udaContact.setUserAOR(UDAAndDSEntityUtil.getUserAOR(userContext, udaContact));
                }
                else
                {
                    Log.logger().debug(
                            "ContactPointOfContactImmutable is Null from DS for contactID "
                                    + dsContact.getId() + " for user "
                                    + userContext.getUser().getName());
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
        return udaContact;
    }

    /**
     * Maps the PersonalContact object of DS with UDA ContactType
     * 
     * @param dsContact PersonalContact
     * @param udaContact ContactType
     * @param userContext Object
     * @return ContactType udaContact
     * @throws ExecutionException
     */
    public static ContactType mapDSToUDAContact(final PersonalContact dsContact,
            final ContactType udaContact, final UserContext userContext) throws ExecutionException
    {
        // mapping between the Contact object of DS to contact object of UDA
        // Directory

        udaContact.setContactId(new UID("" + dsContact.getId()));
        udaContact.setBusinessGroup(dsContact.getBusinessGroup());
        udaContact.setContactCategories(ContactCategoryType.PERSONAL.value());
        udaContact.setContactType(ContactCategoryType.PERSONAL);
        udaContact.setCompanyName(dsContact.getCompany());
        udaContact.setFirstName(dsContact.getFirstName());
        udaContact.setLastName(dsContact.getLastName());
        udaContact.setUserAor("");

        if (dsContact.getLocale() != null)
        {
            udaContact.setCountry(dsContact.getLocale().toString());
        }
        udaContact.setTitle(dsContact.getTitle());

        final PersonalPointOfContactManager pocMgr = new PersonalPointOfContactManager(userContext
                .getSecurityContext());

        if (pocMgr == null)
        {
            Log.logger().debug(
                    "PersonalPointOfContactManager is Null from DS for user "
                            + userContext.getUser().getName());
            throw new ExecutionException("PersonalPointOfContactManager is Null from DS for user "
                    + userContext.getUser().getName());
        }
        // PointsOfContact
        try
        {

            final List<PersonalPointOfContact> ppocList = pocMgr
                    .getPersonalPointOfContactsFor(dsContact);
            if (ppocList == null || ppocList.isEmpty())
            {
                Log.logger().debug(
                        "PersonalPointOfContact from the database is null or empty for contact ID: "
                                + dsContact.getId());
            }
            else
            {
                ContactType.PointsOfContact udaPoC = null;
                final PreferredPersonalPOCManager prePerPOCMgr = new PreferredPersonalPOCManager(
                        userContext.getSecurityContext());
                final PreferredPersonalPOC prePerPOC = prePerPOCMgr
                        .getPreferredPersonalPOCForPersonalContactId(dsContact.getId());
                for (final PersonalPointOfContact ppoc : ppocList)
                {
                    if (ppoc != null)
                    {
                        if (udaContact.getPointsOfContact() == null)
                        {
                            udaContact
                                    .setPointsOfContact(udaPoC = new ContactType.PointsOfContact());
                        }
                        final PointOfContactType udaPOCType = new PointOfContactType();
                        udaPOCType.setContactId(ppoc.getId());
                        udaPOCType.setData(ppoc.getData());

                        if (ppoc.getId() == prePerPOC.getId())
                        {
                            udaPOCType.setDefault(true);
                        }
                        else
                        {
                            udaPOCType.setDefault(false);
                        }
                        udaPOCType.setDescriptor(ppoc.getShortDescriptor());
                        if (ppoc.getMediaType() != null)
                        {
                            udaPOCType.setMediaType(EnumMediaType.fromValue(ppoc.getMediaType()
                                    .name()));
                            if (ppoc.getMediaType().name().equalsIgnoreCase(
                                    EnumMediaType.ICM.toString()))
                            {
                                if (ppoc.getData() != null)
                                {
                                    udaContact.setUserAor(ppoc.getData());
                                }
                                else
                                {
                                    Log.logger().debug(
                                            "PersonalPOC Data is NULL for contact ID: "
                                                    + udaPOCType.getContactId() + " for user: "
                                                    + userContext.getUserName());
                                }
                            }
                        }
                        udaPoC.getPointOfContact().add(udaPOCType);
                    }
                }
            }
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        return udaContact;
    }

    /**
     * Maps the PersonalContact entity object with the UDA ImmutableContactType object
     * 
     * @param dsContact PersonalContact
     * @param udaContact ImmutableContactType
     * @param userContext UserContext
     * @throws ExecutionException
     */
    public static ImmutableContactType mapDSToUDAContact(final PersonalContact dsContact,
            ImmutableContactType udaContact, final UserContext userContext)
            throws ExecutionException
    {
        final SecurityContext basicSecContext = userContext.getSecurityContext();

        // map DS personal contact to ImmutableContactType object
        udaContact = EntityHelper.fromDSPersonalContactToImmutableContact(dsContact);
        udaContact.setBusinessGroup(dsContact.getBusinessGroup());
        udaContact.setCompanyName(dsContact.getCompany());
        udaContact.setContactId(new UID("" + dsContact.getId()));
        udaContact.setFirstName(dsContact.getFirstName());
        udaContact.setLastName(dsContact.getLastName());
        udaContact.setContactType(DirectoryContactType.PERSONAL);

        try
        {
            final PreferredPointOfContactType udaPrePOC = new PreferredPointOfContactType();
            final PersonalContactPersonalPointOfContactImmutableManager perPOCMgr = new PersonalContactPersonalPointOfContactImmutableManager(
                    basicSecContext);
            if (perPOCMgr == null)
            {
                Log.logger().debug(
                        "PersonalContactPersonalPointOfContactImmutableManager object is Null from DS for user "
                                + userContext.getUser().getName());
                throw new ExecutionException(
                        "PersonalContactPersonalPointOfContactImmutableManager object is Null from DS for user "
                                + userContext.getUser().getName());
            }

            final List<PersonalContactPersonalPointOfContactImmutable> perPOCImmList = perPOCMgr
                    .findByPersonalContactIdEqualTo(dsContact.getId());
            if (perPOCImmList == null || perPOCImmList.isEmpty())
            {
                Log.logger().debug(
                        "PersonalContactPersonalPointOfContactImmutable list object is Null from DS for contactID "
                                + dsContact.getId() + " for user "
                                + userContext.getUser().getName());

            }
            else
            {
                final PersonalContactPersonalPointOfContactImmutable dsPOC = perPOCImmList.get(0);
                if (dsPOC == null)
                {
                    Log.logger().debug(
                            "PersonalContactPersonalPointOfContactImmutable is Null from DS for contactID "
                                    + dsContact.getId() + " for user "
                                    + userContext.getUser().getName());
                }
                else
                {
                    if (dsPOC.getMediaType() != null)
                    {
                        udaPrePOC.setMediaType(PocMediaTypeType.fromValue(dsPOC.getMediaType()
                                .name()));
                    }
                    if (dsPOC.getPOCType() != null)
                    {
                        udaPrePOC.setPointOfContactType(PocTypeType.fromValue(dsPOC.getPOCType()
                                .name()));
                    }
                    udaPrePOC.getDescriptor().add(dsPOC.getShortDescriptor());
                    udaPrePOC.setData(dsPOC.getData());
                    udaContact.setPreferredPointOfContact(udaPrePOC);
                    udaContact.setUserAOR(UDAAndDSEntityUtil.getUserAOR(userContext, udaContact));
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
        return udaContact;
    }

    /**
     * Maps DS MFUBucket to the UDA entity
     * 
     * @param mfuBucket MFUBucket
     * @param ctx UserContext
     * @return MfuPointOfContactType mfuContactType
     * @throws ExecutionException
     * @throws InvalidContextException
     * @throws StorageFailureException
     */
    public static MfuPointOfContactType mapDStoUDAMFUBucket(final MFUBucket mfuBucket,
            final UserContext ctx) throws ExecutionException, InvalidContextException,
            StorageFailureException
    {

        final MfuPointOfContactType mfuContactType = new MfuPointOfContactType();
        ContactCategoryType contactCategory = null;
        final UID uid = UID.fromDataServicesID(mfuBucket.getId());
        mfuContactType.setContactId(uid);
        mfuContactType.setCanonicalName(mfuBucket.getFirstName() + mfuBucket.getLastName());
        mfuContactType.setCompany(mfuBucket.getCompany());
        final SecurityContext basicSecContext = ctx.getSecurityContext();

        final PersonalPointOfContactManager perPOCMgr = new PersonalPointOfContactManager(
                basicSecContext);
        // int pocId = perPOCMgr.getPersonalPointOfContactIdFor(mfuBucket);
        final PersonalPointOfContact personalPOC = perPOCMgr
                .getPersonalPointOfContactForMFUBucketId(mfuBucket.getId());
        if (personalPOC != null)
        {
            contactCategory = ContactCategoryType.PERSONAL;
        }
        else
        {
            contactCategory = ContactCategoryType.INSTANCE;
        }
        mfuContactType.setContactType(contactCategory);

        mfuContactType.setData(mfuBucket.getData());
        if (mfuBucket.getMediaType() != null)
        {
            final PocMediaTypeType pocMediaType = PocMediaTypeType.fromValue(mfuBucket
                    .getMediaType().name());
            mfuContactType.setPocMediaType(pocMediaType);
        }
        if (mfuBucket.getPOCType() != null)
        {
            final PocTypeType poctype = PocTypeType.fromValue(mfuBucket.getPOCType().name());
            mfuContactType.setPocType(poctype);
        }
        return mfuContactType;

    }

    /**
     * Map the fields of UDA ContactType object to DS Contact object
     * 
     * @param contactType ContactType
     * @param contactDTO PersonalContact
     * @param userContext UserContext
     * @param ctAddFromPersonalDir boolean
     * @return PersonalContact contactDTO
     * @throws ExecutionException
     */
    public static PersonalContact mapUDAToDSContact(final ContactType contactType,
            final PersonalContact contactDTO, final UserContext userContext,
            final boolean ctAddFromPersonalDir) throws ExecutionException
    {
        final PersonalDirectoryManager pdMgr = new PersonalDirectoryManager(userContext
                .getSecurityContext());
        PersonalDirectory persDir = null;

        try
        {
            final UserCDI userCDI = getUserCDI(userContext);
            persDir = pdMgr.getPersonalDirectoryFor(userCDI);
            if (persDir == null)
            {
                // send the CenterlineMessageEvent to client in order to display the error message
                // on the communicator
                final Event event = new Event();
                final CenterlineMessageEvent clmaEve = new CenterlineMessageEvent();
                clmaEve.setMessage("No personal directory available");
                event.setCenterlineMessage(clmaEve);
                ExecutableResultQueue.<Event> send(event, userContext.getUser(), userContext
                        .getUserID().getDeviceID());

                throw new ExecutionException("Cannot add the personal contact as the User "
                        + userContext.getUserName() + " does not have a personal directory");
            }
        }
        catch (Throwable lException)
        {
            throw new ExecutionException(lException);
        }

        final PersonalPointOfContactManager perPOCMgr = new PersonalPointOfContactManager(
                userContext.getSecurityContext());
        final PointOfContactManager pocMgr = new PointOfContactManager(userContext
                .getSecurityContext());
        final ContactManager ctMgr = new ContactManager(userContext.getSecurityContext());
        final PreferredPersonalPOCManager prePerPOCMgr = new PreferredPersonalPOCManager(
                userContext.getSecurityContext());
        final PersonalContactManager perCtMgr = new PersonalContactManager(userContext
                .getSecurityContext());
        final PreferredPersonalPOC prePerPOC = PreferredPersonalPOCBaseManager
                .NewPreferredPersonalPOC();

        contactDTO.setBusinessGroup(contactType.getBusinessGroup());
        contactDTO.setCompany(contactType.getCompanyName());
        contactDTO.setFirstName(contactType.getFirstName());
        contactDTO.setLastName(contactType.getLastName());
        contactDTO.setTitle(contactType.getTitle());
        final PointsOfContact udaPocsType = contactType.getPointsOfContact();

        // check if the add personal contact request is given from the instance directory or from
        // the personal directory
        if (!ctAddFromPersonalDir && udaPocsType == null)
        {
            Log.logger().debug(
                    "PointOfContacts received is NULL for the contactID: " + contactDTO.getId());

            // personal contact is copied from instance directory
            if (!ctAddFromPersonalDir)
            {
                // populate personalPOC and PreferredPersonalPOC for the contact id given as input
                try
                {
                    // get the instance contact object in order to get its POC and PreferredPOCs
                    final Contact dsContact = ctMgr.getById(Integer.parseInt(contactType
                            .getContactId().toString()));
                    final List<PointOfContact> pocList = pocMgr.getPointOfContactsFor(dsContact);

                    if (pocList == null || pocList.isEmpty())
                    {
                        Log.logger().debug(
                                "There are no PointOfContact for the Contact ID: "
                                        + contactType.getContactId().toString() + " for user: "
                                        + userContext.getUserName());
                    }
                    else
                    {
                        for (final PointOfContact dsPOC : pocList)
                        {
                            // convert instance POC to personal POC
                            final PersonalPointOfContact perPOC = PersonalPointOfContactBaseManager
                                    .NewPersonalPointOfContact();
                            perPOC.setData(dsPOC.getData());
                            perPOC.setMediaType(PersonalPointOfContact.EnumMediaType.valueOf(dsPOC
                                    .getMediaType().name()));
                            perPOC.setPOCType(PersonalPointOfContact.EnumPOCType.valueOf(dsPOC
                                    .getPOCType().name()));
                            perPOC.setShortDescriptor(dsPOC.getShortDescriptor());

                            perPOCMgr.save(perPOC);

                            // add to the newly created personal contact
                            contactDTO.addToPersonalPointOfContacts(perPOC);

                            // get the preferred poc with the given contact id
                            final PreferredPOCManager prePOCMgr = new PreferredPOCManager(
                                    userContext.getSecurityContext());
                            final PreferredPOC prePOC = prePOCMgr.getPreferredPOCFor(dsContact);
                            boolean preferredPerPOCSet = false;
                            if (prePOC == null)
                            {
                                Log.logger().debug(
                                        "There is no PreferredPOC for contact ID: "
                                                + contactType.getContactId().toString()
                                                + " for user: " + userContext.getUserName());
                            }
                            else
                            {
                                final PointOfContact dsPrePOC = pocMgr.getPointOfContactFor(prePOC);
                                if (dsPrePOC.getId() == dsPOC.getId())
                                {
                                    prePerPOC.setPersonalPointOfContact(perPOC);

                                    // set the preferred personal poc
                                    contactDTO.setPreferredPersonalPOC(prePerPOC);
                                    preferredPerPOCSet = true;
                                }
                            }

                            // save the entities created
                            persDir.addToPersonalContacts(contactDTO);
                            pdMgr.save(persDir);
                            perCtMgr.save(contactDTO);
                            if (preferredPerPOCSet)
                            {
                                prePerPOCMgr.save(prePerPOC);
                            }
                        }
                    }
                }
                catch (final Throwable e)
                {
                    throw new ExecutionException(e);
                }
            }
        }
        else
        // add personal contact request is received from personal directory
        {
            if (contactType.getPointsOfContact() != null)
            {
                final List<PointOfContactType> udaPOCList = contactType.getPointsOfContact()
                        .getPointOfContact();

                final Iterator<PointOfContactType> itr = udaPOCList.iterator();

                while (itr.hasNext())
                {
                    final PointOfContactType udaPOCType = itr.next();

                    final PersonalPointOfContact dsPOC = PersonalPointOfContactBaseManager
                            .NewPersonalPointOfContact();
                    dsPOC.setData(udaPOCType.getData());

                    // Media type is an optional field, hence check for null
                    if (udaPOCType.getMediaType() != null)
                    {
                        dsPOC.setMediaType(PersonalPointOfContact.EnumMediaType.valueOf(udaPOCType
                                .getMediaType().value()));
                    }
                    // FIXME: There is no POCType in UDA PointOfContact
                    // dsPOC.setPOCType(udaPOCType.get)
                    dsPOC.setShortDescriptor(udaPOCType.getDescriptor());

                    try
                    {
                        if (udaPOCType.isDefault())
                        {
                            // create PreferredPersonalPOC
                            perPOCMgr.save(dsPOC);
                            contactDTO.addToPersonalPointOfContacts(dsPOC);
                            prePerPOC.setPersonalPointOfContact(dsPOC);
                            contactDTO.setPreferredPersonalPOC(prePerPOC);
                        }
                        // save the entities created
                        persDir.addToPersonalContacts(contactDTO);
                        pdMgr.save(persDir);
                        perCtMgr.save(contactDTO);
                        if (udaPOCType.isDefault())
                        {
                            prePerPOCMgr.save(prePerPOC);
                        }
                    }
                    catch (final Throwable e)
                    {
                        throw new ExecutionException(e);
                    }
                }
                // TODO following not present in ContactType of UDA
                // locale ---------- ctType.getLocale()
                // ctDTO.setUserAOR(ctType.getUserAor())
            }
            else
            {
                Log
                        .logger()
                        .debug(
                                "There are no Points of contact sent by the client for the PersonalContact to be added to DS for user: "
                                        + userContext.getUserName());
            }
        }

        return contactDTO;
    }

    /**
     * Map the fields of UDA PointOfContactType object to DS PersonalPointOfContact object
     * 
     * @param udaPOC PointOfContactType
     * @param dsPOC PersonalPointOfContact
     * @return PersonalPointOfContact dsPOC
     */
    public static PersonalPointOfContact mapUDAToDSPOC(final PointOfContactType udaPOC,
            final PersonalPointOfContact dsPOC)
    {
        dsPOC.setData(udaPOC.getData());
        // Media type is an optional field, hence check for null
        if (udaPOC.getMediaType() != null)
        {
            dsPOC.setMediaType(PersonalPointOfContact.EnumMediaType.valueOf(udaPOC.getMediaType()
                    .name()));
        }
        dsPOC.setShortDescriptor(udaPOC.getDescriptor());

        return dsPOC;
    }

    /**
     * Map the fields of UDA PointOfContactType object to DS PointOfContact object
     * 
     * @param udaPOC PointOfContactType
     * @param dsPOC PointOfContact
     * @return PointOfContact dsPOC
     */
    public static PointOfContact mapUDAToDSPOC(final PointOfContactType udaPOC,
            final PointOfContact dsPOC)
    {
        dsPOC.setData(udaPOC.getData());
        // Media type is an optional field, hence check for null
        if (udaPOC.getMediaType() != null)
        {
            dsPOC.setMediaType(PointOfContact.EnumMediaType.valueOf(udaPOC.getMediaType().name()));
        }
        dsPOC.setShortDescriptor(udaPOC.getDescriptor());
        return dsPOC;
    }

    private static String getUserAOR(final UserContext userContext,
            final ImmutableContactType contact) throws InvalidContextException,
            StorageFailureException
    {
        if (UDAAndDSEntityUtil.isNull(userContext, contact))
        {
            return "";
        }

        if (contact.getPreferredPointOfContact().getMediaType() == PocMediaTypeType.ICM)
        {
            return StringUtils.trim(contact.getPreferredPointOfContact().getData());
        }
        if (contact.getContactType() == DirectoryContactType.INSTANCE)
        {
            final PointOfContactManager pocmgr = new PointOfContactManager(userContext
                    .getSecurityContext());
            final List<PointOfContact> poclist = pocmgr
                    .findByMediaTypeEqualing(PointOfContact.EnumMediaType.ICM);

            if (poclist == null || poclist.size() == 0)
            {
                return "";
            }

            final String value = poclist.get(0).getData();

            return (value == null ? "" : StringUtils.trim(value));
        }
        if (contact.getContactType() == DirectoryContactType.PERSONAL)
        {
            final PersonalPointOfContactManager ppocmgr = new PersonalPointOfContactManager(
                    userContext.getSecurityContext());
            final List<PersonalPointOfContact> ppoclist = ppocmgr
                    .findByMediaTypeEqualing(PersonalPointOfContact.EnumMediaType.ICM);

            if (ppoclist == null || ppoclist.size() == 0)
            {
                return "";
            }

            final String value = ppoclist.get(0).getData();

            return (value == null ? "" : StringUtils.trim(value));
        }

        return "";
    }

    private static boolean isNull(final UserContext userContext, final ImmutableContactType contact)
            throws InvalidContextException
    {
        if (userContext == null || userContext.getSecurityContext() == null)
        {
            throw new InvalidContextException("Received invalid userContext!");
        }

        return (contact == null || contact.getPreferredPointOfContact() == null
                || contact.getPreferredPointOfContact().getMediaType() == null
                || contact.getContactType() == null || contact.getPreferredPointOfContact()
                .getData() == null);
    }

}
