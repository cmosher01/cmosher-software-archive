// -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*-

package com.danger.terminal;

import java.util.Vector;

import danger.app.SettingsDB;
import danger.app.SettingsDBException;
import danger.util.StdActiveObject;


class HostInfo
	extends StdActiveObject
	implements Comparable
{
	HostInfo (SessionManager manager)
	{
		init(manager);
	}
	
	HostInfo (SessionManager manager, String hostname, String username, int port)
	{
		init(manager);

		/* override the defaults */
		mHostname = hostname;
		mUsername = username;
		mPort = port;
	}
	
	HostInfo (SessionManager manager, int index) throws SettingsDBException
	{
		init(manager);
		
		/* try to load our info from the settings - this should replace all defaults*/
		mSettingsIndex = index;
		load();
	}
	
	private void
	init (SessionManager manager)
	{
		mSessionManager = manager;
		mPrefs = manager.getSettingsDB();

		/* some basic stuff */
		mDirty = false;
		mInSettings = false;
        mSessionNameIsDefault = true;
		mProtocol = TerminalWindow.PROT_TELNET;
		mPort = -1;
		mAutoWrap = true;
		mScrollback = 100;
		mBackspaceSendsDel = true;
		mCodec = "ISO-8859-1";

		mConsoles = new Vector();
		mStatus = STATUS_CLOSED;
		
		/* pull other defaults from the app preferences */
		TerminalPrefs prefs = mSessionManager.getTerminalPrefs();
		mFont = prefs.getDefaultFont();
		mUsername = null;
		
		mSessionNameBuffer = null;
	}

	// superclass's javadoc suffices
	public int compareTo(Object other)
	{
		int isEqual;
		
		isEqual = mHostname.compareTo( ((HostInfo) other).mHostname);
		if ( ( isEqual == 0 ) &&  ( mUsername != null ) )
		{
			isEqual = mUsername.compareTo( ((HostInfo) other).mUsername);
		}
		
		if ( isEqual == 0 )
		{
			isEqual = mProtocol == ((HostInfo) other).mProtocol ? 0 : 1;
		}
		
		if ( isEqual == 0 )
		{
			isEqual = mPort == ((HostInfo) other).mPort ? 0 : 1;
		}
		
		return isEqual;
	}

	public void setStatus(int status)
	{
		if ((status < STATUS_CLOSED) || (status > STATUS_OPEN)) {
			throw new IllegalArgumentException("status == " + status);
		}

		mStatus = status;
		update();
	}

	public int getStatus()
	{
		return mStatus;
	}
	
	protected String
	getStringValue (String key, String defaultValue)
		throws SettingsDBException
	{
		if (!mInSettings) {
			return defaultValue;
		}

		String ret = mPrefs.getStringValue(key + "/" + mSettingsIndex);
		if (ret == null) {
			return defaultValue;
		} else {
			return ret;
		}
	}
	
	protected void
	setStringValue (String key, String value)
	{
		if (!mInSettings) {
			return;
		}
			
		mPrefs.setStringValue(key + "/" + mSettingsIndex, value);
	}
	
	int
	getIntValue (String key)
		throws SettingsDBException
	{
		if (!mInSettings) {
			return -1;
		}
		
		return mPrefs.getIntValue(key + "/" + mSettingsIndex);
	}
	
	void
	setIntValue (String key, int value)
	{
		if (!mInSettings) {
			return;
		}
			
		mPrefs.setIntValue(key + "/" + mSettingsIndex, value);
	}
	
	protected boolean
	getBooleanValue (String key, boolean def)
	{
		int value;
		
		if (!mInSettings) {
			return def;
		}

		try {
			value = mPrefs.getIntValue(key + "/" + mSettingsIndex);
			return ( ( value == 0 ) ? false : true );
		} catch (SettingsDBException x) {
			return def;
		}
	}
	
	protected void
	setBooleanValue (String key, boolean value)
	{
		if (!mInSettings) {
			return;
		}

		mPrefs.setIntValue(key + "/" + mSettingsIndex, ( value ? 1 : 0 ) );
	}
	
	public void
	setHostname (String hostname)
	{
		if (hostname != mHostname) {
			mDirty = true;
			mHostname = hostname;
			mSessionNameBuffer = null;
			mSessionManager.sessionChanged(this);
		}
	}
	
	public String
	getHostname ()
	{
		return mHostname;
	}
	
	public String
	getFullHostname ()
	{
		if (mPort == TerminalWindow.DEFAULT_PORT[mProtocol]) {
			return mHostname;
		} else {
			if (mPort < 0) {
				return mHostname + ":local";
			} else {
				return mHostname + ":" + mPort;
			}
		}
	}

    public void
	setSessionName (String name)
    {
        if (name != mSessionName) {
            mDirty = true;
            mSessionName = name;

            /* remember whether we need to keep the session name in sync... */
            mSessionNameIsDefault = mSessionName.equals(getDefaultSessionName());

            mSessionManager.sessionChanged(this);
        }
    }

	public String
	getSessionName()
	{
        /* if our session name is default and it's changed, then get the new one */
        if (mSessionNameBuffer == null && mSessionNameIsDefault) {
            setSessionName(getDefaultSessionName());
        }
        
		return mSessionName;
	}
	
	public void
	setUsername (String username)
	{
		if (username != mUsername) {
			mDirty = true;
			mUsername = username;
			mSessionNameBuffer = null;
			mSessionManager.sessionChanged(this);
		}

		/* update the default login as well */
		/* BRAIN DAMAGE: I'm not sure this is the best place, but it is localized */
		mSessionManager.getTerminalPrefs().setDefaultUsername(username);
	}
	
	public String
	getUsername ()
	{
		return mUsername;
	}

	/* you cannot change the protocol field of an existing session, because
	 * this may be an object descended from HostInfo, based on which
	 * protocol we're using.
	 */
	public void
	setProtocol (int protocol)
	{
		mProtocol = protocol;
	}

	public final int
	getProtocol()
	{	
		return mProtocol;
	}
	
	public String
	getProtocolName()
	{
		switch (mProtocol) {
		case TerminalWindow.PROT_TELNET:
			return Terminal.instance().getString(TerminalResources.PROTOCOL_TELNET);
		case TerminalWindow.PROT_RAW:
		default:
			return Terminal.instance().getString(TerminalResources.PROTOCOL_RAW);
		}
	}
	
	public final void
	setPort (int port)
	{
		if (port != mPort) {
			mDirty = true;
			mPort = port;
			mSessionNameBuffer = null;
			mSessionManager.sessionChanged(this);
		}
	}
		
	public final int
	getPort()
	{	
		return mPort;
	}

	public void
	setFont (String font)
	{
		if (font != mFont) {
			mDirty = true;
			mFont = font;
			mSessionManager.sessionChanged(this);
		}

		/* update the default font as well */
		/* BRAIN DAMAGE: I'm not sure this is the best place, but it is localized */
		mSessionManager.getTerminalPrefs().setDefaultFont(font);
	}

	public final boolean
	getAutoWrap ()
	{
		return mAutoWrap;
	}

	public final void
	setAutoWrap (boolean wrap)
	{
		if (wrap != mAutoWrap) {
			mDirty = true;
			mAutoWrap = wrap;
			mSessionManager.sessionChanged(this);
		}
	}

	public final int
	getEmulator ()
	{
		return mEmulator;
	}

	public final void
	setEmulator (int emulator)
	{
		mEmulator = emulator;
	}

	public final int
	getScrollback ()
	{
		return mScrollback;
	}

	public final void
	setScrollback (int scrollback)
	{
		mScrollback = scrollback;
	}

	public final boolean
	getBackspaceSendsDel ()
	{
		return mBackspaceSendsDel;
	}

	public final void
	setBackspaceSendsDel (boolean bsd)
	{
		mBackspaceSendsDel = bsd;
	}

	public final String
	getCodec ()
	{
		return mCodec;
	}

	public final void
	setCodec (String codec)
	{
		mCodec = codec;
	}

	public int
	getSettingsIndex ()
	{
		return mSettingsIndex;
	}
	
	public void
	setSettingsIndex (int index)
	{
		if (!mInSettings || (index != mSettingsIndex)) {
			mSettingsIndex = index;
			mInSettings = true;
		}
	}
	
	public String
	getFont ()
	{
		return mFont;
	}

    public String
	getDefaultSessionName ()
    {
        if ( mSessionNameBuffer == null ) {
            int len;
            boolean haveUsername;

            haveUsername = ( mUsername != null && mUsername.length() > 0 );

            len = mHostname.length();

            if ( haveUsername ) {
                len += mUsername.length();
            }

            /* add room for port - max ":65536" */
            mSessionNameBuffer = new StringBuffer(len + 6);

            if ( haveUsername ) {
                mSessionNameBuffer.append(mUsername);
                mSessionNameBuffer.append("@");
            }

            mSessionNameBuffer.append(mHostname);

            if (getPort() != TerminalWindow.DEFAULT_PORT[mProtocol]) {
                mSessionNameBuffer.append(":");
                mSessionNameBuffer.append(getPort());
            }
        }

        return mSessionNameBuffer.toString();
    }

    public boolean getSessionNameIsDefault()
    {
        return mSessionNameIsDefault;
    }

	public Vector getConsoles()
	{
		return mConsoles;
	}

    public void consoleChanged ( VirtualConsole console )
    {
        /* BRAIN DAMAGE - huge hack */
        mSessionManager.consoleChanged(console);
    }

    public void addConsole ( VirtualConsole console )
	{
		synchronized (mConsoles)
		{
            if ( !mConsoles.contains(console) )
            {
                mConsoles.addElement( console );
            }
		}
        
        mSessionManager.consoleAdded(console);
	}
	
	public void removeConsole ( VirtualConsole console )
	{
        synchronized (mConsoles)
		{
			mConsoles.removeElement( console );
		}

        mSessionManager.consoleRemoved(console);
	}
	
	public void
	save()
	{
		if (!mDirty) {
			return;
		}

		/* if we don't have a settings index, get one */
		if (!mInSettings) {
			mSettingsIndex = mSessionManager.addSession(this);
			mInSettings = true;
		}

		setStringValue(HOSTNAME_KEY, mHostname);

		if (mUsername != null) {
			setStringValue(USERNAME_KEY, mUsername);
		}

        setStringValue(SESSIONNAME_KEY, mSessionName);
		setStringValue(FONT_KEY, mFont);
		setIntValue(PORT_KEY, mPort);
		setIntValue(PROTOCOL_KEY, mProtocol);
		setBooleanValue(AUTO_WRAP_KEY, mAutoWrap);
		setIntValue(EMULATOR_KEY, mEmulator);
		setIntValue(SCROLLBACK_KEY, mScrollback);
		setBooleanValue(BACKSPACE_KEY, mBackspaceSendsDel);
		setStringValue(CODEC_KEY, mCodec);
        
		mDirty = false;
	}
	
	/* package */ void
	load ()
		throws SettingsDBException
	{
		if (!mInSettings) {
			System.out.println("terminal.HostInfo: Attempt to load unknown host from settings");
			throw new SettingsDBException("Not in settings");
		}

		mHostname = getStringValue(HOSTNAME_KEY, "");
		mUsername = getStringValue(USERNAME_KEY, "");

		mFont = getStringValue(FONT_KEY, Terminal.instance().getString(TerminalResources.DEFAULT_FONT));
		mPort = getIntValue(PORT_KEY);
		mProtocol = getIntValue(PROTOCOL_KEY);
		mAutoWrap = getBooleanValue(AUTO_WRAP_KEY, true);
        mSessionName = getStringValue(SESSIONNAME_KEY, null);
        if (mSessionName == null) {
            mSessionName = getDefaultSessionName();
            setStringValue(SESSIONNAME_KEY, mSessionName);
        }

		try {
			mEmulator = getIntValue(EMULATOR_KEY);
		} catch (SettingsDBException x) {
			mEmulator = VirtualConsole.EMULATE_VT100;
		}

		try {
			mScrollback = getIntValue(SCROLLBACK_KEY);
		} catch (SettingsDBException x) {
			mScrollback = 100;
		}

		mBackspaceSendsDel = getBooleanValue(BACKSPACE_KEY, true);
		mCodec = getStringValue(CODEC_KEY, "ISO-8859-1");

        /* remember whether we need to keep the session name in sync... */
        mSessionNameIsDefault = mSessionName.equals(getDefaultSessionName());
    }


	protected String			mSessionName;
	protected String			mHostname;
	protected String			mUsername;
	protected String			mFont;
	protected int				mProtocol;
	protected int				mPort;
	protected boolean			mAutoWrap;
	protected int				mEmulator;
	protected int				mScrollback;
	protected boolean			mBackspaceSendsDel;
	protected String			mCodec;

	protected SettingsDB		mPrefs;
	protected SessionManager	mSessionManager;
	protected int				mSettingsIndex;
	protected boolean			mDirty;
	protected boolean			mInSettings;
    protected boolean			mSessionNameIsDefault;
    
	protected StringBuffer		mSessionNameBuffer;
	
	private int					mStatus;
	private Vector				mConsoles;

    static final String			SESSIONNAME_KEY		= "name";
    static final String			HOSTNAME_KEY		= "hostname";
	static final String			USERNAME_KEY		= "username";
	static final String			PORT_KEY			= "port";
	static final String			PROTOCOL_KEY		= "protocol";
	static final String			FONT_KEY			= "font";
	static final String			AUTO_WRAP_KEY		= "auto-wrap";
	static final String			EMULATOR_KEY		= "emulator";
	static final String			SCROLLBACK_KEY		= "scrollback";
	static final String			BACKSPACE_KEY		= "backspace";
	static final String			CODEC_KEY			= "codec";

	static public final int		STATUS_CLOSED		= 0;
	static public final int		STATUS_CONNECTING	= 1;
	static public final int		STATUS_OPEN			= 2;
}
