/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */
// vi:ts=4 sw=4

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import danger.app.ComponentVersion;
import danger.ui.Color;
import danger.ui.Screen;


public class VT100Engine extends EmulatorEngine
{
	final boolean DEBUG = false;

	public VT100Engine (int w, int h, int scrollsize)
	{
		super(w, h, scrollsize);

		if (! Screen.isColor()) {
			defaultColorTable[CURSOR_COLOR] = Color.GRAY11;
		}
		colorTable = new int[defaultColorTable.length];
		System.arraycopy(defaultColorTable, 0, colorTable, 0, colorTable.length);

		ansi0 = new VT100InputAnsi0();
		ansi1 = new VT100InputAnsi1();
		ansi2 = new VT100InputAnsi2();
		ansi3 = new VT100InputAnsi3();
		ascii = new VT100InputAscii();
		ignore = new VT100Input();

		ansi_element = new int[16];

		reset();
	}

	public void
	reset ()
	{
		super.reset();

		insert_mode = false;
		auto_wrap = true;

		top_scroll = 0;
		bottom_scroll = mRows;

		cur_ansi_number = 0;

		ansi_elements = 0;
		ansi_reading_number = false;

		input = ascii;

		old_xpos = 0;
		old_ypos = 0;
	}

	public void
	resize (int w, int h)
	{
		// try to keep the scrolling region the same, unless it was the whole screen
		int maxh = h;
		if (mRows < maxh) {
			maxh = mRows;
		}
		int nskip = h - maxh;
		if ((top_scroll != 0) || (bottom_scroll != mRows)) {
			top_scroll += nskip;
			bottom_scroll += nskip;
		} else {
			top_scroll = 0;
			bottom_scroll = h;
		}

		super.resize(w, h);
	}

	public final boolean getWrapMode () { return auto_wrap; }
	public final void setWrapMode (boolean wrap) { auto_wrap = wrap; }

	void
	move_cursor()
	{
		// cursor moved -- mark dirty
		synchronized (this) {
			mDirty = true;
		}
	}

	void
	clear_virtscreen(int mode)
	{
		int i;
		if(DEBUG) System.err.println("CLV " + mode);
		switch(mode){
		case 0:
			clearToEOL();
			for (i = mYPos + 1; i < mRows; i++) {
				clearLine(i);
			}
			break;
		case 1:
			for (i = 0; i < mYPos; i++) {
				clearLine(i);
			}
			break;
		default:
			for (i = 0; i < mRows; i++) {
				clearLine(i);
			}
			break;
		}
	}

	void clear_to_eol(int mode)
	{
		if(DEBUG) System.err.println("CLE " + mode);

		switch(mode) {
		case 0:
			clearToEOL();
			break;
		case 1:
			clearToSOL();
			break;
		default:
			clearLine(mYPos);
			break;
		}
	}

	void position_console(int newy, int newx, boolean rel)
	{
		if (rel) {
			mXPos += newx;
			mYPos += newy;
		} else {
			mXPos = newx;
			mYPos = newy;
		}
		if(mXPos < 0) mXPos = 0;
		if(mXPos >= mCols) mXPos = mCols - 1;
		if(mYPos < 0) mYPos = 0;
		if(mYPos >= mRows) mYPos = mRows - 1;

		move_cursor();
	}

	void scroll_virt_up_at_cursor(boolean up)
	{
		int bottom = bottom_scroll - 1;

		if(DEBUG) System.err.println("SV " + (up ? "up @" : "dn @") + mYPos +
									 "SR["+top_scroll+","+bottom_scroll+"]");

		if ((mYPos < top_scroll) || (mYPos >= bottom_scroll)) {
			return;
		}

		if (up) {
			/* scrolling up */
			scrollUp(mYPos, bottom);
		} else {
			/* scrolling down */
			scrollDown(mYPos, bottom);
		}
	}

	void scroll_virtscreen()
	{
		int i, bottom;
		bottom = bottom_scroll - 1;

		synchronized (this) {
			saveLineToScrollback(top_scroll);
			scrollUp(top_scroll, bottom);
		}
	}

	void set_scroll_region(int low, int high)
	{
		low--;
		if(high >= mRows) high = mRows;
		if(low < 0) low = 0;

		top_scroll = low;
		bottom_scroll = high;

		if(DEBUG) System.err.println("SSR["+low+"-"+high+"]");
	}

	public void
	emulatorWrite (int c)
	{
		if (c > 0) {
			input.write(this, c);
		}
	}

	public String
	getKeyEncoding (int key)
	{
		if ((key < KEY_MIN) || (key > KEY_MAX)) {
			return null;
		}
		if (appmode) {
			return sAppKeyTable[key];
		} else {
			return sKeyTable[key];
		}
	}

	public int
	getColor (int index)
	{
		return colorTable[index];
	}

	public void
	setColor (int index, int color)
	{
		colorTable[index] = color;
		invalidate();
	}

	public final void
	setAutoLFMode (boolean mode)
	{
		auto_lf_mode = mode;
	}

	public final boolean
	getAutoLFMode ()
	{
		return auto_lf_mode;
	}


	static protected final String[] sKeyTable = {
		"",
		"\u001B[D",		// left
		"\u001B[C",		// right
		"\u001B[A",		// up
		"\u001B[B",		// down
		"\u001B[5~",	// page-up
		"\u001B[6~",	// page-down
		"\u001B[1~",	// home
		"\u001B[4~",	// end
		"\u001B[3~",	// delete
		"\u001B[2~",	// insert
	};

	static protected final String[] sAppKeyTable = {
		"",
		"\u001BOD",		// left
		"\u001BOC",		// right
		"\u001BOA",		// up
		"\u001BOB",		// down
		"\u001BO5~",	// page-up
		"\u001BO6~",	// page-down
		"\u001BO1~",	// home
		"\u001BO4~",	// end
		"\u001BO3~",	// delete
		"\u001BO2~",	// insert
	};

	int old_xpos, old_ypos;
	int top_scroll, bottom_scroll;
	int cur_ansi_number;
	int ansi_elements;
	boolean ansi_reading_number;
	int ansi_element[];

	short old_attrib;

	boolean insert_mode;
	boolean auto_lf_mode;
	boolean auto_wrap;
	boolean appmode;

	VT100Input input;

	VT100Input ascii, ansi0, ansi1, ansi2, ignore;
	VT100InputAnsi3 ansi3;

	// ansi colors (approximate): 8 normal, 8 bold, cursor, foreground, background
	int colorTable[];
	int defaultColorTable[] = { Color.BLACK, Color.RED, 0x00cc00, Color.ORANGE,
								Color.BLUE, Color.MAGENTA, Color.CYAN, 0xcccccc,
								Color.GRAY4, Color.PINK, 0x99ff99, Color.YELLOW,
								0x9999ff, Color.VIOLET, 0x99ffff, Color.WHITE,
								// cursor, foreground, background:
								0x00cc00, Color.BLACK, Color.WHITE };
}

class VT100Input
{
	public void
	write(VT100Engine vt, int c)
	{
		vt.input = vt.ascii;
	}
}

class VT100InputAscii extends VT100Input
{
	public void
	write(VT100Engine vt, int c)
	{
		switch(c) {
		case 27: /* escape */
			vt.input = vt.ansi0;
			return;

		case 12:
			vt.clear_virtscreen(2);
			break;

		case 13: /* return = back to start of line */
			vt.mXPos = 0;
			vt.move_cursor();
			break;

		case 10: /* newline */
		case 11: /* vertical tab */
			vt.mYPos ++;
			if(vt.mYPos >= vt.bottom_scroll) {
				if(vt.mYPos == vt.bottom_scroll) vt.scroll_virtscreen();
				vt.mYPos --;
			}
			if (vt.auto_lf_mode) {
				vt.mXPos = 0;
			}
			vt.move_cursor();
			break;

		case 9: /* tab */
			vt.position_console(vt.mYPos, vt.mXPos + (8 - (vt.mXPos & 7)), false);
			break;

		case 8: /* backspace */
			vt.mXPos--;
			if( vt.mXPos < 0) {
				vt.mXPos = vt.mCols - 1;
				vt.mYPos--;
				if(vt.mYPos < 0) vt.mYPos = 0;
			}
			vt.move_cursor();
			break;

		case 7: /* bell */
			if (vt.mListener != null) {
				vt.mListener.eventBell(vt.mListenerConsole);
			}
			break;

		case 14: /* shift to charset G1 */
		case 15: /* shift to charset G0 */
			break;

		default:
			if (c < 0x20) {
				System.err.println("["+c+"]");
				break;
			}

			if (vt.mXPos >= vt.mCols) {
				if(vt.auto_wrap) {
					vt.mXPos = 0;
					vt.mYPos ++;
					if (vt.mYPos == vt.bottom_scroll) {
						vt.mYPos --;
						vt.scroll_virtscreen();
					}
				} else {
					vt.mXPos = vt.mCols - 1;
				}
			}

			if (vt.insert_mode) {
				vt.insertChars(1);
			}

			vt.putChar(c);
			break;
		}

		vt.move_cursor();
	}
}

class VT100InputAnsi0 extends VT100Input
{
	public void
	write(VT100Engine vt, int c)
	{
		switch(c){
		case '=':
		case '>':
				/* enter / exit alt keypad mode */
			break;

		case 27: /* escape again */
			return;

		case '(':
		case ')':
		case '*':
		case '+':
				/* special charset 0 and 1 -- not supported */
			vt.input = vt.ignore;
			return;

		case '[':
			vt.cur_ansi_number = 0;
			vt.ansi_elements = 0;
			vt.ansi_reading_number = false;
			vt.input = vt.ansi1;
			return;

		case ']':
			vt.input = vt.ansi3;
			vt.ansi3.reset();
			return;

		case '7':
			vt.old_attrib = vt.mAttrib;
			vt.old_xpos = vt.mXPos;
			vt.old_ypos = vt.mYPos;
			vt.move_cursor();
			break;

		case '8':
			vt.position_console(vt.old_ypos, vt.old_xpos, false);
			vt.mAttrib = vt.old_attrib;
			break;

		case 'E':
			vt.mXPos = 0;
			vt.move_cursor();

		case 'D':
			vt.mYPos ++;
			if (vt.mYPos >= vt.bottom_scroll) {
				vt.scroll_virtscreen();
				vt.mYPos --;
			}
			vt.move_cursor();
			break;

		case 'M':
			vt.mYPos--;
			if(vt.mYPos < vt.top_scroll) {
				vt.mYPos++;
				vt.scroll_virt_up_at_cursor(false);
			}
			vt.move_cursor();
			break;

		default:
			System.err.println("ansi0? " + c);
		}

		vt.input = vt.ascii;
	}
}

class VT100InputAnsi1 extends VT100Input
{
	public void
	write(VT100Engine vt, int c)
	{
		int temp;

		if ((c >= '0') && (c <= '9')) {
			vt.cur_ansi_number = (vt.cur_ansi_number * 10) + (c - '0');
			vt.ansi_reading_number = true;
			return;
		}

		if (vt.ansi_reading_number || (c == ';')) {
			try {
				vt.ansi_element[vt.ansi_elements] = vt.cur_ansi_number;
				vt.ansi_elements++;
			} catch (Throwable t) {
			}
			vt.ansi_reading_number = false;
		}

		vt.cur_ansi_number = 0;


		switch(c) {
		case '?':
			vt.ansi_elements = 0;
			vt.ansi_reading_number = false;
			vt.input = vt.ansi2;
			return;

		case ';':
			return;

		case 'D': /* move left */
			if(vt.ansi_elements > 0) {
				if(vt.ansi_element[0] == 0) vt.ansi_element[0] = 1;
				vt.position_console(0, -vt.ansi_element[0], true);
			} else {
				vt.position_console(0, -1, true);
			}
			break;

		case 'a':
		case 'C': /* move right */
			if(vt.ansi_elements > 0) {
				if(vt.ansi_element[0] == 0) vt.ansi_element[0] = 1;
				vt.position_console(0, vt.ansi_element[0], true);
			} else {
				vt.position_console(0, 1, true);
			}
			break;

		case 'A': /* move up */
			if(vt.ansi_elements > 0) {
				if(vt.ansi_element[0] == 0) vt.ansi_element[0] = 1;
				vt.position_console(-vt.ansi_element[0], 0, true);
			} else {
				vt.position_console(-1, 0, true);
			}
			break;

		case 'e':
		case 'B': /* move down */
			if(vt.ansi_elements > 0) {
				if(vt.ansi_element[0] == 0) vt.ansi_element[0] = 1;
				vt.position_console(vt.ansi_element[0], 0, true);
			} else {
				vt.position_console(1, 0, true);
			}
			break;

		case '`':
		case 'G':
			temp = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 1;
			if (temp > 0) temp --;
			vt.position_console(vt.mYPos, temp, false);
			break;

		case 'E':
			vt.position_console(vt.mYPos +
								((vt.ansi_elements > 0) ? vt.ansi_element[0] : 1),
								0, false);
			break;

		case 'F':
			vt.position_console(vt.mYPos -
								((vt.ansi_elements > 0) ? vt.ansi_element[0] : 1),
								0, false);
			break;

		case 'd':
			temp = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 1;
			if(temp > 0) temp--;
			vt.position_console(temp, vt.mXPos, false);
			break;

		case 'l':
			if (vt.ansi_elements > 0) {
				int code = vt.ansi_element[0];
				if (code == 4) {
					vt.insert_mode = false;
				} else if (code == 20) {
					vt.auto_lf_mode = false;
				}
			}
			break;

		case 'h':
			if (vt.ansi_elements > 0)  {
				int code = vt.ansi_element[0];
				if (code == 4) {
					vt.insert_mode = true;
				} else if (code == 20) {
					vt.auto_lf_mode = true;
				}
			}
			break;

		case 'f':
		case 'H': {
			int row = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 1;
			int col = (vt.ansi_elements > 1) ? vt.ansi_element[1] : 1;
			if (row > 0) row --;
			if (col > 0) col --;
			vt.position_console(row, col, false);
			break;
		}

		case 'J':
			vt.clear_virtscreen((vt.ansi_elements > 0) ? vt.ansi_element[0] : 0);
			break;

		case 'L': {
			int lines = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 1;
			while (lines > 0) {
				vt.scroll_virt_up_at_cursor(false);
				lines--;
			}
			break;
		}

		case 'M' :{
			int lines = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 1;
			while (lines > 0) {
				vt.scroll_virt_up_at_cursor(true);
				lines--;
			}
			break;
		}

		case '@':
			vt.insertChars((vt.ansi_elements > 0) ? vt.ansi_element[0] : 1);
			break;

		case 'P':
			vt.deleteChars((vt.ansi_elements > 0) ? vt.ansi_element[0] : 1);
			break;

		case 'K':
			vt.clear_to_eol((vt.ansi_elements > 0) ? vt.ansi_element[0] : 0);
			break;

		case 'X':
			// linux
			vt.clearChars((vt.ansi_elements > 0) ? vt.ansi_element[0] : 1);
			break;

		case 'T':
			// xterm mouse tracking; ignore
			break;

		case 's':
			vt.old_xpos = vt.mXPos;
			vt.old_ypos = vt.mYPos;
			break;

		case 'u':
			vt.position_console(vt.old_ypos, vt.old_xpos, false);
			break;

		case 'r': {
			int low = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 1;
			int high = (vt.ansi_elements > 1) ? vt.ansi_element[1] : vt.mRows;
			if(low <= high) vt.set_scroll_region(low, high);
			break;
		}

		case 'g':
			// clear tab stops; ignore
			break;

		case 'n':
			if ((vt.ansi_elements > 0) && (vt.mListener != null)) {
				int code = vt.ansi_element[0];
				if (code == 5) {
					// status report: esc [ 0 n
					vt.mListener.eventRespond(vt.mListenerConsole, "\u001b[0n");
				} else if (code == 6) {
					// cursor position report: esc [ (row) ; (col) R
					StringBuffer out = new StringBuffer("\033[");
					out.append(String.valueOf(vt.mYPos));
					out.append(";");
					out.append(String.valueOf(vt.mXPos));
					out.append("R");
					vt.mListener.eventRespond(vt.mListenerConsole, out.toString());
				} else if (code == 23) {
					// app name report
					StringBuffer out = new StringBuffer("\033]?23;Terminal Monkey ");
					ComponentVersion cv = new ComponentVersion("");
					Terminal.instance().getBundle().getComponentVersion(cv);
					out.append(cv.getVersionString(false));
					out.append("\007");
					vt.mListener.eventRespond(vt.mListenerConsole, out.toString());
				}
			}
			break;

		case 'm': /* set attribute */
			if (vt.ansi_elements == 0) {
				vt.mAttrib = 0;
			}
			for (int i = 0; i < vt.ansi_elements; i++) {
				switch (vt.ansi_element[i]) {
				case 0:
					// reset attrib
					vt.mAttrib = 0;
					break;
				case 1:
					vt.mAttrib |= vt.BOLD_ATTRIB;
					break;
				case 4:
					vt.mAttrib |= vt.UNDERLINE_ATTRIB;
					break;
				case 5:
					vt.mAttrib |= vt.BLINK_ATTRIB;
					break;
				case 7:
					vt.mAttrib |= vt.REVERSE_ATTRIB;
					break;
				case 21:
					vt.mAttrib &= ~vt.BOLD_ATTRIB;
					break;
				case 24:
					vt.mAttrib &= ~vt.UNDERLINE_ATTRIB;
					break;
				case 25:
					vt.mAttrib &= ~vt.BLINK_ATTRIB;
					break;
				case 27:
					vt.mAttrib &= ~vt.REVERSE_ATTRIB;
					break;
				case 38:
					vt.mAttrib |= vt.UNDERLINE_ATTRIB;
					vt.mAttrib &= ~(vt.SET_FG_ATTRIB | vt.FG_COLOR_ATTRIB_MASK);
					break;
				case 39:
					vt.mAttrib &= ~(vt.UNDERLINE_ATTRIB | vt.SET_FG_ATTRIB | vt.FG_COLOR_ATTRIB_MASK);
					break;
				case 49:
					vt.mAttrib &= ~(vt.SET_BG_ATTRIB | vt.BG_COLOR_ATTRIB_MASK);
					break;
				}
				if ((vt.ansi_element[i] >= 30) && (vt.ansi_element[i] <= 37)) {
					vt.mAttrib = (short)((vt.mAttrib & ~vt.FG_COLOR_ATTRIB_MASK) | 
						((vt.ansi_element[i] - 30) << vt.FG_COLOR_ATTRIB_SHIFT));
					vt.mAttrib |= vt.SET_FG_ATTRIB;
				}
				if ((vt.ansi_element[i] >= 40) && (vt.ansi_element[i] <= 47)) {
					vt.mAttrib = (short)((vt.mAttrib & ~vt.BG_COLOR_ATTRIB_MASK) |
						((vt.ansi_element[i] - 40) << vt.BG_COLOR_ATTRIB_SHIFT));
					vt.mAttrib |= vt.SET_BG_ATTRIB;
				}
			}
			break;

		default:
			String s = ("ansi1? " + c);
			for(int i = 0; i < vt.ansi_elements; i++ ){
				s = s + " :"+vt.ansi_element[i];
			}
			System.err.println(s);
		}

		vt.input = vt.ascii;
	}
}

class VT100InputAnsi2 extends VT100Input
{
	public void
	write(VT100Engine vt, int c)
	{
		if((c >= '0') && (c <= '9')) {
			vt.cur_ansi_number = (vt.cur_ansi_number * 10) + (c - '0');
			vt.ansi_reading_number = true;
			return;
		}

		if (vt.ansi_reading_number || (c == ';')) {
			try {
				vt.ansi_element[vt.ansi_elements] = vt.cur_ansi_number;
				vt.ansi_elements++;
			} catch (Throwable t) {
			}
			vt.ansi_reading_number = false;
		}

		vt.cur_ansi_number = 0;

		int op = (vt.ansi_elements > 0) ? vt.ansi_element[0] : 0;

		switch(c) {
		case ';':
			return;

		case 'l':
			switch(op) {
			case 25: /* form feed?! */
				break;

			case 7:
				vt.auto_wrap = false;
				break;

			case 1:
					/* set cursor key mode */
				vt.appmode = false;
				break;

			default:
				System.err.println("ansi2? l " + op);
			}
			break;

		case 'h':
			switch(op) {
			case 25: /* form feed ?! */
				break;

			case 7:
				vt.auto_wrap = true;
				break;

			case 1:
					/* set cursor key mode */
				vt.appmode = true;
				break;

			default:
				System.err.println("ansi2? h " + op);
			}
			break;

		case 'C': {
			if (vt.ansi_elements == 0) {
				// reset all colors to factory defaults
				System.arraycopy(vt.defaultColorTable, 0, vt.colorTable, 0, vt.colorTable.length);
				vt.invalidate();
				break;
			}

			int cmd = vt.ansi_element[0];
			int red = (vt.ansi_elements > 1) ? vt.ansi_element[1] : 0;
			int green = (vt.ansi_elements > 2) ? vt.ansi_element[2] : 0;
			int blue = (vt.ansi_elements > 3) ? vt.ansi_element[3] : 0;
			if (red > 63) {
				red = 63;
			}
			if (green > 63) {
				green = 63;
			}
			if (blue > 63) {
				blue = 63;
			}
			if ((cmd >= 0) && (cmd <= 18)) {
				if (vt.ansi_elements <= 1) {
					vt.colorTable[cmd] = vt.defaultColorTable[cmd];
				} else {
					vt.colorTable[cmd] = new Color(red << 2, green << 2, blue << 2).toPacked();
				}
				vt.invalidate();
			} else if (cmd == 20) {
				vt.flashLED(red << 2, green << 2, blue << 2);
			}
			break;
		}

		default:
			System.err.println("ansi2? " + c + ", " + op);
		}

		vt.input = vt.ascii;

	}
}

class VT100InputAnsi3
	extends VT100Input
{
	private StringBuffer stringArg = new StringBuffer();
	private int numArg;
	private boolean readingNum;

	public void
	reset()
	{
		stringArg.setLength(0);
		numArg = 0;
		readingNum = true;
	}

	public void
	write (VT100Engine vt, int c)
	{
		if (readingNum) {
			if (c == ';') {
				readingNum = false;
			} else if ((c >= '0') && (c <= '9')) {
				numArg = (numArg * 10) + (c - '0');
			} else {
				System.err.println("ansi3? " + c);
				vt.input = vt.ascii;
			}
			return;
		} else if (c != 7) {
			stringArg.append((char) c);
			return;
		}

		/* 0 = icon name and window title */ 
		if (numArg == 1) {
			// set icon title; ignore
		} else if ((numArg == 0) || (numArg == 2)) {
			// set titlebar title
			if (vt.mListener != null) {
				vt.mListener.eventSetTitle(vt.mListenerConsole, stringArg.toString());
			}
		} else if (numArg == 21) {
			// set a marquee message
			if (vt.mListener != null) {
				vt.mListener.eventMarquee(vt.mListenerConsole, stringArg.toString());
			}
		} else {
			System.err.println("ansi3 target? " + numArg + ' ' + stringArg);
		}

		vt.input = vt.ascii;
	}
}
