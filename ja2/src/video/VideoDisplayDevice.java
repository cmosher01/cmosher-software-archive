/*
 * Created on Jan 28, 2008
 */
package video;

public interface VideoDisplayDevice
{
	void powerOn(boolean b);
	boolean isOn();

	void putSignal(int ire);
}
