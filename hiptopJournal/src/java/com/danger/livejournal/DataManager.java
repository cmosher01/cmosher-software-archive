// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.util.StringUtils;
import danger.app.*;
import danger.ui.*;
import danger.system.Hardware;

public class DataManager implements JournalCommands
{
	//*	--------------------	Restore

	public static final void
	Restore(String inUserName)
	{
	int		count;
	byte[]	data;

		sUserName = inUserName;
		sNextJournalUID = 1;
		
		EmptyOldStores();

		//*	Load the data stores or create them if they don't exist
		sJournalStore = DataStore.createDataStore(JournalStoreName(JournalDataRecord.RecordVersion()));

		//*	Restore the journal entries
		count = sJournalStore.getRecordCount();

		if (count > 0)
		{
			for (int i = 0; i < count; i++)
			{
			JournalDataRecord	entry;
			int					uid;
				
				//*	Allocate a browser cache entry and add it to the view
				uid = sJournalStore.getRecordUID(i);
				
				//*	The entry with UID zero is special and holds the last sync date
				if (0 != uid)
				{
					entry = new JournalDataRecord(sJournalStore.getRecordData(i));
					entry.SetUID(uid);

					if (uid >= sNextJournalUID)
						sNextJournalUID = uid + 1;

					entry.SetFlags(0);
	
					LiveJournal.Instance().RestoreEntry(entry);
				}
			}
		}
	}

	//*	--------------------	AddJournalRecord

	public static final int
	AddJournalRecord(byte[] inData)
	{
	int	index;
	int	uid;
	
		synchronized (sJournalStore)
		{
			uid = sNextJournalUID++;
			index = sJournalStore.insertRecordSorted(uid, inData);
			sJournalStore.setRecordUID(index, uid);
		}
		
		return uid;
	}

	//*	--------------------	TouchJournal

	public static final void
	TouchJournal(JournalDataRecord inJournal)
	{
		synchronized (sJournalStore)
		{
		int	index;
	
			index = sJournalStore.binarySearch(inJournal.UID());
			
			if (index != -1)
				sJournalStore.setRecordData(index, inJournal.mData.mData);
		}
	}

	//*	--------------------	Remove

	public static final void
	Remove(JournalDataRecord inJournal)
	{
	int	journalIndex;
	
		synchronized (sJournalStore)
		{
			journalIndex = sJournalStore.binarySearch(inJournal.UID());
			
			if (journalIndex == -1)
				return;

			sJournalStore.removeRecord(journalIndex);
		}
	}

	//*	--------------------	LastSyncDate

	public static final String
	LastSyncDate()
	{
	byte[]	data;
	int		index;
	
		index = sJournalStore.binarySearch(0);
		
		if (-1 == index)
			return "";
			
		data = sJournalStore.getRecordDataByUID(0);
		
		return new String(data);
	}

	//*	--------------------	SetLastSyncDate

	public static final void
	SetLastSyncDate(String inDate)
	{
	int	index;
	
		if (StringUtils.isEmpty(inDate))
			return;
			
		index = sJournalStore.binarySearch(0);
		
		if (-1 == index)
			sJournalStore.insertRecordSorted(0, inDate.getBytes());
		else
			sJournalStore.setRecordData(index, inDate.getBytes());
	}

	//*	--------------------	JournalStoreName

	private static final String
	JournalStoreName(int inVersion)
	{
		return "$" + sUserName + ":" + inVersion + "$";
	}

	//*	--------------------	EmptyOldStores

	private static final void
	EmptyOldStores()
	{
	int	version = JournalDataRecord.RecordVersion();
	
		//*	Remove old journal stores
		for (int i = 0; i < version; i++)
		{
		String		name;
		DataStore	d;
		
			name = JournalStoreName(i);
			
			d = DataStore.findDataStore(name);
			
			if (d != null)
			{
				d.removeAllRecords();
				d.discardOnReboot(true);
			}
		}
	}
	
	//*	--------------------------------	Class Variables

	private static DataStore	sJournalStore;
	private static String		sUserName = "";
	private static int			sNextJournalUID;
}
