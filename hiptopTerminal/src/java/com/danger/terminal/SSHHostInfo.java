// -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*-

package com.danger.terminal;

import danger.app.SettingsDB;
import danger.app.SettingsDBException;


class SSHHostInfo
	extends HostInfo
{
	SSHHostInfo(SessionManager manager)
	{
		super (manager);
		
		init();
	}
	
	SSHHostInfo (SessionManager manager, String hostname, String username, int protocol, int port)
	{
		super (manager, hostname, username, port);
		
		init();
	}
	
	SSHHostInfo (SessionManager manager, int index) throws SettingsDBException
	{
		super (manager, index);
		
		init();
	}
	
	private void
	init()
	{
		mProtocol = TerminalWindow.PROT_SSH2;
		mPort = TerminalWindow.DEFAULT_PORT[mProtocol];
	}
	
	public byte[] getRSAKey()
	{
		/* we try again as another session may have saved it */
		if (mRSAKey == null) {
			try {
				mRSAKey = mPrefs.getBytes(RSA_KEY + "/" + mHostname);
			} catch(SettingsDBException e) { }
		}
		
		return mRSAKey;
	}
	
	public void setRSAKey (byte[] key)
	{
		mRSAKey = key;
		if (key == null) {
			mPrefs.remove(RSA_KEY + "/" + mHostname);
		} else {
			mPrefs.setBytes(RSA_KEY + "/" + mHostname, key);
		}
	}
	
	public byte[] getDSSKey()
	{
		/* we try again as another session may have saved it */
		if (mDSSKey == null) {
			try {
				mDSSKey = mPrefs.getBytes(DSS_KEY + "/" + mHostname);
			} catch(SettingsDBException e) { }
		}
		
		return mDSSKey;
	}
	
	public void setDSSKey (byte[] key)
	{
		mDSSKey = key;
		if (key == null) {
			mPrefs.remove(DSS_KEY + "/" + mHostname);
		} else {
			mPrefs.setBytes(DSS_KEY + "/" + mHostname, key);
		}
	}

	public String getProtocolName()
	{
		return Terminal.instance().getString(TerminalResources.PROTOCOL_SSH2);
	}

	public final boolean
	getRememberPassword ()
	{
		return mRememberPassword;
	}

	public final void
	setRememberPassword (boolean remember)
	{
		if (remember != mRememberPassword) {
			mDirty = true;
			mRememberPassword = remember;
		}
	}

	public final boolean
	getPreferDSS ()
	{
		return mPreferDSS;
	}

	public void
	setPreferDSS (boolean preferDSS)
	{
		if (preferDSS != mPreferDSS) {
			mDirty = true;
			mPreferDSS = preferDSS;
		}
	}

	public final int
	getWindowSize ()
	{
		return mWindowSize;
	}

	public void
	setWindowSize (int size)
	{
		if (size != mWindowSize) {
			mDirty = true;
			mWindowSize = size;
		}
	}

	public final String
	getSavedPassword ()
	{
		if (mRememberPassword) {
			return mSavedPassword;
		} else {
			return null;
		}
	}

	public void
	setSavedPassword (String password)
	{
		if (password != mSavedPassword) {
			mDirty = true;
			mSavedPassword = password;
		}
	}


	public void
	save ()
	{
		if (!mDirty) {
			return;
		}
		
		super.save();
		
		setBooleanValue(REMEMBER_PASSWORD, mRememberPassword);
		setBooleanValue(PREFER_DSS, mPreferDSS);
		setIntValue(WINDOW_SIZE, mWindowSize);

		if ((mSavedPassword == null) || !mRememberPassword) {
			mPrivateDB.remove(PASSWORD_KEY + "/" + mSettingsIndex);
		} else {
			mPrivateDB.setStringValue(PASSWORD_KEY + "/" + mSettingsIndex, mSavedPassword);
		}
	}
	
	void
	load ()
		throws SettingsDBException
	{
		super.load();
		
		mRememberPassword = getBooleanValue(REMEMBER_PASSWORD, false);
		mPreferDSS = getBooleanValue(PREFER_DSS, false);
		mWindowSize = getIntValue(WINDOW_SIZE);
		
		/* not having keys is a normal thing and so we catch the exception here */
		try {
			mRSAKey = mPrefs.getBytes(RSA_KEY + "/" + mHostname);
		} catch (SettingsDBException e) {
			mRSAKey = null;
		}

		try {
			mDSSKey = mPrefs.getBytes(DSS_KEY + "/" + mHostname);
		} catch (SettingsDBException e) {
			mDSSKey = null;
		}

		mSavedPassword = mPrivateDB.getStringValue(PASSWORD_KEY + "/" + mSettingsIndex);
	}


	// store passwords in a private, non-synced db so we don't send them to the service
	private static SettingsDB mPrivateDB = new SettingsDB("private", false);

	protected boolean		mRememberPassword					= DEFAULT_REMEMBER_PASSWORD;
	protected boolean		mPreferDSS							= DEFAULT_PREFER_DSS;
	protected int			mWindowSize							= DEFAULT_WINDOW_SIZE;
	protected String		mSavedPassword						= null;
	protected byte[]		mRSAKey								= null;
	protected byte[]		mDSSKey								= null;

	private static final String		REMEMBER_PASSWORD			= "remember-password";
	private static final boolean	DEFAULT_REMEMBER_PASSWORD	= false;
	private static final String		PREFER_DSS					= "prefer-dss";
	private static final boolean	DEFAULT_PREFER_DSS			= false;
	private static final String		WINDOW_SIZE					= "window-size";
	private static final int		DEFAULT_WINDOW_SIZE			= 8192;
	private static final String		PASSWORD_KEY				= "ssh-password";
	private static final String		RSA_KEY						= "rsa";
	private static final String		DSS_KEY						= "dss";
}
