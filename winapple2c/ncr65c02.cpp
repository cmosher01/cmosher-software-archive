#include "stdafx.h"
#include <math.h>
#include "ncr65c02.h"
#include "aplmode.h"


inline byte lo(word w)
{
	return w & 0x00ff;
}

inline byte hi(word w)
{
	return w >> 8;
}

inline unsigned int test_n(unsigned int x)
{
	return (x & 0x80) != 0;
}

inline unsigned int test_z(unsigned int x)
{
	return x==0;
}





// static member variables
ncr65c02_t::vector_t ncr65c02_t::vectors;
int ncr65c02_t::address_bus_bits;
int ncr65c02_t::data_bus_bits;
int ncr65c02_t::addressable_memory;



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

static DWORD WINAPI cputhread(void* p)
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
	// fetch the opcode from the current PC
	opcode_t op = get_pb();
	// figure out the addressing mode
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
