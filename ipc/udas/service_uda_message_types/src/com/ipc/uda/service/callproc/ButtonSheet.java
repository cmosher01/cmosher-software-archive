/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.callproc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonAppearance;
import com.ipc.uda.types.UID;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;
import com.ipc.va.cti.ConnectionID;

/**
 * @author mordarsd
 * 
 */
public class ButtonSheet
{

    /**
     * Number of buttons in a complete button sheet
     */
    public static final int MAX_NUM_OF_BUTTONS = 600;

    /**
     * Number of pages
     */
    public static final int MAX_NUM_OF_PAGES_ON_BUTTON_SHEET = ButtonSheet.MAX_NUM_OF_BUTTONS
            / ButtonSheet.NUM_OF_BUTTONS_ON_PAGE;

    /**
     * Number of buttons per page
     */
    public static final int NUM_OF_BUTTONS_ON_PAGE = 24;
    private static final int NUM_OF_BUTTONS_PER_ROW = 2;

    private final Map<UID, UdaButton> buttonMap = new HashMap<UID,UdaButton>();
    @Deprecated
    private final Map<Integer, UdaButton> buttonNumMap = new HashMap<Integer, UdaButton>();
    private final Map<ConnectionID, UdaButton> connIdButtonMap = new HashMap<ConnectionID, UdaButton>();
    private final ConcurrentMap<ButtonAppearance, Set<ButtonPressCall>> mapAppearanceToCall = new ConcurrentHashMap<ButtonAppearance, Set<ButtonPressCall>>();

    private int numFixedRows; // minimum number of fixed rows

    // FIXME: Floats are not to be tracked in I3
    @Deprecated
    private int numFloatRows; // minimum number of float rows

    private final UserContext userCtx;
    private UserCDI usrCDI;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * Populate Button sheet for the user
     * 
     * @param userCtx the (owning) {@link UserContext} of this button sheet
     */
    public ButtonSheet(final UserContext userCtx)
    {
        this.userCtx = userCtx;
        populateFixedAndFloatRows();
    }

    /**
     * Adds a button to this button sheet
     * 
     * @param button the {@link UdaButton} to add
     */
    public void addButton(final UdaButton button)
    {
        if (button == null)
        {
            throw new IllegalArgumentException("button cannot be null");
        }

        this.buttonMap.put(button.getId(),button);
        this.buttonNumMap.put(button.getDsButton().getButtonNumber(), button);

        if (button.getAppearance().exists() && button.getCall().exists())
        {
            final ButtonAppearance apr = button.getAppearance().get();
            this.mapAppearanceToCall.putIfAbsent(apr, Collections
                    .<ButtonPressCall> synchronizedSet(new HashSet<ButtonPressCall>()));
            final Set<ButtonPressCall> set = this.mapAppearanceToCall.get(apr);
            if (set == null)
            {
                // could only happen if some other thread just removed the button again,
                // which will probably never ever happen, but we check in order to satisfy Klockwork
                return;
            }
            set.add(button.getCall().get());
            if (this.started.get())
            {
                button.getCall().get().startFSM();
            }
        }
    }

    /**
     * Adds a button the this button sheet, based on the given DS button.
     * 
     * @param dsButton the DS button
     * @param fixed is fixed button?
     * @param floating is floatable button?
     * @throws InvalidContextException (from DS)
     * @throws StorageFailureException (from DS)
     */
    public void addButtonFromDsButton(final Button dsButton, final boolean fixed,
            final boolean floating) throws InvalidContextException, StorageFailureException
    {
        final UdaButton udaButton = ButtonFactory.create(dsButton, this.userCtx);
        udaButton.setFixed(fixed);

        addButton(udaButton);
    }

    /**
     * Gets all buttons in this button sheet. The buttons are added to the given
     * <code>Collection addTo</code>.
     * 
     * @param addTo the {@link Collection} to add all of this sheets buttons to
     */
    public void getAllButtons(final Collection<UdaButton> addTo)
    {
        addTo.addAll(this.buttonNumMap.values());
    }

    /**
     * Gets the button with the given ID.
     * 
     * @param id the buttonID of the button to get
     * @return the {@link UdaButton} with the given ID, if it exists in this button sheet
     */
    public Optional<UdaButton> getButton(final UID id)
    {
        if (this.buttonMap.containsKey(id))
        {
            return new Optional<UdaButton>(this.buttonMap.get(id));
        }
        return new Nothing<UdaButton>();
    }

    /**
     * Returns the buttons on the given page
     * 
     * @param pageNumber int
     * @return Map<Integer, Button> pageMap
     */
    public Map<Integer, UdaButton> getButtonsOfPage(final int pageNumber)
    {
        final Map<Integer, UdaButton> pageMap = new HashMap<Integer, UdaButton>();

        final int cntPaginatingButtons = getNumPaginatingButtons();

        // set the delimiters for the paginating buttons on the given page
        int startButtonNumber = 1;
        if (pageNumber > 1)
        {
            startButtonNumber = ((pageNumber - 1) * cntPaginatingButtons) + 1;
        }
        int endButtonNumber = startButtonNumber + cntPaginatingButtons;

        for (int lIndex = startButtonNumber; lIndex < endButtonNumber; lIndex++)
        {
            final UdaButton buttonOnPage = this.buttonNumMap.get(lIndex);
            if (buttonOnPage != null)
            {
                buttonOnPage.setFixed(false);
                pageMap.put(lIndex, buttonOnPage);
            }
            else
            {
                Log.logger().debug("Button is Null for the button number: " + lIndex);
            }
        }

        // set the delimiters for the fixed buttons
        startButtonNumber = endButtonNumber;
        endButtonNumber = startButtonNumber + (this.numFixedRows * 2);
        for (int kIndex = startButtonNumber, fixedButtonNumberIndex = ButtonSheet.MAX_NUM_OF_BUTTONS; kIndex < endButtonNumber; kIndex++, fixedButtonNumberIndex--)
        {
            final UdaButton buttonOnPage = this.buttonNumMap.get(fixedButtonNumberIndex);
            if (buttonOnPage != null)
            {
                buttonOnPage.setFixed(true);
                pageMap.put(fixedButtonNumberIndex, buttonOnPage);
            }
            else
            {
                Log.logger().debug(
                        "Button is Null for the button number: " + fixedButtonNumberIndex);
            }
        }

        return pageMap;
    }

    public Set<ButtonPressCall> getCalls(final ButtonAppearance buttonID)
    {
        final Set<ButtonPressCall> setOrNull = this.mapAppearanceToCall.get(buttonID);
        if (setOrNull == null)
        {
            return Collections.<ButtonPressCall> unmodifiableSet(new HashSet<ButtonPressCall>());
        }
        return Collections.<ButtonPressCall> unmodifiableSet(setOrNull);
    }

    public void hunt()
    {
        final Collection<UdaButton> udaButtons = this.buttonMap.values();

        for (final UdaButton button : udaButtons)
        {
            final Optional<ButtonAppearance> appearance = button.getAppearance();
            final Optional<ButtonPressCall> call = button.getCall();
            if (!appearance.exists() || !call.exists())
            {
                continue;
            }

            final String buttonAor = appearance.get().getAor();
            final String userAor = this.userCtx.getPersonalExtension();
            if (userAor.equals(buttonAor))
            {
                final PrivateWireFsmContext.PrivateWireState state = call.get().getState();

                if (state.equals(PrivateWireFsmContext.PrivateWireFsm.Null))
                {
                    button.press();
                    return;
                }
            }
        }
        Log.logger().debug(" No valid buttons found for hunt as the UdaButton Map is Empty and udaButtons: " + udaButtons.isEmpty());
    }

    /**
     * Retrieve the buttons for the user and populate the Map
     */
    public void initialize()
    {
        if (!this.initialized.compareAndSet(false,true))
        {
            return;
        }

        final ButtonManager dsButtonMgr = new ButtonManager(this.userCtx.getSecurityContext());

        try
        {
            if (dsButtonMgr == null)
            {
                Log.logger()
                        .debug("dsButtonMgr is Null from DS for user " + this.userCtx.getUser());
            }
            else
            {
                this.usrCDI = UDAAndDSEntityUtil.getUserCDI(this.userCtx);
                if (this.usrCDI != null)
                {
                    final List<Button> lstButton = dsButtonMgr.getButtonsFor(this.usrCDI);
                    if (lstButton == null || lstButton.isEmpty())
                    {
                        Log.logger()
                                .debug(
                                        "Button list is Null/Empty for the user: "
                                                + this.userCtx.getUser());
                    }
                    else
                    {
                        for (final Button button : lstButton)
                        {
                            if (button == null)
                            {
                                Log.logger().debug(
                                        "Button object retrieved from DS is null for user: "
                                                + this.userCtx.getUser());
                            }
                            else
                            {
                                addButtonFromDsButton(button, false, false);
                            }
                        }
                    }
                }
                else
                {
                    Log.logger()
                            .debug("UserCDI is Null from DS for user " + this.userCtx.getUser());
                }
            }
        }
        catch (final Throwable e)
        {
            Log.logger()
                    .debug(
                            "Unable to get the Button(s) from database for user: "
                                    + this.userCtx.getUser(), e);
        }
    }

    /**
     * Removes a button from this button sheet
     * 
     * @param button UdaButton
     */
    public void removeButton(final UdaButton button)
    {
        if (button == null)
        {
            throw new IllegalArgumentException("button cannot be null");
        }

        this.buttonMap.remove(button.getId());
        this.buttonNumMap.remove(button.getDsButton().getButtonNumber());

        if (button.getAppearance().exists() && button.getCall().exists())
        {
            final ButtonAppearance apr = button.getAppearance().get();
            final Set<ButtonPressCall> set = this.mapAppearanceToCall.get(apr);
            if (set != null)
            {
                set.remove(button.getCall().get());
                // this is thread-safe (worst case: will try to remove twice)
                if (set.isEmpty())
                {
                    this.mapAppearanceToCall.remove(apr);
                }
            }
        }
    }

    boolean isEmpty()
    {
        return this.buttonMap.isEmpty();
    }

    /**
     * Returns the number of buttons that will be paginating on the button sheet. This is the count
     * of buttons that will appear on each page (not including fixed and floating button areas).
     * 
     * @return
     */
    private int getNumPaginatingButtons()
    {
        return ButtonSheet.NUM_OF_BUTTONS_ON_PAGE
                - (this.numFixedRows * ButtonSheet.NUM_OF_BUTTONS_PER_ROW);
        // TODO return ButtonSheet.NUM_OF_BUTTONS_ON_PAGE - (this.numFixedRows+this.numFloatRows) *
        // ButtonSheet.NUM_OF_BUTTONS_PER_ROW;
    }

    /**
     * Populates the number of fixed rows and number of float rows from the database
     */
    private void populateFixedAndFloatRows()
    {
        try
        {
            this.usrCDI = UDAAndDSEntityUtil.getUserCDI(this.userCtx);

            if (this.usrCDI != null)
            {
                this.numFixedRows = this.usrCDI.getFixedBtnRows();
                this.numFloatRows = this.usrCDI.getFloatBtnRows();
            }
            else
            {
                Log.logger().debug("UserCDI is Null from DS for user " + this.userCtx.getUser());
            }
        }
        catch (final Exception lException)
        {
            Log.logger().debug(
                    "Unable to get the UserCDI from database for user: " + this.userCtx.getUser(),
                    lException);
        }
    }

    public void startFSMs()
    {
        if (!this.started.compareAndSet(false,true))
        {
            return;
        }
        for (final UdaButton button : this.buttonMap.values())
        {
            final Optional<ButtonPressCall> call = button.getCall();
            if (call.exists())
            {
                button.getCall().get().startFSM();
            }
        }
    }
}
