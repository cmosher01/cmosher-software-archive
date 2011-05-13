/*
 * Created on Jan 28, 2008
 */
package video;

import display.DisplayType;

public interface VideoDisplayDevice
{
	void powerOn(boolean b);
	boolean isOn();

	void setType(DisplayType type);

	void putSignal(int ire);
	void putAsDisconnectedVideoIn();
	void restartSignal();
	void dump();
	void setCompress(boolean checked);
}
