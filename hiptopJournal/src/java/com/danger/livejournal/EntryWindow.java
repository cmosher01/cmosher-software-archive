// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.system.Strings;
import danger.system.Bitmaps;
import danger.app.*;
import danger.ui.*;
import danger.util.*;

import java.util.Calendar;

public class	EntryWindow
	extends		ScreenWindow
	implements	JournalCommands, JournalResources, AppResources, danger.SystemStrings
{
	//*	--------------------	EntryWindow
	
	public
	EntryWindow()
	{
		Init(null);
	}

	//*	--------------------	EntryWindow(JournalDataRecord)
	
	public
	EntryWindow(JournalDataRecord inEntry)
	{
		Init(inEntry);
	}
	
	//*	--------------------	Init
	
	private final void
	Init(JournalDataRecord inEntry)
	{
		setTitle(getApplication().getString(kRsrc_EditEntry));
		setIcon(LiveJournal.Instance().getBitmap(ID_SMALL_ICON));
		
		mView = new EditView();
		mView.setPosition(0, 0);
		mView.setActualSize(getWidth(), getHeight());
		mView.setBackgroundColor(Color.GRAY12);

		addChild(mView);
		mView.show();
		setFocusedChild(mView);
		mView.Init(inEntry);

		if (null == inEntry)
			mEntry = new JournalDataRecord();
		else
			mEntry = inEntry;		
	}

	//*	--------------------	ReceiveEvent

	public final boolean
	receiveEvent(Event e)
	{
		switch (e.type)
		{
			case kCmd_PostEntry:
			case kCmd_SaveAsDraft:
				SaveData();
				hide();
				Event e2 = new Event(e);
				e2.setListener(null);
				sendEventToWindow(e2);
				return true;
				
			case kCmd_Cancel:
				hide();
				return true;
				
			case kCmd_PollHelper:
				(new PollHelper(mView.mEvent)).show();
				return true;

			case kCmd_Formatify:
				mView.formatify();
				return true;
		}
		
		return super.receiveEvent(e);
	}
	
	//*	--------------------	EventDeviceWidgetUp

	public final boolean
	eventWidgetUp(int inWhichWidget, Event event)
	{
		switch (inWhichWidget)
		{
			case Event.DEVICE_BUTTON_BACK:
			{
				if (! EmptyEntry() && EntryChanged())
				{
				Event	e = new Event(this, kCmd_SaveAsDraft, 0, 0, mEntry);

					AlertWindow alert;

					alert = getApplication().getAlert(kSaveAsDraftAlert, this);
					alert.setAutoDismiss(1000, e);
					alert.show();
				}
				else
				{
					hide();
				}
					
				return true;
			}
		}

		return super.eventWidgetUp(inWhichWidget, event);
	}

	//*	--------------------	EmptyEntry

	private final boolean
	EmptyEntry()
	{
		if (mView.mSubject.length() > 0)
			return false;
			
		if (mView.mEvent.length() > 0)
			return false;
			
		return true;
	}

	//*	--------------------	EntryChanged

	private final boolean
	EntryChanged()
	{
		if (! mView.mSubject.toString().equals(mEntry.Subject()))
			return true;
			
		if (! mView.mEvent.toString().equals(mEntry.Entry()))
			return true;

		if (mView.mVisibilityMenu.getValue() != mEntry.Security())
			return true;

		return false;
	}

	//* --------------------	AdjustActionMenuState
	
	public final void
	adjustActionMenuState()
	{
	Menu		m = getActionMenu();
	MenuItem	i;

		m.removeAllItems();

		if (0 == mEntry.ItemID())		//*	Is this a new entry?
		{
			//*	Post new entry
			i = m.addItem(getApplication().getString(kRsrc_PostEntry), kCmd_PostEntry, 0, mEntry, this);
			i.setShortcut('M');
		
			//*	Save as draft
			i = m.addItem(getApplication().getString(kRsrc_SaveAsDraft), kCmd_SaveAsDraft, 0, mEntry, this);
			i.setShortcut(Shortcut.BACK_BUTTON);
		}
		else							//*	Nope, update
		{
			i = m.addItem(getApplication().getString(kRsrc_PostUpdate), kCmd_PostEntry, 0, mEntry, this);
			i.setShortcut('M');
		}
		
		m.addDivider();
		
		i = m.addItem(getApplication().getString(kRsrc_Formatify), kCmd_Formatify, 0, null, this);
		i = m.addItem(getApplication().getString(kRsrc_PollHelper), kCmd_PollHelper, 0, null, this);
		
		m.addDivider();

		i = m.addItem(getApplication().getString(ID_CANCEL), kCmd_Cancel, 0, null, this);
		i.setShortcut('.');
	}
	
	//*	--------------------	SaveData
	
	private final void
	SaveData()
	{
	Calendar	date = Calendar.getInstance();
	
		//*	LiveJournal only has minute granularity
		date.set(Calendar.SECOND, 0);
	
		mEntry.SetSubject(mView.mSubject.toString());
		mEntry.SetEntry(mView.mEvent.toString());
		mEntry.SetSecurity(mView.mVisibilityMenu.getValue());

		if (null != mView.mJournalMenu)
		{
			if (mView.mJournalMenu.getValue() != 0)
				mEntry.SetJournal(mView.mJournalMenu.getItem(mView.mJournalMenu.getValue()).getTitle());
		}

		//*	If this is a draft, update the date/time
		if (0 == mEntry.ItemID())
			mEntry.SetDate((int) (date.getTimeInMillis() / 1000));
	}
	
	//*	--------------------------------	Instance Variables
	
	private EditView			mView;
	private JournalDataRecord	mEntry;
}

class EditView extends ScrollView implements JournalResources
{
	//*	--------------------	Init
	
	public final void
	Init(JournalDataRecord inEntry)
	{
	StaticText	label;
	View		bottomView;
	String[]	journals = LiveJournal.Instance().Journals();
	LiveJournal	app = LiveJournal.Instance();

		setHasScrollbar(true);

		//*	Visibility menu
		mVisibilityMenu = new PopupMenu(app.getString(kRsrc_Visibility));
		mVisibilityMenu.setPosition(kFieldLeft, 2);
		mVisibilityMenu.setWidth(getContentWidth() - kFieldLeft);

		mVisibilityMenu.addItem(app.getString(kRsrc_Public));
		mVisibilityMenu.addItem(app.getString(kRsrc_Private));

		if (null == inEntry)
			mVisibilityMenu.setValue(0);
		else
			mVisibilityMenu.setValue(inEntry.Security());

		addChild(mVisibilityMenu);
		mVisibilityMenu.show();

		bottomView = mVisibilityMenu;
		
		//*	Visibility label
		label = new StaticText(app.getString(kRsrc_Visibility));
		label.setTransparent(true);
		label.setTop(mVisibilityMenu.getTop() + 2);
		label.setRight(LABEL_RIGHT);
		addChild(label);
		label.show();

		//*	Journals menu
		if (journals.length > 0)
		{
			mJournalMenu = new PopupMenu(app.getString(kRsrc_Journal));
			Layout.positionBelow(mJournalMenu, mVisibilityMenu, kFieldLeft, 2);
			mJournalMenu.setWidth(getContentWidth() - kFieldLeft);

			//*	Add our user journal to the menu
			mJournalMenu.addItem("< " + LiveJournal.Instance().Username() + " >");
			
			//*	Add community journals
			for (int i = 0; i < journals.length; i++)
			{
				mJournalMenu.addItem(journals[i]);
				
				//*	If this is the one stored in a previous entry, change the menu to that one
				if (null != inEntry)
				{
					if (journals[i].equals(inEntry.Journal()))
						mJournalMenu.setValue(i + 1);
				}
			}
			
			addChild(mJournalMenu);
			mJournalMenu.show();

			//*	Journal label
			label = new StaticText(app.getString(kRsrc_Journal));
			label.setTransparent(true);
			label.setTop(mJournalMenu.getTop() + 2);
			label.setRight(LABEL_RIGHT);
			addChild(label);
			label.show();

			bottomView = mJournalMenu;
		}

		//*	Subject field
		mSubject = new TextField(true, true);
		mSubject.setInputFilter(TextInputFilter.newLengthFilter(255));
		Layout.positionBelow(mSubject, bottomView, kFieldLeft, 2);
		mSubject.setLeft(LABEL_RIGHT + COLUMN_GAP);
		mSubject.setWidth(getContentWidth() - kFieldLeft);
		mSubject.setAutoResize(true);

		if (null != inEntry)
			mSubject.setText(inEntry.Subject());

		addChild(mSubject);
		mSubject.show();

		//*	Subject label
		label = new StaticText(app.getString(kRsrc_Subject));
		label.setTransparent(true);
		Layout.alignVertical(label, mSubject);
		label.setRight(LABEL_RIGHT);
		addChild(label);
		label.show();

		//*	Event text
		mEvent = new EditText(true, true);	// autotext & autocap
		Layout.positionBelow(mEvent, mSubject, 2);
		mEvent.setLeft(0);
		mEvent.setWidth(getContentWidth());
		mEvent.setAutoResize(true);

		if (null != inEntry)
			mEvent.setText(inEntry.Entry());

		addChild(mEvent);
		mEvent.show();

		setFocusedChild(mSubject);

		setOrigin(0);
	}

	//*	--------------------	formatify
	
	public final void
	formatify()
	{
	Formatify	formatify = new Formatify();
	
		formatify.initForHTML();
		mEvent.setText(formatify.process(mEvent.toString()));
		
		invalidate();
	}

	//*	--------------------------------	Instance Variables
	
	TextField		mSubject;
	EditText		mEvent;
	PopupMenu		mVisibilityMenu;
	PopupMenu		mJournalMenu;

	//*	--------------------------------	Constants
	
	static final int	LABEL_RIGHT			=	45;
	static final int	COLUMN_GAP			=	2;
	static final int	kFieldLeft			= 	(LABEL_RIGHT + COLUMN_GAP);
}
