#include "stdafx.h"
#include "aplmem.h"

void ii_c_memory_t::setup()
{
	b_read_ram = false;
	b_write_ram = false;
	b_bank2 = false;
	b_aux = false;
	b_aux_lo = false;
	fill_ram();
	load_rom();
}

void ii_c_memory_t::load_rom()
{
	FILE* f = 0;
	try
	{
		f = fopen("apple2c.rom","rb");
		if (!f)
			throw "Cannot find Apple //c ROM ($C100-$FFFF) file: apple2c.rom.\n";

		size_t cc = fread(rom,1,0x10000-0xc100,f);
		if (cc!=0x10000-0xc100)
			throw "Invalid Apple //c ROM ($C100-$FFFF) file: apple2c.rom.\n";
	}
	catch (...)
	{
		if (f)
			fclose(f);
		throw;
	}
	if (f)
		fclose(f);
}

void ii_c_memory_t::fill_ram()
{
	fill_ram_section(main_ram_hi,0x10000-0xe000);
	fill_ram_section(main_bsr1,0xe000-0xd000);
	fill_ram_section(main_bsr2,0xe000-0xd000);
	fill_ram_section(main_ram_lo,0xc000-0x0200);
	fill_ram_section(main_ram_p01,0x0200-0x0000);
	fill_ram_section(aux_ram_hi,0x10000-0xe000);
	fill_ram_section(aux_bsr1,0xe000-0xd000);
	fill_ram_section(aux_bsr2,0xe000-0xd000);
	fill_ram_section(aux_ram_lo,0xc000-0x0200);
	fill_ram_section(aux_ram_p01,0x0200-0x0000);
}

void ii_c_memory_t::fill_ram_section(byte* p, int c)
{
	int i(0);
	byte b(0);
	while (c--)
	{
		if (i++%8==0)
			b = ~b;
		*p++ = b;
	}
}

byte* ii_c_memory_t::get_mem_ptr(addr a, bool b_read)
{
	byte* p = 0;
	if (0x0000 <= a && a < 0x0200)
	{
		if (b_aux)
			p = aux_ram_p01-0x0000+a;
		else
			p = main_ram_p01-0x0000+a;
	}
	else if (0x0200 <= a && a < 0xC000)
	{
		if (b_aux_lo)
			p = aux_ram_lo-0x0200+a;
		else
			p = main_ram_lo-0x0200+a;
	}
	else if (0xC000 <= a && a < 0xC100)
	{
		// these addresses are not connected to any memory;
		// they are instead used for I/O for various hardware

		if (a<0xc080)
		{
			if (!b_read && 0xc002)
				b_aux_lo = false;
			else if (!b_read && 0xc003)
				b_aux_lo = true;
			else if (!b_read && ((a>>4)==0xc01))
			{
				// turn off KDBSTRB
				for (int i(0); i < 0x10; ++i)
					hardware[i] &= 0x7f;
			}
			else if (!b_read && (a==0xc008||a==0xc009)
			{
				b_aux = (a-0xc008)>0;
			}
		}
		else // slot-specific I/O addresses
		{
			handle_slot_io(a,b_read);
		}
		p = hardware-0xC000+a;
	}
	else if (0xC100 <= a && a < 0xD000)
	{
		if (b_read)
			p = rom-0xC100+a;
	}
	else if (0xD000 <= a && a < 0xE000)
	{
		if (b_read && !b_read_ram)
			p = rom-0xC100+a;
		else if (b_read || b_write_ram)
		{
			if (!b_bank2 && !b_aux)
				p = main_bsr1-0xD000+a;
			else if (!b_bank2 && b_aux)
				p = aux_bsr1-0xD000+a;
			else if (b_bank2 && !b_aux)
				p = main_bsr2-0xD000+a;
			else if (b_bank2 && b_aux)
				p = aux_bsr2-0xD000+a;
		}
	}
	else if (0xE000 <= a && a < 0x10000)
	{
		if (b_read && !b_read_ram)
			p = rom-0xC100+a;
		else if (b_read || b_write_ram)
		{
			if (b_aux)
				p = aux_ram_hi-0xE000+a;
			else
				p = main_ram_hi-0xE000+a;
		}
	}
	return p;
}

byte ii_c_memory_t::read_byte(addr a)
{
	byte* p = get_mem_ptr(a,true);
	return *p;
}

void ii_c_memory_t::write_byte(addr a, byte b)
{
	byte* p = get_mem_ptr(a,false);
	if (!p)
		return;

	*p = b;
}

void ii_c_memory_t::fill_test_text_screen()
{
	byte* p = get_text_base();
	byte a = 193; // "A"

	for (int s(0); s < 3; ++s)
	{
		for (int n(0); n < 8; ++n)
		{
			for (int c(0); c < 40; ++c)
			{
				*p++ = a;
			}
			a++;
			p += 0x58;
		}
		p -= 0x3d8;
	}
}

byte* ii_c_memory_t::get_text_base()
{
	// $0400 = text screen base
	return main_ram_lo-0x0200+0x0400;
}

byte* ii_c_memory_t::get_kdb()
{
	// $C000 -- KDB
	return hardware-0xc000+0xc000;
}

byte* ii_c_memory_t::get_kdbstrb()
{
	// $C010 -- KDBSTRB or AKD
	return hardware-0xc000+0xc010;
}

void ii_c_memory_t::handle_slot_io(addr a, bool b_read)
{
	/*
	from the address, we determine:
		the slot:
			0 ($C080-$C08F)
			1 ($C090-$C09F)
			...
		the switch "Q" number:
			Q0 ($C0x0 and $C0x1)
			Q1 ($C0x2 and $C0x3)
			...
		whether turning if on or off:
			on (low bit set)
			off (low bit clear)
	*/
	int slot = (a-0xc080)>>4;
	int q = (a&0x000f)>>1;
	bool b_turn_on = !!(a&1);

	switch (slot)
	{
		case 0: // memory routines (bank switching, etc.)
		{
			if (b_read)
			{
				switch (a)
				{
					case 0xc080:
					{
						b_read_ram = true; b_write_ram = false; b_bank2 = true;
					}
					break;
					case 0xc081:
					{
						b_read_ram = false; b_write_ram = true; b_bank2 = true;
					}
					break;
					case 0xc082:
					{
						b_read_ram = false; b_write_ram = false; b_bank2 = true;
					}
					break;
					case 0xc083:
					{
						b_read_ram = true; b_write_ram = true; b_bank2 = true;
					}
					break;
					case 0xc088:
					{
						b_read_ram = true; b_write_ram = false; b_bank2 = false;
					}
					break;
					case 0xc089:
					{
						b_read_ram = false; b_write_ram = true; b_bank2 = false;
					}
					break;
					case 0xc08a:
					{
						b_read_ram = false; b_write_ram = false; b_bank2 = false;
					}
					break;
					case 0xc08b:
					{
						b_read_ram = true; b_write_ram = true; b_bank2 = false;
					}
					break;
				}
			}
		}
		break;
		case 1: // RS-232 serial port 1 (for printer)
		{
		}
		break;
		case 2: // RS-232 serial port 2 (for modem)
		{
		}
		break;
		case 3: // 80-column video card
		{
		}
		break;
		case 4: // mouse port
		{
		}
		break;
		case 5: // drive (intelligent disk port device)
		{
		}
		case 6: // floppy drive
		{
		}
		break;
		case 7: // reserved
		{
		}
		break;
	}
}
