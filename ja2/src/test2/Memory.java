/*
 * Created on Aug 2, 2007
 */
package test2;

public class Memory
{
	private final Bus addressBus;
	private final Bus dataBus;

	public Memory(final Bus addressBus, final Bus dataBus)
	{
		this.addressBus = addressBus;
		this.dataBus = dataBus;
	}

}
