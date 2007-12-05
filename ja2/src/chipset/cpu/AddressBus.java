/*
 * Created on Nov 30, 2007
 */
package chipset.cpu;

public interface AddressBus
{
	byte read(int address);
	void write(int address, byte data);
	void reset();
}
