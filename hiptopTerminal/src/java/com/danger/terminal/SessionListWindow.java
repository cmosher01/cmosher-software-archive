/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

package com.danger.terminal;

import java.util.Calendar;
import java.util.Vector;

import danger.app.ComponentVersion;
import danger.app.Event;
import danger.app.Timer;
import danger.ui.AlertWindow;
import danger.ui.DialogWindow;
import danger.ui.Menu;
import danger.ui.ScreenWindow;
import danger.ui.StaticTextBox;
import danger.util.format.StringFormat;


public class SessionListWindow extends ScreenWindow
	implements TerminalResources, TerminalEvents, danger.SystemStrings
{

	public SessionListWindow()
	{
		super();

		new Timer(1000*3600, true, this).start();
	}

	public void Init(SessionManager manager, TerminalWindow terminal)
	{
		mMe = this;
		mTerminal = terminal;

		mSessionManager = manager;
	    mListView = (SessionListView) getDescendantWithID(ID_SESSION_LIST_VIEW);
 		mListView.Init(manager);

		setOpenCloseModifyDeleteEnabled(false, false, false, false);
	}

	public void
	onDecoded ()
	{
		Calendar today = Calendar.getInstance();
		int month = today.get(Calendar.MONTH);
		int day = today.get(Calendar.DAY_OF_MONTH);

		setIcon(getApplication().getBundle().getSmallIcon());

		if (month == Calendar.JANUARY) {
			if (day == 26) {
				setIcon(getApplication().getBitmap(ID_AUS_ICON));
			}
		} else if (month == Calendar.FEBRUARY) {
			if (day == 6) {
				// missing NZ flag
			}
		} else if (month == Calendar.MARCH) {
			if (day == 1) {
				setIcon(getApplication().getBitmap(ID_WALES_ICON));
			} else if (day == 17) {
				setIcon(getApplication().getBitmap(ID_NIRELAND_ICON));
			}
		} else if (month == Calendar.APRIL) {
			if (day == 23) {
				setIcon(getApplication().getBitmap(ID_ENGLAND_ICON));
			}
		} else if (month == Calendar.MAY) {
			if (day == 5) {
				setIcon(getApplication().getBitmap(ID_CINCO_ICON));
			}
		} else if (month == Calendar.JULY) {
			if (day == 1) {
				setIcon(getApplication().getBitmap(ID_CANADA_ICON));
			} else if (day == 4) {
				setIcon(getApplication().getBitmap(ID_IND_ICON));
			} else if (day == 14) {
				setIcon(getApplication().getBitmap(ID_BASTILLE_ICON));
			}
		} else if (month == Calendar.SEPTEMBER) {
			if (day == 19) {
				setIcon(getApplication().getBitmap(ID_PIRATE_ICON));
			}
		} else if (month == Calendar.OCTOBER) {
			if (day == 3) {
				setIcon(getApplication().getBitmap(ID_DEUTSCH_ICON));
			}
		} else if (month == Calendar.NOVEMBER) {
			if (day == 30) {
				setIcon(getApplication().getBitmap(ID_SCOTLAND_ICON));
			}
		}
	}

	/**
	 * Set whether the open, close, and modify actions are enabled.
	 *
	 * @param canOpen whether open is enabled
	 * @param canClose whether close is enabled
	 * @param canModify whether modifications are enabled
	 */
	/*package*/ void setOpenCloseModifyDeleteEnabled(boolean canOpen, 
											   boolean canClose,
											   boolean canModify,
											   boolean canDelete)
	{
		// xxx should probably cache the items
		Menu menu = getActionMenu();
		menu.getItemWithID(kID_OpenHostMenuItem).setEnabled(canOpen);
		menu.getItemWithID(kID_CloseSessionMenuItem).setEnabled(canClose);
		menu.getItemWithID(kID_EditHostMenuItem).setEnabled(canModify);
		menu.getItemWithID(kID_DeleteHostMenuItem).setEnabled(canDelete);
	}

	public boolean
	eventShortcut (char c, Event e)
	{
		switch (c) {
		case 'v':
			showAbout();
			return true;
		case '1':
			setIcon(getApplication().getBitmap(ID_IND_ICON));
			return true;
		case '2':
			setIcon(getApplication().getBitmap(ID_BASTILLE_ICON));
			return true;
		case '3':
			setIcon(getApplication().getBitmap(ID_CANADA_ICON));
			return true;
		case '4':
			setIcon(getApplication().getBitmap(ID_AUS_ICON));
			return true;
		case '5':
			setIcon(getApplication().getBitmap(ID_DEUTSCH_ICON));
			return true;
		case '6':
			setIcon(getApplication().getBitmap(ID_CINCO_ICON));
			return true;
		case '!':
			setIcon(getApplication().getBitmap(ID_ENGLAND_ICON));
			return true;
		case '\\':
			setIcon(getApplication().getBitmap(ID_SCOTLAND_ICON));
			return true;
		case '#':
			setIcon(getApplication().getBitmap(ID_WALES_ICON));
			return true;
		case '$':
			setIcon(getApplication().getBitmap(ID_NIRELAND_ICON));
			return true;
		case '%':
			setIcon(getApplication().getBitmap(ID_PIRATE_ICON));
			return true;
		case '0':
			setIcon(getApplication().getBundle().getSmallIcon());
			return true;
		}

		return super.eventShortcut(c, e);
	}

	public final boolean eventWidgetUp(int widget, Event event)
	{
		switch(widget)
		{
			case Event.DEVICE_BUTTON_BACK:
				getApplication().returnToLauncher();
				return true;
		}

		return super.eventWidgetUp(widget, event);
	}

	public boolean
	eventWidgetDown (int widget, Event event)
	{
		switch (widget) {
		case Event.DEVICE_ARROW_RIGHT:
			if (event.modifiers == Event.EVENT_MODIFIER_MENU_BUTTON) {
				// go to first console
				Vector vec = mSessionManager.getConsoleList();
				if (vec.size() > 0) {
					mTerminal.switchToConsole((TerminalConsole)vec.elementAt(0));
					mTerminal.show();
				}
				return true;
			}
			break;
		case Event.DEVICE_ARROW_LEFT:
			if (event.modifiers == Event.EVENT_MODIFIER_MENU_BUTTON) {
				// go to last
				Vector vec = mSessionManager.getConsoleList();
				if (vec.size() > 0) {
					mTerminal.switchToConsole((TerminalConsole)vec.elementAt(vec.size()-1));
					mTerminal.show();
				}
				return true;
			}
			break;
		}

		return super.eventWidgetDown(widget, event);
	}

	public boolean receiveEvent(Event e)
	{
		ConnectDialog dialog;
		HostInfo info;
        SessionListItem item;
		
		switch (e.type) {
			case kNewHost:
				dialog = (ConnectDialog) getApplication().getDialog(ID_CONNECT_DIALOG, this);
				dialog.show();
				return true;
			
			case kOpenHost:
				item = (SessionListItem) mListView.getFocusedItem();
				item.itemActivated();
				return true;
				
			case kEditHost:
                item = (SessionListItem) mListView.getFocusedItem();
				Terminal.instance().openSettings(item.getHostInfo());
				return true;
				
			case kDeleteHost:
				item = (SessionListItem) mListView.getFocusedItem();
				mSessionManager.deleteSession(item.getHostInfo());
				return true;
			
			case kCloseSession:
				item = (SessionListItem) mListView.getFocusedItem();
                item.close();
				return true;
				
		case EVENT_SAVE:
			dialog = (ConnectDialog) e.argument;
			if (dialog.getHostname().indexOf('.') < 0) {
				dialog.show();
				new AlertWindow(getApplication().getString(STRING_INVALID_HOSTNAME)).show();
				return true;
			}
			mSessionManager.sessionChanged(dialog.getHostInfo());
			return true;

		case EVENT_CONNECT:
			dialog = (ConnectDialog) e.argument;
			if (dialog.getHostname().indexOf('.') < 0) {
				dialog.show();
				new AlertWindow(getApplication().getString(STRING_INVALID_HOSTNAME)).show();
				return true;
			}
			mSessionManager.openSession(dialog.getHostInfo());
			return true;

		case EVENT_HINTS:
			getApplication().getResources().getDialog(ID_HELP_DIALOG).show();
			break;

		case Event.EVENT_TIMER:
			onDecoded();
			break;
        }

		return super.receiveEvent(e);
	}
	
	public void saveConnection(ConnectDialog dialog)
	{
		mSessionManager.addSession ( dialog.getHostInfo() );
	}

	public static SessionListWindow getSessionListWindow()
	{
		return mMe;
	}

	final private void
	showAbout ()
	{
		ComponentVersion cv = new ComponentVersion("");
		getApplication().getBundle().getComponentVersion(cv);

		DialogWindow dialog = getApplication().getDialog(ID_ABOUT_DIALOG, this);
		StaticTextBox box = (StaticTextBox) dialog.getDescendantWithID(ID_ABOUT_TEXT1);
		box.setText(StringFormat.withFormat(box.getText(), cv.getVersionString(false)));

		dialog.show();
	}

	
	private static SessionListWindow	mMe;
	private SessionManager				mSessionManager;
  	private TerminalWindow				mTerminal;
	private SessionListView				mListView;
}
