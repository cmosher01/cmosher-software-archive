// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.app.DataRecord;

public class JournalDataRecord
{
	//*	--------------------	JournalDataRecord

	public
	JournalDataRecord()
	{
		mData = new DataRecord(kNumRows);
		
		SetSubject("");
		SetEntry("");
		SetJournal("");
		
		SetFlags(0);
		SetUID(0);
		SetItemID(0);
		SetDate(0);
		SetAllowMask(0);
		SetSecurity(0);
	}

	//*	--------------------	JournalDataRecord

	public
	JournalDataRecord(byte[] inData)
	{
		mData = new DataRecord(inData, kNumRows);
	}

	//*	--------------------	Overlay

	public final void
	Overlay(byte[] inData)
	{
		mData.mData = inData;
	}

	//*	--------------------	Subject

	public final String
	Subject()
	{
		return mData.getString(kSubjectRow, 0);
	}
	
	//*	--------------------	SetSubject

	public final void
	SetSubject(String inSubject)
	{
		if (null != inSubject)
			mData.putString(kSubjectRow, 0, inSubject);
	}

	//*	--------------------	Entry

	public final String
	Entry()
	{
		return mData.getString(kEntryRow, 0);
	}
	
	//*	--------------------	SetEntry

	public final void
	SetEntry(String inEntry)
	{
		if (null != inEntry)
			mData.putString(kEntryRow, 0, inEntry);
	}

	//*	--------------------	Journal

	public final String
	Journal()
	{
		return mData.getString(kJournalRow, 0);
	}
	
	//*	--------------------	SetJournal

	public final void
	SetJournal(String inEntry)
	{
		if (null != inEntry)
			mData.putString(kJournalRow, 0, inEntry);
	}

	//*	--------------------	Date

	public final int
	Date()
	{
		return mData.getInt(MISC_ROW, kDateColumn);
	}

	//*	--------------------	SetDate

	public final void
	SetDate(int inDate)
	{
		mData.putInt(MISC_ROW, kDateColumn, inDate);
	}

	//*	--------------------	ItemID

	public final int
	ItemID()
	{
		return mData.getInt(MISC_ROW, kItemIDColumn);
	}

	//*	--------------------	SetItemID

	public final void
	SetItemID(int inID)
	{
		mData.putInt(MISC_ROW, kItemIDColumn, inID);
	}

	//*	--------------------	UID

	public final int
	UID()
	{
		return mData.getInt(MISC_ROW, kUIDColumn);
	}

	//*	--------------------	SetUID

	public final void
	SetUID(int inUID)
	{
		mData.putInt(MISC_ROW, kUIDColumn, inUID);
	}

	//*	--------------------	Security

	public final int
	Security()
	{
		return mData.getInt(MISC_ROW, kSecurityColumn);
	}

	//*	--------------------	SetSecurity

	public final void
	SetSecurity(int inSecurity)
	{
		mData.putInt(MISC_ROW, kSecurityColumn, inSecurity);
	}

	//*	--------------------	AllowMask

	public final int
	AllowMask()
	{
		return mData.getInt(MISC_ROW, kAllowMaskColumn);
	}

	//*	--------------------	SetAllowMask

	public final void
	SetAllowMask(int inMask)
	{
		mData.putInt(MISC_ROW, kAllowMaskColumn, inMask);
	}

	//*	--------------------	Flags

	public final int
	Flags()
	{
		return mData.getInt(MISC_ROW, kFlagsColumn);
	}

	//*	--------------------	SetFlags

	public final void
	SetFlags(int inFlags)
	{
		mData.putInt(MISC_ROW, kFlagsColumn, inFlags);
	}

	//*	--------------------	SetDeleting

	public final void
	SetDeleting()
	{
		SetFlags(Flags() | kDeleting);
	}

	//*	--------------------	Deleting

	public final boolean
	Deleting()
	{
		return (Flags() & kDeleting) != 0;
	}

	//*	--------------------	SetEditing

	public final void
	SetEditing()
	{
		SetFlags(Flags() | kEditing);
	}

	//*	--------------------	SetPosting

	public final void
	SetPosting()
	{
		SetFlags(Flags() | kPosting);
	}

	//*	--------------------	RecordVersion

	public static final int
	RecordVersion()
	{
		return 2;
	}

	//*	--------------------------------	Instance Variables

	DataRecord	mData;
	
	//*	--------------------------------	Constants

	private static final int	kSubjectRow			=	0;
	private static final int	kEntryRow			=	1;
	private static final int	kJournalRow			=	2;
	private static final int	MISC_ROW			=	3;
	private static final int	kNumRows			=	4;
	
	private static final int	kSecurityColumn		=	0;
 	private static final int	kAllowMaskColumn	=	1;
	private static final int	kDateColumn			=	2;
	private static final int	kItemIDColumn		=	3;
	private static final int	kUIDColumn			=	4;
	private static final int	kFlagsColumn		=	5;

	private static final int	kPosting			=	1;
	private static final int	kEditing			=	2;
	private static final int	kDeleting			=	4;
}
