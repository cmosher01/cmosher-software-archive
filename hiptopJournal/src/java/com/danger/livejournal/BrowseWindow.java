// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.app.*;
import danger.ui.*;
import danger.util.*;

public 	class		BrowseWindow
		extends		ScreenWindow
		implements	JournalCommands, JournalResources, AppResources
{
	//*	--------------------	BrowseWindow

	public
	BrowseWindow()
	{
		//*	Create the title bar
		setTitle(getApplication().getString(kRsrc_LiveJournal));
		setIcon(LiveJournal.Instance().getBitmap(ID_SMALL_ICON));

		setBackgroundColor(Color.GRAY12);
		mFont = Font.findSystemFont();
		
		//*	Set up the status line
		mStatus = new StaticText(getApplication().getString(kRsrc_Idle));
		mStatus.setHasBorder(true);
		mStatus.setWidth(getWidth());
		addChild(mStatus);
		mStatus.show();
		
		//*	Set up the journal folder view
		mFolderView = new JournalFolderView();
		mFolderView.setTop(mStatus.getHeight());
		mFolderView.setLeft(0);
		mFolderView.setWidth(getWidth());
		mFolderView.setHeight(getHeight() - mStatus.getHeight());
		
		addChild(mFolderView);

		Reset();

		mFolderView.show();
		setFocusedChild(mFolderView);
	}

	//*	--------------------	show

	public final void
	show()
	{
	String	name = LiveJournal.Instance().FullName();
	
		if (StringUtils.isEmpty(name))
			setTitle(getApplication().getString(kRsrc_LiveJournal));
		else
			setTitle(name);
			
		super.show();
	}

	//*	--------------------	adjustActionMenuState

	public void
	adjustActionMenuState()
	{
	Menu		m = getActionMenu();
	MenuItem	i;
	boolean		isFolder = (mFolderView.getCurrentItemIndex() == -1);
	
		m.removeAllItems();
	
		//*	New entry
		i = m.addItem(getApplication().getString(kRsrc_NewEntry), kCmd_NewEntry);
		i.setShortcut('N');
		
		if (! isFolder)
		{
		JournalDataRecord	e = (JournalDataRecord) mFolderView.getSelection();
		int					flags = e.Flags();
		
			//*	Edit entry
			i = m.addItem(getApplication().getString(kRsrc_EditEntry), kCmd_ReopenEntry, 0, e, null);
			i.setShortcut('E');
			
			if (0 != flags)
				i.disable();
			
			m.addDivider();
			
			//*	Delete entry
			i = m.addItem(getApplication().getString(kRsrc_DeleteEntry), CMD_DELETE_ENTRY, 0, e, null);
			i.setShortcut('\b');

			if (0 != flags)
				i.disable();
		}
		
		m.addDivider();
		
		//*	Sync now
		m.addItem(getApplication().getString(kRsrc_SyncNow), kCmd_SyncNow, 0, null, null);
		
		m.addDivider();
		
		m.addItem(getApplication().getString(kRsrc_ShowFriends), kCmd_ShowFriends);
		
		//*	Logout
		i = m.addItem(getApplication().getString(kRsrc_Logout), kCmd_Logout);
		i.setShortcut('L');
	}

	//*	--------------------	SelectedMessage

	public JournalDataRecord
	SelectedMessage()
	{
	Object	selection = mFolderView.getSelection();
	
		if ((null == selection) || (selection instanceof Folder))
			return null;
			
		return (JournalDataRecord) selection;
	}

	//*	--------------------	EventDeviceWidgetUp

	public boolean
	eventWidgetUp(int inWhichWidget, Event event)
	{
		if (Event.DEVICE_BUTTON_BACK == inWhichWidget)
		{
			if (false == super.eventWidgetUp(inWhichWidget, event))
			{
				getApplication().returnToLauncher();
				return true;
			}
			else
			{
				return true;
			}
		}
		
		return super.eventWidgetUp(inWhichWidget, event);
	}

	//*	--------------------	Reset

	public final void
	Reset()
	{
		mFolderView.removeAll();

		mFolderView.createFolder(getApplication().getString(kRsrc_PostedEntries));
		mFolderView.createFolder(getApplication().getString(kRsrc_Drafts));
	}

	//*	--------------------	UpdateStatus

	public final void
	UpdateStatus(String inStatus)
	{
		mStatus.setText(StringUtils.makeDisplayString(inStatus, mStatus.getWidth(), mFont));
		invalidate();
	}

	//*	--------------------	GetItemByItemID

	public final JournalDataRecord
	GetItemByItemID(int inItemID)
	{
	Folder	f = mFolderView.getFolder(getApplication().getString(kRsrc_PostedEntries));
	int		numItems = f.size();
	
		for (int i = 0; i < numItems; i++)
		{
		JournalDataRecord	e = (JournalDataRecord) f.getItem(i);
		
			if (e.ItemID() == inItemID)
				return e;
		}
		
		return null;
	}

	//*	--------------------	Finalize

	public final void
	Finalize(JournalDataRecord inEntry)
	{
	Folder	f;
	int		numEntries;
	int		i;
	
		f = mFolderView.getFolder(getApplication().getString(kRsrc_Drafts));
		numEntries = f.size();
		
		for (i = 0; i < numEntries; i++)
		{
		JournalDataRecord	e = (JournalDataRecord) f.getItem(i);
		
			if (e.UID() == inEntry.UID())
			{
				f.removeItemAt(i);
				break;
			}
		}

		if (i == numEntries)
			return;

		f = mFolderView.getFolder(getApplication().getString(kRsrc_PostedEntries));
		f.insertItemSorted(LiveJournal.Instance().Comparator(), inEntry);
	}

	//*	--------------------------------	Class Variables
	
	JournalFolderView	mFolderView;
	StaticText			mStatus;
	Font				mFont;
}
