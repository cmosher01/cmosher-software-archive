/*
 * Created on Dec 1, 2007
 */
package cli;

import java.io.Closeable;
import java.io.IOException;
import gui.UI;

public class CLI implements UI
{
	final Closeable app;

	public CLI(final Closeable app)
	{
		this.app = app;
	}

	public void showMessage(String message)
	{
		System.err.println(message);
	}

	public void updateDrives()
	{
		// nothing to update
	}

	public void updateScreen()
	{
		// nothing to update
	}

	public void handleStdInEOF()
	{
		try
		{
			this.app.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public boolean isHyper()
	{
		return true;
	}

	public void setHyper(@SuppressWarnings("unused") boolean isHyper)
	{
		// don't need to do anything
	}
}
