// -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*-

package com.danger.terminal;


import java.util.Vector;

import danger.app.Event;
import danger.app.EventType;
import danger.app.Listener;
import danger.app.SettingsDB;
import danger.app.SettingsDBException;
import danger.util.VectorUtils;


class SessionManager extends Listener implements TerminalResources
{
	public SessionManager ( SettingsDB prefs )
	{
		mPrefs = prefs;
		mPrefs.setAutoSyncNotifyee(this);

		mTerminalPrefs = new TerminalPrefs(this);
		
		mListeners = new Vector(2);
		mPrefsLockCount = 0;
		
		mHosts = new HostInfo[MAX_NUM_HOSTS];
		mHostIndex = new int[MAX_NUM_HOSTS];
		
		for (int i=0; i < MAX_NUM_HOSTS; ++i)
		{
			mHostIndex[ i ] = -1;
		}

        mConsoleList = new Vector();
        
		mNumHosts = 0;
		mIndexDirty = false;
		mHostsDirty = false;
	}

	public void registerListener(Listener target)
	{
		synchronized (mListeners)
		{
			mListeners.addElement(target);
		}
	}

	public void unregisterListener(Listener target)
	{
		synchronized (mListeners)
		{
			VectorUtils.eqRemoveElement(mListeners, target);
		}
	}

    private void dispatchEvent(Event event)
	{
		synchronized (mListeners)
		{
			int count, max = mListeners.size();

			for (count = 0; count < max; count++) 
			{
				Listener theListener = (Listener)mListeners.elementAt(count);

				Event e = new Event(event);
				e.setListener(theListener);
				e.sendToTarget();
			}
		}
	}
	
	private void dispatchMessage ( Object object, int message )
	{
		Event e = new Event( null, EventType.EVENT_MESSAGE, message, 0, object );
		dispatchEvent ( e );
	}
	
	public boolean receiveEvent (Event e)
	{
		boolean handled = false;
		
		if ( e.type == Event.EVENT_DATASTORE_RESTORED ) {
			System.out.println ( "SessionManager: EVENT_DATASTORE_RESTORED" );
			loadSessions();
			handled = true;
		} else if ( e.type == EventType.EVENT_MESSAGE ) {
			if ( e.what == SESSION_NOTIFIER ) {
				mHostsDirty = false;
				saveSessionPrefs();
				
				/* tell anyone that cares */
				dispatchMessage ( e.argument, e.data );
			}
		}
		
		return handled;
	}
	
	public void loadSessions()
	{
		int offset;
		
		// make sure we're on v3, or else erase everything
		int version = 0;
		try	{
			version = mPrefs.getIntValue("prefs-version");
		} catch (SettingsDBException x) { }

		if (version < 2) {
			if (mPrefs.getNumEntries() > 0) {
				mPrefs.clearAll();
			}

			mPrefs.setIntValue("prefs-version", 2);
		}

		/* load the app defaults */
		mTerminalPrefs.loadSettings();
		holdPrefsUpdate();
		mPrefs.dump();
		
		/*
		 * how many sessions do we already have - if some, then we may need to delete old ones
		 * coming in from restored prefs...
		 */
		offset = mNumHosts;
		int historyIndex = offset;
		
		/* if we're synching, then we'll need to update the index */
		mIndexDirty = (offset != 0);
			
		for(int i = 0; i < (MAX_NUM_HOSTS - offset); ++i)
		{
			try
			{
				int hostIndex;
				int actualIndex;
				int protocol;
				
				hostIndex = mPrefs.getIntValue("hostIndex" + i);
				if (hostIndex < 0)
				{
					continue;
				}
								
				if ( historyIndex < MAX_NUM_HOSTS )
				{
					protocol = mPrefs.getIntValue(HostInfo.PROTOCOL_KEY + "/" + hostIndex);
					
					/* load the host based on their saved index */
					HostInfo session = createSession(protocol);
					if (session == null) {
						// skip
						continue;
					}
					session.setSettingsIndex(hostIndex);
					session.load();
					
					/* if we're synching, then we re-index */
					if ( offset != 0 )
					{
						hostIndex = historyIndex;
						session.setSettingsIndex(hostIndex);
					}
					
					/* and save it */
					mHosts[ hostIndex ] = session;
					mHostIndex[ historyIndex ] = hostIndex;
					++mNumHosts;
					++historyIndex;
				}
				else
				{
					/* older than our cutoff, we lose it */
					System.out.println ( "session index " + hostIndex + " older than cutoff offset "
						+ offset );
				}
				
			}
			catch (SettingsDBException e)
			{
				/* this one goes away... */
			}
		}
		
		/* be sure to erase any slots at the end of the index */
		for( int i = historyIndex; i < MAX_NUM_HOSTS; ++i)
		{
			mHostIndex[ i ] = -1;
		}
		
		releasePrefsUpdate();
		
		/* tell anyone that cares */
		sessionChanged(null, SESSIONS_CHANGED);
	}

	public void holdPrefsUpdate()
	{
		mPrefsLockCount++;
	}
	
	public void releasePrefsUpdate()
	{
		if ( mPrefsLockCount > 0 )
		{
			if ( --mPrefsLockCount == 0 )
			{
				saveSessionPrefs();
			}
		}
	}
	
	public void saveSessionPrefs()
	{
		/* unless we're locked, run through our session list and save anyone that's dirty */
		if ( mPrefsLockCount > 0 )
		{
			return;
		}
		
		/* write out index if dirty */
		if (mIndexDirty)
		{

			for (int i = 0; i < MAX_NUM_HOSTS; ++i)
			{
				mPrefs.setIntValue("hostIndex" + i, mHostIndex[ i ] );
			}
			mIndexDirty = false;
		}
		
		/* write the sessions */
		for (int i = 0; i < mNumHosts; ++i)
		{
			HostInfo session = (HostInfo) mHosts[ i ];
			if (session != null)
			{
				session.save();
			}
		}
    }
	
	public int getNumHosts()
	{
		return mNumHosts;
	}
	
	public HostInfo getSession(int index)
	{        
		if (index >= mNumHosts)
		{
			return null;
		}

        int count = 0;
        
        for (int i = 0; i < MAX_NUM_HOSTS; ++i)
        {
            if (mHosts[ i ] != null)
            {
                if (count == index)
                {
                    return mHosts[ i ];
                }

                ++count;
            }
        }
		
		return null;
	}
	
	public void updateMostRecentSession(HostInfo info)
	{
		udpateMostRecentIndex(info.getSettingsIndex());
	}
	
	public void udpateMostRecentIndex(int index)
	{
		boolean found = false;
		mIndexDirty = true;

        /* if we're already at top, then quit quickly */
        if (mHostIndex[ 0 ] == index) {
        	return;
        }
        
		/* slide everyone down in the history, or not if empty */
		if (mNumHosts > 1) {
			/* search from the end to find where we were */
			for (int i = MAX_NUM_HOSTS - 1; i >= 0; --i) {
				if (mHostIndex[ i ] == index) {
					/* move everyone ahead of us down one */
                    if ( i > 0 ) {
                        for (int j = i-1; j >= 0; --j) {
                            mHostIndex[ j + 1 ] = mHostIndex[ j ];
                        }
                    }
					
					found = true;
					break;
				}
			}
			
			/* if we didn't find it, shift it all down */
			if (!found) {
				for(int i = MAX_NUM_HOSTS - 2; i >= 0; --i) {
					mHostIndex[ i + 1 ] = mHostIndex[ i ];
				}
			}
		}
        
		/* we're now at the head */
		mHostIndex[ 0 ] = index;

		/* and save */
		saveSessionPrefs();
	}

	public HostInfo
	createSession (int protocol)
	{
		HostInfo session;

		switch (protocol) {
		case TerminalWindow.PROT_SSH2:
			session = new SSHHostInfo(this);
			break;
		case TerminalWindow.PROT_TELNET:
		case TerminalWindow.PROT_RAW:
			session = new HostInfo(this);
			session.setProtocol(protocol);
			break;
		default:
			session = null;
			break;
		}
		
		return session;
	}
	
	public int
	addSession (HostInfo info)
	{
		int index;

		/* will we be bumping someone off the end? */
		if ( mNumHosts == MAX_NUM_HOSTS) {
			/* yep, so steal their index */
			index = mHostIndex[ MAX_NUM_HOSTS - 1 ];
			
			/*
			 * BRAIN DAMAGE: We probably want to delete their host keys as well (if no one else is using
			 * them) as the host keys will never be deleted otherwise...
			 */
		} else {
			/* nope, find a free slot */
			for ( index = 0; index < MAX_NUM_HOSTS; ++index) {
				if (mHosts[ index ] == null) {
					++mNumHosts;
					break;
				}
			}
			if (index == MAX_NUM_HOSTS) {
				System.out.println("out session count is out of synch with the index?");
			}
		}
		
		mIndexDirty = true;

		mHosts[ index ] = info;
		info.setSettingsIndex(index);
		
		/* update the index */
		udpateMostRecentIndex(index);
		
		saveSessionPrefs();
		
		/* tell anyone that cares */
		sessionChanged(info, SESSION_ADDED);
		
		return (index);
	}
	
	public void
	deleteSession (HostInfo info)
	{
		int index = info.getSettingsIndex();

        /* find ourself */
		for (int i = MAX_NUM_HOSTS - 1; i >= 0; --i) {
			if (mHostIndex[ i ] == index) {
				info.setHostname( "" );
				info.setUsername( "" );
				// must call save before changing the index, since it uses the index to save
				info.save();

				/* give the session a new index so that it goes away in the prefs */
				info.setSettingsIndex ( -1 );
				
				/* now clear it from our list */
				mIndexDirty = true;
				mHosts[ index ] = null;
				mNumHosts--;
				
				/* copy everyone from here down up */
                for (int j = i; j < ( MAX_NUM_HOSTS - 1 ); ++j) {
                    mHostIndex[ j ] = mHostIndex[ j + 1 ];
                }

				mHostIndex[MAX_NUM_HOSTS - 1] = -1;
			}
		}

        /* save our prefs at a high level so we get the new index */
        saveSessionPrefs();
        
		/* tell anyone that cares */
		sessionChanged(info, SESSION_REMOVED);
	}
    
	public void openSession(HostInfo info)
	{
		Event e = new Event( TerminalEvents.kOpenHost, 0, 0, info );
		dispatchEvent ( e );
	}
	
	public void sessionChanged(HostInfo info, int what)
	{
		if (!mHostsDirty) {
			/*
			 * We send an event to ourselves to delay processing of this a bit in case there
			 * are many things changing.
			 *
			 * BRAIN DAMAGE: This seems to be implementation dependent - would a timer be better?
			 */
			//mHostsDirty = true;
			Event e = new Event( this, EventType.EVENT_MESSAGE, SESSION_NOTIFIER, what, info );
			e.sendToTarget();
		}
	}

    public void sessionChanged(HostInfo info)
    {
        sessionChanged(info, SESSION_UPDATED);
    }

	public void
	replaceSession (HostInfo oldInfo, HostInfo info)
	{
		int index = oldInfo.getSettingsIndex();

		// copy whatever's absolutely necessary
		info.mHostname = oldInfo.mHostname;

		mIndexDirty = true;

		mHosts[index] = info;
		info.setSettingsIndex(index);

		sessionChanged(null, SESSIONS_CHANGED);
	}

    public void consoleChanged(VirtualConsole console)
    {
        dispatchMessage(console, CONSOLE_CHANGED);
    }

    public void consoleAdded(VirtualConsole console)
    {
        synchronized(mConsoleList)
        {
            mConsoleList.addElement(console);
        }
        
        dispatchMessage(console, CONSOLE_ADDED);
    }

    public void consoleRemoved(VirtualConsole console)
    {
        synchronized(mConsoleList)
        {
            mConsoleList.removeElement(console);
        }

        dispatchMessage(console, CONSOLE_REMOVED);
    }

    public Vector getConsoleList()
    {
        return mConsoleList;
    }
    
    public TerminalConsole consoleAt (int index)
    {
        return (TerminalConsole) mConsoleList.elementAt(index);
    }

    public int getConsoleIndex(VirtualConsole console)
    {
        int numConsoles = mConsoleList.size();
        if ((numConsoles == 0) || (console == null))
        {
            return -1;
        }
        
        for (int i = 0; i < numConsoles; i++)
        {
            VirtualConsole consoleAt = consoleAt(i);
            if ((consoleAt != null) && (consoleAt == console))
            {
                return i;
            }
        }
        
        return -1;
    }

    public int getNextConsoleIndex(VirtualConsole console)
    {
        int i = getConsoleIndex(console);

        if (i >= 0)
        {
            i++;

            if (i >= mConsoleList.size())
            {
                i = 0;
            }
        }

        return i;
    }

    public int getPreviousConsoleIndex(VirtualConsole console)
    {
        int i = getConsoleIndex(console);

        if (i >= 0)
        {
            i--;

            if (i < 0)
            {
                i = mConsoleList.size()-1;
            }
        }
    
        return i;
    }

	// FIXME
    public boolean
	consoleNameExists (String name)
    {
        synchronized (mConsoleList) {
            int sz = mConsoleList.size();
            for (int i = 0; i < sz; i++) {
                VirtualConsole console = consoleAt(i);
                if (console.getName().equals(name)) {
                    return true;
                }
            }
        }
        
        return false;
    }

	public SettingsDB getSettingsDB()
	{
		return mPrefs;
	}
	
	public TerminalPrefs getTerminalPrefs()
	{
		return mTerminalPrefs;
	}
	
	private static SettingsDB	mPrefs;
	
	private	TerminalPrefs		mTerminalPrefs;
	private int					mNumHosts;
	private int					mHostIndex[];
	private HostInfo			mHosts[];
	private int					mPrefsLockCount;
	private boolean				mIndexDirty;
	private Vector				mListeners;
	private boolean				mHostsDirty;
    private Vector				mConsoleList;
    
	private static final int	MAX_NUM_HOSTS		= 16;
    public static final int		SESSION_NOTIFIER	= 1;
	public static final int		SESSIONS_CHANGED	= 2;
    public static final int		SESSION_ADDED		= 3;
    public static final int		SESSION_REMOVED		= 4;
    public static final int		SESSION_UPDATED		= 5;
    public static final int		SESSION_LRU_CHANGED	= 6;
    public static final int		CONSOLE_ADDED		= 7;
    public static final int		CONSOLE_REMOVED		= 8;
    public static final int		CONSOLE_CHANGED		= 9;
}
