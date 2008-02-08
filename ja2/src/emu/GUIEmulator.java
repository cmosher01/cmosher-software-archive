/*
 * Created on Feb 7, 2008
 */
package emu;

import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import cards.stdio.StandardIn;
import display.DisplayType;
import keyboard.ClipboardProducer;
import keyboard.FnKeyHandler;
import keyboard.HyperKeyHandler;
import keyboard.KeyboardProducer;
import paddle.PaddleButtons;
import gui.ComputerControlPanel;
import gui.GUI;
import gui.MonitorControlPanel;
import gui.Screen;

public class GUIEmulator extends Emulator
{
	private Screen screen;

	public GUIEmulator() throws IOException
	{
	}

	public void init()
	{
		this.screen = new Screen(this.screenImage);
    	final ComputerControlPanel compControls = new ComputerControlPanel(this);
    	final MonitorControlPanel monitorControls = new MonitorControlPanel(this);

    	new GUI(this,this.screen,compControls,monitorControls,this.apple2.slots);

    	this.screenImage.addObserver(new Observer()
		{
    		@SuppressWarnings({ "unused", "synthetic-access" })
			public void update(final Observable observableThatChagned, final Object typeOfChange)
			{
    			GUIEmulator.this.screen.plot();
			}
		});

    	initKeyListeners();


    	this.screen.setFocusTraversalKeysEnabled(false);
    	this.screen.requestFocus();

    	setDisplayType(DisplayType.MONITOR_COLOR);
    	powerOffComputer();
    	powerOffMonitor();
	}

	private void initKeyListeners()
	{
		if (this.screen == null)
		{
			return;
		}
		final KeyListener[] rkl = this.screen.getKeyListeners();
		for (final KeyListener listener: rkl)
		{
			this.screen.removeKeyListener(listener);
		}

		this.screen.addKeyListener(new KeyboardProducer(this.keypresses));
		this.screen.addKeyListener(new ClipboardProducer(this.keypresses));
		this.screen.addKeyListener(new HyperKeyHandler(this.hyper,this.buffered));
		this.screen.addKeyListener(new FnKeyHandler(this.apple2,this.screenImage,this.apple2.ram,this.throttle));
	    this.screen.addKeyListener(new PaddleButtons(this.paddleButtonStates));
	}

	@Override
	protected StandardIn.EOFHandler getStdInEOF()
	{
		return new StandardIn.EOFHandler()
		{
			public void handleEOF()
			{
				// For a GUI, we don't do anything special when STDIN hits EOF
			}
		};
	}
}
