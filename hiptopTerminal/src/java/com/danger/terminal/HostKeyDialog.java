/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import danger.app.Event;
import danger.app.Listener;
import danger.crypto.MD5;
import danger.ui.Bitmap;
import danger.ui.Button;
import danger.ui.Color;
import danger.ui.DialogWindow;
import danger.ui.ImageView;
import danger.ui.Pen;
import danger.ui.StaticTextBox;
import danger.ui.View;
import danger.util.format.StringFormat;


public class HostKeyDialog
	extends DialogWindow
	implements TerminalResources, TerminalEvents, danger.SystemStrings
{
	public
	HostKeyDialog ()
	{
		// need to create a persistent event here so we can change the target
		// when our own target is changed with setListener().
		mCancelEvent = new Event(mListener, EVENT_REJECT_HOST_KEY, 0, 0, null);
	}

	public void
	onDecoded ()
	{
		setCancelButtonEvent(mCancelEvent);
	}

	/* scan the children, looking for that ImageView.
	 * can't use getDescendantWithID() because a bug in drc prevents two
	 * resources of the same class from using the same ID constants. :(
	 */
	private ImageView
	findImageView ()
	{
		for (View v = getFirstChild(); v != null; v = v.getSibling()) {
			if (v instanceof ImageView) {
				return (ImageView)v;
			}
		}
		return null;
	}

	private StaticTextBox
	findTextBox ()
	{
		// gotta find the LAST one
		StaticTextBox last = null;

		for (View v = getFirstChild(); v != null; v = v.getSibling()) {
			if (v instanceof StaticTextBox) {
				last = (StaticTextBox)v;
			}
		}
		return last;
	}

	public void
	setHostKey (String keytype, byte[] hostKey)
	{
		MD5 hash = new MD5();
		hash.update(hostKey);
		mFingerprint = hash.digest();

		findImageView().setImage(makeKeyIcon());

		StaticTextBox box = findTextBox();
		box.setText(StringFormat.withFormat(box.getText(), keytype, getHostKeyHexFingerprint()));
	}

	public void
	setHostname (String hostname)
	{
		setTitleFormatText(hostname);
	}

	public void
	setListener (Listener l)
	{
		super.setListener(l);
		if (mCancelEvent != null) {
			mCancelEvent.setListener(l);
		}
	}

	public void
	useEraseButton ()
	{
		((Button) getDescendantWithID(ID_HOST_KEY_SAVE_BUTTON)).setTitle(getApplication().getString(STRING_ERASE));
		setEventForControlWithID(ID_HOST_KEY_SAVE_BUTTON, new Event(EVENT_ERASE_HOST_KEY, 0, 0, this));
		setShowCancel(false);
	}

	public void
	foo ()
	{
		setDefaultFrameButton(TOP_POSITION1);
	}

	private String
	getHostKeyHexFingerprint ()
	{
		StringBuffer fpstr = new StringBuffer();
		for (int i = 0; i < mFingerprint.length; i++) {
			if (i > 0) {
				if ((i & 7) == 0) {
					fpstr.append('\n');
				} else {
					fpstr.append(' ');
				}
			}
			int n = ((int)mFingerprint[i]) & 0xff;
			fpstr.append(Integer.toString(n >> 4, 16));
			fpstr.append(Integer.toString(n & 15, 16));
		}
		return fpstr.toString();
	}

	private Bitmap
	makeKeyIcon ()
	{
		Bitmap bitmap = new Bitmap(32, 32);
		Pen pen = bitmap.createPen();
		int[] colorTable = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };

		for (int i = 0; i < mFingerprint.length; i++) {
			for (int j = 0; j < 4; j++) {
				int n = (mFingerprint[i] >> ((3-j)*2)) & 0x03;
				int y =	(i & 0x0e) << 1;
				int x = ((i & 1) << 4) + (j << 2);
				pen.setColor(colorTable[n]);
				pen.drawRect(x, y, x+7, y+7);
				pen.fillRect(x, y, x+7, y+7);
			}
		}
		return bitmap;
	}


	private byte[] mFingerprint;
	private Event mCancelEvent;
}
