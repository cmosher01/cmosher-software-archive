#pragma once
#include "apltypes.h"
#include "aplregs.h"
#include "aplmem.h"
#include "opcode.h"

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
