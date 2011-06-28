/*
 * Created on Jun 16, 2005
 */
package com.surveysampling.emailpanel.counts.prototype;

import java.awt.Font;
import java.awt.Frame;
import java.io.IOException;

import thinlet.FrameLauncher;
import thinlet.Thinlet;

/**
 * Show a non-working GUI layout.
 * 
 * @author Chris Mosher
 */
public class NonworkingLayout extends Thinlet
{
    private static final int BORDER = 40;
    private static final String GUI_DEFINITION_PATH = "/EpanCountPrototype.xml";

    private static Object spotCountsComponent;

    /**
     * Main program entry point.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        final Thinlet spotCounts = new NonworkingLayout();
        spotCountsComponent = spotCounts.parse(GUI_DEFINITION_PATH);
        spotCounts.add(spotCountsComponent);
        spotCounts.setFont(new Font("Tahoma",Font.PLAIN,11));
        spotCounts.setColors(0xece9d8, 0x000000, 0xffffff, 0x909090, 0xb0b0b0, 0xededed, 0xc7c5b2, 0xe68b2c, 0xf2c977);
        new FrameLauncher("Spot Counts",spotCounts,800-2*BORDER,600-2*BORDER).setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    /** does nothing */
    public void clickCount() {}
    /** does nothing */
    public void setDirty() {}
    /** does nothing */
    public void blankNew() {}
    /** does nothing */
    public void copyToNew() {}
    /** does nothing */
    public void xdem() {}
    /** does nothing */
    public void groupGeo() {}
    /** does nothing */
    public void calcGeo() {}
    /** does nothing */
    public void showMatches() {}
    /** does nothing */
    public void resolveGeo() {}
    /** does nothing */
    public void runCount() {}
    /** does nothing */
    public void abort() {}
    /** does nothing */
    public void reportToClipboard() {}
    /** does nothing */
    public void csvToClipboard() {}
    /** does nothing */
    public void save() {}
    /** does nothing */
    public void cancel() {}
    /** does nothing */
    public void delete() {}
    /** does nothing */
    public void debugInfo() {}
    /** does nothing */
    public void preferences() {}

    /** exits the application */
    public void exitApplication()
    {
        System.exit(0);
    }
}
