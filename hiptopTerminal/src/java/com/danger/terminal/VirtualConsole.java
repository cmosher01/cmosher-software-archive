/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.io.InputStream;

import danger.ui.Bitmap;
import danger.ui.Font;
import danger.ui.Pen;
import danger.ui.Rect;
import danger.util.DEBUG;
import danger.util.Pasteboard;


public class VirtualConsole implements Runnable, TerminalResources
{
	public
	VirtualConsole (String fontname, int width, int height, int scroll_lines)
	{
		mWidth = width;
		mHeight = height;
		setFont(fontname);
		mFontName = fontname;
		mColumns = width / mCCW;
		mRows = height / mCCH;
		mDeltas = new short[mRows];
		mTerminalEmulator = new VT100Engine(mColumns, mRows, scroll_lines);
	}

	public void
	resize (int width, int height)
	{
		mVisibleTop = 0;
		mWidth = width;
		mHeight = height;
		mColumns = width / mCCW;
		mRows = height / mCCH;
		mDeltas = new short[mRows];
		if (mTerminalEmulator != null) {
			mTerminalEmulator.resize(mColumns, mRows);
		}
	}

    public final boolean
	hasData()
	{
        return mHasData;
    }

	// store the font here since there's one for each connection
	public void
	setFont (String fontname)
	{
		if (fontname == mFontName) {
			return;
		}

		mFont = Font.findFont(fontname);
		mCCW = mFont.charWidth('M');
		mCCH = mFont.getAscent() + mFont.getDescent();
		mCCA = mFont.getAscent();
		mFontName = fontname;

		// need to redraw, and clear any existing scrollbar
		resize(mWidth, mHeight);
		// FIXME -- this is a bit ugly:
		Terminal.instance().getTerminalWindow().hideScrollbar();
		if (mTerminalEmulator != null) {
			mTerminalEmulator.invalidate();
		}
	}

	public final String
	getFontName ()
	{
		return mFontName;
	}

	public final Font
	getFont ()
	{
		return mFont;
	}

	public final void
	setEmulator (int emulator)
	{
		mEmulator = emulator;
	}

	public boolean
	getWrapMode ()
	{
		return mTerminalEmulator.getWrapMode();
	}

	public void
	setWrapMode (boolean wrap)
	{
		mTerminalEmulator.setWrapMode(wrap);
	}

	public String
	getName ()
	{
		return mName;
	}

	public void
	setName (String name)
	{
		mName = name;
		mTitle = name;
	}

	public String
	getTitle ()
	{
		return mTitle;
	}

	public void
	setTitle (String title)
	{
		mTitle = title;
	}

	public void
	setAutoLFMode (boolean autoLFMode)
	{
		mTerminalEmulator.setAutoLFMode(autoLFMode);
	}

	public boolean
	getAutoLFMode ()
	{
		return mTerminalEmulator.getAutoLFMode();
	}

	public void
	setCodec (String codec)
	{
		mTerminalEmulator.setCharacterDecoder(codec);
	}

	public boolean
	isDirty ()
	{
		return mTerminalEmulator.isDirty();
	}

	public void
	insert (byte[] buffer, int offset, int length)
	{
		mTerminalEmulator.write(buffer, offset, length);
		synchronized (this) {
			mHasData = true;
		}
	}

	public void
	insert (String s)
	{
		mTerminalEmulator.write(s);
		synchronized (this) {
			mHasData = true;
		}
	}

	public void
	insert (char[] c)
	{
		mTerminalEmulator.write(c);
		synchronized (this) {
			mHasData = true;
		}
	}

	public void
	insert (int c)
	{
		mTerminalEmulator.write(c);
		synchronized (this) {
			mHasData = true;
		}
	}

	public void
	reset ()
	{
		stopScrolling();
		mTerminalEmulator.reset();
		// force redraw:
		mTerminalEmulator.resize(mColumns, mRows);
		mHasData = false;
	}

	public void
	dumpTraceBuffer ()
	{
		mTerminalEmulator.dumpTraceBuffer();
	}

	public void
	setInputStream (InputStream is)
	{
		mInputStream = is;
	}

	public void
	startThread ()
	{
		mThread = new Thread(this, "terminal:VirtualConsole");
		mThread.start();
	}

	public void
	run ()
	{
		byte[] buffer = new byte[2048];
		try {
			for (;;) {
				int n = mInputStream.read(buffer);
				if (n > 0) {
					insert(buffer, 0, n);
				}
			}
		} catch (Exception exc) {
			DEBUG.p("exception in console! " + exc.toString());
		}

		mInputStream = null;
	}

	public void
	scrollUp (int how)
	{
		int size = mTerminalEmulator.getScrollSize();

		if (size + mVisibleTop == 0) {
			// at top already
			return;
		}
		switch (how) {
		case SCROLL_ONE_LINE:
			mVisibleTop -= 1;
			break;
		case SCROLL_ONE_PAGE:
			mVisibleTop -= mRows-1;
			break;
		case SCROLL_MAX:
			mVisibleTop = -size;
			break;
		}
		if (size + mVisibleTop < 0) {
			mVisibleTop = -size;
		}
	}

	public void
	scrollDown (int how)
	{
		if (mVisibleTop == 0) {
			// at bottom already
			return;
		}

		switch (how) {
		case SCROLL_ONE_LINE:
			mVisibleTop += 1;
			break;
		case SCROLL_ONE_PAGE:
			mVisibleTop += mRows-1;
			break;
		case SCROLL_MAX:
			mVisibleTop = 0;
			break;
		}
		if (mVisibleTop >= 0) {
			// stop scrolling
			mVisibleTop = 0;
		}
	}

	public void
	stopScrolling ()
	{
		if (mVisibleTop < 0) {
			mVisibleTop = 0;
			mTerminalEmulator.invalidate();
		}
	}

	public boolean 
	isScrolledBack ()
	{
		return mVisibleTop < 0;
	}

	/* these 3 functions are for use in drawing a scrollbar. */
	public int
	getScrollbarRange ()
	{
		return mTerminalEmulator.getScrollSize() + mRows;
	}

	public int
	getScrollbarPageSize ()
	{
		return mRows;
	}

	public int
	getScrollbarPosition ()
	{
		return mTerminalEmulator.getScrollSize() + mVisibleTop;
	}

	/* The selection string is marked by start and end anchors stored in
	 * mAnchor as two pairs of (row, column).
	 */
	public String
	getSelectionString ()
	{
		sortAnchors();
		EmulatorEngine.Cell cell = new EmulatorEngine.Cell();

		String s = "";

		/* Its easier to pull this out line by line because lines
		 * aren't nececarilly stored in order
		 */
		for (int line = mAnchors[ANCHOR1_ROW]; line <= mAnchors[ANCHOR2_ROW]; line++) {
			int fillSpaces = 0;

			int startChar = 0, endChar = mColumns-1;
			if (line == mAnchors[ANCHOR1_ROW]) {
				startChar = mAnchors[ANCHOR1_COLUMN];
			}
			if (line == mAnchors[ANCHOR2_ROW]) {
				endChar = mAnchors[ANCHOR2_COLUMN] - 1;
			}

			for (int c=startChar; c<=endChar; c++) {
				mTerminalEmulator.getChar(line, c, cell);

				if (cell.glyph == 0x00) {
					// this is a null character, lets keep
					// track of it, if we get a non-null
					// character after this one we'll
					// print a space for it
					fillSpaces++;
					continue;
				}
				if (fillSpaces > 0) {
					// we've seen null characters, lets
					// put some spaces
					for (int i=0; i<fillSpaces; i++) {
						s = s + " ";
					}
					fillSpaces = 0;
				}
				s = s + (char) cell.glyph;
			}

			if (fillSpaces > 0) {
				// so, at this point if fillSpaces > 0 then
				// the line ended with some null characters
				// that we want to discard, however we do want
				// to terminate the line
				s = s + "\n";
			}
		}

		DEBUG.p ("selection: "+s);

		return s;
	}

	/** Make sure ANCHOR1 is the start and ANCHOR2 is the end,
	 * reversing their order if necessary.
	 */
	protected void
	sortAnchors ()
	{
		if ((mAnchors[ANCHOR1_ROW] < mAnchors[ANCHOR2_ROW]) ||
			((mAnchors[ANCHOR1_ROW] == mAnchors[ANCHOR2_ROW]) &&
			 (mAnchors[ANCHOR1_COLUMN] < mAnchors[ANCHOR2_COLUMN]))) {
				return;
		}

		swapAnchors();
	}

	protected void
	swapAnchors ()
	{
		int temp = mAnchors[ANCHOR1_ROW];
		mAnchors[ANCHOR1_ROW] = mAnchors[ANCHOR2_ROW];
		mAnchors[ANCHOR2_ROW] = temp;
		temp = mAnchors[ANCHOR1_COLUMN];
		mAnchors[ANCHOR1_COLUMN] = mAnchors[ANCHOR2_COLUMN];
		mAnchors[ANCHOR2_COLUMN] = temp;
		mAnchorsSwapped = !mAnchorsSwapped;
	}

	public void
	anchorScroll (int direction, int how)
	{
		if (mAnchorsSwapped) {
			swapAnchors();
		}
							   
		// always move second anchor
		switch (direction) {
		case SCROLL_UP:
			mAnchors[ANCHOR2_ROW]--;
			break;
		case SCROLL_DOWN:
			mAnchors[ANCHOR2_ROW]++;
			break;
		case SCROLL_LEFT:
			mAnchors[ANCHOR2_COLUMN]--;
			// handle going off the lhs of the screen
			if (mAnchors[ANCHOR2_COLUMN] < 0) {
				mAnchors[ANCHOR2_ROW]--;
				mAnchors[ANCHOR2_COLUMN] = mColumns-1;
			}
			break;
		case SCROLL_RIGHT:
			mAnchors[ANCHOR2_COLUMN]++;
			// handle going off the rhs of the screen
			if (mAnchors[ANCHOR2_COLUMN] >= mColumns) {
				mAnchors[ANCHOR2_ROW]++;
				mAnchors[ANCHOR2_COLUMN] = 0;
			}
			break;
		case SCROLL_NONE:
			// the anchors have been manually changed,
			// ensure clipping is applied
			break;
		default:
			// unexpected
			return;
		}

		// clip to the top of the scrollback
		int size = mTerminalEmulator.getScrollSize();
		if (size + mAnchors[ANCHOR2_ROW] < 0) {
			mAnchors[ANCHOR2_ROW] = -size;
		}

		if (mAnchors[ANCHOR2_ROW] >= mRows) {
			mAnchors[ANCHOR2_ROW] = mRows-1;
		}

		// ensure the currently active anchor is on screen
		// that will of course always be ANCHOR2
		if (mAnchors[ANCHOR2_ROW] < mVisibleTop || 
				mAnchors[ANCHOR2_ROW] >= mVisibleTop+mRows) {
			mVisibleTop = mAnchors[ANCHOR2_ROW] - (mRows/2);
			if (mVisibleTop < -size) mVisibleTop = -size;
			if (mVisibleTop > 0) mVisibleTop = 0; // Justin Cayes
		}

		// if we're moving our first anchor then the
		// it needs to be updated to equal the second
		if (!mSecondAnchor) {
			mAnchors[ANCHOR1_ROW] = mAnchors[ANCHOR2_ROW];
			mAnchors[ANCHOR1_COLUMN] = mAnchors[ANCHOR2_COLUMN];
		}
	}

	public void
	anchorAbort ()
	{
		// reset back to bse state
		mInCopyMode = false;
		mSecondAnchor = false;
	}

	public void
	anchorNextState ()
	{
		// move to the next state in our copy state machine
		if (!mInCopyMode) {
			// start copying
			mAnchors[ANCHOR1_ROW] = mTerminalEmulator.getCursorY();
			mAnchors[ANCHOR1_COLUMN] = mTerminalEmulator.getCursorX();
			mAnchors[ANCHOR2_ROW] = mAnchors[ANCHOR1_ROW];
			mAnchors[ANCHOR2_COLUMN] = mAnchors[ANCHOR1_COLUMN];
			mInCopyMode = true;
			mSecondAnchor = false;
			mAnchorsSwapped = false;
		} else if (mSecondAnchor) {
			// copy is complete
			mInCopyMode = false;
			Pasteboard.setString (getSelectionString ());
		} else {
			mSecondAnchor = true;
		}
	}

	public void
	reverseVideo ()
	{
		int temp = mTerminalEmulator.getColor(EmulatorEngine.FOREGROUND_COLOR);
		mTerminalEmulator.setColor(EmulatorEngine.FOREGROUND_COLOR,
								   mTerminalEmulator.getColor(EmulatorEngine.BACKGROUND_COLOR));
		mTerminalEmulator.setColor(EmulatorEngine.BACKGROUND_COLOR, temp);
	}

	public void
	setListener (EmulatorEngine.EmulatorListener listener)
	{
		mTerminalEmulator.setListener(listener, this);
	}

	public int
	getBackgroundColor ()
	{
		return mTerminalEmulator.getColor(EmulatorEngine.BACKGROUND_COLOR);
	}

	/*
	 * 'line' is the screen line (0 .. mRows-1)
	 */
	protected void
	drawLine (Pen p, int line)
	{
		int cursorx = mTerminalEmulator.getCursorX(), cursory = mTerminalEmulator.getCursorY();
		int cursorColor = mTerminalEmulator.getColor(EmulatorEngine.CURSOR_COLOR);
		int foregroundColor = mTerminalEmulator.getColor(EmulatorEngine.FOREGROUND_COLOR);
		int backgroundColor = mTerminalEmulator.getColor(EmulatorEngine.BACKGROUND_COLOR);

		p.setColor(backgroundColor);
		p.fillRect(0, line * mCCH, mColumns * mCCW, (line + 1) * mCCH);
		p.setColor(foregroundColor);

		mTerminalEmulator.getLine(line + mVisibleTop, mVLine);

		boolean drawingSelection = false;
		int startColumn = 0, endColumn = 0;
		if (mInCopyMode) {
			// assume already sorted
			int startRow = mAnchors[ANCHOR1_ROW] - mVisibleTop;
			int endRow = mAnchors[ANCHOR2_ROW] - mVisibleTop;
			if ((startRow <= line) && (endRow >= line)) {
				drawingSelection = true;
				startColumn = (startRow == line) ? mAnchors[ANCHOR1_COLUMN] : 0;
				endColumn = (endRow == line) ? mAnchors[ANCHOR2_COLUMN] : mColumns - 1;
			}
		}

		for (int col = 0; col < mColumns; col++) {
			short attr = mVLine.attr[col];

			int fgColor = foregroundColor;
			int bgColor = backgroundColor;

			if ((attr & EmulatorEngine.SET_FG_ATTRIB) != 0) {
				if ((attr & EmulatorEngine.BOLD_ATTRIB) != 0) {
					fgColor = mTerminalEmulator.getColor(((attr & EmulatorEngine.FG_COLOR_ATTRIB_MASK) >> EmulatorEngine.FG_COLOR_ATTRIB_SHIFT) | 8);
				} else {
					fgColor = mTerminalEmulator.getColor((attr & EmulatorEngine.FG_COLOR_ATTRIB_MASK) >> EmulatorEngine.FG_COLOR_ATTRIB_SHIFT);
				}
			}
			if ((attr & EmulatorEngine.SET_BG_ATTRIB) != 0) {
				// apparently it's common practice to reinterpret "blink" as "bold background".
				// probably comes from the old DOS machines.
				if ((attr & EmulatorEngine.BLINK_ATTRIB) != 0) {
					bgColor = mTerminalEmulator.getColor(((attr & EmulatorEngine.BG_COLOR_ATTRIB_MASK) >> EmulatorEngine.BG_COLOR_ATTRIB_SHIFT) | 8);
				} else {
					bgColor = mTerminalEmulator.getColor((attr & EmulatorEngine.BG_COLOR_ATTRIB_MASK) >> EmulatorEngine.BG_COLOR_ATTRIB_SHIFT);
				}
			}

			if (!mInCopyMode && (cursorx == col) && (cursory == line + mVisibleTop)) {
				// draw the cursor
				p.setColor(cursorColor);
				p.fillRect(col * mCCW, line * mCCH, (col + 1) * mCCW, (line + 1) * mCCH);
			} else {
				// draw a background color, if present
				boolean swap = false;

				if (drawingSelection && (col >= startColumn) && (col <= endColumn)) {
					// draw the selection with fg and bg swapped
					swap = !swap;
				}
				if ((attr & EmulatorEngine.REVERSE_ATTRIB) != 0) {
					swap = !swap;
				}
				if (swap) {
					int tmp = fgColor;
					fgColor = bgColor;
					bgColor = tmp;
				}
				if (bgColor != backgroundColor) {
					p.setColor(bgColor);
					p.fillRect(col * mCCW, line * mCCH, (col + 1) * mCCW, (line + 1) * mCCH);
				}
			}

			char c = mVLine.text[col];
			// draw null characters as spaces
			if (c == 0) {
				c = ' ';
			}

			p.setColor(fgColor);
			p.drawChar(col * mCCW, line * mCCH + mCCA, c);
			if ((attr & EmulatorEngine.UNDERLINE_ATTRIB) != 0) {
				p.drawLine(col * mCCW, (line + 1) * mCCH - 1, (col + 1) * mCCW - 1, (line + 1) * mCCH - 1);
			}
		}
	}

	private void
	drawDeltas (Bitmap bitmap, short[] deltas)
	{
		Pen p = bitmap.createPen(0, 0);
		p.setFont(mFont);

		int cursory = mTerminalEmulator.getCursorY();
		int len = deltas.length;
		int i;
		boolean invalidateAll = true;
		for (i = 0; i < len; i++) {
			if (deltas[i] >= 0) {
				invalidateAll = false;
			}
		}

		if (invalidateAll) {
			p.setColor(mTerminalEmulator.getColor(EmulatorEngine.BACKGROUND_COLOR));
			p.fillRect(p.getClip());
		}

		for (i = 0; i < len + mVisibleTop; i++) {
			short del = deltas[i];

			if ((del < 0) || (del == mLastCursorY) || (i == mLastCursorY) || (i == cursory) ||
				(del >= len + mVisibleTop)) {
				// redraw line
				drawLine(p, i - mVisibleTop);
			} else if (del != i) {
				/* copy from line in previous buffer.
				 * since we only do this for scrolling up, any copy is
				 * guaranteed to have an origin lower than the destination,
				 * so iterating from top to bottom will avoid conflicts.
				 */
				int topFrom = (del - mVisibleTop) * mCCH;
				int topTo = (i - mVisibleTop) * mCCH;
				int bottomFrom = (del - mVisibleTop + 1) * mCCH;
				int bottomTo = (i - mVisibleTop + 1) * mCCH;
				while ((i+1 < len) && (deltas[i+1] == deltas[i] + 1) && (deltas[i+1] < len + mVisibleTop) &&
					   (deltas[i+1] != mLastCursorY) && (i+1 != mLastCursorY) && (i+1 != cursory)) {
					// multi-line scroll; optimize.
					i++;
					bottomFrom += mCCH;
					bottomTo += mCCH;
				}
				Rect fromRect = new Rect(0, topFrom, mColumns * mCCW, bottomFrom);
				Rect toRect = new Rect(0, topTo, mColumns * mCCW, bottomTo);
				Bitmap.copyBits(bitmap, toRect, bitmap, fromRect);
			}
		}

		mLastCursorY = cursory;
	}

	public void
	paintInto (Bitmap bitmap, boolean force)
	{
		int rows = mRows;
		int visibleTop = mVisibleTop;

		if (mInCopyMode) {
			sortAnchors();
		}

		// FIXME: copy mode always sets force, currently.  that's slow.
		if (force) {
			synchronized (mTerminalEmulator) {
				mTerminalEmulator.resetDirty();

				Pen p = bitmap.createPen(0, 0);
				p.setFont(mFont);

				p.setColor(mTerminalEmulator.getColor(EmulatorEngine.BACKGROUND_COLOR));
				p.fillRect(p.getClip());

				for (int i = 0; i < mRows; i++) {
					drawLine(p, i);
				}
			}
			return;
		}

		if (visibleTop + rows <= 0) {
			// nothing from the active area is on-screen: only scrollback.
			return;
		}

		synchronized (mTerminalEmulator) {
			if (mTerminalEmulator.checkDirty(mDeltas)) {
				drawDeltas(bitmap, mDeltas);
			}
		}
	}


	protected VT100Engine mTerminalEmulator;
	private InputStream mInputStream;
	private int mCCW, mCCH, mCCA;		// extreme acronyms for the width/height/ascent of a character cell
	protected int mRows, mColumns;
	protected int mWidth, mHeight;
	private String mName;
	private String mTitle;
	private boolean mHasData;
	protected Thread mThread;
	private Font mFont;
	private String mFontName;
	private int mEmulator;

	private int mVisibleTop;
	private int mLastCursorY = -1;

	private static EmulatorEngine.VirtualLine mVLine = new EmulatorEngine.VirtualLine();
	private short[] mDeltas = null;

	// FIXME: this should be somewere else? this should certainly have accessors
	public boolean mInCopyMode;
	protected boolean mSecondAnchor;
	protected boolean mHasSelection;
	protected int[] mAnchors = new int[4];
	protected boolean mAnchorsSwapped = false;
	protected static final int ANCHOR1_ROW = 0;
	protected static final int ANCHOR1_COLUMN = 1;
	protected static final int ANCHOR2_ROW = 2;
	protected static final int ANCHOR2_COLUMN = 3;

	public static final int SCROLL_NONE = 0;
	public static final int SCROLL_UP = 1;
	public static final int SCROLL_DOWN = 2;
	public static final int SCROLL_LEFT = 3;
	public static final int SCROLL_RIGHT = 4;

	public static final int SCROLL_ONE_LINE = 1;
	public static final int SCROLL_ONE_PAGE = 2;
	public static final int SCROLL_MAX = 3;

	// magic numbers for terminal emulation
	// (we implement a superset of all three)
	public static final int EMULATE_VT100	= 0;
	public static final int EMULATE_ANSI	= 1;
	public static final int EMULATE_XTERM	= 2;
	public static final int EMULATE_LINUX	= 3;
	public static final String[] EMULATOR_NAMES = { "vt100", "ansi", "xterm", "linux" };
}
