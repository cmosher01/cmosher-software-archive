/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.InputStream;
import java.io.OutputStream;

public class RawSession
	implements Session
{
	public
	RawSession (HostInfo info, InputStream in, OutputStream out)
	{
		mInfo = info;
		mInStream = in;
		mOutStream = out;
		mInfo.setStatus(HostInfo.STATUS_OPEN);
	}

	public InputStream
	getInputStream ()
	{
		return mInStream;
	}

	public OutputStream
	getOutputStream ()
	{
		return mOutStream;
	}

	public void
	refreshShow ()
	{
		// pass
	}

	public void
	resize (int cols, int rows)
	{
		// pass
	}


	private HostInfo				mInfo;
	private InputStream				mInStream;
	private OutputStream			mOutStream;
}
