// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.app.*;
import danger.net.URL;
import danger.system.Strings;
import danger.ui.*;
import danger.util.StringUtils;
import danger.util.format.StringFormat;

import java.util.Vector;

public 	class		FriendsWindow
		extends		ScreenWindow
		implements	JournalCommands, JournalResources, AppResources, danger.SystemStrings
{
	//*	--------------------	FriendsWindow

	public
	FriendsWindow()
	{
		//*	Create the title bar
		setTitle(getApplication().getString(kRsrc_Friends));
		setIcon(LiveJournal.Instance().getBitmap(ID_SMALL_ICON));

		//*	Create the scroll view
		mView = new ScrollView();
		mView.setActualSize(getWidth(), getHeight());
		addChild(mView);
		setFocusedChild(mView);
		
		//*	Add the friend list to it
		mFriendsList = new ListView();
		mFriendsList.autoResize(true);
		mFriendsList.setWidth(mView.getContentWidth());
		mView.addChild(mFriendsList);
		mFriendsList.show();
		mView.setFocusedChild(mFriendsList);

		//*	Create web menu for later insertion as a sub-menu
		mWebMenu = new Menu("Web Options");

		Reset();
		
		mView.show();
	}

	//*	--------------------	AdjustActionMenuState

	public void
	adjustActionMenuState()
	{
	Menu		m = getActionMenu();
	MenuItem	i;
	
		m.removeAllItems();

		m.addItem(getApplication().getString(kRsrc_AddFriend), kCmd_AddFriend, 0, null, this);
		m.addItem(getApplication().getString(kRsrc_UpdateFriendsList), kCmd_GetFriends);
		
		if (mFriendsList.itemCount() > 0)
		{
		Vector	friends;
		Friend	f;
		
			friends = LiveJournal.Instance().Friends();
			f = (Friend) friends.elementAt(mFriendsList.getValue());

			//*	Web resources menu
			mWebMenu.removeAllItems();
			i = mWebMenu.addItem(getApplication().getString(kRsrc_ViewFriendEntries), kCmd_ViewFriendEntries, 0, f.mUser, this);
			i = mWebMenu.addItem(getApplication().getString(kRsrc_ViewFriendsEntries), kCmd_ViewFriendsEntries, 0, f.mUser, this);
			i = mWebMenu.addItem(getApplication().getString(kRsrc_ViewFriendCalendar), kCmd_ViewFriendCalendar, 0, f.mUser, this);
			i = mWebMenu.addItem(getApplication().getString(kRsrc_ViewFriendInfo), kCmd_ViewFriendInfo, 0, f.mUser, this);

			i = m.addItem(getApplication().getString(kRsrc_Web));
			i.addSubMenu(mWebMenu);
			
			m.addDivider();
			
			//*	Delete friend
			m.addItem(getApplication().getString(kRsrc_DeleteFriend), kCmd_DeleteFriend, 0, f.mUser, this);
		}
	}

	//*	--------------------	EventDeviceWidgetUp

	public boolean
	eventWidgetUp(int inWhichWidget, Event event)
	{
		if (Event.DEVICE_BUTTON_BACK == inWhichWidget)
		{
			hide();
			return true;
		}
		
		return super.eventWidgetUp(inWhichWidget, event);
	}

	//*	--------------------	ReceiveEvent

	public final boolean
	receiveEvent(Event e)
	{
		switch (e.type)
		{
			case kCmd_AddFriend:
				if (e.data == 0)
				{
				AddDialog	a = new AddDialog(getApplication().getString(kRsrc_AddFriend), this);
				
					a.show();
				}
				else
				{
					if (HasFriend((String) e.argument))
					{
						AlertDuplicateFriend((String) e.argument);
					}
					else
					{
					Event	e2 = new Event(e);
					
						e2.setListener(null);
						sendEventToWindow(e2);
					}
				}

				return true;
				
			case kCmd_DeleteFriend:
				if (e.data == 0)
				{
					WarnDeleteFriend((String) e.argument);
				}
				else
				{
				Event	e2 = new Event(e);
				
					e2.setListener(null);
					sendEventToWindow(e2);
				}

				return true;

			case kCmd_ViewFriendEntries:
				GotoURL(getApplication().getString(kRsrc_UsersBase) + ((String) e.argument));
				return true;

			case kCmd_ViewFriendsEntries:
				GotoURL(getApplication().getString(kRsrc_UsersBase) + ((String) e.argument) + "/friends");
				return true;

			case kCmd_ViewFriendCalendar:
				GotoURL(getApplication().getString(kRsrc_UsersBase) + ((String) e.argument) + "/calendar");
				return true;

			case kCmd_ViewFriendInfo:
				GotoURL(getApplication().getString(kRsrc_LJBase) + "userinfo.bml?user=" + ((String) e.argument));
				return true;
		}

		return super.receiveEvent(e);
	}

	//*	--------------------	GotoURL

	private final void
	GotoURL(String inURL)
	{
		try
		{
			URL.gotoURL(inURL);
		}
		catch (Exception e)
		{
		
		}
	}

	//*	--------------------	Reset

	public final void
	Reset()
	{
	Vector	friends = LiveJournal.Instance().Friends();
	int		numFriends = friends.size();
	
		mFriendsList.removeAllItems();
		
		for (int i = 0; i < numFriends; i++)
		{
		Friend	f = (Friend) friends.elementAt(i);
		
			mFriendsList.addItem(f.mName + " [" + f.mUser + "]");
		}

		invalidate();
	}

	//*	--------------------	Paint

	public final void
	paint(Pen p)
	{
		if (mFriendsList.itemCount() == 0)
		{
			clear(p);
			p.setColor(Color.BLACK);
			p.drawText(20, 20, getApplication().getString(kRsrc_EmptyFriendsList));
		}
		else
		{
			super.paint(p);
		}
	}

	//*	--------------------	HasFriend

	private final boolean
	HasFriend(String inName)
	{
	int		numFriends;
	Vector	friends = LiveJournal.Instance().Friends();

		numFriends = friends.size();
		
		for (int i = 0; i < numFriends; i++)
		{
		Friend	f = (Friend) friends.elementAt(i);
		
			if (inName.equalsIgnoreCase(f.mUser))
				return true;
		}
		
		return false;
	}

	//*	--------------------	AlertDuplicateFriend

	private final void
	AlertDuplicateFriend(String inFriendName)
	{
		AlertWindow	alert;
		String		text;
	
		text = StringFormat.fromRsrc(kRsrc_DuplicateFriendFormat, inFriendName);
	
		alert = new AlertWindow(text);
		alert.show();
		
		return;
	}

 	//*	--------------------	WarnDeleteFriend
 	
	private final void
	WarnDeleteFriend(String inFriend)
	{
		AlertWindow	alert;
	
		alert = getApplication().getAlert(kWarnDeleteFriendAlert, this);

		alert.setEventForControlWithID(kID_WarnDeleteFriendButton,
			new Event(kCmd_DeleteFriend, 0 /* what */,
					1 /* data */, inFriend));

		alert.show();
	}

	//*	--------------------------------	Class Variables
	
	ScrollView	mView;
	ListView	mFriendsList;
	Menu		mWebMenu;
	
	//*	--------------------------------	Constants

}

class AddDialog extends DialogWindow implements JournalResources, JournalCommands
{
	//*	--------------------	AddDialog

	AddDialog(String inTitle, FriendsWindow inBrowser)
	{
		super(inTitle);
		
		Rect viewRect = getContentRect();
	
		mBrowser = inBrowser;
	
		StaticText	label = new StaticText(LiveJournal.Instance().getString(kRsrc_FriendName));
		label.setPosition(viewRect.left + 6, viewRect.top + 4);
		label.setFont(Font.findBoldSystemFont());
		addChild(label);
		label.show();

		mName = new TextField(false, true);
		Layout.alignTop(mName, label);
		Layout.positionToRight(mName, label, label.getTop() - 3, 2);
		mName.setWidth(getWidth() - mName.getLeft() - 5);
		addChild(mName);
		mName.show();
		
		setFocusedChild(mName);

		mButton = Button.makeDoneButton(this, CMD_NEW);
		Layout.alignRight(mButton, mName);
		Layout.positionBelow(mButton, mName, 2);
		addChild(mButton);
		mButton.show();

		setBackEvent(new Event(this, CMD_NEW, 0, 0, null));

		snapToChildren(5, 5, 5, 5);
	}

	//*	--------------------	ReceiveEvent

	public boolean
	receiveEvent(Event e)
	{
		switch (e.type)
		{
			case CMD_NEW:
			{
			String	name = StringUtils.trimWhitespace(mName.toString());
			
				if (false == StringUtils.isEmpty(name))
					sendEventToWindow(new Event(mBrowser, kCmd_AddFriend, 0, 1, name));

				hide();
				return true;
			}
		}
		
		return super.receiveEvent(e);
	}

	//*	--------------------------------	Instance Variables

	FriendsWindow	mBrowser;
	TextField		mName;
	Button			mButton;
	
	//*	--------------------------------	Constants

	static final int	CMD_NEW		=	1;
	static final int	CMD_EDIT	=	2;
}
