/*
 * Created on Nov 28, 2007
 */
package chipset;

public interface Card
{
	byte io(int address, byte data);
	void reset();
}
