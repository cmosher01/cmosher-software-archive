/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */
// vi:ts=4 sw=4

// Copyright 2001-2003, Danger, Inc.  All Rights Reserved.
// This file is subject to the Danger, Inc. Sample Code License,
// which is provided in the file SAMPLE_CODE_LICENSE.
// Copies are also available from http://developer.danger.com/

package com.danger.terminal;

import java.util.Enumeration;
import java.util.Vector;

import danger.app.Application;
import danger.app.Event;
import danger.app.Timer;
import danger.helpers.SpecialCharacterHelper;
import danger.ui.Bitmap;
import danger.ui.Color;
import danger.ui.DialogWindow;
import danger.ui.Font;
import danger.ui.MarqueeAlert;
import danger.ui.Menu;
import danger.ui.MenuItem;
import danger.ui.NotificationManager;
import danger.ui.Pen;
import danger.ui.ScreenWindow;
import danger.ui.Scrollbar;
import danger.ui.TextField;
import danger.util.Pasteboard;
import danger.util.format.StringFormat;

// FIXME: should be called TerminalScreen
class TerminalWindow extends ScreenWindow
	implements TerminalResources, TerminalEvents, danger.SystemStrings, EmulatorEngine.EmulatorListener
{
	TerminalWindow (SessionManager sessionManager)
	{
		super();
		Application app = getApplication();
		setTitle(app.getString(STRING_TITLE));
		setIcon(app.getBundle().getSmallIcon());
		
        mSessionManager = sessionManager;
		mDefaultFont = app.getString(DEFAULT_FONT);

		// check every 100ms for changes to the VirtualConsole
		mRefreshTimer = new Timer(100, true, this);
		mRefreshTimer.start();

		mScrollbar = new Scrollbar(true);
		addChild(mScrollbar);

		// set up the (hidden, for now) scrollbar & initialize the backing store
		resize(getWidth(), getHeight());
	}
	
	public void
	show()
	{
		super.show();
		if (mCurrentConsole != null) {
			mCurrentConsole.refreshShow();
		}
	}

	/* package */ void
	toggleTextField (TerminalConsole console)
	{
		if (console == null) {
			return;
		}

		TextField tf = console.getTextField();
		if (tf == null) {
			// open a text field
			tf = new TextField(true, false);
			//tf.setHasBorder(false);
			tf.setFont(console.getFont());
			tf.setSize(getWidth(), 0);
			tf.setPosition(0, getHeight() - tf.getHeight());
			tf.setActivateListener(this, EVENT_LINE_ENTRY);
			if (console == mCurrentConsole) {
				tf.takeFocus(true);
				tf.show();
			}
			addChild(tf);
			synchronized (console) {
				console.setTextField(tf);
				console.resize(getWidth(), getHeight());
			}
		} else {
			// close the text field
			int height = tf.getHeight();
			if (console == mCurrentConsole) {
				tf.hide();
			}
			removeChild(tf);
			synchronized (console) {
				console.setTextField(null);
				console.resize(getWidth(), getHeight());
			}
		}
		refresh(true);
	}

	protected void
	resize (int width, int height)
	{
		mScrollbar.setSize(0, height);
		mScrollbar.setPosition(width - mScrollbar.getWidth(), 0);
		mScrollbar.hide();
		mBackingStore = new Bitmap(width, height);

		// the consoles will send the stty settings if necessary
		Vector cvec = mSessionManager.getConsoleList();
		for (Enumeration e = cvec.elements(); e.hasMoreElements(); ) {
			VirtualConsole c = (VirtualConsole)e.nextElement();
			c.resize(width, height);
		}

		refresh(true);
	}

	void
	toggleFullScreen()
	{
		setFullScreen(!isFullScreen());
		resize(getWidth(), getHeight());
		invalidate();
	}

	protected final void
	send (int c)
	{
		if (mCurrentConsole != null) {
			mCurrentConsole.write((int) c);
		}
	}

	protected final void
	send (char[] c)
	{
		if (mCurrentConsole != null) {
			mCurrentConsole.write(c);
		}
	}

	public boolean
	eventShortcut (char c, Event e)
	{
		boolean inTextEntry = (mCurrentConsole != null) && (mCurrentConsole.getTextField() != null);

		if (!inTextEntry && (c >= 'a') && (c <= 'z')) {
			send(c - 'a' + 1);
			return true;
		}

		switch (c) {
			case '@':
				if (! inTextEntry) {
					// nul aka ctrl-@
					send(0);
					return true;
				}
				break;
			case '9':
			case '1':
				if (! inTextEntry) {
					// escape aka ctrl-[
					send(27);
					return true;
				}
				break;
			case '2':
				if (! inTextEntry) {
					// fs aka ctrl-\
					send(28);
					return true;
				}
				break;
			case '0':
				if (! inTextEntry) {
					// gs aka ctrl-]
					send(29);
					return true;
				}
				break;
			case '6':
				if (! inTextEntry) {
					// rs aka ctrl-^
					send(30);
					return true;
				}
				break;
			case '.':
				if (! inTextEntry) {
					// us aka ctrl-_
					send(31);
					return true;
				}
				break;
			case '\n': {
				toggleTextField(mCurrentConsole);
				return true;
			}
			case 'T': {
				toggleFullScreen();
				return true;
			}
			case 'R': {
				if (mCurrentConsole != null) {
					mCurrentConsole.reverseVideo();
				}
				return true;
			}
			case 'D': {
				if (mCurrentConsole != null) {
					mCurrentConsole.dumpTraceBuffer();
				}
				return true;
			}
			case '\b': {
				// reset terminal
				if (mCurrentConsole != null) {
					mScrollbar.hide();
					mCurrentConsole.reset();
				}
				return true;
			}
			case 'C': {
				// start copy
				if (mCurrentConsole != null) {
					mCurrentConsole.anchorNextState ();
					refresh(true);
				}
				return true;
			}
			case 'V': {
				paste();
				return true;
			}
			case ' ':
				new SpecialCharacterHelper(new FakeEditText()).show();
				break;
		}

		return super.eventShortcut(c, e);
	}
	
	public boolean
	eventKeyDown (char c, Event event)
	{
		// translate these keys before potentially shunting off to a TextEntry field
		// Shift-Alt-/	|
		// Shift-Alt-'	`
		if ((event != null) && (event.modifierIsActive(Event.EVENT_MODIFIER_CAPS))) {
			switch (c) {
			case '\'':
				c = '`';
				break;
			case '/':
				c = '|';
				break;
			}
		}

		if (mCurrentConsole != null && mCurrentConsole.mInCopyMode) {
			if (c == ' ') {
				// treat spacebar the same as wheel-button, for us crusty old
				// 'screen' users (wuff! wuff!)
				mCurrentConsole.anchorNextState ();
				refresh(true);
				// FIXME: play a sound
				return true;
			}

			// if we get a regular key in this mode we
			// should abort copy mode and return to
			// our homes...
			mCurrentConsole.anchorAbort ();
			System.err.println("copy aborted");
			// FIXME: play a sound
			refresh(true);
		}

		if (mCurrentConsole != null) {
			TextField tf = mCurrentConsole.getTextField();
			if (tf != null) {
				return tf.eventKeyDown(c, event);
			}
		}

		// event could be null if this is a forged "key down" event
		if (event != null) {
			if (event.modifierIsActive(Event.EVENT_MODIFIER_CAPS)) {
				switch (c) {
				case 8:
					// shift-del = [DELETE] key
					mCurrentConsole.writeKey(EmulatorEngine.KEY_DELETE);
					return true;
				}
			} else {
				if (c == 8) {
					/* backspace key should send DEL, according to deb standards.
					 * some older machines may still be confused.
					 */
					if (mCurrentConsole.getHostInfo().getBackspaceSendsDel()) {
						c = 127;
					}
				}
			}
		}

		if (mMetaKey) {
			char[] combo = new char[2];
			combo[0] = '\u001b';
			combo[1] = c;
			send(combo);
			mMetaKeySent = true;
		} else {
			send(c);
		}

		return true;
	}

	public boolean
	eventWidgetUp (int widget, Event event)
	{
		// handle copy-mode keystrokes first
		if (mCurrentConsole != null && mCurrentConsole.mInCopyMode) {
			switch (widget) {
			case Event.DEVICE_WHEEL_BUTTON:
				// FIXME: play a sound
				mCurrentConsole.anchorNextState ();
				refresh(true);
				return true;

			case Event.DEVICE_WHEEL_PAGE_UP:
				// FIXME: selection page up
			case Event.DEVICE_MULTIPLE_WHEEL:
				// FIXME: multiple wheel event
			case Event.DEVICE_WHEEL:
				mCurrentConsole.anchorScroll(VirtualConsole.SCROLL_UP, 1);
				refresh(true);
				return true;
			}
		}

		switch (widget) {
		case Event.DEVICE_BUTTON_BACK:
			hide();
			Terminal.instance().returnToSessionList();
			return true;

		case Event.DEVICE_WHEEL_BUTTON:
			mMetaKey = false;
			if (! mMetaKeySent) {
				send(27);
			}
			return true;

		case Event.DEVICE_WHEEL:
			if (mCurrentConsole != null) {
				if (event.modifiers == Event.EVENT_MODIFIER_ALT) {
					mCurrentConsole.scrollUp(VirtualConsole.SCROLL_MAX);
				} else {
					mCurrentConsole.scrollUp(VirtualConsole.SCROLL_ONE_LINE);
				}
				refresh(true);
			}
			return true;

		case Event.DEVICE_WHEEL_PAGE_UP:
			if (mCurrentConsole != null) {
				mCurrentConsole.scrollUp(VirtualConsole.SCROLL_ONE_PAGE);
				refresh(true);
			}
			return true;

		default:
			return super.eventWidgetUp(widget, event);
		}
	}

	public boolean
	eventWidgetDown (int widget, Event event)
	{
		if (mCurrentConsole == null) {
			return super.eventWidgetDown(widget, event);
		}

		// handle copy-mode keystrokes first
		if (mCurrentConsole.mInCopyMode) {
			switch (widget) {
			case Event.DEVICE_WHEEL_PAGE_DOWN:
				// FIXME: selection page up
			case Event.DEVICE_MULTIPLE_WHEEL:
				// FIXME: multiple wheel event
			case Event.DEVICE_WHEEL:
			case Event.DEVICE_ARROW_DOWN:
				mCurrentConsole.anchorScroll(VirtualConsole.SCROLL_DOWN, 1);
				refresh(true);
				return true;

			case Event.DEVICE_ARROW_UP:
				mCurrentConsole.anchorScroll(VirtualConsole.SCROLL_UP, 1);
				refresh(true);
				return true;

			case Event.DEVICE_ARROW_LEFT:
				mCurrentConsole.anchorScroll(VirtualConsole.SCROLL_LEFT, 1);
				refresh(true);
				return true;

			case Event.DEVICE_ARROW_RIGHT:
				mCurrentConsole.anchorScroll(VirtualConsole.SCROLL_RIGHT, 1);
				refresh(true);
				return true;

			case Event.DEVICE_WHEEL_BUTTON:
				// can't we just ignore this?
				return true;
			}
		}

		TextField tf = mCurrentConsole.getTextField();
		if ((tf != null) && ((event.modifiers & Event.EVENT_MODIFIER_MENU_BUTTON) == 0)) {
			if ((widget == Event.DEVICE_ARROW_LEFT) || (widget == Event.DEVICE_ARROW_RIGHT)) {
				return tf.eventWidgetDown(widget, event);
			} else if (widget == Event.DEVICE_ARROW_UP) {
				mCurrentConsole.previousHistory();
				return true;
			} else if (widget == Event.DEVICE_ARROW_DOWN) {
				mCurrentConsole.nextHistory();
				return true;
			}
		}

		switch (widget) {
		case Event.DEVICE_WHEEL_BUTTON:
			mMetaKey = true;
			mMetaKeySent = false;
			return true;
		case Event.DEVICE_WHEEL:
			if (event.modifiers == Event.EVENT_MODIFIER_ALT) {
				mCurrentConsole.scrollDown(VirtualConsole.SCROLL_MAX);
			} else {
				mCurrentConsole.scrollDown(VirtualConsole.SCROLL_ONE_LINE);
			}
			refresh(true);
			return true;
		case Event.DEVICE_WHEEL_PAGE_DOWN:
			mCurrentConsole.scrollDown(VirtualConsole.SCROLL_ONE_PAGE);
			refresh(true);
			return true;
		case Event.DEVICE_ARROW_UP:
			if (event.modifiers == Event.EVENT_MODIFIER_ALT) {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_PAGE_UP);
			} else {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_UP);
			}
			return true;
		case Event.DEVICE_ARROW_DOWN:
			if (event.modifiers == Event.EVENT_MODIFIER_ALT) {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_PAGE_DOWN);
			} else {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_DOWN);
			}
			return true;
		case Event.DEVICE_ARROW_RIGHT:
			if (event.modifiers == Event.EVENT_MODIFIER_MENU_BUTTON) {
				nextConsole();
			} else if (event.modifiers == Event.EVENT_MODIFIER_ALT) {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_END);
			} else {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_RIGHT);
			}
			return true;
		case Event.DEVICE_ARROW_LEFT:
			if (event.modifiers == Event.EVENT_MODIFIER_MENU_BUTTON) {
				previousConsole();
			} else if (event.modifiers == Event.EVENT_MODIFIER_ALT) {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_HOME);
			} else {
				mCurrentConsole.writeKey(EmulatorEngine.KEY_LEFT);
			}
			return true;
		}

		return super.eventWidgetDown(widget, event);
	}

	/* package */ void
	openSession (HostInfo info)
	{
        Vector consoleList = mSessionManager.getConsoleList();
		TerminalConsole console = new TerminalConsole(info.getFont(), getWidth(), getHeight(), info.getScrollback());
		String name = info.getSessionName();

		synchronized (consoleList) {
			int index = 1;
			while (mSessionManager.consoleNameExists(name)) {
				index++;
				name = info.getSessionName() + " [" + index + "]";
			}
			console.setName(name);
		}

		console.connect(info);
		console.setListener(this);
		console.setEmulator(info.getEmulator());
		console.setCodec(info.getCodec());
		switchToConsole(console);
	}

	/* package */ void
	switchToConsole (TerminalConsole console)
	{
		synchronized (this) {
			if (mBlinking) {
				mCurrentConsole.reverseVideo();
				mBlinking = false;
			}

			if (mCurrentConsole != null) {
				TextField tf = mCurrentConsole.getTextField();
				if (tf != null) {
					tf.hide();
				}
			}

			mCurrentConsole = console;

			TextField tf = mCurrentConsole.getTextField();
			if (tf != null) {
				//tf.takeFocus(false);
				tf.show();
			}

			refresh(true);
			Terminal.instance().makeConsoleActive();
			show();
		}
	}

	public boolean
	receiveEvent (Event e)
	{
		if (e.type >= kSwitchConsole && e.type < kSwitchConsole + 64) {
			// Switch console
			switchToConsole(mSessionManager.consoleAt(e.type - kSwitchConsole));
			return true;
		}
		
		switch (e.type) {
		case Event.EVENT_TIMER:
			synchronized (this) {
				if (mCurrentConsole != null) {
					if (mCurrentConsole.isDirty()) {
						refresh(mDelayedRefresh);
						mDelayedRefresh = false;
					}
					if (mBlinking) {
						mCurrentConsole.reverseVideo();
						mBlinking = false;
					}
				}
			}
			return true;

		case TerminalEvents.kOpenHost:
			openSession((HostInfo) e.argument);
			return true;

		case EVENT_CLOSE:
			mCurrentConsole.disconnect();
            hide();
			Terminal.instance().returnToSessionList();
			return true;
			
		case EVENT_SETTINGS:
			if (mCurrentConsole != null) {
				Terminal.instance().openSettings(mCurrentConsole.getHostInfo());
			}
			return true;

		case EVENT_PASTE:
			paste();
			return true;

		case EVENT_COPY:
			mCurrentConsole.anchorNextState ();
			refresh(true);
			return true;

		case EVENT_FONT:
			if (mCurrentConsole != null) {
				mCurrentConsole.setFont(getApplication().getStringArray(FONT_NAMES)[e.data]);
			}
			return true;

        case EVENT_CLEAR_SCREEN:
			if (mCurrentConsole != null) {
				mCurrentConsole.reset();
				eraseView();
			}
            return true;
        
        case EVENT_RECONNECT:
			if (mCurrentConsole != null) {
				mCurrentConsole.reconnect();
			}
            return true;

		case EVENT_SESSION_LIST:
			hide();
			Terminal.instance().returnToSessionList();
			return true;

		case EVENT_HINTS:
			synchronized (this) {
				if (mHelpDialog == null) {
					mHelpDialog = Terminal.instance().getResources().getDialog(ID_HELP_DIALOG);
				}
				mHelpDialog.setListener(this);
				mHelpDialog.show();
			}
			return true;

		case ID_DISMISS_HELP_DIALOG:
			mHelpDialog.hide();
			return true;

		case EVENT_LINE_ENTRY_MODE:
			toggleTextField(mCurrentConsole);
			return true;

		case EVENT_LINE_ENTRY: {
			String text = e.argument.toString();
			if (text.charAt(text.length() - 1) == '\u00a4') {
				// turtle: extremely hacky way to avoid sending a CR
				send(text.substring(0, text.length() - 1).toCharArray());
			} else {
				char[] textBlob = text.toCharArray();
				char[] nBlob = new char[textBlob.length + 1];
				System.arraycopy(textBlob, 0, nBlob, 0, textBlob.length);
				nBlob[textBlob.length] = '\r';
				send(nBlob);
			}
			mCurrentConsole.commitHistory();
			((TextField)e.argument).clear();
			return true;
		}
		}
		
		return super.receiveEvent(e);
	}

	private int
	getCurrentConsoleIndex ()
	{
        return mSessionManager.getConsoleIndex(mCurrentConsole);
	}

	private void
	nextConsole ()
	{
        int i = mSessionManager.getNextConsoleIndex(mCurrentConsole);
        if (i < 0) {
            return;
        }

		if (i == 0) {
			// wrap around to session list
			hide();
			Terminal.instance().returnToSessionList();
		} else {
			sendEvent(kSwitchConsole + i, 0, 0, null);
		}
    }

	private void
	previousConsole ()
	{
		int i = mSessionManager.getPreviousConsoleIndex(mCurrentConsole);
		if (i < 0) {
			return;
		}
		if (getCurrentConsoleIndex() == 0) {
			// wrap around to session list
			hide();
			Terminal.instance().returnToSessionList();
		} else {
			sendEvent(kSwitchConsole + i, 0, 0, null);
		}
	}
	
	public void
	adjustActionMenuState ()
	{
		Menu actionMenu = getActionMenu();
		Application app = getApplication();

		actionMenu.removeAllItems();
		if (mCurrentConsole == null) {
			System.err.println("@@@ should not happen: action menu when current-console = null");
			return;
		}

		actionMenu.addFromResource(app.getResources(), ID_CONSOLE_MENU, this);

		MenuItem item = actionMenu.getItemWithID(ID_CONSOLE_CLOSE);
		item.setTitle(StringFormat.withFormat(item.getTitle(), mCurrentConsole.getName()));

		if (mCurrentConsole.isOpen()) {
			actionMenu.removeItemWithID(ID_CONSOLE_RECONNECT);
			actionMenu.removeItemWithID(ID_CONSOLE_CLEAR);

			Menu fontMenu = actionMenu.getItemWithID(ID_CONSOLE_FONT_MENU).getSubMenu();
			String[] f = getApplication().getStringArray(FONT_NAMES);
			for (int i = 0; i < fontMenu.itemCount(); i++) {
				item = fontMenu.getItem(i);
				item.setEvent(this, EVENT_FONT, i, null);
				if (f[i].equals(mCurrentConsole.getFontName())) {
					item.setChecked(true);
					fontMenu.selectItem(i);
				}
			}
			fontMenu.setDefaultSelectionToFirstMenuItem(false);

			actionMenu.getItemWithID(ID_CONSOLE_LINE_ENTRY).setShortcut('\r');
			if (mCurrentConsole.getTextField() != null) {
				actionMenu.getItemWithID(ID_CONSOLE_LINE_ENTRY).check();
			}

			/* you may notice that the shortcut string for:
			 * "(menu) (shift) C"
			 * looks pretty strange.  i agree with you.  however, i think
			 * it's just that the glyph for "(shift)" is strange.  and that
			 * might be because we're the only app that uses it.
			 */
			char[] carr = new char[5];
			carr[0] = Font.GLYPH_MENU;
			carr[1] = ' ';
			carr[2] = Font.GLYPH_SHIFT;
			carr[3] = ' ';
			carr[4] = 'C';
			actionMenu.getItemWithID(ID_CONSOLE_COPY).setShortcutLabel(new String(carr));
			carr[4] = 'V';
			actionMenu.getItemWithID(ID_CONSOLE_PASTE).setShortcutLabel(new String(carr));
		} else {
			actionMenu.removeItemWithID(ID_CONSOLE_SETTINGS);
			actionMenu.removeItemWithID(ID_CONSOLE_FONT_MENU);
			actionMenu.removeItemWithID(ID_CONSOLE_LINE_ENTRY);
			actionMenu.removeItemWithID(ID_CONSOLE_COPY);
			actionMenu.removeItemWithID(ID_CONSOLE_PASTE);
			if (! mCurrentConsole.hasData()) {
				actionMenu.removeItemWithID(ID_CONSOLE_CLEAR);
			}
		}

		appendActiveConsolesToMenu(actionMenu, mCurrentConsole);
		actionMenu.invalidate();
	}

	public void
	appendActiveConsolesToMenu (Menu actionMenu, TerminalConsole current)
	{
		boolean divided = false;
        Vector consoleList = mSessionManager.getConsoleList();
        
		synchronized (consoleList) {
			int numConsoles = consoleList.size();
			for (int i = 0; i < numConsoles; i++) {
				TerminalConsole console = (TerminalConsole) consoleList.elementAt(i);
				if ((console != null) && (console.isOpen())) {
					if (!divided) {
						actionMenu.addDivider();
						divided = true;
					}
			
					MenuItem item = new MenuItem(console.getName(), kSwitchConsole + i, 0, null, this);
					if (console == current)
						item.check();
				
					actionMenu.addItem(item);
				}
			}			
		}
	}

	public void
	paint (Pen p)
	{
		p.drawBitmap(0, 0, mBackingStore);
		paintChildren(p);
	}

	protected void
	refresh (boolean force)
	{
		if (mCurrentConsole == null) {
			return;
		}

		synchronized (mCurrentConsole) {
			updateScrollbar();
			mCurrentConsole.paintInto(mBackingStore, force);

			setTitle(mCurrentConsole.getTitle());
			if (mCurrentConsole.isOpen()) {
				setIcon(getApplication().getBitmap(ID_CONNECTED_ICON));
			} else {
				setIcon(getApplication().getBitmap(ID_DISCONNECTED_ICON));
			}
		}

		invalidate();
	}

	protected void
	eraseView ()
	{
		Pen p = mBackingStore.createPen(0, 0);
		if (mCurrentConsole != null) {
			p.setColor(mCurrentConsole.getBackgroundColor());
		} else {
			p.setColor(Color.WHITE);
		}
		p.fillRect(0, 0, getWidth(), getHeight());
		invalidate();
	}

	/* Update the scrollbar to reflect the scroll state of the current
	 * VirtualConsole
	 */
	protected void
	updateScrollbar ()
	{
		if (mCurrentConsole == null) {
			return;
		}

		if (!mCurrentConsole.isScrolledBack()) {
			// stop scrolling
			mScrollbar.hide();
		} else {
			mScrollbar.show();
			mScrollbar.setData(mCurrentConsole.getScrollbarPosition(),
							   mCurrentConsole.getScrollbarPageSize(),
							   mCurrentConsole.getScrollbarRange());
			mScrollbar.invalidate();
		}
	}

	// this is kinda hacky.
	public void
	hideScrollbar ()
	{
		mScrollbar.hide();
	}

	/** If the terminal emulator pushed more stuff into the scrollback buffer,
	 * and we're in scroll mode, we need to roll the scrollback up the same
	 * number of lines (if possible).  The effect is that the screen display
	 * stays the same, but the scrollbar thumb rolls up to indicate that more
	 * stuff came in below.
	 */
	public void
	eventScrolled (VirtualConsole console, int lines)
	{
		if (console != mCurrentConsole) {
			// happening on some other console; deal with it later
			return;
		}

		if (console.isScrolledBack()) {
			while (lines > 0) {
				console.scrollUp(VirtualConsole.SCROLL_ONE_LINE);
				lines--;
			}
			// wait for the emulator engine to finish drawing what changed
			mDelayedRefresh = true;
		}
	}

	public void
	eventSetTitle (VirtualConsole console, String title)
	{
		console.setTitle(title);
		if (console == mCurrentConsole) {
			setTitle(title);
		}
	}

	public void
	eventBell (VirtualConsole console)
	{
		synchronized (this) {
			if (console == mCurrentConsole) {
				if (!mBlinking) {
					console.reverseVideo();
				}
				mBlinking = true;
			}
		}
	}

	public void
	eventRespond (VirtualConsole console, String data)
	{
		((TerminalConsole)console).write(data);
	}

	public void
	eventMarquee (VirtualConsole console, String message)
	{
		if (mMarqueeAlert != null) {
			NotificationManager.marqueeAlertRemove(mMarqueeAlert);
		}
		mMarqueeAlert = new MarqueeAlert(message, getApplication().getBundle().getSmallIcon(), 1);
		NotificationManager.marqueeAlertNotify(mMarqueeAlert);
	}

	private void 
	paste ()
	{
		if (mCurrentConsole == null) {
			return;
		}

		TextField tf = mCurrentConsole.getTextField();
		if (tf == null) {
			send(Pasteboard.getString().toCharArray());
		} else {
			tf.insert(Pasteboard.getString());
		}
	}


	private Bitmap mBackingStore;
	private Scrollbar mScrollbar;
	private Timer mRefreshTimer;
	private SessionManager mSessionManager;
	private boolean mShowSessions;
	private boolean mMetaKey;
	private boolean mMetaKeySent;
	private boolean mDelayedRefresh = false;
  	private TerminalConsole mCurrentConsole;
	private String mHostnameList[] = null;
	private int mPortList[] = null;
	private int mProtocolList[] = null;
	private String mFontList[] = null;
	private boolean mAutoWrapList[] = null;
	private boolean mBlinking = false;

	private Menu mSpecialCharsMenu;
	private Menu mHostsMenu;
	private DialogWindow mHelpDialog;
	private MarqueeAlert mMarqueeAlert = null;

	private String mDefaultFont;

	// FIXME: should move these to the resource file.
  	private static final int kSwitchConsole = 100;

	// magic numbers to set protocol
	public static final int PROT_RAW		= 0;
	public static final int PROT_TELNET		= 1;
	public static final int PROT_SSH2		= 2;
	public static final int DEFAULT_PORT[] = { 3000, 23, 22 };


	/* this is such an evil, evil hack */
	private class FakeEditText extends danger.ui.EditText
	{
		public boolean
		eventKeyDown (char c, Event e)
		{
			return TerminalWindow.this.eventKeyDown(c, e);
		}
	}
}
