// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.app.*;
import danger.ui.*;

public 	class		LoginWindow
		extends		ScreenWindow
		implements	JournalCommands, JournalResources, AppResources
{
	//*	--------------------	LoginWindow

	public
	LoginWindow()
	{
	StaticText	label;
	ImageView	b;
	
		//*	Create the title bar
		setTitle(getApplication().getString(kRsrc_LiveJournal));
		setIcon(LiveJournal.Instance().getBitmap(ID_SMALL_ICON));

		setBackgroundColor(Color.GRAY12);
		b = new ImageView(LiveJournal.Instance().Background());
		addChild(b);
		b.show();

		//*	Name field
		mName = new TextField();
		mName.setPosition(100, 77);
		mName.setWidth(getWidth() - 105);
		addChild(mName);
		mName.show();

		//*	Name label
		label = new StaticText(getApplication().getString(ID_NAME));
		label.setTransparent(true);
		Layout.positionToLeft(label, mName, mName.getTop() + 3, 2);
		addChild(label);
		label.show();

		//*	Password field
		mPassword = new PasswordTextField();
		mPassword.setWidth(getWidth() - 105);
		Layout.positionBelow(mPassword, mName, 100, 2);
		addChild(mPassword);
		mPassword.show();

		//*	Password label
		label = new StaticText(getApplication().getString(kRsrc_Password));
		label.setTransparent(true);
		Layout.positionToLeft(label, mPassword, mPassword.getTop() + 3, 2);
		addChild(label);
		label.show();

		//*	Add login button.  I do this swizzle on the title so that the button
		//*	is sized properly for the longer of the two strings.
		mLoginButton = new Button(getApplication().getString(kRsrc_CancelLogin));
		mLoginButton.setTitle(getApplication().getString(kRsrc_Login));
		mLoginButton.setPosition(getWidth() - (mLoginButton.getWidth() + 5), getHeight() - (mLoginButton.getHeight() + 5));
		mLoginButton.setEvent(new Event(this, kCmd_Login));
		mLoginButton.setTransparent(true);
		addChild(mLoginButton);
		mLoginButton.show();
		
		setFocusedChild(mName);
	}

	//*	--------------------	Reset

	public final void
	Reset()
	{
		mName.enable();
		mPassword.enable();
		
		mName.setText("");
		mPassword.setText("");

		mLoginButton.setTitle(getApplication().getString(kRsrc_Login));
		mLoginButton.setEvent(new Event(this, kCmd_Login));
		
		setFocusedChild(mName);
	}

	//*	--------------------	ReceiveEvent

	public final boolean
	receiveEvent(Event e)
	{
		if (e.type == kCmd_Login)
		{
		String[]	args = new String[2];
		Event		e2;

			//*	Disable the text controls so that focus stays on the login/cancel button
			mName.disable();
			mPassword.disable();

			mLoginButton.setTitle(getApplication().getString(kRsrc_CancelLogin));
			mLoginButton.setEvent(new Event(null, kCmd_CancelLogin));

			args[0] = mName.toString();
			args[1] = mPassword.toString();

			e2 = new Event(getApplication(), kCmd_Login, 0, 0, args);
			sendEventToWindow(e2);
			
			return true;
		}
		
		return super.receiveEvent(e);
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

	//*	--------------------------------	Class Variables

	private TextField			mName;
	private PasswordTextField	mPassword;
	private Button				mLoginButton;
	
	//*	--------------------------------	Constants

}
