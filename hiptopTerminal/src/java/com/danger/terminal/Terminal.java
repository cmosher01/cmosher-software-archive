/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import danger.app.AppResources;
import danger.app.Application;
import danger.app.Event;
import danger.app.SettingsDB;
import danger.app.SocketRegistrar;
import danger.net.IPCServerSocket;


public class Terminal
	extends Application implements TerminalResources, TerminalEvents, AppResources
{
	IPCServerSocket sock;
	
	public Terminal() 
	{
		sMe = this;
		mPrefs = new SettingsDB(PREFS_DBNAME, true);

		mSessionManager = new SessionManager(mPrefs);
        mSessionManager.registerListener(this);

		mTerminalWindow = new TerminalWindow(mSessionManager);
		mListWindow = (SessionListWindow)getResources().getScreen(ID_SESSION_LIST_SCREEN);
		mListWindow.Init(mSessionManager, mTerminalWindow);
		mConsoleIsActive = false;
		
		/* load any saved sessions */
		mSessionManager.loadSessions();

		System.err.println("------------------------------starting theshell-------------");
		sock = SocketRegistrar.makeServerSocket("theshell");
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					while (true)
					{
						new ClientConnection(sock.accept());
					}
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
		th.start();
	}

	public void resume()
	{
		if (mSettingsScreen != null) {
			mSettingsScreen.show();
		} else if (mConsoleIsActive) {
			mTerminalWindow.show();
		} else {
			mListWindow.show();
		}
	}

	public static SettingsDB
	getSettingsDB ()
	{
		return mPrefs;
	}
	
	public static Terminal instance()
	{
		return sMe;
	}

	public SessionManager getSessionManager()
	{
		return mSessionManager;
	}

	public void
	returnToSessionList ()
	{
		mConsoleIsActive = false;
		mListWindow.show();
	}

	public void
	makeConsoleActive ()
	{
		mConsoleIsActive = true;
	}

	public void
	openSettings (HostInfo info)
	{
		mSettingsScreen = (SettingsScreen) getResources().getScreen(ID_SETTINGS_SCREEN, this);
		mSettingsScreen.fillFrom(info);
		mSettingsScreen.show();
	}

	public boolean
	receiveEvent (Event e)
	{
		HostInfo info;
		boolean handled = false;
		
		switch (e.type) {
		case TerminalEvents.kOpenHost:
			info = (HostInfo) e.argument;
			System.out.println ( "Terminal: TerminalEvents.kOpenSession" );
			mSessionManager.updateMostRecentSession(info);
			mTerminalWindow.openSession(info);
			mTerminalWindow.show();
			mConsoleIsActive = true;
			handled = true;
			break;
				
		case Event.EVENT_DATASTORE_RESTORED:
			return mSessionManager.receiveEvent(e);

		case EVENT_SETTINGS_CANCEL:
			mSettingsScreen.hide();
			mSettingsScreen = null;
			break;

		case EVENT_SETTINGS_DONE:
			mSettingsScreen.save();
			mSettingsScreen.hide();
			mSettingsScreen = null;
			break;
		}

		if (!handled) {
			handled = super.receiveEvent(e);
		}

		return handled;
	}

	public TerminalWindow getTerminalWindow()
	{
		return mTerminalWindow;
	}


	private SessionManager		mSessionManager;
 	private SessionListWindow	mListWindow;
 	private TerminalWindow		mTerminalWindow;
	private static SettingsDB	mPrefs;
	private boolean				mConsoleIsActive;
	private SettingsScreen		mSettingsScreen;

	private static Terminal		sMe;
	private final static String	PREFS_DBNAME	= "pref";


	// useful tool for debugging
	protected static final String HEX = "0123456789ABCDEF";

	protected static String
	generateHexLine (byte[] data, int off, int len)
	{
		if (len > 16) {
			len = 16;
		}

		StringBuffer outL = new StringBuffer();
		StringBuffer outR = new StringBuffer();
		for (int i = 0; i < 16; i++) {
			if (i < len) {
				int b = (int)data[off+i] & 0xff;
				outL.append(HEX.charAt((b >> 4) & 0x0f));
				outL.append(HEX.charAt(b & 0x0f));
				outL.append(' ');
				if ((b >= 0x20) && (b <= 0x7e)) {
					outR.append((char)b);
				} else {
					outR.append('.');
				}
			} else {
				outL.append("   ");
				outR.append(' ');
			}
		}

		return outL.toString() + "  " + outR.toString();
	}

	public static String[]
	generateHex (byte[] data, int off, int len)
	{
		int nLines = (len + 15) / 16;
		String[] out = new String[nLines];

		for (int i = 0; i < nLines; i++) {
			out[i] = generateHexLine(data, off, len);
			off += 16;
		}
		return out;
	}

	public static void
	dumpHex (byte[] data, int off, int len)
	{
		String[] line = generateHex(data, off, len);
		for (int i = 0; i < line.length; i++) {
			System.err.println(line[i]);
		}
	}

    public void launch()
    {
        super.launch();
    }
}
