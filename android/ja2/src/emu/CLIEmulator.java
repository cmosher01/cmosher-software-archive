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
		// TODO make display-type configurable for CLI
		this.display.setType(DisplayType.MONITOR_COLOR);
    	powerOnComputer();
		this.display.powerOn(true);
	}
}
