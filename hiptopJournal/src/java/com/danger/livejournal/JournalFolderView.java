// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.app.*;
import danger.ui.*;
import danger.util.*;
import danger.util.format.DateFormat;

import java.util.Calendar;

public class JournalFolderView extends FolderView implements JournalCommands, JournalResources
{
	//*	--------------------	JournalFolderView

	public
	JournalFolderView()
	{
		mApp = LiveJournal.Instance();
		mFontPlain = Font.findSystemFont();
		sFontItalic = Font.findItalicSystemFont();
		sFontDate = Font.findFont("AddisonBook5");

		mAscent = mFontPlain.getAscent();

		mDateNow = Calendar.getInstance();
		mDateThen = Calendar.getInstance();

		setHasScrollbar(true);
	}

	//*	--------------------	DrawFolderLine

	public final void
	drawFolderLine(Pen p, Object inEntry, Rect r, boolean inSelected)
	{
	Folder	entry = (Folder) inEntry;
	int		indent = entry.getOpenIcon().getWidth() + COLUMN_GAP;
	int		xoffset = r.left + indent;
	String	title = entry.getTitle();
	int		sz = entry.size();

		if (sz > 0)
			title += " (" + sz + ")";
		else
			title += " (" + mApp.getString(kRsrc_Empty) + ")";

		title = StringUtils.truncate(title, r.right - xoffset, mFontBold, StringUtils.TRUNCATE_AT_END);

		p.setFont(mFontBold);
		
		p.drawText(xoffset, r.top + mAscent, title);

		if (true == entry.isOpen())
			p.drawBitmap(r.left, r.top + 1, entry.getOpenIcon());
		else
			p.drawBitmap(r.left, r.top + 1, entry.getClosedIcon());
	}

	//*	--------------------	DrawItemLine

	public final void
	drawItemLine(Pen p, Object inItem, Rect inDim, boolean inSelected)
	{
	JournalDataRecord	e = (JournalDataRecord) inItem;
	String				s;
	String				dateString;
	Font				font = mFontPlain;
	int					tempWidth;

		s = e.Subject();
		dateString = StringUtils.makeDateString(e.Date());
		p.setFont(sFontDate);

		//*	Draw the date
		tempWidth = sFontDate.getWidth(dateString);
		p.drawText(inDim.right - tempWidth, inDim.top + mAscent, dateString);

		//*	Draw the subject
		if (0 == e.Flags())
			p.setFont(mFontPlain);
		else
			p.setFont(sFontItalic);
			
		s = StringUtils.makeDisplayString(s, (inDim.right - inDim.left) - (10 + 2 + tempWidth), font);
		p.drawText(inDim.left + 10, inDim.top + mAscent, s);

		//*	If it's in the process of being deleted then draw it with strike-through
		if (e.Deleting())
		{
		int	y = (inDim.top + inDim.bottom) / 2;
		
			p.drawLine(inDim.left, y, inDim.right, y);
		}
	}

	//*	--------------------	EventKeyUp

	public boolean
	eventKeyUp(char inChar, Event event)
	{
		switch (inChar)
		{
			case '\r':
			{
				//*	Check to see if it is a folder	
				if (-1 == mCurrentItem)
					toggleFolder(getFolder(mCurrentFolder));
				else
					selectItem(getFolder(mCurrentFolder).getItem(mCurrentItem));
			}
			return true;
		
			case '\b':
			{
			JournalDataRecord	e;
			
				if (-1 == mCurrentItem)
					return true;

				e = (JournalDataRecord) getSelection();

				if (0 == e.Flags())
					sendEventToWindow(new Event(CMD_DELETE_ENTRY, 0, 0, getSelection()));
			}
			return true;
		}
		return false;
	}
	
	//*	--------------------	SelectItem
	
	public final void
	selectItem(Object o)
	{
	JournalDataRecord	e = (JournalDataRecord) o;
	
		if (0 == e.Flags())
			sendEventToWindow(new Event(kCmd_ReopenEntry, 0, 0, e));
	}
	
	//*	--------------------------------	Class Variables
	
	static Font			sFontDate;
	static Font			sFontItalic;
	
	//*	--------------------------------	Instance Variables

	private Calendar	mDateNow;
	private Calendar	mDateThen;

	private LiveJournal	mApp;

	//*	--------------------------------	Constants
	
	static final int	kSecondsInYear			=	31536000;
	static final long	kMillisecondsInYear		=	((long) kSecondsInYear) * 1000;
}

