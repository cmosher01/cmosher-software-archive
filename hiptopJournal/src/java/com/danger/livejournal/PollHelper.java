// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.system.Strings;
import danger.system.Bitmaps;
import danger.util.StringUtils;
import danger.app.*;
import danger.ui.*;

public 	class		PollHelper
		extends		ScreenWindow
		implements	JournalCommands, JournalResources, AppResources, danger.SystemStrings
{
	//*	--------------------	PollHelper

	public
	PollHelper(EditText inText)
	{
	View		bottomView;
	StaticText	s;

		mText = inText;
	
		//*	Create the title bar
		setTitle(getApplication().getString(kRsrc_PollHelper));
		setIcon(LiveJournal.Instance().getBitmap(ID_SMALL_ICON));

		//*	Create the scroll view
		mView = new ScrollView();
		mView.setActualSize(getWidth(), getHeight());
		addChild(mView);

		//*	Title
		mTitle = new TextField();
		mTitle.setLeft(kFieldLeft);
		mTitle.setWidth(getWidth() - kFieldLeft - 2);
		mView.addChild(mTitle);
		mTitle.show();

		//*	Title label
		s = new StaticText(getApplication().getString(kRsrc_Title));
		Layout.positionToLeft(s, mTitle, mTitle.getTop() + 3, 2);
		mView.addChild(s);
		s.show();

		//*	Question
		mQuestion = new TextField();
		Layout.positionBelow(mQuestion, mTitle, kFieldLeft, 2);
		mQuestion.setWidth(getWidth() - kFieldLeft - 2);
		mView.addChild(mQuestion);
		mQuestion.show();
		
		//*	Question label
		s = new StaticText(getApplication().getString(kRsrc_Question));
		Layout.positionToLeft(s, mQuestion, mQuestion.getTop() + 3, 2);
		mView.addChild(s);
		s.show();

		//*	Type
		mType = new PopupMenu("type");
		mType.addItem("Radio Buttons");
		mType.addItem("Check Boxes");
		mType.addItem("Drop Down");
		Layout.positionBelow(mType, mQuestion, kMenuLeft, 2);
		mType.setWidth(getWidth() - kMenuLeft - 2);
		mView.addChild(mType);
		mType.show();

		//*	Type label
		s = new StaticText(getApplication().getString(kRsrc_Type));
		Layout.positionToLeft(s, mType, mType.getTop() + 3, 2);
		mView.addChild(s);
		s.show();

		//*	Visibility
		mVisibility = new PopupMenu("visibility");
		mVisibility.addItem("Everyone");
		mVisibility.addItem("Friends Only");
		mVisibility.addItem("Private");
		Layout.positionBelow(mVisibility, mType, kMenuLeft, 2);
		mVisibility.setWidth(getWidth() - kMenuLeft - 2);
		mView.addChild(mVisibility);
		mVisibility.show();

		//*	Visibility label
		s = new StaticText(getApplication().getString(kRsrc_WhoView));
		Layout.positionToLeft(s, mVisibility, mVisibility.getTop() + 3, 2);
		mView.addChild(s);
		s.show();

		//*	Votability
		mVotability = new PopupMenu("votability");
		mVotability.addItem("Everyone");
		mVotability.addItem("Friends Only");
		Layout.positionBelow(mVotability, mVisibility, kMenuLeft, 2);
		mVotability.setWidth(getWidth() - kMenuLeft - 2);
		mView.addChild(mVotability);
		mVotability.show();

		//*	Votability label
		s = new StaticText(getApplication().getString(kRsrc_WhoVote));
		Layout.positionToLeft(s, mVotability, mVotability.getTop() + 3, 2);
		mView.addChild(s);
		s.show();

		//*	Choices label
		s = new StaticText(getApplication().getString(kRsrc_Choices));
		Layout.positionBelow(s, mVotability, 2, 5);
		mView.addChild(s);
		s.show();

		//*	Poll entries
		mOptions = new TextField[kNumPollEntries];
		bottomView = s;
		
		for (int i = 0; i < kNumPollEntries; i++)
		{
		TextField	e = new TextField();
		
			mOptions[i] = e;
			Layout.positionBelow(e, bottomView, 2, 2);
			e.setWidth(getWidth() - 4);
			mView.addChild(e);
			e.show();
			bottomView = e;
		}
		
		mView.show();

		mView.setFocusedChild(mTitle);
		setFocusedChild(mView);
	}

	//*	--------------------	AdjustActionMenuState

	public final void
	adjustActionMenuState()
	{
	Menu		m = getActionMenu();
	MenuItem	i;
	
		m.removeAllItems();

		i = m.addItem(getApplication().getString(ID_DONE), CMD_DONE, 0, null, this);
		
		if (! ValidPoll())
			i.disable();
		
		m.addDivider();
		
		i = m.addItem(getApplication().getString(ID_CANCEL), kCmd_Cancel, 0, null, this);
		i.setShortcut('.');
	}

	//*	--------------------	ReceiveEvent

	public final boolean
	receiveEvent(Event e)
	{
		switch (e.type)
		{
			case CMD_DONE:
				SaveSettings();
				hide();
				return true;
				
			case kCmd_Cancel:
				hide();
				return true;
		}
	
		return super.receiveEvent(e);
	}

	//*	--------------------	EventDeviceWidgetUp

	public final boolean
	eventWidgetUp(int inWhichWidget, Event event)
	{
		if (Event.DEVICE_BUTTON_BACK == inWhichWidget)
		{
			hide();
			return true;
		}
		
		return super.eventWidgetUp(inWhichWidget, event);
	}

	//*	--------------------	SaveSettings

	private final void
	SaveSettings()
	{
	StringBuffer	s = new StringBuffer();
	
		//*	Opening poll tag
		s.append("<lj-poll");
		
		//*	Title
		if (! StringUtils.isEmpty(mTitle.toString()))
		{
			s.append(" name=\"");
			s.append(mTitle.toString());
			s.append('"');
		}

		//*	Votability
		s.append(" whovote=\"");
		
		switch (mVotability.getValue())
		{
			case 0:	s.append("all\"");		break;
			case 1:	s.append("friends\"");	break;
		}
		
		//*	Visibility
		s.append(" whoview=\"");
		
		switch (mVotability.getValue())
		{
			case 0:	s.append("all");		break;
			case 1:	s.append("friends");	break;
			case 2:	s.append("none");		break;
		}

		s.append("\">\r");

		//*	Question
		s.append("<lj-pq type=");
		
		switch (mType.getValue())
		{
			case 0:	s.append("radio");		break;
			case 1:	s.append("check");		break;
			case 2:	s.append("drop");		break;
		}

		s.append(">\r");
		s.append(mQuestion.toString());
		s.append("\r");
		
		//*	Options
		for (int i = 0; i < kNumPollEntries; i++)
		{
		TextField	e = mOptions[i];
		
			if (StringUtils.isEmpty(e.toString()))
				continue;
				
			s.append("<lj-pi>");
			s.append(e.toString());
			s.append("</lj-pi>\r");
		}
		
		//*	Close question
		s.append("</lj-pq>\r");

		//*	Close poll
		s.append("</lj-poll>\r\r");
		
		mText.insert(s.toString());
	}

	//*	--------------------	ValidPoll

	private final boolean
	ValidPoll()
	{
		//*	Must have a question
		if (StringUtils.isEmpty(mQuestion.toString()))
			return false;

		//*	And at least one option
		for (int i = 0; i < kNumPollEntries; i++)
		{
			if (! StringUtils.isEmpty(mOptions[i].toString()))
				return true;
		}
		
		return false;
	}

	//*	--------------------------------	Class Variables

	EditText	mText;
	
	ScrollView	mView;

	TextField	mTitle;
	TextField	mQuestion;
	PopupMenu	mType;
	PopupMenu	mVisibility;
	PopupMenu	mVotability;
	TextField[]	mOptions;
	
	//*	--------------------------------	Constants

	static final int	kNumPollEntries		=	20;
	
	static final int	kFieldLeft			= 	47;
	static final int	kMenuLeft			=	67;
}
