/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

/*
 * This implements an ssh "message" (yet another serialization scheme, really).
 * They're used extensively in the ssh protocol, and can store bools, ints,
 * arbitrary-length strings, string lists, and infinite-length integers.
 */

package com.danger.terminal;

import danger.crypto.BigInteger;

public class SSHMessage
{
	public SSHMessage ()
	{
		mBufferSize = kInitialBufferSize;
		mBuffer = new byte[mBufferSize];
		mIndex = 0;
	}

	public SSHMessage (byte[] content)
	{
		this(content, 0, content.length);
	}

	public SSHMessage (byte[] content, int offset, int length)
	{
		mBufferSize = length;
		mBuffer = new byte[mBufferSize];
		mIndex = 0;
		System.arraycopy(content, offset, mBuffer, 0, length);
	}

	//*  -----  get things out of a message

	public byte getByte ()
	{
		if (mIndex >= mBufferSize) {
			return 0;
		}
		return mBuffer[mIndex++];
	}

	public byte[] getBytes (int n)
	{
		byte[] out = new byte[n];

		if (mIndex + n <= mBufferSize) {
			System.arraycopy(mBuffer, mIndex, out, 0, n);
			mIndex += n;
			return out;
		}

		// this is an error, really, but i'll humour them
		int remainder = mBufferSize - mIndex;
		System.arraycopy(mBuffer, mIndex, out, 0, remainder);
		for (int i = remainder; i < n; i++) {
			out[i] = 0;
		}
		mIndex = mBufferSize;
		return out;
	}

	public boolean getBoolean ()
	{
		byte x = getByte();
		return (x == 0) ? false : true;
	}

	public int getInt ()
	{
		if (mIndex + 4 <= mBufferSize) {
			int out = (((int) mBuffer[mIndex] & 0xff) << 24) |
				(((int) mBuffer[mIndex+1] & 0xff) << 16) |
				(((int) mBuffer[mIndex+2] & 0xff) << 8) |
				((int) mBuffer[mIndex+3] & 0xff);
			mIndex += 4;
			return out;
		}
		mIndex = mBufferSize;
		return 0;
	}

	public BigInteger getBigInteger ()
	{
		if (mIndex + 4 <= mBufferSize) {
			int len = getInt();
			if (mIndex + len <= mBufferSize) {
				BigInteger bigint = new BigInteger(mBuffer, mIndex, len);
				mIndex += len;
				return bigint;
			}
		}
		return new BigInteger(0);
	}

	public String getString ()
	{
		if (mIndex + 4 <= mBufferSize) {
			int len = getInt();
			if (mIndex + len <= mBufferSize) {
				String out = new String(mBuffer, mIndex, len);
				mIndex += len;
				return out;
			}
		}
		return null;
	}

	public byte[] getByteString ()
	{
		if (mIndex + 4 <= mBufferSize) {
			int len = getInt();
			if (mIndex + len <= mBufferSize) {
				byte[] out = new byte[len];
				System.arraycopy(mBuffer, mIndex, out, 0, len);
				mIndex += len;
				return out;
			}
		}
		return null;
	}

	public String[] getStringList ()
	{
		String in = getString();
		int count, n, i, last;

		if (in == null) {
			return null;
		}

		// first, count the commas
		count = 1;
		n = in.indexOf(',');
		while (n >= 0) {
			count++;
			n = in.indexOf(',', n+1);
		}

		// now, build the strings
		String[] out = new String[count];
		last = 0;
		i = 0;
		n = in.indexOf(',');
		while (n >= 0) {
			out[i++] = in.substring(last, n);
			last = n+1;
			n = in.indexOf(',', last);
		}
		out[i++] = in.substring(last);
		return out;
	}

	//*  -----  put things into a message

	public void add (byte x)
	{
		checkSpace(1);
		mBuffer[mIndex++] = x;
	}

	public void add (byte[] x, int offset, int length)
	{
		checkSpace(length);
		System.arraycopy(x, offset, mBuffer, mIndex, length);
		mIndex += length;
	}

	public void add (byte[] x)
	{
		add(x, 0, x.length);
	}

	public void add (boolean x)
	{
		add(x ? (byte)1 : (byte)0);
	}

	public void add (int x)
	{
		checkSpace(4);
		mBuffer[mIndex] = (byte)((x >> 24) & 0xff);
		mBuffer[mIndex+1] = (byte)((x >> 16) & 0xff);
		mBuffer[mIndex+2] = (byte)((x >> 8) & 0xff);
		mBuffer[mIndex+3] = (byte)(x & 0xff);
		mIndex += 4;
	}

	public void add (BigInteger x)
	{
		byte[] flat = x.toByteArray();
		checkSpace(4 + flat.length);
		add(flat.length);
		add(flat);
	}

	public void add (String x)
	{
		int len = x.length();
		checkSpace(4 + len);
		add(len);
		if (len > 0) {
			add(x.getBytes());
		}
	}

	public void addByteString (byte[] x)
	{
		int len = x.length;
		checkSpace(4 + len);
		add(len);
		if (len > 0) {
			add(x);
		}
	}

	public void add (String[] x)
	{
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < x.length; i++) {
			if (i > 0) {
				buf.append(',');
			}
			buf.append(x[i]);
		}
		add(buf.toString());
	}

	public byte[] getBuffer ()
	{
		byte[] out = new byte[mIndex];
		System.arraycopy(mBuffer, 0, out, 0, mIndex);
		return out;
	}

	public void copyBuffer (byte[] buffer, int offset)
	{
		System.arraycopy(mBuffer, 0, buffer, offset, mIndex);
	}

	int getBufferLength ()
	{
		return mIndex;
	}


	//*  -----  debugging

	// return a string describing the current contents
	public static String makeBufferPrintable (byte[] in, int offset, int length)
	{
		StringBuffer buf = new StringBuffer();

		for (int i = offset; i < offset + length; i++) {
			if (i > offset) {
				buf.append(' ');
			}
			int n = ((int)in[i] & 0xff);
			if ((n >= 0x20) && (n <= 0x7f)) {
				buf.append((char)n);
				buf.append('/');
			}
			buf.append(Integer.toString(n/16, 16));
			buf.append(Integer.toString(n%16, 16));
			if ((i - offset) % 12 == 11) {
				buf.append("\n");
			}
		}
		return buf.toString();
	}

	public static String makeBufferPrintable (byte[] in)
	{
		return makeBufferPrintable(in, 0, in.length);
	}

	public String toString ()
	{
		return makeBufferPrintable(mBuffer);
	}

	//*  -----  private

	// make sure there's enough space in the buffer to put N extra bytes
	private void checkSpace (int extra)
	{
		if (mIndex + extra <= mBufferSize) {
			return;
		}

		int newSize = mBufferSize * 4;
		byte[] newBuffer = new byte[newSize];

		System.arraycopy(mBuffer, 0, newBuffer, 0, mIndex);
		mBuffer = newBuffer;
		mBufferSize = newSize;
	}

	//*  -----  member variables

    private byte[] mBuffer;
	private int mIndex;
	private int mBufferSize;

	static final int kInitialBufferSize = 64;
}
