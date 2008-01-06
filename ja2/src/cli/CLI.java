/*
 * Created on Dec 1, 2007
 */
package cli;

import java.io.Closeable;
import java.io.IOException;
import gui.UI;
import gui.UserCancelled;

public class CLI implements UI
{
	public void showMessage(String message)
	{
		System.err.println(message);
	}

	public void updateScreen()
	{
		// nothing to update
	}

	public boolean isHyper()
	{
		return true;
	}

	public void setHyper(@SuppressWarnings("unused") boolean isHyper)
	{
		// don't need to do anything
	}

	public void verifyLoseUnsaveChanges()
	{
		// OK to discard any unsaved disk changes
	}

	public void askLoseUnsavedChanges() throws UserCancelled
	{
		// OK to discard any unsaved disk changes
	}
}
