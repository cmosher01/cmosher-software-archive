/*
 * Created on Dec 1, 2007
 */
package cli;

import gui.UI;

public class CLI implements UI
{
	public void showMessage(final String message)
	{
		System.err.println(message);
	}

	public void updateScreen()
	{
		// nothing to update
	}

	public void verifyLoseUnsaveChanges()
	{
		// OK to discard any unsaved disk changes
	}

	public void askLoseUnsavedChanges()
	{
		// OK to discard any unsaved disk changes
	}
}
