/*
 * Created on Aug 2, 2007
 */
package test2;

public class CPU6502
{
	private final Bus addressBus;
	private final Bus dataBus;

	public CPU6502(final Bus addressBus, final Bus dataBus)
	{
		this.addressBus = addressBus;
		this.dataBus = dataBus;
	}

}
