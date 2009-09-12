#pragma once
#include "apltypes.h"

class ii_c_memory_t
{
	byte main_ram_hi[0x10000-0xe000]; // $E000-$FFFF main RAM
	byte main_bsr1[0xe000-0xd000]; // $D000-$DFFF main BSR 1
	byte main_bsr2[0xe000-0xd000]; // $D000-$DFFF main BSR 2
	// [0xd000-0xc000]; // $C000-$CFFF no RAM
	byte main_ram_lo[0xc000-0x0200]; // $0200-$BFFF main RAM, pages $02-$BF
	byte main_ram_p01[0x0200-0x0000]; // $0000-$01FF main RAM, pages $00-$01

	byte aux_ram_hi[0x10000-0xe000]; // $E000-$FFFF aux RAM
	byte aux_bsr1[0xe000-0xd000]; // $D000-$DFFF aux BSR 1
	byte aux_bsr2[0xe000-0xd000]; // $D000-$DFFF aux BSR 2
	// [0xd000-0xc000]; // $C000-$CFFF no RAM
	byte aux_ram_lo[0xc000-0x0200]; // $0200-$BFFF aux RAM, pages $02-$BF
	byte aux_ram_p01[0x0200-0x0000]; // $0000-$01FF aux RAM, pages $00-$01

	byte rom[0x10000-0xc100]; // $C100-$FFFF ROM
	// [0xc100-0xc000] // $C000-$C0FF I/O area (no actual RAM or ROM)
	byte hardware[0xc100-0xc000];

	// current state of switches:
	// brilliant idea: use the hardware array itself instead of these bools: ???
	bool b_read_ram; // t: read RAM, f: read ROM
	bool b_write_ram; // t: write RAM, f: write-protect RAM
	bool b_bank2; // t: use bsr1, f: use bsr2
	bool b_aux; // t: use aux RAM (p0,p1,bsr), f: use main RAM (p0,p1,bsr)
	bool b_aux_lo; // t: use aux RAM (p2-pbf), f: use main RAM (p2-pbf)

	byte* get_mem_ptr(addr a, bool b_read);
	void load_rom();
	void fill_ram();
	void fill_ram_section(byte*p, int c);
	void handle_slot_io(addr a, bool b_read);

public:
	ii_c_memory_t() {}
	void setup();
	byte read_byte(addr a);
	void write_byte(addr a, byte b);

	byte* get_text_base();
	byte* get_kdb();
	byte* get_kdbstrb();

	void fill_test_text_screen();
};
