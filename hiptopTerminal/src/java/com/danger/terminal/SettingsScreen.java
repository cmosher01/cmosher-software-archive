// -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*-

package com.danger.terminal;

import java.util.Enumeration;

import danger.app.Event;
import danger.ui.Button;
import danger.ui.CheckBox;
import danger.ui.PopupMenu;
import danger.ui.ScreenWindow;
import danger.ui.StaticText;
import danger.ui.TextField;
import danger.util.format.StringFormat;


public class SettingsScreen
    extends ScreenWindow
	implements TerminalResources, TerminalEvents
{
	public static class HostTextField extends TextField
	{
		public void
		loseFocus ()
		{
			mScreen.updateHostName();
			super.loseFocus();
		}

		/* package */ SettingsScreen mScreen;
	}

	public static class SessionTextField extends TextField
	{
		public void
		loseFocus ()
		{
			mScreen.updateSessionName();
			super.loseFocus();
		}

		/* package */ SettingsScreen mScreen;
	}


	public
	SettingsScreen ()
	{
		// pass
	}

	public void
	onDecoded ()
	{
		setIcon(getApplication().getBundle().getSmallIcon());
	}

	private void
	disableSSHSettings ()
	{
		((CheckBox) getDescendantWithID(ID_SETTINGS_REMEMBER_PASSWORD)).disable();
		((CheckBox) getDescendantWithID(ID_SETTINGS_PREFER_DSS)).disable();
		((Button) getDescendantWithID(ID_SETTINGS_KEY_BUTTON)).disable();
		((StaticText) getDescendantWithID(ID_SETTINGS_WINDOW_TEXT)).disable();
		((TextField) getDescendantWithID(ID_SETTINGS_WINDOW)).disable();

		// (there is also no username for telnet sessions)
		((StaticText) getDescendantWithID(ID_SETTINGS_USERNAME_TEXT)).disable();
		((TextField) getDescendantWithID(ID_SETTINGS_USERNAME)).disable();
	}

	private void
	enableSSHSettings ()
	{
		((CheckBox) getDescendantWithID(ID_SETTINGS_REMEMBER_PASSWORD)).enable();
		((CheckBox) getDescendantWithID(ID_SETTINGS_PREFER_DSS)).enable();
		((Button) getDescendantWithID(ID_SETTINGS_KEY_BUTTON)).enable();
		((StaticText) getDescendantWithID(ID_SETTINGS_WINDOW_TEXT)).enable();
		((TextField) getDescendantWithID(ID_SETTINGS_WINDOW)).enable();

		// (there is also no username for telnet sessions)
		((StaticText) getDescendantWithID(ID_SETTINGS_USERNAME_TEXT)).enable();
		((TextField) getDescendantWithID(ID_SETTINGS_USERNAME)).enable();
	}

	/* package */ void
	fillFrom (HostInfo info)
	{
		mInfo = info;

		SessionTextField sfield = (SessionTextField) getDescendantWithID(ID_SETTINGS_NAME);
		sfield.setText(info.getSessionName());
		sfield.mScreen = this;

		mSessionNameIsDefault = info.getSessionNameIsDefault();

		setTitle(StringFormat.withFormat(getTitle(), info.getSessionName()));

		HostTextField hfield = (HostTextField) getDescendantWithID(ID_SETTINGS_HOST);
		hfield.setText(info.getFullHostname());
		hfield.mScreen = this;

		hfield = (HostTextField) getDescendantWithID(ID_SETTINGS_USERNAME);
		hfield.setText(info.getUsername());
		hfield.mScreen = this;

        PopupMenu menu = (PopupMenu) getDescendantWithID(ID_SETTINGS_PROTOCOL);
		switch (info.getProtocol()) {
		case TerminalWindow.PROT_SSH2:
			menu.setValueWithItemID(ID_PROTOCOL_SSH2);
			break;
		case TerminalWindow.PROT_TELNET:
			menu.setValueWithItemID(ID_PROTOCOL_TELNET);
			break;
		case TerminalWindow.PROT_RAW:
			menu.setValueWithItemID(ID_PROTOCOL_RAW);
			break;
		}
		menu.setExitEvent(new Event(this, EVENT_PROTOCOL_MENU));

		String[] fontNames = getApplication().getStringArray(FONT_NAMES);
		String font = info.getFont();
		for (int i = 0; i < fontNames.length; i++) {
			if (fontNames[i].equals(font)) {
				((PopupMenu) getDescendantWithID(ID_SETTINGS_FONT)).setValue(i);
			}
		}

		menu = (PopupMenu) getDescendantWithID(ID_SETTINGS_EMULATOR);
		switch (info.getEmulator()) {
		case VirtualConsole.EMULATE_VT100:
			menu.setValueWithItemID(ID_EMULATE_VT100);
			break;
		case VirtualConsole.EMULATE_ANSI:
			menu.setValueWithItemID(ID_EMULATE_ANSI);
			break;
		case VirtualConsole.EMULATE_XTERM:
			menu.setValueWithItemID(ID_EMULATE_XTERM);
			break;
		case VirtualConsole.EMULATE_LINUX:
			menu.setValueWithItemID(ID_EMULATE_LINUX);
			break;
		}

		((CheckBox) getDescendantWithID(ID_SETTINGS_WRAP_CHECKBOX)).setValue(info.getAutoWrap() ? 1 : 0);
		((CheckBox) getDescendantWithID(ID_SETTINGS_BACKSPACE_CHECKBOX)).setValue(info.getBackspaceSendsDel() ? 1 : 0);

		((TextField) getDescendantWithID(ID_SETTINGS_SCROLLBACK)).setText(String.valueOf(info.getScrollback()));

		menu = (PopupMenu) getDescendantWithID(ID_SETTINGS_CODEC);
		if (info.getCodec().equals("ISO-8859-1")) {
			menu.setValueWithItemID(ID_CODEC_LATIN1);
		}
		if (info.getCodec().equals("ISO-8859-15")) {
			menu.setValueWithItemID(ID_CODEC_LATIN9);
		}
		if (info.getCodec().equals("UTF-8")) {
			menu.setValueWithItemID(ID_CODEC_UTF8);
		}

		if (info instanceof SSHHostInfo) {
			fillFromSSH((SSHHostInfo) info);
		} else {
			disableSSHSettings();
		}
	}

	private void
	fillFromSSH (SSHHostInfo info)
	{
		CheckBox box = (CheckBox) getDescendantWithID(ID_SETTINGS_REMEMBER_PASSWORD);
		box.setValue(info.getRememberPassword() ? 1 : 0);

		box = (CheckBox) getDescendantWithID(ID_SETTINGS_PREFER_DSS);
		box.setValue(info.getPreferDSS() ? 1 : 0);

		Button button = (Button) getDescendantWithID(ID_SETTINGS_KEY_BUTTON);
		if ((info.getRSAKey() == null) && (info.getDSSKey() == null)) {
			// no host key to display
			button.disable();
		} else {
			setEventForControlWithID(ID_SETTINGS_KEY_BUTTON, new Event(this, EVENT_VIEW_HOST_KEY));
		}

		((TextField) getDescendantWithID(ID_SETTINGS_WINDOW)).setText(String.valueOf(info.getWindowSize()));
	}

	/* package */ void
	save ()
	{
		/* first, some hairiness.  the user may have changed the protocol. */
		int protocol = ((PopupMenu) getDescendantWithID(ID_SETTINGS_PROTOCOL)).getValueAsItemID();
		switch (protocol) {
		case ID_PROTOCOL_SSH2:
			protocol = TerminalWindow.PROT_SSH2;
			break;
		case ID_PROTOCOL_TELNET:
			protocol = TerminalWindow.PROT_TELNET;
			break;
		case ID_PROTOCOL_RAW:
			protocol = TerminalWindow.PROT_RAW;
			break;
		}

		if (protocol != mInfo.getProtocol()) {
			/* need to create a new session-info object, because the old one might
			 * be a descendant of HostInfo with protocol-specific fields.
			 */
			SessionManager manager = Terminal.instance().getSessionManager();
			HostInfo info = manager.createSession(protocol);
			manager.replaceSession(mInfo, info);
			mInfo = info;
		}
		mInfo.setProtocol(protocol);

		String name = ((TextField) getDescendantWithID(ID_SETTINGS_NAME)).toString();

		/* user might have changed the session name field, but never moved
		 * focus elsewhere before saving.  ditto hostname/username fields.
		 */
		if (mSessionNameIsDefault && !mInfo.getSessionName().equals(name)) {
			mSessionNameIsDefault = false;
		}
		if (mSessionNameIsDefault) {
			generateSessionName();
			name = ((TextField) getDescendantWithID(ID_SETTINGS_NAME)).toString();
		}
		mInfo.setSessionName(name);

        String hostname = ((TextField) getDescendantWithID(ID_SETTINGS_HOST)).toString();
        int port = TerminalWindow.DEFAULT_PORT[protocol];
        int separator = hostname.indexOf(':');
        if (separator == -1) {
            separator = hostname.indexOf(' ');
        }
        if (separator != -1) {
            String portStr = hostname.substring(separator + 1).trim();
            if (portStr.equals("local")) {
                port = -1;
            } else {
                try {
					port = Integer.parseInt(portStr);
				} catch (NumberFormatException exc) {
                    // pass
				}
            }
            hostname = hostname.substring(0, separator);
        }
		mInfo.setHostname(hostname);
		mInfo.setPort(port);
		mInfo.setUsername(((TextField) getDescendantWithID(ID_SETTINGS_USERNAME)).toString());

		String[] fontNames = getApplication().getStringArray(FONT_NAMES);
		mInfo.setFont(fontNames[((PopupMenu) getDescendantWithID(ID_SETTINGS_FONT)).getValue()]);

		switch (((PopupMenu) getDescendantWithID(ID_SETTINGS_EMULATOR)).getValueAsItemID()) {
		case ID_EMULATE_VT100:
			mInfo.setEmulator(VirtualConsole.EMULATE_VT100);
			break;
		case ID_EMULATE_ANSI:
			mInfo.setEmulator(VirtualConsole.EMULATE_ANSI);
			break;
		case ID_EMULATE_XTERM:
			mInfo.setEmulator(VirtualConsole.EMULATE_XTERM);
			break;
		case ID_EMULATE_LINUX:
			mInfo.setEmulator(VirtualConsole.EMULATE_LINUX);
			break;
		}

		mInfo.setAutoWrap(((CheckBox) getDescendantWithID(ID_SETTINGS_WRAP_CHECKBOX)).getValue() != 0);
		mInfo.setBackspaceSendsDel(((CheckBox) getDescendantWithID(ID_SETTINGS_BACKSPACE_CHECKBOX)).getValue() != 0);
		try {
			mInfo.setScrollback(Integer.parseInt(((TextField) getDescendantWithID(ID_SETTINGS_SCROLLBACK)).toString()));
		} catch (NumberFormatException x) {
			// pass
		}

		switch (((PopupMenu) getDescendantWithID(ID_SETTINGS_CODEC)).getValueAsItemID()) {
		case ID_CODEC_LATIN1:
			mInfo.setCodec("ISO-8859-1");
			break;
		case ID_CODEC_LATIN9:
			mInfo.setCodec("ISO-8859-15");
			break;
		case ID_CODEC_UTF8:
			mInfo.setCodec("UTF-8");
			break;
		}

		if (mInfo instanceof SSHHostInfo) {
			saveSSH();
		}

		for (Enumeration e = mInfo.getConsoles().elements(); e.hasMoreElements(); ) {
			VirtualConsole c = (VirtualConsole)e.nextElement();
			updateConsole(c);
		}
	}

	private void
	saveSSH ()
	{
		SSHHostInfo sinfo = (SSHHostInfo) mInfo;

		sinfo.setRememberPassword(((CheckBox) getDescendantWithID(ID_SETTINGS_REMEMBER_PASSWORD)).getValue() != 0);
		if (sinfo.getRememberPassword()) {
			// search active consoles on this host for one that had a successful login
			for (Enumeration e = sinfo.getConsoles().elements(); e.hasMoreElements(); ) {
				TerminalConsole c = (TerminalConsole)e.nextElement();
				if (c.getSession() instanceof SSHSession) {
					String password = ((SSHSession) c.getSession()).getPassword();
					if (password != null) {
						// got one!
						sinfo.setSavedPassword(password);
						break;
					}
				}
			}
		} else {
			sinfo.setSavedPassword(null);
		}

		sinfo.setPreferDSS(((CheckBox) getDescendantWithID(ID_SETTINGS_PREFER_DSS)).getValue() != 0);

		try {
			int windowSize = Integer.parseInt(((TextField) getDescendantWithID(ID_SETTINGS_WINDOW)).toString());
			sinfo.setWindowSize(windowSize);
		} catch (NumberFormatException x) {
			// pass
		}
	}

	private void
	updateConsole (VirtualConsole console)
	{
		// this is an active console attached to this HostInfo -- tell it to change its settings.
		console.setFont(mInfo.getFont());
		console.setWrapMode(mInfo.getAutoWrap());
		console.setCodec(mInfo.getCodec());

		// so far, nothing dynamic to set in ssh-land...
	}


	public boolean
	receiveEvent (Event e)
	{
		switch (e.type) {
		case EVENT_PROTOCOL_MENU:
			if (e.what >= 0) {
				switch (((PopupMenu) getDescendantWithID(ID_SETTINGS_PROTOCOL)).getValueAsItemID()) {
				case ID_PROTOCOL_SSH2:
					enableSSHSettings();
					((StaticText) getDescendantWithID(ID_SETTINGS_EMULATOR_TEXT)).enable();
					((PopupMenu) getDescendantWithID(ID_SETTINGS_EMULATOR)).enable();
					break;
				case ID_PROTOCOL_TELNET:
					disableSSHSettings();
					((StaticText) getDescendantWithID(ID_SETTINGS_EMULATOR_TEXT)).enable();
					((PopupMenu) getDescendantWithID(ID_SETTINGS_EMULATOR)).enable();
					break;
				case ID_PROTOCOL_RAW:
					disableSSHSettings();
					((StaticText) getDescendantWithID(ID_SETTINGS_EMULATOR_TEXT)).disable();
					((PopupMenu) getDescendantWithID(ID_SETTINGS_EMULATOR)).disable();
					break;
				}
			}
			return true;
		case EVENT_VIEW_HOST_KEY:
			displayHostKey();
			return true;
		case EVENT_ERASE_HOST_KEY:
			eraseHostKey();
			return true;
		}

		return super.receiveEvent(e);
	}


	private void
	generateSessionName ()
	{
		String username = ((TextField) getDescendantWithID(ID_SETTINGS_USERNAME)).toString();
		String hostname = ((TextField) getDescendantWithID(ID_SETTINGS_HOST)).toString();
		String sessionName = null;
		if (username.length() > 0) {
			sessionName = username + "@" + hostname;
		} else {
			sessionName = hostname;
		}

		((TextField) getDescendantWithID(ID_SETTINGS_NAME)).setText(sessionName);
		((TextField) getDescendantWithID(ID_SETTINGS_NAME)).invalidate();
	}

	/** Called from {@link HostTextField} when focus leaves the host or
	 * username fields.  We use it to update the session name field if
	 * it's being auto-generated.
	 */
	/* package */ void
	updateHostName ()
	{
		if (mSessionNameIsDefault) {
			generateSessionName();
		}
	}

	/** Called from {@link SessionTextField} when focus leaves the session name
	 * field.  We use it to check if the field was changed, so we know if we
	 * need to autogenerate it.
	 */
	/* package */ void
	updateSessionName ()
	{
		String newName = ((TextField) getDescendantWithID(ID_SETTINGS_NAME)).toString();

		if (mSessionNameIsDefault) {
			if (mInfo.getSessionName().equals(newName)) {
				// nothing changed; return
				return;
			}
			// now manually editing the session name
			mSessionNameIsDefault = false;
		} else {
			if (newName.length() == 0) {
				// cleared out the session name; autogenerate
				mSessionNameIsDefault = true;
				generateSessionName();
			}
		}
		return;
	}

	private void
	displayHostKey ()
	{
		HostKeyDialog dialog = (HostKeyDialog) getApplication().getDialog(ID_HOST_KEY_DIALOG, this);
		dialog.setTitleFormatText(mInfo.getHostname());

		boolean useDSS = ((CheckBox) getDescendantWithID(ID_SETTINGS_PREFER_DSS)).getValue() != 0;
		SSHHostInfo sinfo = (SSHHostInfo) mInfo;
		byte[] key = useDSS ? sinfo.getDSSKey() : sinfo.getRSAKey();
		if (key == null) {
			key = useDSS ? sinfo.getRSAKey() : sinfo.getDSSKey();
			useDSS = !useDSS;
		}
		if (key == null) {
			return;
		}

		mDisplayingDSS = useDSS;
		dialog.setHostKey(useDSS ? "DSS" : "RSA", key);
		dialog.useEraseButton();
		dialog.show();
	}

	private void
	eraseHostKey ()
	{
		SSHHostInfo sinfo = (SSHHostInfo) mInfo;
		if (mDisplayingDSS) {
			sinfo.setDSSKey(null);
		} else {
			sinfo.setRSAKey(null);
		}

		if ((sinfo.getRSAKey() == null) && (sinfo.getDSSKey() == null)) {
			((Button) getDescendantWithID(ID_SETTINGS_KEY_BUTTON)).disable();
			// make sure we don't lose focus
			setFocusedDescendant(getDescendantWithID(ID_SETTINGS_PREFER_DSS));
		}
	}


	private boolean						mSessionNameIsDefault = false;
	private boolean						mDisplayingDSS = false;
	private HostInfo					mInfo = null;
}
