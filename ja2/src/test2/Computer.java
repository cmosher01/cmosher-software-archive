/*
 * Created on Aug 2, 2007
 */
package test2;

public class Computer
{
	private final Bus addressBus = new Bus16();
	private final Bus dataBus = new Bus8();

	private final Memory rom = new Memory(this.addressBus,this.dataBus);
	private final CPU6502 cpu = new CPU6502(this.addressBus,this.dataBus);
}
