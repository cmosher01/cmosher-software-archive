/*
 * Created on Nov 28, 2007
 */
package gui;

import java.awt.Image;

public interface GUI
{
	void updateScreen(Image image);
	void updateDrives();
	void showMessage(String message);
}
