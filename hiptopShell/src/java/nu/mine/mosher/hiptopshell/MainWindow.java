package nu.mine.mosher.hiptopshell;

import danger.app.Event;
import danger.ui.Color;
import danger.ui.Font;
import danger.ui.Pen;
import danger.ui.Rect;
import danger.ui.ScreenWindow;



class MainWindow extends ScreenWindow
{
    private final Rect mBounds;
    private final Font mFont;

    public MainWindow()
    {
        mBounds = getBounds();
        mFont = Font.findBoldSystemFont();
    }
    public void paint(Pen inPen)
    {
        String message = "Shell";

        clear(inPen);

        inPen.setColor(Color.BLACK);
        inPen.drawRect(mBounds);

        inPen.setFont(mFont);
        inPen.drawText(
            (mBounds.getWidth() - mFont.getWidth(message)) / 2,
            (mBounds.getHeight() - (mFont.getAscent() + mFont.getDescent())) / 2,
            message);
    }

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
