/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */
// vi:ts=4 sw=4

/**
 * CharacterEncoder is a small class that decodes a unicode character stream
 * into bytes.
 */

package com.danger.terminal;


public class CharacterEncoder
{
	protected
	CharacterEncoder ()
	{
		mBuffer = new byte[8];
		mBufferTail = 0;
	}

	public void
	feed (char c)
	{
		// override me
	}

	public int
	next ()
	{
		if (mBufferTail == 0) {
			return -1;
		}
		int out = (int)mBuffer[0] & 0xff;
		System.arraycopy(mBuffer, 1, mBuffer, 0, mBufferTail - 1);
		mBufferTail--;
		return out;
	}

	static CharacterEncoder
	getEncoder (String name)
	{
		if (name.equals("ISO-8859-1")) {
			return new Latin1CharacterEncoder();
		}
		if (name.equals("ISO-8859-15")) {
			return new Latin9CharacterEncoder();
		}
		if (name.equals("UTF-8")) {
			return new UTF8CharacterEncoder();
		}
		return null;
	}


	protected byte[]			mBuffer;
	protected int				mBufferTail;
}

/* package */ class Latin1CharacterEncoder
	extends CharacterEncoder
{
	protected
	Latin1CharacterEncoder ()
	{
		super();
	}

	public void
	feed (char c)
	{
		if (c > 255) {
			mBuffer[mBufferTail++] = 0x3f;	// '?'
		} else {
			mBuffer[mBufferTail++] = (byte)((int)c & 0xff);
		}
	}
}

/* package */ class Latin9CharacterEncoder
	extends CharacterEncoder
{
	protected
	Latin9CharacterEncoder ()
	{
		super();
	}

	public void
	feed (char c)
	{
		byte b = (byte)((int)c & 0xff);

		switch (c) {
		case '\u20ac':
			// euro sign -> A4
			b = -92;
			break;
		case '\u0160':
			// capital S with caron -> A6
			b = -90;
			break;
		case '\u0161':
			// small S with caron -> A8
			b = -88;
			break;
		case '\u017d':
			// capital Z with caron -> B4
			b = -76;
			break;
		case '\u017e':
			// small Z with caron -> B8
			b = -72;
			break;
		case '\u0152':
			// capital ligature OE -> BC
			b = -68;
			break;
		case '\u0153':
			// small ligature OE -> BD
			b = -67;
			break;
		case '\u0178':
			// capital Y with umlaut -> BE
			b = -66;
			break;
		default:
			if (c > 255) {
				b = 0x3f;	// '?'
			}
			break;
		}

		mBuffer[mBufferTail++] = b;
	}
}

/* package */ class UTF8CharacterEncoder
	extends CharacterEncoder
{
	protected UTF8CharacterEncoder ()
	{
		super();
	}

	public void
	feed (char c)
	{
		if (c < 0x80) {
			mBuffer[mBufferTail++] = (byte)((int)c & 0xff);
		} else if (c < 0x800) {
			mBuffer[mBufferTail++] = (byte)(0xc0 | (((int)c >> 6) & 0x1f));
			mBuffer[mBufferTail++] = (byte)(0x80 | ((int)c & 0x3f));
		} else {
			mBuffer[mBufferTail++] = (byte)(0xe0 | (((int)c >> 12) & 0x0f));
			mBuffer[mBufferTail++] = (byte)(0x80 | (((int)c >> 6) & 0x3f));
			mBuffer[mBufferTail++] = (byte)(0x80 | ((int)c & 0x3f));
		}
	}
}
