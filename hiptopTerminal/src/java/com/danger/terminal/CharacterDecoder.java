/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */
// vi:ts=4 sw=4

/**
 * CharacterDecoder is a small class that decodes a byte stream into unicode
 * characters.
 */

package com.danger.terminal;


public class CharacterDecoder
{
	protected
	CharacterDecoder ()
	{
		mBuffer = new char[8];
		mBufferTail = 0;
	}

	public void
	feed (byte b)
	{
		//
	}

	public int
	next ()
	{
		if (mBufferTail == 0) {
			return -1;
		}
		int out = (int)mBuffer[0];
		System.arraycopy(mBuffer, 1, mBuffer, 0, mBufferTail - 1);
		mBufferTail--;
		return out;
	}

	static CharacterDecoder
	getDecoder (String name)
	{
		if (name.equals("ISO-8859-1")) {
			return new Latin1CharacterDecoder();
		}
		if (name.equals("ISO-8859-15")) {
			return new Latin9CharacterDecoder();
		}
		if (name.equals("UTF-8")) {
			return new UTF8CharacterDecoder();
		}
		return null;
	}


	protected char[]			mBuffer;
	protected int				mBufferTail;
}

/* package */ class Latin1CharacterDecoder
	extends CharacterDecoder
{
	protected
	Latin1CharacterDecoder ()
	{
		super();
	}

	public void
	feed (byte b)
	{
		// latin-1 bytes *are* unicode characters
		mBuffer[mBufferTail++] = (char)((int)b & 0xff);
	}
}

/* package */ class Latin9CharacterDecoder
	extends CharacterDecoder
{
	protected
	Latin9CharacterDecoder ()
	{
		super();
	}

	public void
	feed (byte b)
	{
		char c = (char)((int)b & 0xff);
		switch (b) {
		case -92:
			// A4 -> euro sign
			c = '\u20ac';
			break;
		case -90:
			// A6 -> capital S with caron
			c = '\u0160';
			break;
		case -88:
			// A8 -> small S with caron
			c = '\u0161';
			break;
		case -76:
			// B4 -> capital Z with caron
			c = '\u017d';
			break;
		case -72:
			// B8 -> small Z with caron
			c = '\u017e';
			break;
		case -68:
			// BC -> capital ligature OE
			c = '\u0152';
			break;
		case -67:
			// BD -> small ligature OE
			c = '\u0153';
			break;
		case -66:
			// BE -> capital Y with umlaut
			c = '\u0178';
			break;
		default:
			break;
		}

		mBuffer[mBufferTail++] = c;
	}
}

/* package */ class UTF8CharacterDecoder
	extends CharacterDecoder
{
	protected UTF8CharacterDecoder ()
	{
		super();
	}

	public void
	feed (byte b)
	{
		int bi = (int)b & 0xff;
		if (bi < 0x80) {
			mBuffer[mBufferTail++] = (char)bi;
		} else if (bi < 0xc0) {
			// 10xxxxxx
			if (mCount <= 0) {
				// illegal
				mBuffer[mBufferTail++] = (char)0xffff;
				return;
			}
			mSoFar = (mSoFar << 6) | (bi & 0x3f);
			mCount--;
			if (mCount <= 0) {
				mBuffer[mBufferTail++] = (char)mSoFar;
				mSoFar = 0;
				mCount = 0;
			}
		} else if (bi < 0xe0) {
			// 110xxxxx -- first byte of 2-byte sequence
			mSoFar = bi & 0x1f;
			mCount = 1;
		} else if (bi < 0xf0) {
			// 1110xxxx -- first byte of 3-byte sequence
			mSoFar = bi & 0x0f;
			mCount = 2;
		} else {
			// illegal in UCS-2 (16-bit unicode)
			mBuffer[mBufferTail++] = (char)0xffff;
		}
	}

	private int		mSoFar = 0;
	private int		mCount = 0;
}
