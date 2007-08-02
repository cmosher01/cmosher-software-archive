public class EmAppleII extends Em6502
{
	// public byte mem[];
	Peripheral slots[];
	int debugflags;
	private static final int HW_LO = 0xC000;
	private static final int ROM_LO = 0xD000;
	private static final int DBG_RDMEM = 2;
	private static final int DBG_WRMEM = 4;
	private static final int DBG_KBD = 0x10000;
	private static final int DBG_GR = 0x50000;
	private int kbdlatch;
	int grswitch;
	int soundstate;
	boolean dirty[]; // used by AppleDisplay class
	// language card switches
	boolean auxRAMselected;
	int auxRAMbank;
	boolean writeinhibit;
	// value to add when reading & writing each of these banks
	// bank 1 is D000-FFFF, bank 2 is D000-DFFF
	int bank1rdoffset, bank2rdoffset, bank1wroffset, bank2wroffset;

	public EmAppleII()
	{
		mem = new byte[0x13000]; // 64K + 16K LC RAM - 4K hardware
		dirty = new boolean[0xc000 >> 7]; // dirty entry every 0x80 bytes
		slots = new Peripheral[8];
		for (int slot = 0; slot < 8; slot++)
			slots[slot] = new Peripheral(this);
		auxRAMselected = false;
		auxRAMbank = 1;
		writeinhibit = true;
	}

	public final void makeAllDirty()
	{
		for (int i = 0; i < dirty.length; i++)
			dirty[i] = false;
	}

	public final void cleanAllDirty()
	{
		for (int i = 0; i < dirty.length; i++)
			dirty[i] = false;
	}

	public final boolean doDebug(int flags)
	{
		return ((debugflags & flags) != 0);
	}

	final int noise()
	{
		return mem[clock & 0xffff] & 0xff;
	}

	final int doIO(int address, int value)
	{
		int slot = (address >> 4) & 0x0f;
		switch (slot)
		{
			case 0:
				// kbd
				return kbdlatch;
			case 1:
				// kbd strobe
				clearStrobe();
			break;
			case 3:
				// spkr
				soundstate = soundstate ^ 1;
			break;
			case 5:
				if ((address & 0x0f) < 8)
				{
					// graphics
					if ((address & 1) != 0)
						grswitch |= 1 << ((address >> 1) & 0x07);
					else
						grswitch &= ~(1 << ((address >> 1) & 0x07));
					if (doDebug(DBG_GR))
					{
						System.out.println("switch " + Integer.toString(address,16) + " grswitch = " + Integer.valueOf(grswitch));
					}
				}
				else
				{
					// annunciators
				}
			break;
			case 6:
				// tapein, joystick, buttons
				switch (address & 7)
				{
					// buttons (off)
					case 1:
					case 2:
					case 3:
						return noise() & 0x7f;
						// joystick
					case 4:
					case 5:
						return noise() | 0x80;
					default:
						return noise();
				}
			case 7:
				// joy reset
				if (address == 0xc070)
					return noise() | 0x80;
			case 8:
				return doLanguageCardIO(address,value);
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				if (doDebug((slot - 8) << 8))
				{
					int result = slots[slot - 8].doIO(address,value);
					System.out.println("slot " + Integer.toString(slot - 8) + " @ " + Integer.toString(address,16) + " = " + Integer.toString(result,16));
					return result;
				}
				return slots[slot - 8].doIO(address,value);
			default:
		}
		return noise();
	}

	public int readMemory(int address)
	{
		if (doDebug(DBG_RDMEM))
			System.out.println("Read @ " + Integer.toString(address,16));
		// see if it's from main memory (0x0000-0xbfff)
		if (address < HW_LO)
			return (mem[address] & 0xff);
		// see if it came from the ROM/LC area (0xd000-0xffff)
		if (address >= ROM_LO)
		{
			if (address >= 0xe000)
				return (mem[address + bank1rdoffset] & 0xff);
			return (mem[address + bank2rdoffset] & 0xff);
		}
		// it must be an I/O location (0xc000-0xcfff)
		if (address < HW_LO + 0x100)
			return doIO(address,-1);
		return slots[(address >> 8) & 7].doHighIO(address,-1);
	}

	public int readMemory512(int address)
	{
		if (doDebug(DBG_RDMEM))
			System.out.println("Read @ " + Integer.toString(address,16));
		return (mem[address] & 0xff);
	}

	public void writeMemory(int address, int value)
	{
		if (doDebug(DBG_WRMEM))
			System.out.println("Write " + Integer.toString(value,16) + " -> " + Integer.toString(address,16));
		// see if it's from main memory (0x0000-0xbfff)
		if (address < HW_LO)
		{
			mem[address] = (byte)value;
			dirty[address >> 7] = true;
			return;
		}
		// see if it came from the ROM/LC area (0xd000-0xffff)
		if (address >= ROM_LO && /* auxRAMselected && */!writeinhibit)
		{
			if (address >= 0xe000)
				mem[address + bank1wroffset] = (byte)value;
			else
				mem[address + bank2wroffset] = (byte)value;
			return;
		}
		// it must be an I/O location (0xc000-0xcfff)
		if (address < HW_LO + 0x100)
			doIO(address,value);
		else
			slots[(address >> 8) & 7].doHighIO(address,value);
	}

	public void writeMemory512(int address, int value)
	{
		if (doDebug(DBG_WRMEM))
			System.out.println("Write " + Integer.toString(value,16) + " -> " + Integer.toString(address,16));
		mem[address] = (byte)value;
	}

	public void clearStrobe()
	{
		kbdlatch &= 0x7f;
		if (doDebug(DBG_KBD))
			System.out.println("Clear strobe");
	}

	public void keyPressed(int key)
	{
		key = (key | 0x80) & 0xff;
		// since we're an Apple II+, we don't do lowercase
		if (key >= 0xe1 && key <= 0xfa)
			key -= 0x20;
		kbdlatch = key;
		if (doDebug(DBG_KBD))
			System.out.println("Key pressed : " + Integer.valueOf(key));
	}

	private int doLanguageCardIO(int address, int value)
	{
		switch (address & 0x0f)
		{
			/*
			 * Select aux RAM bank 2, write protected.
			 */
			case 0x0:
			case 0x4:
				auxRAMselected = true;
				auxRAMbank = 2;
				writeinhibit = true;
			break;
			/*
			 * Select ROM, write enable aux RAM bank 2.
			 */
			case 0x1:
			case 0x5:
				auxRAMselected = false;
				auxRAMbank = 2;
				writeinhibit = false;
			break;
			/*
			 * Select ROM, write protect aux RAM (either bank).
			 */
			case 0x2:
			case 0x6:
			case 0xA:
			case 0xE:
				auxRAMselected = false;
				writeinhibit = true;
			break;
			/*
			 * Select aux RAM bank 2, write enabled.
			 */
			case 0x3:
			case 0x7:
				auxRAMselected = true;
				auxRAMbank = 2;
				writeinhibit = false;
			break;
			/*
			 * Select aux RAM bank 1, write protected.
			 */
			case 0x8:
			case 0xC:
				auxRAMselected = true;
				auxRAMbank = 1;
				writeinhibit = true;
			break;
			/*
			 * Select ROM, write enable aux RAM bank 1.
			 */
			case 0x9:
			case 0xD:
				auxRAMselected = false;
				auxRAMbank = 1;
				writeinhibit = false;
			break;
			/*
			 * Select aux RAM bank 1, write enabled.
			 */
			case 0xB:
			case 0xF:
				auxRAMselected = true;
				auxRAMbank = 1;
				writeinhibit = false;
		}
		// reset language card constants
		//
		if (auxRAMselected)
		{
			bank1rdoffset = 0x3000; // map 0xd000-0xffff -> 0x10000-0x12fff
			if (auxRAMbank == 2)
				bank2rdoffset = -0x1000; // map 0xd000-0xdfff ->
											// 0xc000-0xcfff
			else
				bank2rdoffset = 0x3000; // map 0xd000-0xdfff -> 0x10000-0x10fff
		}
		else
		{
			bank1rdoffset = 0; // map to ROM
			bank2rdoffset = 0;
		}
		if (!writeinhibit)
		{
			bank1wroffset = 0x3000; // map 0xd000-0xffff -> 0x10000-0x12fff
			if (auxRAMbank == 2)
				bank2wroffset = -0x1000; // map 0xd000-0xdfff ->
											// 0xc000-0xcfff
			else
				bank2wroffset = 0x3000; // map 0xd000-0xdfff -> 0x10000-0x10fff
		}
		return noise();
	}
}
