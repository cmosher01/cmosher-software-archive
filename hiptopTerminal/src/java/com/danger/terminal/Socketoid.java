/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import danger.app.SocketRegistrar;
import danger.net.IPCSocket;

/**
 * This class exists to wrap around the socket-like objects, so that the
 * rest of the terminal code doesn't really have to understand what's going
 * on.
 */
final public class Socketoid
{
	/** null-ok; a normal socket if that's what we've got */
	private Socket mSocket;

	/** null-ok; a local ipc socket if that's what we've got */
	private IPCSocket mIPCSocket;


	
	// ------------------------------------------------------------------------
	// constructors

	/**
	 * Construct an instance. This opens the connection.
	 *
	 * @param host non-null; the host to connect to, or the name of the
	 * local socket
	 * @param port the port number, or <code>-1</code> to connect to a
	 * local socket
	 */
	public Socketoid(String host, int port)
		throws IOException
	{
		if (port == -1) {
			mIPCSocket = SocketRegistrar.makeSocket(host, 10000);
		}
		else {
			mSocket = new Socket(host, port);
		}
	}



	// ------------------------------------------------------------------------
	// public instance methods

	/**
	 * Close this instance.
	 */
	public void close()
		throws IOException
	{
		if (mSocket != null) {
			mSocket.close();
			mSocket = null;
		}

		if (mIPCSocket != null) {
			mIPCSocket.close();
			mIPCSocket = null;
		}
	}

	/**
	 * Get the input stream.
	 *
	 * @return non-null; the input stream
	 */
	public InputStream getInputStream()
		throws IOException
	{
		if (mSocket != null) {
			return mSocket.getInputStream();
		}

		return mIPCSocket.getInputStream();
	}

	/**
	 * Get the output stream.
	 *
	 * @return non-null; the input stream
	 */
	public OutputStream getOutputStream()
		throws IOException
	{
		if (mSocket != null) {
			return mSocket.getOutputStream();
		}

		return mIPCSocket.getOutputStream();
	}
}
