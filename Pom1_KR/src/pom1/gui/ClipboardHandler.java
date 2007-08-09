// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClipboardHandler.java

package pom1.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Timer;

import pom1.apple1.Pia6820;

public class ClipboardHandler
{
  GUI gui;
  long delay;
  
    public ClipboardHandler(GUI theGui)
    {
      gui = theGui;
      delay = Long.parseLong(System.getProperty("PASTE_DELAY", "15"));
    }
    
    public void sendDataToApple1(Pia6820 pia)
    {
      if(pia.getKbdInterrups())
      {
        gui.synchronise(false);
        String data = getClipboardContents();
        TyperTimerTask typerTask = new TyperTimerTask(data, gui, delay);
        theTimer.schedule(typerTask, delay, delay);
      }
    }

    public static String getClipboardContents()
    {
        String data = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if(hasTransferableText)
        {
            try
            {
                data = (String)contents.getTransferData(DataFlavor.stringFlavor);
            }
            catch(UnsupportedFlavorException ex)
            {
                System.out.println(ex);
            }
            catch(IOException ex)
            {
                System.out.println(ex);
            }
        }
        return data;
    }

    static Timer theTimer = new Timer();

}
