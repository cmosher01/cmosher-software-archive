/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import danger.ui.Font;
import danger.ui.TextField;
import danger.util.format.StringFormat;

/**
 * TerminalConsole is just a descendant of VirtualConsole that tracks some
 * other Terminal-specific information per console.  It also handles the
 * socket connecting, and attaching a specific "session" to each virtual
 * console.
 */

/* package */ class TerminalConsole extends VirtualConsole
{
	public
	TerminalConsole (String fontname, int width, int height, int scroll_lines)
	{
		super(fontname, width, height, scroll_lines);

		mHistory = new String[MAX_HISTORY];
		mOutBuffer = new byte[128];
	}

	public void
	refreshShow()
	{
		if (mSession != null) {
			mSession.refreshShow();
		}
	}

	public void
	resize (int width, int height)
	{
		if (mTextField != null) {
			mTextField.setSize(width, 0);
			mTextField.setPosition(0, height - mTextField.getHeight());
			height -= mTextField.getHeight() + TEXT_FIELD_PADDING;
		}

		super.resize(width, height);
		if (mSession != null) {
			mSession.resize(mColumns, mRows);
		}
	}

	public void
	setFont (String fontName)
	{
		if (mTextField != null) {
			mHeight += mTextField.getHeight() + TEXT_FIELD_PADDING;
			mTextField.setFont(Font.findFont(fontName));
			mTextField.invalidate();
		}
		super.setFont(fontName);
	}

	/* package */ void
	setLocalEcho (boolean localEcho)
	{
		mLocalEcho = localEcho;
	}

	/* package */ void
	setLineMode (boolean lineMode)
	{
		boolean inLineMode = (mTextField != null);
		if (inLineMode != lineMode) {
			Terminal.instance().getTerminalWindow().toggleTextField(this);
		}
	}

	public void
	setCodec (String codec)
	{
		super.setCodec(codec);
		mEncoder = CharacterEncoder.getEncoder(codec);
	}

	public void
	reconnect()
	{
		if (!mIsOpen) {            
			mIsOpen = true;
            mInfo.setStatus(HostInfo.STATUS_CONNECTING);
            mInfo.consoleChanged(this);
			startSession(mInfo);
		}
	}
	
	public void
	connect (HostInfo info)
	{
		mInfo = info;
		mIsOpen = true;

		info.addConsole(this);
        info.setStatus(HostInfo.STATUS_CONNECTING);
        info.consoleChanged(this);

		setFont(info.getFont());

		startSession(info);
	}

	public void
	disconnect ()
	{
		mThread.interrupt();
		if (mSocket != null) {
			try {
				mSocket.close();
			} catch (IOException x) { }
		}
		
        mInfo.setStatus(HostInfo.STATUS_CLOSED);
        mInfo.removeConsole(this);
	}

	public final boolean
	isOpen()
	{
		return mIsOpen;
	}

	public Session
	getSession ()
	{
		return mSession;
	}

	public HostInfo
	getHostInfo()
	{
		return mInfo;
	}

	/* package */ TextField
	getTextField ()
	{
		return mTextField;
	}

	/* package */ void
	setTextField (TextField tf)
	{
		mTextField = tf;
	}

	/* package */ void
	previousHistory ()
	{
		if (mCurrentHistoryIndex == mNextHistoryIndex) {
			// just starting
			mHistory[mCurrentHistoryIndex] = mTextField.toString();
		}
		mCurrentHistoryIndex = (mCurrentHistoryIndex + MAX_HISTORY - 1) % MAX_HISTORY;
		if (mHistory[mCurrentHistoryIndex] == null) {
			// can't go back any further -- reverse.
			mCurrentHistoryIndex = (mCurrentHistoryIndex + 1) % MAX_HISTORY;
			return;
		}
		mTextField.setText(mHistory[mCurrentHistoryIndex]);
	}

	/* package */ void
	nextHistory ()
	{
		if (mCurrentHistoryIndex == mNextHistoryIndex) {
			// can't go down any further
			return;
		}
		mCurrentHistoryIndex = (mCurrentHistoryIndex + 1) % MAX_HISTORY;
		mTextField.setText(mHistory[mCurrentHistoryIndex]);
	}

	/* package */ void
	commitHistory ()
	{
		mHistory[mNextHistoryIndex] = mTextField.toString();
		mNextHistoryIndex = (mNextHistoryIndex + 1) % MAX_HISTORY;
		mCurrentHistoryIndex = mNextHistoryIndex;
	}

	public void
	writeKey (int key)
	{
		if (mOutputStream == null) {
			return;
		}

		write(mTerminalEmulator.getKeyEncoding(key));
	}

	public void
	write (int ch)
	{
		if (mOutputStream == null) {
			// don't even try if we're not connected
			return;
		}

		stopScrolling();

		if (mLocalEcho) {
			insert(ch);
			if (ch == 13) {
				insert(10);
			}
		}

		int x = 0;
		mEncoder.feed((char)ch);
		for (int c = mEncoder.next(); c >= 0; c = mEncoder.next()) {
			mOutBuffer[x++] = (byte)c;
		}

		try {
			mOutputStream.write(mOutBuffer, 0, x);
			if ((ch == 13) && getAutoLFMode()) {
				mOutputStream.write(10);
			}
		} catch (IOException exc) {	}
	}

	public void
	write (char[] stuff)
	{
		if (mOutputStream == null) {
			// don't even try if we're not connected
			return;
		}

		stopScrolling();

		if (mLocalEcho) {
			insert(stuff);
		}

		int x = 0, len = stuff.length, buflen = mOutBuffer.length;
		for (int i = 0; i < len; i++) {
			mEncoder.feed(stuff[i]);
			for (int c = mEncoder.next(); c >= 0; c = mEncoder.next()) {
				if (x == buflen) {
					try {
						mOutputStream.write(mOutBuffer);
					} catch (IOException exc) { }
					x = 0;
				}
				mOutBuffer[x++] = (byte)c;
			}
		}
		if (x > 0) {
			try {
				mOutputStream.write(mOutBuffer, 0, x);
			} catch (IOException exc) { }
		}

		if ((stuff.length > 0) && (stuff[stuff.length - 1] == '\r') && getAutoLFMode()) {
			write(10);
		}
	}

	public void
	write (String s)
	{
		write(s.toCharArray());
	}

	private void
	startSession (HostInfo info)
	{
		// clear out any existing session
		if (mSession != null) {
			if (mSocket != null) {
				try {
					mSocket.close();
				} catch (IOException x) { }
				mSocket = null;
			}
			mSession = null;
		}

		// do the hard work in another thread
		startThread();
	}

	public void
	run ()
	{
		InputStream istream = null;
		insert(StringFormat.fromRsrc(STRING_TRYING, mInfo.getHostname()));

		try {
			mSocket = new Socketoid(mInfo.getHostname(), mInfo.getPort());

			switch (mInfo.getProtocol()) {
			case TerminalWindow.PROT_TELNET:
				mSession = new TelnetSession(mInfo, mSocket.getInputStream(), mSocket.getOutputStream(),
											 mColumns, mRows, EMULATOR_NAMES[mInfo.getEmulator()]);
				((TelnetSession)mSession).setConsole(this);
				istream = mSession.getInputStream();
				mOutputStream = mSession.getOutputStream();
				setAutoLFMode(true);
				mLocalEcho = true;
				break;
			case TerminalWindow.PROT_SSH2:
				mSession = new SSHSession((SSHHostInfo)mInfo, mSocket.getInputStream(), mSocket.getOutputStream(),
										  mColumns, mRows, EMULATOR_NAMES[mInfo.getEmulator()]);
				istream = mSession.getInputStream();
				mOutputStream = mSession.getOutputStream();
				mLocalEcho = false;
				((SSHSession)mSession).setHostname(mInfo.getHostname());
				((SSHSession)mSession).jumpStart();
				break;
			default:
				mSession = new RawSession(mInfo, mSocket.getInputStream(), mSocket.getOutputStream());
				istream = mSession.getInputStream();
				mOutputStream = mSession.getOutputStream();
				setAutoLFMode(true);
				mLocalEcho = true;
				break;
			}

			insert(StringFormat.fromRsrc(STRING_CONNECTED_TO, mInfo.getHostname()));
		} catch (Exception e) {
			insert(Terminal.instance().getString(STRING_CONNECTION_FAILED));
			System.err.println("connect exception: " + e.toString());
			mIsOpen = false;
            mInfo.setStatus(HostInfo.STATUS_CLOSED);
            mInfo.consoleChanged(this);
			return;
		}

		// input stream stuff
		setInputStream(istream);
		super.run();

		mIsOpen = false;
		setLineMode(false);

        mInfo.setStatus(HostInfo.STATUS_CLOSED);
        mInfo.consoleChanged(this);
		try {
			mSocket.close();
		} catch (IOException x) { }
		mSocket = null;
		mSession = null;
		mOutputStream = null;
		insert(Terminal.instance().getString(STRING_CONNECTION_CLOSED));
	}


    private Session					mSession;
	private Socketoid				mSocket;
	private HostInfo				mInfo;
	private OutputStream			mOutputStream;
	private TextField				mTextField;
	private boolean					mIsOpen = false;
	private boolean					mLocalEcho = false;
	private CharacterEncoder		mEncoder;
	private byte[]					mOutBuffer;

	// line-entry history
	private static final int		MAX_HISTORY = 20;

	private String[]				mHistory;
	private int						mNextHistoryIndex = 0;
	private int						mCurrentHistoryIndex = 0;

	private final static int		TEXT_FIELD_PADDING = 1;
}
