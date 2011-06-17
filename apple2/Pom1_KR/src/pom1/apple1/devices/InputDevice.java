/*
 * Created on Aug 22, 2007
 */
package pom1.apple1.devices;

public interface InputDevice
{
	int getCharacter() throws InterruptedException;
	boolean isReady(boolean wait) throws InterruptedException;
}
