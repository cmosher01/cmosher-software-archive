package com.danger.helloworld;

import danger.app.Application;
import danger.app.Event;
import danger.util.DEBUG;

/**
 * Implements the main application class.
 *
 */
public class HelloWorld extends Application
{
    /**
     * Creates the main application class.
     *
     */
    public HelloWorld()
    {
        //	Create our application's main window
        mWindow = new MainWindow();
        // Set it's title.
        mWindow.setTitle(getString(Resources.kStrHelloWorld));

        System.err.println("Use this to print debug messages...");
        DEBUG.p("Another way to print debug messages...");
    }

    //*	--------------------	receiveEvent

    /**
     * Handles events.  Called automatically whenever the application receives an event.
     *
     */
    public boolean receiveEvent(Event e)
    {
        switch (e.type)
        {
            case Commands.kCmd_One :
                // Todo: Insert code here...
                DEBUG.p("Helloworld: Received kCmd_One");
                break;
            case Commands.kCmd_Two :
                // Todo: Insert code here...
                DEBUG.p("Helloworld: Received kCmd_Two");
                break;
        }
        return (super.receiveEvent(e));
    }

    //*	--------------------	launch

    /**
     * Handles the launch event.  Called once whenever the application is launched.
     *
     */
    public void launch()
    {
        DEBUG.p("Helloworld: launch");
    }

    //*	--------------------	resume

    /**
     * Handles the resume event.  Called automatically whenever the application is resumed.
     *
     */
    public void resume()
    {
        DEBUG.p("Helloworld: resume");
        mWindow.show();
    }

    //*	--------------------	Suspend

    /**
     * Handles the Suspend event.  Called automatically whenever the application is suspended.
     *
     */
    public void suspend()
    {
        DEBUG.p("Helloworld: suspend");
    }

    /* strings */
//    static final String kStrMenuItem1 = "Menu Item 1";
//    static final String kStrMenuItem2 = "Menu Item 2";

    //*	--------------------------------	Class Variables
    static MainWindow mWindow;
}
