/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import danger.ui.DialogWindow;
import danger.ui.PopupMenu;
import danger.ui.TextField;


public class ConnectDialog extends DialogWindow implements TerminalResources, TerminalEvents
{
	public static class HostTextField extends TextField
	{
		public void
		loseFocus ()
		{
			((ConnectDialog)getParent()).updateHostName();
			super.loseFocus();
		}
	}


    public
	ConnectDialog ()
    {
        mHostInfo = null;
        mHadUsername = false;
        mLastSessionName = "";
    }

	public void
	onDecoded ()
	{
        setProtocol(TerminalWindow.PROT_SSH2);
	}

    // called when the cursor leaves the hostname field
    /* package */ void
	updateHostName ()
    {
        if (mLastSessionName.equals(getSessionName())) {
            /* just copy whatever they had */
            mLastSessionName = getHostname();
            setSessionName(mLastSessionName);
        }
    }

    protected void
	updateHostInfo ()
    {
		int protocol = getProtocol();

		if (protocol != mHostInfo.getProtocol()) {
			/* need to create a new session-info object, because the old one might
			 * be a descendant of HostInfo with protocol-specific fields.
			 */
			SessionManager manager = Terminal.instance().getSessionManager();
			HostInfo info = manager.createSession(protocol);
			manager.replaceSession(mHostInfo, info);
			mHostInfo = info;
		}

        /* now set common things */
        String hostname = getHostname();

        /* the port is kinda hidden for now... */
        int port = (protocol == TerminalWindow.PROT_SSH2) ? 22 : 23;
        int separator = hostname.indexOf(':');
        if (separator == -1) {
            separator = hostname.indexOf(' ');
        }

        if (separator != -1) {
            String portStr = hostname.substring(separator + 1).trim();
            if (portStr.equals("local")) {
                port = -1;
            } else {
                try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException exc) {
                    // pass
				}
            }
            hostname = hostname.substring(0, separator);
        }

        /*
         Look for an '@' and set the username to be the portion
         before it, and the hostname to be the portion after it.
         */
        int   AtOffset = hostname.indexOf('@');

        if (AtOffset > 0) {
            mHostInfo.setHostname(hostname.substring(AtOffset + 1));
            mHostInfo.setUsername(hostname.substring(0, AtOffset));
        } else {
            mHostInfo.setHostname(hostname);

            /* if we started with a user name, then clear it */
            if (mHadUsername) {
                mHostInfo.setUsername("");
            }
        }

        mHostInfo.setSessionName(getSessionName());
        mHostInfo.setPort(port);
    }
	
	public HostInfo
	getHostInfo ()
	{
		if (mHostInfo == null) {
			mHostInfo = Terminal.instance().getSessionManager().createSession(getProtocol());
            updateHostInfo();
            Terminal.instance().getSessionManager().addSession(mHostInfo);
        } else {
            /* make sure what we have is up to date! */
            updateHostInfo();
        }
        
		return mHostInfo;
	}

    public String
	extractUsername (String hostname)
    {
        String username;
        
        /*
         Look for an '@' and set the username to be the portion
         before it, and the hostname to be the portion after it.
         */
        int   AtOffset = hostname.indexOf('@');

        if (AtOffset > 0) {
            username = hostname.substring(0, AtOffset);
        } else {
            username = null;
        }

        return username;
    }

    public final String
	getHostname ()
	{
        return getDescendantWithID(ID_CONNECT_HOST).toString();
    }

    public final void
	setHostname (String hostname)
    {
        ((TextField)getDescendantWithID(ID_CONNECT_HOST)).setText(hostname);
    }

    public final String
	getSessionName ()
    {
        return getDescendantWithID(ID_CONNECT_NAME).toString();
    }
    
    public final void
	setSessionName (String name)
    {
		((TextField)getDescendantWithID(ID_CONNECT_NAME)).setText(name);
		// FIXME -- bug in TextField:
		((TextField)getDescendantWithID(ID_CONNECT_NAME)).invalidate();
    }

	public int
	getProtocol ()
	{
        PopupMenu menu = (PopupMenu) getDescendantWithID(ID_CONNECT_PROTOCOL);

		switch (menu.getValueAsItemID()) {
		case ID_PROTOCOL_SSH2:
			return TerminalWindow.PROT_SSH2;
		case ID_PROTOCOL_TELNET:
			return TerminalWindow.PROT_TELNET;
		case ID_PROTOCOL_RAW:
			return TerminalWindow.PROT_RAW;
		default:
			System.err.println("WHAT:? " + menu.getValueAsItemID());
		}
		return 0;
	}

	public void
	setProtocol (int protocol)
	{
        PopupMenu menu = (PopupMenu) getDescendantWithID(ID_CONNECT_PROTOCOL);
		switch (protocol) {
		case TerminalWindow.PROT_SSH2:
			menu.setValueWithItemID(ID_PROTOCOL_SSH2);
			break;
		case TerminalWindow.PROT_TELNET:
			menu.setValueWithItemID(ID_PROTOCOL_TELNET);
			break;
		case TerminalWindow.PROT_RAW:
			menu.setValueWithItemID(ID_PROTOCOL_RAW);
			break;
		}
	}


	private HostInfo			mHostInfo;
    private String				mLastSessionName;
    private boolean				mHadUsername;
}
