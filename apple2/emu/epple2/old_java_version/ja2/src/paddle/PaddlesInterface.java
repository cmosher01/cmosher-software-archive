/*
 * Created on Dec 4, 2007
 */
package paddle;

public interface PaddlesInterface
{
	void tick();
	void startTimers();
	boolean isTimedOut(int paddle);
}
