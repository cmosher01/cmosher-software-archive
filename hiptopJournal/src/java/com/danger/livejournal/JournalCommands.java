// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.livejournal;

public interface JournalCommands
{
	int	kCmd_NewEntry			=	1;
	int	kCmd_PostEntry			=	2;
	int	kCmd_SaveAsDraft		=	3;
	int	kCmd_Cancel				=	4;
	int	kCmd_Login				=	5;
	int	kCmd_Logout				=	6;
	int	CMD_DELETE_ENTRY		=	7;
	int	kCmd_ReopenEntry		=	8;
	int	kCmd_SyncNow			=	9;
	int	kCmd_GetFriends			=	10;
	int	kCmd_CancelLogin		=	11;
	int	kCmd_ShowFriends		=	12;
	int	kCmd_PollHelper			=	13;
	int	CMD_DONE				=	14;
	int	kCmd_AddFriend			=	15;
	int	kCmd_DeleteFriend		=	16;
	int	kCmd_ViewFriendEntries	=	17;
	int	kCmd_ViewFriendsEntries	=	18;
	int	kCmd_ViewFriendCalendar	=	19;
	int	kCmd_ViewFriendInfo		=	20;
	int kCmd_Formatify			=	21;
}
