package com.danger.helloworld;

import danger.app.Application;
import danger.app.Event;
import danger.ui.Color;
import danger.ui.Font;
import danger.ui.Menu;
import danger.ui.Pen;
import danger.ui.Rect;
import danger.ui.ScreenWindow;

/**
 * Implements the main window class.
 *
 */
class MainWindow extends ScreenWindow
{
    private final Rect mBounds;
    private final Font mFont;

    public MainWindow()
    {
        mBounds = getBounds();
        mFont = Font.findBoldSystemFont();

        buildActionMenu();
    }

    //* --------------------    buildActionMenu

    final void buildActionMenu()
    {
        Menu menu = getActionMenu();

        menu.removeAllItems();
        menu.addFromResource(Application.getCurrentApp().getResources(), Resources.kHelloWorldMenuResID);
    }

    //* --------------------    paint

    public void paint(Pen inPen)
    {
        String message = Application.getCurrentApp().getString(Resources.kStrHelloWorld);

        clear(inPen);

        inPen.setColor(Color.BLACK);
        inPen.drawRect(mBounds);

        inPen.setFont(mFont);
        inPen.drawText(
            (mBounds.getWidth() - mFont.getWidth(message)) / 2,
            (mBounds.getHeight() - (mFont.getAscent() + mFont.getDescent())) / 2,
            message);
    }

    //* --------------------    eventWidgetUp

    public boolean eventWidgetUp(int inWhichWidget, Event inEvent)
    {
        boolean consumed = false;

        switch (inWhichWidget)
        {
            case Event.DEVICE_WHEEL :
                break;
            case Event.DEVICE_MULTIPLE_WHEEL :
                break;
            case Event.DEVICE_WHEEL_BUTTON :
                break;
            case Event.DEVICE_ARROW_UP :
                break;
            case Event.DEVICE_ARROW_DOWN :
                break;
            case Event.DEVICE_ARROW_LEFT :
                break;
            case Event.DEVICE_ARROW_RIGHT :
                break;
        }
        return (consumed || super.eventWidgetUp(inWhichWidget, inEvent));
    }

    //* --------------------    eventWidgetDown

    public boolean eventWidgetDown(int inWhichWidget, Event inEvent)
    {
        boolean consumed = false;
        switch (inWhichWidget)
        {
            case Event.DEVICE_WHEEL :
                break;
            case Event.DEVICE_MULTIPLE_WHEEL :
                break;
            case Event.DEVICE_WHEEL_BUTTON :
                break;
            case Event.DEVICE_ARROW_UP :
                break;
            case Event.DEVICE_ARROW_DOWN :
                break;
            case Event.DEVICE_ARROW_LEFT :
                break;
            case Event.DEVICE_ARROW_RIGHT :
                break;
        }
        return (consumed || super.eventWidgetDown(inWhichWidget, inEvent));
    }
}
