/*
 * Created on Nov 28, 2007
 */
package gui;

public interface UI
{
	void updateScreen();
	void updateDrives();
	void showMessage(String message);
	void close();
	boolean isClosedOnStdInEOF();
}
