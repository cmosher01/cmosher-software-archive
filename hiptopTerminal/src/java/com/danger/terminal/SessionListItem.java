/* -*- Mode: java; indent-tabs-mode: t; c-basic-offset: 4; tab-width: 4 -*- */

package com.danger.terminal;

import danger.ui.Font;
import danger.ui.Pen;
import danger.ui.Style;
import danger.util.StringUtils;


class SessionListItem
{
	SessionListItem (HostInfo session, SessionManager manager)
	{
		mHostInfo = session;
		mSessionManager = manager;
		mConsole = null;
        mType = HOST_TYPE;
	}
	
	SessionListItem (TerminalConsole console, SessionManager manager)
	{
		mHostInfo = null;
		mSessionManager = manager;
		mConsole = console;
        mType = SESSION_TYPE;
	}
	
	HostInfo getHostInfo()
	{
		return mHostInfo;
	}
	
	TerminalConsole getConsole ()
	{
		return mConsole;
	}
	
	int getType()
	{		
		return mType;
	}
	
	void paintItem(Style style, Pen p, int left, int top, int right, int bottom,
							boolean focused)
	{
		switch( getType() ) {
			case HOST_TYPE:
				paintHost(style, p, left, top, right, bottom, focused);
				break;
			
			case SESSION_TYPE:
				paintSession(style, p, left, top, right, bottom, focused);
				break;
		}
	}
	
	private void
	paintHost (Style style, Pen p, int left, int top, int right, int bottom, boolean focused)
	{
        Font oldFont = p.getFont();
		Font font = Font.findBoldSystemFont();
        p.setFont(font);
        
		int fontDescent = font.getDescent();
		int fontHeight = fontDescent + font.getAscent();
		int baseline = (top + bottom) / 2 + (fontHeight / 2) - fontDescent;

		/* figure out how big the protocol label is so we can trim ourselves */
        String protocol = mHostInfo.getProtocolName();
        int protocolWidth = font.getWidth(protocol);
        
		left += SESSION_LABEL_LEFT_EDGE;
        right -= SESSION_LABEL_RIGHT_INSET;
        
        int width = ( right - left ) - protocolWidth - LABEL_GAP;
        
		String displayString = StringUtils.makeDisplayString(mHostInfo.getSessionName(), width, font);
		p.drawText( left, baseline, displayString );
		
		/* right align this one */
		p.drawText( right - protocolWidth, baseline, protocol );
        p.setFont(oldFont);
	}
	
	private void
	paintSession (Style style, Pen p, int left, int top, int right, int bottom, boolean focused)
	{
        String status;
		Font font = p.getFont();
		int fontDescent = font.getDescent();
		int fontHeight = fontDescent + font.getAscent();
		int baseline = (top + bottom) / 2 + (fontHeight / 2) - fontDescent;

		/* BRAIN DAMAGE: Use icons here! */
        if (mConsole.isOpen()) {
            status = "o";
        } else {
            status = "x";
        }

        p.drawText( left + CONSOLE_ICON_LEFT_EDGE, baseline, status );

        int width = right - left - CONSOLE_NAME_LEFT_EDGE;
        String displayString = StringUtils.makeDisplayString(mConsole.getTitle(), width, font);
		p.drawText( left + CONSOLE_NAME_LEFT_EDGE, baseline, displayString );
	}

	public void itemActivated()
	{
		switch( getType() ) {
			case HOST_TYPE:
				mSessionManager.openSession( mHostInfo );
				break;
			
			case SESSION_TYPE:
				TerminalWindow window = Terminal.instance().getTerminalWindow();
				window.switchToConsole(mConsole);
				window.show();
				break;
		}
	}

    public void close()
    {
		if (getType() != SESSION_TYPE) {
			System.err.println("@@@ should not happen!  SessionListItem.close called on HOST");
			return;
		}
		mConsole.disconnect();
    }
    
	private SessionManager		mSessionManager;
	private TerminalConsole		mConsole;
	private HostInfo			mHostInfo;
    private int					mType;

	static final int			HOST_TYPE					= 0;
	static final int			SESSION_TYPE				= 1;
	
	/** the corner size of the border round-rects */
    static private final int	CORNER_SIZE					= 3;
    static private final int	LABEL_GAP					= 5;
    private static final int	SESSION_LABEL_RIGHT_INSET	= 2;
    private static final int	SESSION_LABEL_LEFT_EDGE		= 2;
    private static final int	CONSOLE_ICON_LEFT_EDGE		= 12;
    private static final int	CONSOLE_NAME_LEFT_EDGE		= 30;
}
