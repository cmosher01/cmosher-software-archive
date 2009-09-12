// my2c.cpp : Defines the entry point for the application.
//

#include "stdafx.h"
#include <iostream>
#include <math.h>

typedef unsigned char byte;
typedef char offset;
typedef unsigned short int word;
typedef word addr;





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
	bool b_read_ram; // t: read RAM, f: read ROM
	bool b_write_ram; // t: write RAM, f: write-protect RAM
	bool b_bank2; // t: use bsr1, f: use bsr2
	bool b_aux; // t: use aux RAM, f: use main RAM

	byte* get_mem_ptr(addr a, bool b_read);
	void load();

public:
	ii_c_memory_t() {}
	void setup();
	byte read_byte(addr a);
	void write_byte(addr a, byte b);
};

void ii_c_memory_t::setup()
{
	b_read_ram = false;
	b_write_ram = false;
	b_bank2 = false;
	b_aux = false;
	load();
}

void ii_c_memory_t::load()
{
	FILE* f = fopen("my2c.rom","rb");
	fread(rom,1,0x10000-0xc100,f);
	fclose(f);
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
		if (b_aux)
			p = aux_ram_lo-0x0200+a;
		else
			p = main_ram_lo-0x0200+a;
	}
	else if (0xC000 <= a && a < 0xC100)
	{
		// these addresses are not connected to any memory;
		// they are instead used for I/O for various hardware
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






class opcode_t
{
	byte code;
	void calc_inst();

public:
	opcode_t(byte x) : code(x) { calc_inst(); }
	operator byte() { return code; }

	enum t
	{
#define INST(i) i,
#include "op.h"
#undef INST
		count
	};
	t inst;
};

void opcode_t::calc_inst()
{
	switch (code)
	{
#define INST(i,a,o) case o: inst = i; break;
#include "inst.h"
#undef INST
		default:
			inst = nop;
	};
}

class mode
{
public:
	enum t
	{
		immediate, // #0
		absolute, // $0000
		zero_page, // $00
		accumulator,
		implied,
		zero_page_x_indirect, // ($00,X)
		zero_page_indirect_y, // ($00),Y
		zero_page_x, // $00,X
		zero_page_y, // $00,Y
		absolute_x, // $0000,X
		absolute_y, // $0000,Y
		relative,
		absolute_indirect, // ($0000)
		absolute_x_indirect,
		zero_page_indirect // ($00)
	};
	t m_mode;

	mode(opcode_t op);
	bool operator==(t x) { return m_mode==x; }
};

mode::mode(opcode_t op)
{
	switch (op)
	{
#define INST(i,a,o) case o: m_mode = mode::a; break;
#include "inst.h"
#undef INST
	default: // unrecognized opcodes are treated as NOPs by the 65C02
		m_mode = mode::implied;
	};
}

class registers_t
{
public:
	unsigned int A; // accumulator
	unsigned int Y; // index register Y
	unsigned int X; // index register X
	addr PC; // program counter
	byte S; // stack pointer

	// processor status register bits:
	unsigned int N; // negative
	unsigned int V; // overflow
	unsigned int B; // break
	unsigned int D; // decimal mode
	unsigned int I; // IRQ disable
	unsigned int Z; // zero
	unsigned int C; // carry

	void setup()
	{
		A = Y = X = PC = N = V = B = D = I = Z = C = 0;
		S = 0xff; // can't find this documented anywhere, but it makes sense
	}
	byte get_p()
	{
		byte b(0);
		b |= C;
		b |= (Z << 1);
		b |= (I << 2);
		b |= (D << 3);
		b |= (B << 4);
		b |= (1 << 5);
		b |= (V << 6);
		b |= (N << 7);
		return b;
	}
	void set_p(byte b)
	{
		C = b & 1;
		Z = (b >> 1) & 1;
		I = (b >> 2) & 1;
		D = (b >> 3) & 1;
		B = (b >> 4) & 1;
		V = (b >> 6) & 1;
		N = (b >> 7) & 1;
	}
};

class ncr65c02_t
{
	registers_t regs;

	ii_c_memory_t* busses;

	// useful internal constants
	class vector_t
	{
	public:
		int irq;
		int res;
		int nmi;
	};
	static vector_t vectors;
	static int address_bus_bits;
	static int data_bus_bits;
	static int addressable_memory;

	HANDLE th;

	void step();

	void do_immediate(opcode_t op, byte b);
	void do_absolute(opcode_t op, addr a);
	void do_zero_page(opcode_t op, addr a);
	void do_accumulator(opcode_t op);
	void do_implied(opcode_t op);
	void do_zero_page_x_indirect(opcode_t op, addr a);
	void do_zero_page_indirect_y(opcode_t op, addr a);
	void do_zero_page_x(opcode_t op, addr a);
	void do_zero_page_y(opcode_t op, addr a);
	void do_absolute_x(opcode_t op, addr a);
	void do_absolute_y(opcode_t op, addr a);
	void do_relative(opcode_t op, offset off);
	void do_absolute_indirect(opcode_t op, addr a);
	void do_absolute_x_indirect(opcode_t op, addr a);
	void do_zero_page_indirect(opcode_t op, addr a);

	void compare(unsigned int r, byte b);
	void add(byte b);
	void fix_carry(unsigned int& x);
	void subtract(byte b);
	void and(byte b);
	void or(byte b);
	void eor(byte b);
	void fix_nz(unsigned int x);
	void bit(byte b, bool set_nv = true);
	addr stack_addr();
	void push_word(word w);
	void push_byte(byte b);
	word pop_word();
	byte pop_byte();

	bool b_irq;
	bool b_res;
	bool b_nmi;

	void handle_interrupts();
	void do_irq();
	void do_reset();
	void do_nmi();

public:
	ncr65c02_t() : busses(0) {}
	void setup(ii_c_memory_t& x);

	void run();
	void cycle();

	void irq() { b_irq = true; }
	void reset() { b_res = true; }
	void nmi() { b_nmi = true; }

	byte read_byte(addr a);
	void write_byte(addr a, byte b);
	word read_word(addr a);
	word read_zp_word(addr a);
	void write_word(addr a, word w);
	byte get_pb(); // get byte at PC (and increment)
	word get_pw(); // get word at PC (and increment)
};

inline byte lo(word w)
{
	return w & 0x00ff;
}

inline byte hi(word w)
{
	return w >> 8;
}

byte ncr65c02_t::read_byte(addr a)
{
	return busses->read_byte(a);
}

void ncr65c02_t::write_byte(addr a, byte b)
{
	busses->write_byte(a,b);
}

word ncr65c02_t::read_word(addr a)
{
	byte lob = read_byte(a++);
	byte hib = read_byte(a++);
	return (hib << 8) | lob;
}

word ncr65c02_t::read_zp_word(addr a)
{
	byte lob = read_byte(a);
	a++; a &= 0xff;
	byte hib = read_byte(a);
	return (hib << 8) | lob;
}

void ncr65c02_t::write_word(addr a, word w)
{
	write_byte(a++,lo(w));
	write_byte(a++,hi(w));
}

byte ncr65c02_t::get_pb()
{
	return read_byte(regs.PC++);
}

word ncr65c02_t::get_pw()
{
	word w = read_word(regs.PC);
	regs.PC++; regs.PC++;
	return w;
}

ncr65c02_t::vector_t ncr65c02_t::vectors;
int ncr65c02_t::address_bus_bits;
int ncr65c02_t::data_bus_bits;
int ncr65c02_t::addressable_memory;

void ncr65c02_t::setup(ii_c_memory_t& x)
{
	th = 0;

	busses = &x;

	address_bus_bits = 0x10;
	data_bus_bits = 0x08;

	addressable_memory = pow(2,address_bus_bits)+.00000001;

	vectors.irq = addressable_memory-sizeof(word);
	vectors.res = vectors.irq-sizeof(word);
	vectors.nmi = vectors.res-sizeof(word);

	b_irq = false;
	b_res = false;
	b_nmi = false;

	regs.setup();
}

void ncr65c02_t::do_irq()
{
	if (regs.I)
		return;
	push_word(regs.PC);
	push_byte(regs.get_p());
	regs.I = 1;
	regs.PC = read_word(vectors.irq);
}

void ncr65c02_t::do_reset()
{
	// (not implemented) // waste 6 cycles
	regs.I = 1; // set IRQ disable
	regs.D = 0; // clear decimal mode

	// read PC from the reset vector:
	regs.PC = read_word(vectors.res);
}

void ncr65c02_t::do_nmi()
{
	regs.PC = read_word(vectors.nmi);
}

void ncr65c02_t::handle_interrupts()
{
	if (b_nmi)
	{
		b_nmi = false;
		do_nmi();
	}

	if (b_res)
	{
		b_res = false;
		do_reset();
	}

	if (b_irq)
	{
		b_irq = false;
		do_irq();
	}
}

DWORD WINAPI cputhread(void* p)
{
	ncr65c02_t* t = reinterpret_cast<ncr65c02_t*>(p);
	t->cycle();
	return 0;
}

void ncr65c02_t::run()
{
	if (th)
		return;

	DWORD id;
	th = ::CreateThread(0,0,cputhread,reinterpret_cast<void*>(this),0,&id);
	if (!th)
		throw "Error: cannot create thread to run cpu.";
}



void ncr65c02_t::cycle()
{
	while (true)
	{
		handle_interrupts();
		step();
	}
}

void ncr65c02_t::step()
{
	// put PC on the address bus and read a byte from the data bus
	// this will be an opcode
	opcode_t op = busses->read_byte(regs.PC++);
	mode m(op);

	if (m == mode::immediate)
	{
		byte b = get_pb();
		do_immediate(op,b);
	}
	else if (m == mode::absolute)
	{
		addr a = get_pw();
		do_absolute(op,a);
	}
	else if (m == mode::zero_page)
	{
		byte b = get_pb();
		do_zero_page(op,b);// will zero extend b to a word address
	}
	else if (m == mode::accumulator)
	{
		do_accumulator(op);
	}
	else if (m == mode::implied)
	{
		do_implied(op);
	}
	else if (m == mode::zero_page_x_indirect)
	{
		byte b = get_pb();
		do_zero_page_x_indirect(op,b);
	}
	else if (m == mode::zero_page_indirect_y)
	{
		byte b = get_pb();
		do_zero_page_indirect_y(op,b);
	}
	else if (m == mode::zero_page_x)
	{
		byte b = get_pb();
		do_zero_page_x(op,b);
	}
	else if (m == mode::zero_page_y)
	{
		byte b = get_pb();
		do_zero_page_y(op,b);
	}
	else if (m == mode::absolute_x)
	{
		addr a = get_pw();
		do_absolute_x(op,a);
	}
	else if (m == mode::absolute_y)
	{
		addr a = get_pw();
		do_absolute_y(op,a);
	}
	else if (m == mode::relative)
	{
		offset b = (offset)get_pb();
		do_relative(op,b);
	}
	else if (m == mode::absolute_indirect)
	{
		addr a = get_pw();
		do_absolute_indirect(op,a);
	}
	else if (m == mode::absolute_x_indirect)
	{
		addr a = get_pw();
		do_absolute_x_indirect(op,a);
	}
	else if (m == mode::zero_page_indirect)
	{
		byte b = get_pb();
		do_zero_page_indirect(op,b);
	}
}

inline unsigned int test_n(unsigned int x)
{
	return (x & 0x80) != 0;
}

inline unsigned int test_z(unsigned int x)
{
	return x==0;
}

void ncr65c02_t::compare(unsigned int r, byte b)
{
	unsigned int t = r + 0x100; // set virtual carry bit
	t -= b;
	regs.C = (t<0x100);
	t &= 0xff;
	fix_nz(t);
}

void ncr65c02_t::fix_carry(unsigned int& x)
{
	if (x >= 0x100)
	{
		x &= 0xff;
		regs.C = 1;
	}
	else
	{
		regs.C = 0;
	}
}

void ncr65c02_t::fix_nz(unsigned int x)
{
	regs.N = test_n(x);
	regs.Z = test_z(x);
}

void ncr65c02_t::add(byte b)
{
	// ??? decimal mode

	// save the sign of A (to be checked later)
	unsigned int n_before = test_n(regs.A);

	// do the addition
	regs.A += b+regs.C;

	// set status flags
	fix_carry(regs.A);
	fix_nz(regs.A);
	regs.V = (regs.N != n_before);
}

void ncr65c02_t::subtract(byte b)
{
	// ??? decimal mode

	// save the sign of A (to be checked later)
	unsigned int n_before = test_n(regs.A);

	// do the addition
	regs.A -= b+test_z(regs.C);

	// set status flags
	if (regs.A >> (sizeof(unsigned int)-1))
	{
		regs.A &= 0xff;
		regs.C = 0;
	}
	else
	{
		regs.C = 1;
	}
	fix_nz(regs.A);
	regs.V = (regs.N != n_before);
}

void ncr65c02_t::and(byte b)
{
	regs.A &= b;
	fix_nz(regs.A);
}

void ncr65c02_t::or(byte b)
{
	regs.A |= b;
	fix_nz(regs.A);
}

void ncr65c02_t::eor(byte b)
{
	regs.A ^= b;
	fix_nz(regs.A);
}

void ncr65c02_t::bit(byte b, bool set_nv)
{
	if (set_nv)
	{
		regs.N = b&0x80;
		regs.V = b&0x40;
	}
	regs.Z = test_z(regs.A&b);
}

void ncr65c02_t::push_word(word w)
{
	push_byte(hi(w));
	push_byte(lo(w));
}

word ncr65c02_t::pop_word()
{
	byte lob = pop_byte();
	byte hib = pop_byte();
	return (hib << 8) | lob;
}

addr ncr65c02_t::stack_addr()
{
	return 0x0100+regs.S;
}

void ncr65c02_t::push_byte(byte b)
{
	write_byte(stack_addr(),b);
	regs.S--;
}

byte ncr65c02_t::pop_byte()
{
	regs.S++;
	byte b = read_byte(stack_addr());
	return b;
}

void ncr65c02_t::do_immediate(opcode_t op, byte b)
{
	if (op.inst==opcode_t::adc)
	{
		add(b);
	}
	else if (op.inst==opcode_t::and)
	{
		and(b);
	}
	else if (op.inst==opcode_t::bit)
	{
		// immediate mode BIT does not change V or N
		bit(b,false);
	}
	else if (op.inst==opcode_t::cmp)
	{
		compare(regs.A,b);
	}
	else if (op.inst==opcode_t::cpx)
	{
		compare(regs.X,b);
	}
	else if (op.inst==opcode_t::cpy)
	{
		compare(regs.Y,b);
	}
	else if (op.inst==opcode_t::eor)
	{
		eor(b);
	}
	else if (op.inst==opcode_t::lda)
	{
		regs.A = b;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::ldx)
	{
		regs.X = b;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::ldy)
	{
		regs.Y = b;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::ora)
	{
		or(b);
	}
	else if (op.inst==opcode_t::sbc)
	{
		subtract(b);
	}
}

void ncr65c02_t::do_absolute(opcode_t op, addr a)
{
	// ??? jmp, jsr, sta, stx, sty, stz shouldn't do this:???
	byte b = read_byte(a);

	if (op.inst==opcode_t::adc)
	{
		add(b);
	}
	else if (op.inst==opcode_t::and)
	{
		and(b);
	}
	else if (op.inst==opcode_t::asl)
	{
		unsigned int t = b;
		t <<= 1;
		write_byte(a,b);
		fix_carry(t);
		fix_nz(t);
	}
	else if (op.inst==opcode_t::bit)
	{
		bit(b);
	}
	else if (op.inst==opcode_t::cmp)
	{
		compare(regs.A,b);
	}
	else if (op.inst==opcode_t::cpx)
	{
		compare(regs.X,b);
	}
	else if (op.inst==opcode_t::cpy)
	{
		compare(regs.Y,b);
	}
	else if (op.inst==opcode_t::dec)
	{
		write_byte(a,--b);
		fix_nz(b);
	}
	else if (op.inst==opcode_t::eor)
	{
		eor(b);
	}
	else if (op.inst==opcode_t::inc)
	{
		write_byte(a,++b);
		fix_nz(b);
	}
	else if (op.inst==opcode_t::jmp)
	{
		regs.PC = a;
	}
	else if (op.inst==opcode_t::jsr)
	{
		push_word(regs.PC-1);
		regs.PC = a;
	}
	else if (op.inst==opcode_t::lda)
	{
		regs.A = b;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::ldx)
	{
		regs.X = b;
		fix_nz(regs.X);
	}
	else if (op.inst==opcode_t::ldy)
	{
		regs.Y = b;
		fix_nz(regs.Y);
	}
	else if (op.inst==opcode_t::lsr)
	{
		regs.C = b & 0x01;
		b >>= 1;
		write_byte(a,b);
		fix_nz(b);
	}
	else if (op.inst==opcode_t::ora)
	{
		or(b);
	}
	else if (op.inst==opcode_t::rol)
	{
		unsigned int t = test_n(b);
		b <<= 1;
		b |= regs.C;
		regs.C = t;
		write_byte(a,b);
		fix_nz(b);
	}
	else if (op.inst==opcode_t::ror)
	{
		unsigned int t = b & 0x01;
		b >>= 1;
		b |= 0x80*regs.C;
		regs.C = t;
		write_byte(a,b);
		fix_nz(b);
	}
	else if (op.inst==opcode_t::sbc)
	{
		subtract(b);
	}
	else if (op.inst==opcode_t::sta)
	{
		write_byte(a,regs.A);
	}
	else if (op.inst==opcode_t::stx)
	{
		write_byte(a,regs.X);
	}
	else if (op.inst==opcode_t::sty)
	{
		write_byte(a,regs.Y);
	}
	else if (op.inst==opcode_t::stz)
	{
		write_byte(a,0);
	}
	else if (op.inst==opcode_t::trb)
	{
		b &= ~regs.A;
		regs.Z = test_z(b);
		write_byte(a,b);
	}
	else if (op.inst==opcode_t::tsb)
	{
		b |= regs.A;
		regs.Z = test_z(b);
		write_byte(a,b);
	}
}

void ncr65c02_t::do_zero_page(opcode_t op, addr a)
{
	do_absolute(op,a);
}

void ncr65c02_t::do_accumulator(opcode_t op)
{
	if (op.inst==opcode_t::asl)
	{
		regs.A <<= 1;
		fix_carry(regs.A);
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::dea)
	{
		regs.A--;
		regs.A &= 0xff;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::ina)
	{
		regs.A++;
		regs.A &= 0xff;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::lsr)
	{
		regs.C = regs.A & 0x01;
		regs.A >>= 1;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::rol)
	{
		unsigned int t = test_n(regs.A);
		regs.A <<= 1;
		regs.A |= regs.C;
		regs.C = t;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::ror)
	{
		unsigned int t = regs.A & 0x01;
		regs.A >>= 1;
		regs.A |= 0x80*regs.C;
		regs.C = t;
		fix_nz(regs.A);
	}
}

void ncr65c02_t::do_implied(opcode_t op)
{
	if (op.inst==opcode_t::brk)
	{
		push_word(regs.PC+1);
		regs.B = 1;
		push_byte(regs.get_p());
		regs.I = 1;
		// read PC from the reset vector:
		regs.PC = read_word(vectors.res);
	}
	else if (op.inst==opcode_t::clc)
	{
		regs.C = 0;
	}
	else if (op.inst==opcode_t::cld)
	{
		regs.D = 0;
	}
	else if (op.inst==opcode_t::cli)
	{
		regs.I = 0;
	}
	else if (op.inst==opcode_t::clv)
	{
		regs.V = 0;
	}
	else if (op.inst==opcode_t::dex)
	{
		regs.X--;
		regs.X &= 0xff;
		fix_nz(regs.X);
	}
	else if (op.inst==opcode_t::dey)
	{
		regs.Y--;
		regs.Y &= 0xff;
		fix_nz(regs.Y);
	}
	else if (op.inst==opcode_t::inx)
	{
		regs.X++;
		regs.X &= 0xff;
		fix_nz(regs.X);
	}
	else if (op.inst==opcode_t::iny)
	{
		regs.Y++;
		regs.Y &= 0xff;
		fix_nz(regs.Y);
	}
	else if (op.inst==opcode_t::nop)
	{
		// really do nothing!
	}
	else if (op.inst==opcode_t::pha)
	{
		push_byte(regs.A);
	}
	else if (op.inst==opcode_t::php)
	{
		push_byte(regs.get_p());
	}
	else if (op.inst==opcode_t::phx)
	{
		push_byte(regs.X);
	}
	else if (op.inst==opcode_t::phy)
	{
		push_byte(regs.Y);
	}
	else if (op.inst==opcode_t::pla)
	{
		regs.A = pop_byte();
	}
	else if (op.inst==opcode_t::plp)
	{
		byte b = pop_byte();
		regs.set_p(b);
	}
	else if (op.inst==opcode_t::plx)
	{
		regs.X = pop_byte();
	}
	else if (op.inst==opcode_t::ply)
	{
		regs.Y = pop_byte();
	}
	else if (op.inst==opcode_t::rti)
	{
		byte b = pop_byte();
		regs.set_p(b);
		regs.PC = pop_word();
	}
	else if (op.inst==opcode_t::rts)
	{
		regs.PC = pop_word();
		regs.PC++;
	}
	else if (op.inst==opcode_t::sec)
	{
		regs.C = 1;
	}
	else if (op.inst==opcode_t::sed)
	{
		regs.D = 1;
	}
	else if (op.inst==opcode_t::sei)
	{
		regs.I = 1;
	}
	else if (op.inst==opcode_t::tax)
	{
		regs.X = regs.A;
		fix_nz(regs.X);
	}
	else if (op.inst==opcode_t::tay)
	{
		regs.Y = regs.A;
		fix_nz(regs.Y);
	}
	else if (op.inst==opcode_t::tsx)
	{
		regs.X = regs.S;
		fix_nz(regs.X);
	}
	else if (op.inst==opcode_t::txa)
	{
		regs.A = regs.X;
		fix_nz(regs.A);
	}
	else if (op.inst==opcode_t::txs)
	{
		regs.S = regs.X;
		// don't affect N or Z
	}
	else if (op.inst==opcode_t::tya)
	{
		regs.A = regs.Y;
		fix_nz(regs.A);
	}
}

void ncr65c02_t::do_zero_page_x_indirect(opcode_t op, addr a)
{
	a += regs.X;
	a = read_zp_word(a);
	do_absolute(op,a);
}

void ncr65c02_t::do_zero_page_indirect_y(opcode_t op, addr a)
{
	a = read_zp_word(a);
	a += regs.Y;
	do_absolute(op,a);
}

void ncr65c02_t::do_zero_page_x(opcode_t op, addr a)
{
	a += regs.X;
	a &= 0xff;
	do_absolute(op,a);
}

void ncr65c02_t::do_zero_page_y(opcode_t op, addr a)
{
	a += regs.Y;
	a &= 0xff;
	do_absolute(op,a);
}

void ncr65c02_t::do_absolute_x(opcode_t op, addr a)
{
	a += regs.X;
	do_absolute(op,a);
}

void ncr65c02_t::do_absolute_y(opcode_t op, addr a)
{
	a += regs.Y;
	do_absolute(op,a);
}

void ncr65c02_t::do_relative(opcode_t op, offset off)
{
	bool b_branch(false);
	if (op.inst==opcode_t::bcc)
		b_branch = !regs.C;
	else if (op.inst==opcode_t::bcs)
		b_branch = !!regs.C;
	else if (op.inst==opcode_t::beq)
		b_branch = !!regs.Z;
	else if (op.inst==opcode_t::bmi)
		b_branch = !!regs.N;
	else if (op.inst==opcode_t::bne)
		b_branch = !regs.Z;
	else if (op.inst==opcode_t::bpl)
		b_branch = !regs.N;
	else if (op.inst==opcode_t::bra)
		b_branch = true;
	else if (op.inst==opcode_t::bvc)
		b_branch = !regs.V;
	else if (op.inst==opcode_t::bvs)
		b_branch = !!regs.V;

	if (b_branch)
		regs.PC += off;
}

void ncr65c02_t::do_absolute_indirect(opcode_t op, addr a)
{
	a = read_word(a);
	if (op.inst==opcode_t::jmp)
		regs.PC = a;
}

void ncr65c02_t::do_absolute_x_indirect(opcode_t op, addr a)
{
	a += regs.X;
	a = read_word(a);
	if (op.inst==opcode_t::jmp)
		regs.PC = a;
}

void ncr65c02_t::do_zero_page_indirect(opcode_t op, addr a)
{
	a = read_zp_word(a);
	do_absolute(op,a);
}





class apple_ii_c
{
	ii_c_memory_t mem;
	ncr65c02_t cpu;

public:

	apple_ii_c() {}

	void run();
};

void apple_ii_c::run()
{
	mem.setup();
	cpu.setup(mem);

	cpu.reset();
	cpu.run();
}












int APIENTRY WinMain(HINSTANCE hInstance,
                     HINSTANCE hPrevInstance,
                     LPSTR     lpCmdLine,
                     int       nCmdShow)
{
	apple_ii_c my2c;
	my2c.run();
	while (true) {}
	return 0;
}
