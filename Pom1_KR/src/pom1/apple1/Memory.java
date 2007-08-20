package pom1.apple1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Memory
{
	private static final int KBD = 0xD010;
	private static final int KBDCR = 0xD011;
	private static final int DSP = 0xD012;
	private static final int DSPCR = 0xD013;
	private static final int ROMBASE = 0xFF00;
	private static final int MEMSIZE = 0x10000;

	private final Pia6820 pia;

	private final int mem[] = new int[MEMSIZE];

//	private boolean ram8k;
//	private boolean writeInRom;

	public Memory(Pia6820 pia) throws IOException
	{
		this.pia = pia;
//		this.writeInRom = true;
		reset();
	}

//	public void setRam8k(boolean b)
//	{
//		ram8k = b;
//	}
//
//	public void setWriteInRom(boolean b)
//	{
//		writeInRom = b;
//	}

	public int read(int address)
	{
		if (address == KBD)
			return pia.readKbd();
		if (address == KBDCR)
			return pia.readKbdCr();
		if (address == DSP)
			return pia.readDsp();
		if (address == DSPCR)
			return pia.readDspCr();

		return mem[address % MEMSIZE];
	}

	public void write(int address, int value)
	{
		if (address == DSPCR)
		{
//			mem[address] = value;
			pia.writeDspCr(value);
			return;
		}
		if (address == DSP)
		{
//			mem[address] = value;
			pia.writeDsp(value);
			return;
		}
		if (address == KBDCR)
		{
//			mem[address] = value;
			pia.writeKbdCr(value);
			return;
		}
		if (address == KBD)
		{
//			mem[address] = value;
			pia.writeKbd(value);
			return;
		}
		if (ROMBASE <= address) // && !writeInRom)
		{
			return;
		}
//		if (ram8k && (0x2000 <= address && address < ROMBASE))
//		{
//			return;
//		}

		mem[address % MEMSIZE] = value;
	}

	public void reset() throws IOException
	{
		Arrays.fill(mem,0);
		loadRom();
	}

	public int[] dumpMemory(int start, int end)
	{
		int fbrut[] = new int[(end - start) + 1];
		for (int i = 0; i < (end - start) + 1; i++)
		{
			fbrut[i] = mem[start + i] & 0xff;
		}
		return fbrut;
	}

	public void loadRom() throws IOException
	{
		final String filename = System.getProperty("user.dir") + "/bios/" + System.getProperty("ROMFILE","6502.rom.bin");
		final File romFile = new File(filename);
		final InputStream fis = new BufferedInputStream(new FileInputStream(romFile));
		final int romsize = (int)romFile.length();
		final int startingAddress = MEMSIZE - romsize;
		if (startingAddress < 0)
		{
			throw new IOException("file is larger than available address space of 65536 bytes");
		}
		for (int i = startingAddress; i < MEMSIZE; i++)
		{
			mem[i] = fis.read();
		}
		fis.close();
	}
}
