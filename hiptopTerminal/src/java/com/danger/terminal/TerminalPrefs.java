// -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*-

package com.danger.terminal;

import danger.app.SettingsDB;

public class TerminalPrefs
{
	public TerminalPrefs (SessionManager manager)
	{
		mSessionManager = manager;
		mPrefs = manager.getSettingsDB();
		
		/* default to these until we get real data */
		mDefaultUsername = null;
		mDefaultFont = kDefaultFont;
	}
	
	public void loadSettings()
	{
		mDefaultUsername = mPrefs.getStringValue(USERNAME_KEY);		
		mDefaultFont = mPrefs.getStringValue(FONT_KEY);
		if ( mDefaultFont == null )
		{
			setDefaultFont(kDefaultFont);
		}
	}
	
	public String getDefaultUsername()
	{
		return mDefaultUsername;
	}
	
	public void setDefaultUsername(String username)
	{
		if (mDefaultUsername != username)
		{
			mDefaultUsername = username;
			mPrefs.setStringValue(USERNAME_KEY, username);
		}
	}
	
	public String getDefaultFont()
	{
		return mDefaultFont;
	}
	
	public void setDefaultFont(String font)
	{
		if (mDefaultFont != font)
		{
			mDefaultFont = font;
			mPrefs.setStringValue(FONT_KEY, font);
			
		}
	}
	
	protected SettingsDB		mPrefs;
	protected SessionManager	mSessionManager;
	protected String			mDefaultUsername;
	protected String			mDefaultFont;
	
	/* settings keys */
	static final String			USERNAME_KEY		= "username";
	static final String			FONT_KEY			= "font";

	static final String			kDefaultFont		= "Fixed5x7";
}
