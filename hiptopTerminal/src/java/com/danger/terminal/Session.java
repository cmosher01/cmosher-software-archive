/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.InputStream;
import java.io.OutputStream;

public interface Session
{
	public InputStream getInputStream();
	public OutputStream getOutputStream();

	// a small hack (i admit it) to allow sessions to have dialog boxes.
	// we call refreshShow() on the visible console when Terminal gets a resume()
	public void refreshShow();

	public void resize (int cols, int rows);
}
