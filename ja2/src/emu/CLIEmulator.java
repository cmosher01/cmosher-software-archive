/*
 * Created on Feb 7, 2008
 */
package emu;

import java.io.IOException;
import cards.stdio.StandardIn;
import display.DisplayType;

public class CLIEmulator extends Emulator
{
	public CLIEmulator() throws IOException
	{
	}

	@Override
	protected StandardIn.EOFHandler getStdInEOF()
	{
		return new StandardIn.EOFHandler()
		{
			public void handleEOF()
			{
				CLIEmulator.this.close();
			}
		};
	}

	@Override
	public void init()
	{
    	setDisplayType(DisplayType.MONITOR_COLOR);
    	powerOnComputer();
    	powerOnMonitor();
	}
}
