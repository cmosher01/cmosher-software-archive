/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

package com.danger.terminal;

import danger.app.Event;
import danger.app.EventType;
import danger.ui.ActiveListView;
import danger.ui.MenuItem;
import danger.ui.Pen;
import danger.ui.Style;
import danger.util.ActiveList;


public class SessionListView extends ActiveListView
	implements TerminalResources, TerminalEvents
{
	public void Init(SessionManager manager)
	{
		mSessionManager = manager;
		mSessionManager.registerListener ( this );
	}

	// ------------------------------------------------------------------------
	// public instance methods

	// superclass's javadoc suffices
	public void onAddedToParent()
	{
		mWindow = (SessionListWindow) getParent();
		
		mSessionList = new ActiveList();
		setItemHeight(15);
		setList(mSessionList);
	}



	// ------------------------------------------------------------------------
	// protected instance methods

	// superclass's javadoc suffices
	protected void itemFocused(Object item)
	{
		if (item == null) {
			mWindow.setOpenCloseModifyDeleteEnabled(false, false, false, false);
		} else if (item.getClass() == SessionListItem.class) {
			SessionListItem listItem = (SessionListItem) item;
			
			switch( listItem.getType() ) {
				case SessionListItem.HOST_TYPE:
					if (listItem.getHostInfo().getConsoles().size() == 0) {
						mWindow.setOpenCloseModifyDeleteEnabled(true, false, true, true);
					} else {
						mWindow.setOpenCloseModifyDeleteEnabled(true, false, true, false);
					}
					break;
				
				case SessionListItem.SESSION_TYPE:
					mWindow.setOpenCloseModifyDeleteEnabled(true, true, false, false);
					break;
			}
		}
	}

	// superclass's javadoc suffices
	protected void itemActivated(Object item)
	{
		((SessionListItem) item).itemActivated();
	}

	// superclass's javadoc suffices
	protected void itemUpdated(Object item, boolean isFocused)
	{
		if (isFocused)
		{
			itemFocused(item);
		}
	}

    protected boolean handleSessionMessage(Event e)
    {
        boolean handled = false;

        switch(e.what)
        {
            case SessionManager.SESSIONS_CHANGED:
                updateSessions();
                handled = true;
                break;
                
            case SessionManager.SESSION_ADDED:
                addSession((HostInfo) e.argument);
                handled = true;
                break;

            case SessionManager.SESSION_REMOVED:
                removeSession((HostInfo) e.argument);
                handled = true;
                break;

            case SessionManager.SESSION_UPDATED:
                invalidate();
                handled = true;
                break;
                
            case SessionManager.CONSOLE_ADDED:
                addConsole((TerminalConsole) e.argument);
                handled = true;
                break;

            case SessionManager.CONSOLE_REMOVED:
                removeConsole((TerminalConsole) e.argument);
                handled = true;
                break;

            case SessionManager.CONSOLE_CHANGED:
                invalidate();
                handled = true;
                break;
        }
        
        return handled;
    }

	public boolean receiveEvent(Event e)
	{
		boolean handled = false;
		MenuItem item;
		
		switch ( e.type )
		{
			case EventType.EVENT_MESSAGE:
                handled = handleSessionMessage(e);
                break;
		}
		
		if ( !handled )
		{
			handled = super.receiveEvent ( e );
		}
		
		return handled;
	}
	
	protected void paintItemLine(Pen p, 
								 int left, int top, int right, int bottom,
								 boolean focused, Object item)
	{		
		/* this is just stolen from super */
		if (focused)
		{
			paintSelection(p, left, top, right, bottom);
			p.setColor(mStyle.getColor(Style.BACKGROUND_COLOR));
		}
		else
		{
			p.setColor(mStyle.getColor(Style.FOREGROUND_COLOR));
		}

        ((SessionListItem) item).paintItem(mStyle, p, left, top, right, bottom, focused);
	}

    protected void addSession(HostInfo info)
    {
        mSessionList.addItem(new SessionListItem( info, mSessionManager ));
    }

    protected void removeSession(HostInfo info)
    {
        /* find the session list item */
        int index = findSessionIndex(info);
        if (index >= 0)
        {
            mSessionList.removeItemAt(index);

            /* now remove the consoles */
            for (int i = info.getConsoles().size() - 1; i >= 0; --i)
            {
                mSessionList.removeItemAt(index);
            }
        }
    }

    protected int findSessionIndex(HostInfo info)
    {
            int index;
    
            for (index = mSessionList.size()-1; index >= 0; --index)
            {
                SessionListItem item = (SessionListItem) mSessionList.getItem(index);
                if (item.getHostInfo() == info)
                {
                    break;
                }
            }
    
            return index;
    }
    
    protected int findConsoleIndex (TerminalConsole console)
    {
        int index;
    
        for (index = mSessionList.size()-1; index >= 0; --index)
        {
            SessionListItem item = (SessionListItem) mSessionList.getItem(index);
            if (item.getConsole() == console)
            {
                break;
            }
        }
    
        return index;
    }

    protected void addConsole (TerminalConsole console)
    {
        int index = findSessionIndex(console.getHostInfo());

        if (index >= 0)
        {
            SessionListItem item = new SessionListItem( console, mSessionManager );
            mSessionList.insertItemAt(item, index+1);
        }
    }

    protected void removeConsole (TerminalConsole console)
    {
        int index = findConsoleIndex(console);

        if (index >= 0)
        {
            mSessionList.removeItemAt(index);
        }
    }

	public void updateSessions()
	{
		int numSessions;
		SessionListItem item;
		
		if ( mSessionList == null )
		{
			return;
		}
		 
		/* remove everything and then add them all back */
		mSessionList.removeAllItems();
		
		/* add the saved sessions */
		numSessions = mSessionManager.getNumHosts();
		if (numSessions > 0) {
			for (int i = 0; i < numSessions; i++) {
				HostInfo h = mSessionManager.getSession(i);
				if (h != null) {
                    addSession(h);
				}
			}
		} else {
			mWindow.receiveEvent(new Event(kNewHost, 0, 0, null));
		}
	}


	private SessionListWindow	mWindow;
	private SessionManager		mSessionManager;
	private ActiveList			mSessionList;
}
