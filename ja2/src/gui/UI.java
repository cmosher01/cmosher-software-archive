/*
 * Created on Nov 28, 2007
 */
package gui;

public interface UI
{
	void updateScreen();

	void showMessage(String message);

	void verifyLoseUnsaveChanges() throws UserCancelled;
	void askLoseUnsavedChanges() throws UserCancelled;
}
