// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Exec.java

package pom1;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import pom1.gui.GUI;

class Exec
{
    Exec()
    {
    }

    public static void main(String args[]) throws InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		        new GUI();
			}
    	});
    }
}
