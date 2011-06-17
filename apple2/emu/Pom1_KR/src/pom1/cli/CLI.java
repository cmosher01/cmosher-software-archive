/*
 * Created on Sep 21, 2007
 */
package pom1.cli;

import java.io.IOException;
import pom1.apple1.Memory;
import pom1.apple1.Pia6820;
import pom1.apple1.cpu.M6502;
import pom1.apple1.cpu.M65C02;
import pom1.apple1.devices.StandardInput;
import pom1.apple1.devices.StandardOutput;

public class CLI
{
	private Memory mem;
	private M6502 cpu;
	private Pia6820 pia;

	public CLI() throws IOException
	{
		initApple1();
	}

	private void initApple1() throws IOException
	{
		pia = new Pia6820(new StandardInput(), new StandardOutput());

		mem = new Memory(pia);

		try
		{
			if (Boolean.parseBoolean(System.getProperty("65C02","false")))
			{
				cpu = new M65C02(mem);
			}
			else
			{
				cpu = new M6502(mem);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		cpu.start();
		mem.setCpu(cpu);
	}
}
