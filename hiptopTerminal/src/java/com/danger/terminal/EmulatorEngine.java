/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */
// vi:ts=4 sw=4

/**
 * EmulatorEngine is an abstract class representing a terminal emulator.  It
 * maintains a screen buffer of given dimensions, containing attribute info,
 * and a scrollback buffer.  Incoming bytes are fed into the emulator to change
 * the screen buffer.  You can also fetch the codes to send to emulate certain
 * non-printable keys (like arrow keys).
 */

package com.danger.terminal;

import danger.app.Event;
import danger.app.Listener;
import danger.app.Timer;
import danger.system.Hardware;


// format of screen data:
// (unicode char: short, attribute: short) * width * height

abstract class EmulatorEngine extends Listener
{
	final static boolean TRACE_BUFFER = false;


	static class Cell
	{
		public int glyph;
		public int attr;
	}

	static class VirtualLine
	{
		public char[] text = new char[60];
		public short[] attr = new short[60];
	}

	static interface EmulatorListener
	{
		public void eventScrolled (VirtualConsole console, int lines);
		public void eventSetTitle (VirtualConsole console, String title);
		public void eventBell (VirtualConsole console);

		/** Sometimes the emulator may want to send data back to the remote
		 * host.  For example, in VT100, the remote host may send a code
		 * requesting the terminal's cursor position.  In these cases, the
		 * emulator will call this event to reply.
		 */
		public void eventRespond (VirtualConsole console, String data);

		public void eventMarquee (VirtualConsole console, String message);
	}


	protected
	EmulatorEngine (int width, int height, int scrollback)
	{
		mScreenBuffer = new short[width * height * STRIDE];
		mBlankLine = new short[width * STRIDE];

		mRows = height;
		mCols = width;

		if (TRACE_BUFFER) {
			mTraceBuffer = new byte[4096];
			mTraceBufferOffset = 0;
		}

		mScrollMax = scrollback;
		if (scrollback > 0) {
			mScrollBuffer = new short[width * scrollback * STRIDE];
		}

		for (int i = 0; i < width * STRIDE; i += STRIDE) {
			mBlankLine[i] = 0 /* empty slot */;
			mBlankLine[i+1] = 0;
		}

		mDeltas = new short[height];
		for (int i = 0; i < height; i++) {
			mDeltas[i] = -1;
		}
	}

	public void
	reset ()
	{
		for (int i = 0; i < mRows; i++) {
			System.arraycopy(mBlankLine, 0, mScreenBuffer, i * mCols * STRIDE, mCols * STRIDE);
		}

		mScrollUsed = 0;
		mScrollOffset = 0;

		mXPos = 0;
		mYPos = 0;

		invalidate();
	}

	public void
	setListener (EmulatorListener listener, VirtualConsole console)
	{
		mListener = listener;
		mListenerConsole = console;
	}

	protected void
	noticeScroll (int start, int end)
	{
		System.arraycopy(mDeltas, start + 1, mDeltas, start, end - start);
		mDeltas[end] = -1;
	}

	public void
	resize (int width, int height)
	{
		short[] ndata = new short[width * height * STRIDE];

		mBlankLine = new short[width * STRIDE];
		for (int i = 0; i < width * STRIDE; i += STRIDE) {
			mBlankLine[i] = 0 /* empty slot */;
			mBlankLine[i+1] = 0;
		}
		for (int i = 0; i < height; i++) {
			System.arraycopy(mBlankLine, 0, ndata, i * width * STRIDE, width * STRIDE);
		}

		/* try to salvage as much of the visble screen as we can, anchored at
		 * the lower-left corner...
		 */
		int maxh = height;
		int maxw = width;
		if (mRows < maxh) {
			maxh = mRows;
		}
		if (mCols < maxw) {
			maxw = mCols;
		}
		int nskip = height - maxh;
		int skip = mRows - maxh;

		/* if the screen is getting shorter, find rows below the cursor that
		 * are completely blank and trim those first.
		 */
		if ((skip > 0) && (mYPos < mRows - 1)) {
			int potentialRow = mRows - 1;
			while ((skip > 0) && (mYPos < potentialRow)) {
				/* detecting blank rows is irritating. */
				boolean match = true;
				for (int i = 0; i < mCols * STRIDE; i++) {
					if (mScreenBuffer[potentialRow * mCols * STRIDE + i] != 0) {
						/* something written on this row: bail */
						match = false;
						break;
					}
				}
				if (! match) {
					break;
				}
				skip--;
				potentialRow--;
			}
		}
		for (int i = 0; i < maxh; i++) {
			System.arraycopy(mScreenBuffer, (skip + i) * mCols * STRIDE, ndata,
							 (nskip + i) * width * STRIDE, maxw * STRIDE);
		}

		synchronized (this) {
			mScreenBuffer = ndata;
			mRows = height;
			mCols = width;
			if (nskip > 0) {
				mYPos += nskip;
			} else {
				mYPos -= skip;
			}

			// FIXME: adjust the scroll buffer too, if applicable
			// (right now, we're just erasing the scrollback -- clearly the wrong thing to do)
			mScrollOffset = mScrollUsed = 0;
			if (mScrollMax > 0) {
				mScrollBuffer = new short[width * mScrollMax * STRIDE];
			}

			mDirty = true;
			mDeltas = new short[height];
			for (int i = 0; i < height; i++) {
				mDeltas[i] = -1;
			}
		}
	}

	public int
	getCursorX ()
	{
		return (mXPos >= mCols) ? mCols - 1 : mXPos;
	}

	public int
	getCursorY ()
	{
		return mYPos;
	}

	public int
	getScrollSize()
	{
		return (mScrollUsed / (mCols * STRIDE));
	}

	protected void
	insertChars (int y, int x, int count)
	{
		synchronized (this) {
			if (count > mCols - x) {
				count = mCols - x;
			}

			int base = (y * mCols + x) * STRIDE;
			int copy = mCols - (x + count);
			if (copy > 0) {
				System.arraycopy(mScreenBuffer, base, mScreenBuffer, base + count * STRIDE, copy * STRIDE);
			}
			System.arraycopy(mBlankLine, 0, mScreenBuffer, base, count * STRIDE);

			mDirty = true;
			mDeltas[y] = -1;
		}
	}

	protected void
	insertChars (int count)
	{
		insertChars(mYPos, mXPos, count);
	}

	protected void
	deleteChars (int y, int x, int count)
	{
		synchronized (this) {
			if (count > mCols - x) {
				count = mCols - x;
			}

			int base = (y * mCols + x) * STRIDE;
			int copy = mCols - (x + count);
			if (copy > 0) {
				System.arraycopy(mScreenBuffer, base + count * STRIDE, mScreenBuffer, base, copy * STRIDE);
			}
			System.arraycopy(mBlankLine, 0, mScreenBuffer, base + copy * STRIDE, count * STRIDE);

			mDirty = true;
			mDeltas[y] = -1;
		}
	}

	protected void
	deleteChars (int count)
	{
		deleteChars(mYPos, mXPos, count);
	}

	/** clear the next N spaces */
	protected void
	clearChars (int count)
	{
		synchronized (this) {
			if (count > mCols - mXPos) {
				count = mCols - mXPos;
			}
			System.arraycopy(mBlankLine, 0, mScreenBuffer, (mYPos * mCols + mXPos) * STRIDE, count * STRIDE);

			mDirty = true;
			mDeltas[mYPos] = -1;
		}
	}

	protected void
	clearToEOL ()
	{
		synchronized (this) {
			System.arraycopy(mBlankLine, 0, mScreenBuffer, (mYPos * mCols + mXPos) * STRIDE,
							 (mCols - mXPos) * STRIDE);

			mDirty = true;
			mDeltas[mYPos] = -1;
		}
	}

	protected void
	clearToSOL ()
	{
		synchronized (this) {
			System.arraycopy(mBlankLine, 0, mScreenBuffer, mYPos * mCols * STRIDE, mXPos * STRIDE);

			mDirty = true;
			mDeltas[mYPos] = -1;
		}
	}

	protected void
	clearLine (int y)
	{
		synchronized (this) {
			System.arraycopy(mBlankLine, 0, mScreenBuffer, y * mCols * STRIDE, mCols * STRIDE);

			mDirty = true;
			mDeltas[y] = -1;
		}
	}

	protected void
	copyLine (int yFrom, int yTo)
	{
		synchronized (this) {
			System.arraycopy(mScreenBuffer, yFrom * mCols * STRIDE, mScreenBuffer, yTo * mCols * STRIDE,
							 mCols * STRIDE);

			mDirty = true;

			// for now, we guarantee that a delta from another line is only scrolling up, not down.
			// this makes the redraw logic easier, even though it means scroll-down operations are
			// pretty slow.
			if (yFrom < yTo) {
				mDeltas[yTo] = -1;
			} else {
				mDeltas[yTo] = mDeltas[yFrom];
			}
		}
	}

	protected void
	saveLineToScrollback (int y)
	{
		synchronized (this) {
			if (mScrollMax <= 0) {
				return;
			}

			System.arraycopy(mScreenBuffer, y * mCols * STRIDE, mScrollBuffer, mScrollOffset, mCols * STRIDE);
			mScrollOffset += mCols * STRIDE;
			if (mScrollOffset >= mScrollBuffer.length) {
				mScrollOffset = 0;
			}
			if (mScrollUsed < mScrollBuffer.length) {
				mScrollUsed += mCols * STRIDE;
			}

			mDirty = true;
		}

		if (mListener != null) {
			mListener.eventScrolled(mListenerConsole, 1);
		}
	}

	protected void
	scrollUp (int start, int end)
	{
		synchronized (this) {
			for (int i = start; i < end; i++) {
				System.arraycopy(mScreenBuffer, (i + 1) * mCols * STRIDE, mScreenBuffer, i * mCols * STRIDE,
								 mCols * STRIDE);
			}
			System.arraycopy(mBlankLine, 0, mScreenBuffer, end * mCols * STRIDE, mCols * STRIDE);

			mDirty = true;
			noticeScroll(start, end);
		}
	}

	protected void
	scrollDown (int start, int end)
	{
		synchronized (this) {
			for (int i = end; i > start; i--) {
				System.arraycopy(mScreenBuffer, (i - 1) * mCols * STRIDE, mScreenBuffer, i * mCols * STRIDE,
								 mCols * STRIDE);
			}
			System.arraycopy(mBlankLine, 0, mScreenBuffer, start * mCols * STRIDE, mCols * STRIDE);

			mDirty = true;

			// for now, we guarantee that a delta from another line is only scrolling up, not down.
			// this makes the redraw logic easier, even though it means scroll-down operations are
			// pretty slow.
			for (int i = start; i <= end; i++) {
				mDeltas[i] = -1;
			}
		}
	}

	protected void
	flashLED (int red, int green, int blue)
	{
		synchronized (this) {
			if (mTimer != null) {
				mTimer.resetDelay(LED_DELAY);
			} else {
				mTimer = new Timer(LED_DELAY, false, this);
				mTimer.start();
			}

			Hardware.setLEDRGB(red, green, blue);
		}
	}

	// only timer events
	public boolean
	receiveEvent (Event e)
	{
		if (e.type == Event.EVENT_TIMER) {
			synchronized (this) {
				mTimer = null;
				Hardware.setLEDRGB(0, 0, 0);
			}
			return true;
		} else {
			return super.receiveEvent(e);
		}
	}

	protected void
	putChar (int c)
	{
		synchronized (this) {
			int loc = (mYPos * mCols + mXPos) * STRIDE;
			mScreenBuffer[loc] = (short) c;
			mScreenBuffer[loc + 1] = mAttrib;

			mXPos++;

			mDirty = true;
			mDeltas[mYPos] = -1;
		}
	}

	public boolean
	isDirty ()
	{
		synchronized (this) {
			return mDirty;
		}
	}

	public void
	resetDirty ()
	{
		synchronized (this) {
			mDirty = false;
			for (int i = 0; i < mDeltas.length; i++) {
				mDeltas[i] = (short)i;
			}
		}
	}

	public void
	invalidate ()
	{
		synchronized (this) {
			mDirty = true;
			for (int i = 0; i < mDeltas.length; i++) {
				mDeltas[i] = -1;
			}
		}
	}

	/** Call only when synchronized on this EmulatorEngine! */
	public boolean
	checkDirty (short[] deltas)
	{
		boolean result = mDirty;
		mDirty = false;

		if (result) {
			System.arraycopy(mDeltas, 0, deltas, 0, mDeltas.length);
			for (int i = 0; i < mDeltas.length; i++) {
				mDeltas[i] = (short)i;
			}
		}
		return result;
	}

	/** Returns the character and attribute flags for a given cell.
	 * This is rather ineffecient; consider {@link #getLine(int,VirtualLine)}.
	 * @param y row on screen (0 through <code>mRows - 1</code>), or scrollback
	 *     buffer (numbered from <code>-1</code> representing the most recently
	 *     scrolled-off line, back to <code>-mScrollUsed</code>)
	 * @param x column (0 through <code>mCols - 1</code>)
	 * @param cell an allocated Cell object, which will be filled in with the
	 *     requested cell's character glyph and attribute flags
	 */
	public void
	getChar (int y, int x, Cell cell)
	{
		if (y >= 0) {
			int loc = (y * mCols + x) * STRIDE;
			cell.glyph = ((int) mScreenBuffer[loc]) & 0xffff;
			cell.attr = ((int) mScreenBuffer[loc+1]) & 0xffff;
		} else {
			int line = -y;
			if (line > (mScrollUsed / (mCols * STRIDE))) {
				line = mScrollUsed / (mCols * STRIDE);
			}
			int loc = mScrollOffset - (mCols * line + x) * STRIDE;
			if (loc < 0) {
				loc += mScrollBuffer.length;
			}
			cell.glyph = ((int) mScrollBuffer[loc]) & 0xffff;
			cell.attr = ((int) mScrollBuffer[loc+1]) & 0xffff;
		}
	}

	/** Returns the character and attribute information for a terminal line
	 * (in scrollback or on-screen).
	 * @param y row on screen (0 through <code>mRows - 1</code>), or scrollback
	 *     buffer (numbered from <code>-1</code> representing the most recently
	 *     scrolled-off line, back to <code>-mScrollUsed</code>)
	 * @param vl VirtualLine object which will be filled in: <code>text</code>
	 *     will contain the byte array of character cells, and <code>attr</code>
	 *     will contain the corresponding attribute flags
	 */
	public void
	getLine (int y, VirtualLine vl)
	{
		short[] buf;
		int loc;

		if (y >= 0) {
			loc = y * mCols * STRIDE;
			buf = mScreenBuffer;
		} else {
			int line = -y;
			if (line > (mScrollUsed / (mCols * STRIDE))) {
				line = mScrollUsed / (mCols * STRIDE);
			}
			loc = mScrollOffset - mCols * line * STRIDE;
			if (loc < 0) {
				loc += mScrollBuffer.length;
			}
			buf = mScrollBuffer;
		}

		for (int i = 0; i < mCols; i++) {
			vl.text[i] = (char)buf[loc];
			vl.attr[i] = buf[loc+1];
			loc += STRIDE;
		}
	}

	/** Feed a character into the emulator.
	 * This method only adds the character to the trace buffer.
	 * It doesn't handle any display translation at all.  Subclasses that
	 * implement an emulator should call {@link putChar(int)} themselves.
	 */
	public final void
	write (int c)
	{
		if (c > 0) {
			if (TRACE_BUFFER) {
				mTraceBuffer[mTraceBufferOffset++] = (byte)c;
				if (mTraceBufferOffset >= mTraceBuffer.length) {
					mTraceBufferOffset = 0;
				}
			}
		}
		emulatorWrite(c);
	}

	/** Feed a string directly into the emulator.
	 * No character decoding is done.
	 */
	public final void
	write (String s)
	{
		write(s.toCharArray());
	}

	/** Feed a char[] directly into the emulator.
	 * No character decoding is done.
	 */
	public final void
	write (char[] c)
	{
		for (int i = 0; i < c.length; i++) {
			write(c[i]);
		}
	}

	/** Feed bytes into the emulator.
	 * The bytes are decoded into characters using the current
	 * CharacterDecoder (Latin-1 if none is set).
	 */
	public final void
	write (byte[] buffer, int offset, int length)
	{
		for (int i = 0; i < length; i++) {
			if (mDecoder != null) {
				mDecoder.feed(buffer[offset+i]);
				int c = mDecoder.next();
				while (c >= 0) {
					write(c);
					c = mDecoder.next();
				}
			} else {
				/* no character decoder: assume Latin-1 */
				write((int)buffer[offset+i] & 0xff);
			}
		}
	}

	public final void
	setCharacterDecoder (String name)
	{
		mDecoder = CharacterDecoder.getDecoder(name);
	}


	abstract protected void	emulatorWrite (int c);
	abstract protected String getKeyEncoding (int key);
	abstract protected int getColor (int index);

	// 16 colors, then: cursor, foreground, background
	static final int CURSOR_COLOR = 16;
	static final int FOREGROUND_COLOR = 17;
	static final int BACKGROUND_COLOR = 18;


	private void
	dumpHex (byte[] buffer, int start)
	{
		StringBuffer hexline = new StringBuffer();
		StringBuffer charline = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			byte b = buffer[(start + i) % buffer.length];
			hexline.append(Integer.toHexString((b >> 4) & 0x0f).toUpperCase());
			hexline.append(Integer.toHexString(b & 0x0f).toUpperCase());
			hexline.append(' ');
			if ((b >= 0x20) && (b <= 0x7e)) {
				charline.append((char)b);
			} else {
				charline.append('.');
			}

			if ((i & 15) == 15) {
				System.err.println("    " + hexline.toString() + " " + charline.toString());
				hexline = new StringBuffer();
				charline = new StringBuffer();
			}
		}

		if (hexline.length() > 0) {
			while (hexline.length() < 48) {
				hexline.append(" ");
			}
			System.err.println("    " + hexline.toString() + " " + charline.toString());
		}
	}

	public void
	dumpTraceBuffer ()
	{
		if (! TRACE_BUFFER) {
			System.err.println("--> Hey, you need to turn on trace first.  (edit EmulatorEngine.java)");
			return;
		}
		System.err.println("Trace of vt100 input:");
		dumpHex(mTraceBuffer, mTraceBufferOffset);
	}


	protected short[] mScreenBuffer;
	protected short[] mBlankLine;

	protected short[] mScrollBuffer;
	protected int mScrollUsed;
	protected int mScrollOffset;
	protected int mScrollMax;

	protected boolean mDirty = true;
	protected short[] mDeltas;
	protected EmulatorListener mListener = null;
	protected VirtualConsole mListenerConsole = null;
	protected Timer mTimer = null;
	protected CharacterDecoder mDecoder = null;

	// package
	int mRows;
	int mCols;
	int mXPos;
	int mYPos;
	short mAttrib;

	protected byte[] mTraceBuffer;
	protected int mTraceBufferOffset;

	// how many shorts are used per character cell
	// (1 character short + 1 attribute short) = 2
	static protected final int STRIDE = 2;

	// milliseconds to leave the LED on during an LED flash
	static protected final int LED_DELAY = 200;

	// these have to fit into a short
	static final int REVERSE_ATTRIB = 0x100;
	static final int UNDERLINE_ATTRIB = 0x200;
	static final int SET_FG_ATTRIB = 0x400;
	static final int SET_BG_ATTRIB = 0x800;
	static final int BOLD_ATTRIB = 0x1000;
	static final int BLINK_ATTRIB = 0x2000;
	static final int FG_COLOR_ATTRIB_MASK = 0x07;
	static final int FG_COLOR_ATTRIB_SHIFT = 0;
	static final int BG_COLOR_ATTRIB_MASK = 0x38;
	static final int BG_COLOR_ATTRIB_SHIFT = 3;

	// keys that can be emulated
	static final int KEY_LEFT = 1;
	static final int KEY_RIGHT = 2;
	static final int KEY_UP = 3;
	static final int KEY_DOWN = 4;
	static final int KEY_PAGE_UP = 5;
	static final int KEY_PAGE_DOWN = 6;
	static final int KEY_HOME = 7;
	static final int KEY_END = 8;
	static final int KEY_DELETE = 9;
	static final int KEY_INSERT = 10;

	static final int KEY_MIN = 1;
	static final int KEY_MAX = 10;
}
