// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

import danger.app.*;
import danger.io.*;
import danger.net.*;
import danger.system.Strings;
import danger.ui.*;
import danger.util.*;
import danger.util.format.StringFormat;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

public	class		LiveJournal
		extends		Application
		implements	JournalCommands, JournalResources, danger.SystemStrings
{
	//*	--------------------	LiveJournal

	public
	LiveJournal()
	{
		sMe = this;

		mComparator = new JournalComparator();

		mNetworkQueue = new Vector();
		mFriends = new Vector();

		mBrowseWindow = new BrowseWindow();
		mLoginWindow = new LoginWindow();
		mFriendsWindow = new FriendsWindow();

		mLastSyncDate = "";

		mState = kState_LoggedOut;
	}

	//*	--------------------	launch

	public void
	launch()
	{
		mLoginWindow.show();
	}

	//*	--------------------	ReceiveEvent

	public final boolean
	receiveEvent(Event e)
	{
		switch (e.type)
		{
			case kCmd_NewEntry:
				(new EntryWindow()).show();
				return true;

			case kCmd_ReopenEntry:
				(new EntryWindow((JournalDataRecord) e.argument)).show();
				return true;

			case kCmd_PostEntry:
				PostEntry((JournalDataRecord) e.argument);
				return true;

			case kCmd_SaveAsDraft:
				SaveAsDraft((JournalDataRecord) e.argument);
				return true;

			case CMD_DELETE_ENTRY:
				if (0 == e.data)
					WarnDeleteEntry((JournalDataRecord) e.argument);
				else
					DeleteEntry((JournalDataRecord) e.argument);
				return true;

			case kCmd_Login:
				Login((String[]) e.argument);
				return true;

			case kCmd_Logout:
				Logout();
				return true;

			case kCmd_SyncNow:
				SyncNow();
				return true;

			case kCmd_GetFriends:
				GetFriends();
				return true;

			case kCmd_CancelLogin:
				mState = kState_LoggedOut;
				mLoginWindow.Reset();
				return true;

			case kCmd_ShowFriends:
				mFriendsWindow.show();
				return true;

			case kCmd_AddFriend:
				AddFriend((String) e.argument);
				return true;

			case kCmd_DeleteFriend:
				DeleteFriend((String) e.argument);
				return true;
		}

		return super.receiveEvent(e);
	}

	//*	--------------------	NetworkEvent

	public final void
	networkEvent(Object s)
	{
		if (s instanceof HTTPTransaction)
		{
		HTTPTransaction	t = (HTTPTransaction) s;

			switch (mState)
			{
				case kState_LoggingIn:
					ProcessLoginResponse(t);
					return;

				case kState_PostingEvent:
					ProcessPost(t);
					return;

				case kState_DeletingEvent:
					ProcessDelete(t);
					return;

				case kState_EditingEvent:
					ProcessEdit(t);
					return;

				case kState_SyncAll:
				case kState_SyncDelta:
					ProcessSyncResponse(t);
					return;

				case kState_SyncingItem:
					ProcessSyncItemResponse(t);
					return;

				case kState_SyncingDeltas:
					ProcessSyncDeltaResponse(t);
					return;

				case kState_GettingFriends:
					ProcessGetFriendsResponse(t);
					return;

				case kState_AddingFriend:
					ProcessAddFriend(t);
					return;

				case kState_DeletingFriend:
					ProcessDeleteFriend(t);
					return;
			}
		}
	}

	//*	--------------------	ProcessLoginResponse

	private final void
	ProcessLoginResponse(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);

			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
				mFullName = (String) table.get("name");

				mState = kState_LoggedIn;

				//*	Load in the list of journals that this user can post to
				if (! StringUtils.isEmpty((String) table.get("access_count")))
				{
				int	count = StringUtils.toInt((String) table.get("access_count"));

					mJournals = new String[count];

					for (int i = 1; i <= count; i++)
						mJournals[i - 1] = (String) table.get("access_" + i);
				}
				else
				{
					mJournals = new String[0];
				}

				//*	Clear the browser window
				mBrowseWindow.Reset();

				//*	Repopulate it with saved entries
				DataManager.Restore(mUserName);

				mLastSyncDate = DataManager.LastSyncDate();

				//*	Display the data
				mBrowseWindow.show();

				mLoginWindow.hide();

				//*	Show any login messages that were attached to the response
				ShowResponseHeader(t, "message");

				error = false;
			}
		}
		else
		{
			mState = kState_LoggedOut;
			ReportServerError();
		}

		if (error)
		{
			mLoginWindow.Reset();
			ShowResponseHeader(t, "errmsg");
		}
	}

	//*	--------------------	SaveAsDraft

	private final void
	SaveAsDraft(JournalDataRecord inEntry)
	{
	int		uid;
	Folder	f;

		//*	Place it in the datastore if it isn't already there
		if (0 == inEntry.UID())
		{
			uid = DataManager.AddJournalRecord(inEntry.mData.mData);
			inEntry.SetUID(uid);

			f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_Drafts));
			f.insertItemSorted(mComparator, inEntry);
		}
		else	//*	Otherwise just update the entry
		{
			DataManager.TouchJournal(inEntry);
			f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_Drafts));
			f.sort(mComparator);
		}
	}

	//*	--------------------	PostEntry

	private final void
	PostEntry(JournalDataRecord inEntry)
	{
	int		uid;
	boolean	newEntry = false;

		if (kState_LoggedIn != mState)
		{
			QueueEvent(inEntry, kState_PostingEvent);
			return;
		}

		mActiveEntry = inEntry;

		//*	Place it in the datastore if it isn't already there
		if (0 == inEntry.UID())
		{
			uid = DataManager.AddJournalRecord(inEntry.mData.mData);
			inEntry.SetUID(uid);
			newEntry = true;
		}

		//*	If this entry isn't on the site, post it
		if (0 == inEntry.ItemID())
		{
		Folder	f;

			mState = kState_PostingEvent;
			inEntry.SetPosting();
			mBrowseWindow.UpdateStatus(StringFormat.fromRsrc(kRsrc_PostingFormat, inEntry.Subject()));
			Network.Post(inEntry);

			if (newEntry)
			{
				f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_Drafts));
				f.insertItemSorted(mComparator, inEntry);
			}
		}
		else	//*	Otherwise, update it
		{
			mState = kState_EditingEvent;
			inEntry.SetEditing();
			DataManager.TouchJournal(inEntry);
			mBrowseWindow.UpdateStatus(StringFormat.fromRsrc(kRsrc_EditingFormat, inEntry.Subject()));
			Network.Edit(inEntry, false);
		}
	}

	//*	--------------------	ProcessPost

	private final void
	ProcessPost(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);

			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			String	id;

				id = (String) table.get("itemid");
				mActiveEntry.SetItemID(StringUtils.toInt(id));
				DataManager.TouchJournal(mActiveEntry);

				mBrowseWindow.Finalize(mActiveEntry);

				error = false;
			}
		}
		else
		{
			mBrowseWindow.invalidate();
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		mActiveEntry.SetFlags(0);
		DataManager.TouchJournal(mActiveEntry);

		Idle();
	}

	//*	--------------------	NetDelete

	private final void
	NetDelete(int inItemID)
	{
	Folder				f;
	JournalDataRecord	e;

		//*	All server item numbering starts at 1
		if (inItemID < 1)
			return;

		//*	See if we have a local copy of this entry
		e = mBrowseWindow.GetItemByItemID(inItemID);

		if (null == e)
			return;

		//*	Remove it from the UI
		f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_PostedEntries));
		f.removeItem(e);

		//*	Remove it from the data store
		DataManager.Remove(e);
	}

	//*	--------------------	DeleteEntry

	private final void
	DeleteEntry(JournalDataRecord inEntry)
	{
	Folder	f;
	int		uid;

		//*	If it's a draft we don't have to tell the server
		if (inEntry.ItemID() == 0)
		{
			f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_Drafts));
			f.removeItem(inEntry);

			DataManager.Remove(inEntry);
			return;
		}

		//*	If there is another network operation going on then queue this one
		inEntry.SetDeleting();

		if (kState_LoggedIn != mState)
		{
			mBrowseWindow.invalidate();
			QueueEvent(inEntry, kState_DeletingEvent);
			return;
		}

		mState = kState_DeletingEvent;

		mActiveEntry = inEntry;
		mState = kState_DeletingEvent;
		mBrowseWindow.UpdateStatus(StringFormat.fromRsrc(kRsrc_DeletingFormat, inEntry.Subject()));

		Network.Edit(inEntry, true);
	}

	//*	--------------------	ProcessDelete

	private final void
	ProcessDelete(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);
			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			Folder	f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_PostedEntries));

				f.removeItem(mActiveEntry);
				DataManager.Remove(mActiveEntry);

				error = false;
			}
		}
		else
		{
			ReportServerError();
			mActiveEntry.SetFlags(0);
			DataManager.TouchJournal(mActiveEntry);
			mBrowseWindow.invalidate();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	ProcessEdit

	private final void
	ProcessEdit(HTTPTransaction t)
	{
	boolean	error = true;

		mState = kState_LoggedIn;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);
			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
				error = false;
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		mActiveEntry.SetFlags(0);
		DataManager.TouchJournal(mActiveEntry);

		Idle();
	}

	//*	--------------------	SyncNow

	private final void
	SyncNow()
	{
		if (kState_LoggedIn != mState)
		{
			QueueEvent(null, kState_SyncAll);
			return;
		}

		if (StringUtils.isEmpty(mLastSyncDate))
			mState = kState_SyncAll;
		else
			mState = kState_SyncDelta;

		mBrowseWindow.UpdateStatus(getString(kRsrc_RequestingSyncInfo));
		Network.Sync(mLastSyncDate);
	}

	//*	--------------------	ProcessSyncResponse

	private final void
	ProcessSyncResponse(HTTPTransaction t)
	{
	boolean	error = true;

		mBrowseWindow.UpdateStatus(getString(kRsrc_ProcessingSyncInfo));

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);
			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			Vector	items = ParseItemList(table);

				PruneSyncItemList(items);

				if (items.size() > 0)
				{
					if (mState == kState_SyncAll)
					{
						mSyncItems = items;
						mNumSyncItems = items.size();
					}
					else
					{
					String	oldestDate = mLastSyncDate;
					int		numItems = items.size();

						for (int i = 0; i < numItems; i++)
						{
						SyncNode	n = (SyncNode) items.elementAt(i);

							if (oldestDate.compareTo(n.mDateString) > 0)
								oldestDate = n.mDateString;
						}

						mState = kState_SyncingDeltas;
						Network.GetSyncDelta(oldestDate);
						return;
					}
				}

				error = false;
			}
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	ProcessSyncItemResponse

	private final void
	ProcessSyncItemResponse(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);
			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			int					count;

				count = StringUtils.toInt((String) table.get("events_count"));

				if (0 == count)
				{
					//*	I'm guessing that a count of zero means that this item was deleted
					NetDelete(mLastSyncItemID);
				}
				else
				{
				JournalDataRecord	e;
				String				subject;
				String				security;
				String				event;
				int					itemid;
				int					date;
				boolean				newEntry = false;

					itemid = StringUtils.toInt((String) table.get("events_1_itemid"));
					date = ParseDateString((String) table.get("events_1_eventtime"));
					event = (String) table.get("events_1_event");
					security = (String) table.get("events_1_security");
					subject = (String) table.get("events_1_subject");

					e = mBrowseWindow.GetItemByItemID(itemid);

					if (null == e)	//*	New entry
					{
						e = new JournalDataRecord();
						e.SetUID(DataManager.AddJournalRecord(e.mData.mData));
						newEntry = true;
						e.SetDate(date);
					}

					e.SetItemID(itemid);
					e.SetEntry(event);

					if (null == security)
						e.SetSecurity(0);
					else
						e.SetSecurity(1);

					e.SetSubject(subject);
					e.SetFlags(0);

					DataManager.TouchJournal(e);

					if (newEntry)
						RestoreEntry(e);
				}

				error = false;
			}
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	ProcessSyncDeltaResponse

	private final void
	ProcessSyncDeltaResponse(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);
			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			int	count;

				count = StringUtils.toInt((String) table.get("events_count"));

				for (int i = 1; i <= count; i++)
				{
				JournalDataRecord	e;
				String				subject;
				String				security;
				String				event;
				int					itemid;
				int					date;
				boolean				newEntry = false;

					itemid = StringUtils.toInt((String) table.get("events_" + i + "_itemid"));
					date = ParseDateString((String) table.get("events_" + i + "_eventtime"));
					event = (String) table.get("events_" + i + "_event");
					security = (String) table.get("events_" + i + "_security");
					subject = (String) table.get("events_" + i + "_subject");

					e = mBrowseWindow.GetItemByItemID(itemid);

					if (null == e)	//*	New entry
					{
						e = new JournalDataRecord();
						e.SetUID(DataManager.AddJournalRecord(e.mData.mData));
						newEntry = true;
						e.SetDate(date);
					}

					e.SetItemID(itemid);
					e.SetEntry(event);

					if (null == security)
						e.SetSecurity(0);
					else
						e.SetSecurity(1);

					e.SetSubject(subject);
					e.SetFlags(0);

					DataManager.TouchJournal(e);

					if (newEntry)
						RestoreEntry(e);
				}

				error = false;
			}
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	DeleteFriend

	private final void
	DeleteFriend(String inName)
	{
		if (kState_LoggedIn != mState)
		{
			QueueEvent(inName, kState_DeletingFriend);
			return;
		}

		mState = kState_DeletingFriend;
		mBrowseWindow.UpdateStatus(getString(kRsrc_DeletingFriend));
		mLastFriendDelete = inName;

		Network.DeleteFriend(inName);
	}

	//*	--------------------	ProcessDeleteFriend

	private final void
	ProcessDeleteFriend(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);

			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			int	numFriends = mFriends.size();

				for (int i = 0; i < numFriends; i++)
				{
				Friend	f = (Friend) mFriends.elementAt(i);

					if (mLastFriendDelete.equalsIgnoreCase(f.mUser))
					{
						mFriends.removeElementAt(i);
						break;
					}
				}

				mFriendsWindow.Reset();
				error = false;
			}
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	AddFriend

	private final void
	AddFriend(String inName)
	{
		if (kState_LoggedIn != mState)
		{
			QueueEvent(inName, kState_GettingFriends);
			return;
		}

		mState = kState_AddingFriend;
		mBrowseWindow.UpdateStatus(getString(kRsrc_AddingFriend));

		Network.AddFriend(inName);
	}

	//*	--------------------	ProcessAddFriend

	private final void
	ProcessAddFriend(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);

			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			int	count;

				count = StringUtils.toInt((String) table.get("friends_added"));

				for (int i = 1; i <= count; i++)
				{
				Friend	f;

					f = new Friend();

					f.mUser = (String) table.get("friend_" + i + "_user");
					f.mName = (String) table.get("friend_" + i + "_name");

					mFriends.addElement(f);
				}

				mFriendsWindow.Reset();

				error = false;
			}
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	GetFriends

	private final void
	GetFriends()
	{
		if (kState_LoggedIn != mState)
		{
			QueueEvent(null, kState_GettingFriends);
			return;
		}

		mState = kState_GettingFriends;
		mBrowseWindow.UpdateStatus(getString(kRsrc_GettingFriends));

		Network.GetFriends();
	}

	//*	--------------------	ProcessGetFriendsResponse

	private final void
	ProcessGetFriendsResponse(HTTPTransaction t)
	{
	boolean	error = true;

		if (200 == t.getResponse())
		{
		Hashtable	table;
		String		result;

			table = HashValues(t);

			result = (String) table.get("success");

			if ((null != result) && result.equals("OK"))
			{
			int	count;

				count = StringUtils.toInt((String) table.get("friend_count"));
				mFriends.removeAllElements();

				for (int i = 1; i <= count; i++)
				{
				Friend	f;

					f = new Friend();

					f.mUser = (String) table.get("friend_" + i + "_user");
					f.mName = (String) table.get("friend_" + i + "_name");

					mFriends.addElement(f);
				}

				mFriendsWindow.Reset();

				error = false;
			}
		}
		else
		{
			ReportServerError();
		}

		if (error)
			ShowResponseHeader(t, "errmsg");

		Idle();
	}

	//*	--------------------	RestoreEntry

	public final void
	RestoreEntry(JournalDataRecord inEntry)
	{
	Folder	f;

		if (inEntry.ItemID() > 0)
			f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_PostedEntries));
		else
			f = mBrowseWindow.mFolderView.getFolder(getString(kRsrc_Drafts));

		f.insertItemSorted(mComparator, inEntry);
	}

	//*	--------------------	Login

	private final void
	Login(String[] args)
	{
		mState = kState_LoggingIn;

		mUserName = args[0];
		mPassword = args[1];
		Network.Login();
	}

	//*	--------------------	Logout

	private final void
	Logout()
	{
		mState = kState_LoggedOut;
		mLoginWindow.Reset();
		mLoginWindow.show();
		mBrowseWindow.hide();
	}

	//*	--------------------	Instance

	public static final LiveJournal
	Instance()
	{
		return sMe;
	}

	//*	--------------------	Journals

	public final String[]
	Journals()
	{
		return mJournals;
	}

	//*	--------------------	Friends

	public final Vector
	Friends()
	{
		return mFriends;
	}

	//*	--------------------	Background

	public final Bitmap
	Background()
	{
		return getBitmap(kRsrc_Background);
	}

	//*	--------------------	FullName

	public final String
	FullName()
	{
		return mFullName;
	}

	//*	--------------------	Username

	public final String
	Username()
	{
		return mUserName;
	}

	//*	--------------------	Password

	public final String
	Password()
	{
		return mPassword;
	}

	//*	--------------------	Comparator

	public final Comparator
	Comparator()
	{
		return mComparator;
	}

	//*	--------------------	ReportServerError

	private final void
	ReportServerError()
	{
		showAlert(kServerErrorAlert);
	}

	//*	--------------------	ShowResponseHeader

	private final void
	ShowResponseHeader(HTTPTransaction t, String inHeader)
	{
		Hashtable	values;
		String		message;
		AlertWindow	alert;

		values = HashValues(t);

		message = (String) values.get(inHeader);

		if (null == message)
			return;

		alert = new AlertWindow(message);
		alert.show();
	}

	//*	--------------------	HashValues

	private final Hashtable
	HashValues(HTTPTransaction t)
	{
	Hashtable		table;
	String			data;
	StringTokenizer	tokenizer;
	int				numPairs;
	String			key, value;
	String			delim;

		table = new Hashtable();
		data = t.getString();

		//*	We sometimes seem to get CR, sometimes LF, sometimes CRLF delimiting
		//*	the response from the server so fix things up here so the tokenizer can
		//*	do its job properly.
		if (-1 == data.indexOf("\r"))
		{
			//*	No CR?  Ok, just split on LF.
			delim = "\n";
		}
		else
		{
			if (-1 != data.indexOf("\n"))
			{
				//*	Ok, we have CR and LF.  Strip the LF and split
				//*	on CR.
				data = StringUtils.stripLF(data);
				delim = "\n";
			}
			else
			{
				//*	Only LF are present.  Make that the delimiter.
				delim = "\r";
			}
		}

		tokenizer = new StringTokenizer(data, delim);
		numPairs = tokenizer.countTokens() / 2;

		for (int i = 0; i < numPairs; i++)
		{
			key = tokenizer.nextToken();
			value = StringUtils.urlDecodeUTF8(tokenizer.nextToken());

			table.put(key, value);
		}

		return table;
	}

	//*	--------------------	QueueEvent

	private final void
	QueueEvent(Object inEvent, int inAction)
	{
	QueueNode	node = new QueueNode();

		node.mEntry = inEvent;
		node.mAction = inAction;

		mNetworkQueue.addElement(node);
	}

	//*	--------------------	Idle

	private final void
	Idle()
	{
		if (mSyncItems != null)
		{
			if (0 == mSyncItems.size())	//*	Done processing
			{
				mSyncItems = null;
				mNumSyncItems = 0;
				mLastSyncItemID = 0;
				mBrowseWindow.setSubTitle(null);
			}
			else
			{
			SyncNode	n = (SyncNode) mSyncItems.elementAt(0);
			int			num = (mNumSyncItems - mSyncItems.size()) + 1;
			String		s;

				s = StringFormat.fromRsrc(kRsrc_SyncingFormat, String.valueOf(num), String.valueOf(mNumSyncItems));
				mBrowseWindow.setSubTitle(s);

				mSyncItems.removeElementAt(0);

				//*	Find the most recent sync date and hold onto it
				if (StringUtils.isEmpty(mLastSyncDate))
				{
					mLastSyncDate = n.mDateString;
					DataManager.SetLastSyncDate(mLastSyncDate);
				}
				else
				{
					if (mLastSyncDate.compareTo(n.mDateString) < 0)
					{
						mLastSyncDate = n.mDateString;
						DataManager.SetLastSyncDate(mLastSyncDate);
					}
				}

				mState = kState_SyncingItem;
				s = StringFormat.fromRsrc(kRsrc_SyncDateFormat, n.mDateString, String.valueOf(n.mItemID));
				mBrowseWindow.UpdateStatus(s);
				mLastSyncItemID = n.mItemID;
				Network.SyncItem(n.mItemID);

				return;
			}
		}

		mState = kState_LoggedIn;
		mActiveEntry = null;
		mBrowseWindow.UpdateStatus(getString(kRsrc_Idle));

		if (mNetworkQueue.size() > 0)
		{
		QueueNode	node = (QueueNode) mNetworkQueue.elementAt(0);

			mNetworkQueue.removeElementAt(0);

			switch (node.mAction)
			{
				case kState_PostingEvent:
					PostEntry((JournalDataRecord) node.mEntry);
					break;

				case kState_DeletingEvent:
					DeleteEntry((JournalDataRecord) node.mEntry);
					break;

				case kState_SyncAll:
					SyncNow();
					break;

				case kState_AddingFriend:
					AddFriend((String) node.mEntry);
					break;
			}
		}
	}

	//*	--------------------	ParseItemList

	private final Vector
	ParseItemList(Hashtable table)
	{
	Vector	items = new Vector();
	int		total, count;

		total = StringUtils.toInt((String) table.get("sync_total"));
		count = StringUtils.toInt((String) table.get("sync_count"));

		for (int i = 1; i <= count; i++)
		{
		String		item;
		String		time;
		String		action;
		int			itemid;
		SyncNode	node;
		boolean		create = false;

			item = (String) table.get("sync_" + i + "_item");

			//*	Not there?  Fuck, just go around it.  Hmmm....
			if (StringUtils.isEmpty(item))
				continue;

			//*	Not a journal entry?  Skip it.
			if ('L' != item.charAt(0))
				continue;

			//*	item is in the form L-number
			itemid = StringUtils.toInt(item.substring(2));
			time = (String) table.get("sync_" + i + "_time");
			action = (String) table.get("sync_" + i + "_action");

			node = new SyncNode();
			node.mItemID = itemid;
			node.mDateString = time;
			node.mDate = ParseDateString(time);
			node.mAction = action;

			items.addElement(node);
		}

		return items;
	}

	//*	--------------------	PruneSyncItemList

	private final void
	PruneSyncItemList(Vector items)
	{
	int		index = items.size();

		while (--index > -1)
		{
		SyncNode	n = (SyncNode) items.elementAt(index);

			//*	If the last action was 'delete' then kill our
			//*	local copy if there is one.
			if (n.mAction.equals("del"))
			{
				NetDelete(n.mItemID);
				items.removeElementAt(index);
				continue;
			}

			//*	If the last action was 'create' and we already
			//*	have a copy then we're done.
			if (n.mAction.equals("create"))
			{
				if (null != mBrowseWindow.GetItemByItemID(n.mItemID))
					items.removeElementAt(index);

				continue;
			}

			//*	If the last action was 'update' and our local copy is more
			//*	recent then we're finished.
			//*	<WIP>	Need to upload our newer version to the server.
			if (n.mAction.equals("update"))
			{
			JournalDataRecord	e = mBrowseWindow.GetItemByItemID(n.mItemID);

				if (e != null)
				{
					if (e.Date() >= n.mDate)
						items.removeElementAt(index);
					else
						e.SetDate(n.mDate);
				}

				continue;
			}

			//*	If we got here then the last action on the entry is something
			//*	that we don't know about.  For now we'll just ignore that
			//*	entry completely.
			items.removeElementAt(index);
		}
	}

	//*	--------------------	ParseDateString

	private final int
	ParseDateString(String inDate)
	{
	Calendar	d = Calendar.getInstance();

		//*	inDate is in the form yyyy-mm-dd hh:mm:ss
		//		0123456789012345678
		d.set(Calendar.YEAR, StringUtils.toInt(inDate, 0));
		d.set(Calendar.MONTH, StringUtils.toInt(inDate, 5) - 1);	//*	LJ months are 1-based, Java 0-based
		d.set(Calendar.DATE, StringUtils.toInt(inDate, 8));

		d.set(Calendar.HOUR, StringUtils.toInt(inDate, 11));
		d.set(Calendar.MINUTE, StringUtils.toInt(inDate, 14));
		d.set(Calendar.SECOND, 0);		//*	LJ dates only have minute resolution

		return (int) (d.getTimeInMillis() / 1000);
	}

 	//*	--------------------	WarnDeleteEntry

	private final void
	WarnDeleteEntry(JournalDataRecord inEntry)
	{
		AlertWindow	alert;

		alert = getAlert(kWarnDeleteMessageAlert, this);

		alert.setEventForControlWithID(kID_DeleteMessageButton,
				new Event(CMD_DELETE_ENTRY, 0, 1, inEntry));

		alert.show();
	}

	//*	--------------------------------	Class Variables

	private static LiveJournal	sMe;

	//*	--------------------------------	Instance Variables

	private BrowseWindow		mBrowseWindow;
	private LoginWindow			mLoginWindow;
	private FriendsWindow		mFriendsWindow;

	private String[]			mJournals;

	private int					mState;
	private String				mLastSyncDate;
	private int					mNumSyncItems;
	private int					mLastSyncItemID;

	private Vector				mNetworkQueue;
	private Vector				mSyncItems;

	private String				mFullName;
	private String				mUserName;
	private String				mPassword;

	private Vector				mFriends;
	private String				mLastFriendDelete;

	private JournalDataRecord	mActiveEntry;
	private JournalComparator	mComparator;

	//*	--------------------------------	Constants

	private static final int	kState_LoggedOut		=	0;
	private static final int	kState_LoggingIn		=	1;
	private static final int	kState_LoggedIn			=	2;
	private static final int	kState_PostingEvent		=	3;
	private static final int	kState_EditingEvent		=	4;
	private static final int	kState_DeletingEvent	=	5;
	private static final int	kState_SyncAll			=	6;
	private static final int	kState_SyncDelta		=	7;
	private static final int	kState_SyncingItem		=	8;
	private static final int	kState_SyncingDeltas	=	9;
	private static final int	kState_GettingFriends	=	10;
	private static final int	kState_AddingFriend		=	11;
	private static final int	kState_DeletingFriend	=	13;
}

class QueueNode
{
	Object	mEntry;
	int		mAction;
}

class SyncNode
{
	int		mItemID;
	int		mDate;
	String	mDateString;
	String	mAction;
}

class JournalComparator implements Comparator
{
	//*	--------------------	Compare

	public int
	compare(Object inReferenceObject, Object inObject2)
	{
	JournalDataRecord	reference, other;
	int					value1 = 0;
	int					value2 = 0;

		reference = (JournalDataRecord) inReferenceObject;
		other = (JournalDataRecord) inObject2;

		value1 = reference.Date();
		value2 = other.Date();

		//*	The logic is backward from what you would expect.  Since these are dates
		//*	the largest number is the newest and I want it at the top of the list.
		if (value1 > value2)
			return -1;
		else if (value1 < value2)
			return 1;

		return 0;
	}
}
