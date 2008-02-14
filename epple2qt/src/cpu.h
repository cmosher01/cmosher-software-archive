/***************************************************************************
 *   Copyright (C) 2008 by Chris Mosher,,,   *
 *   chris@mosher.mine.nu   *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 ***************************************************************************/
#ifndef CPU_H
#define CPU_H

class AddressBus;

class CPU
{
private:
	unsigned char adl;
	unsigned char adh;
	unsigned char bal;
	unsigned char bah;
	unsigned char ial;
	unsigned char iah;
	unsigned char idx;
	signed char offset;
	bool branch;
	char sc;
	bool wc;

 	bool IRQ;
    bool NMI;
    bool reset;

    bool started;

	unsigned char a;
	unsigned char x;
	unsigned char y;
	unsigned char s;
	unsigned char p;
	unsigned short pc;

	AddressBus& addressBus;

	unsigned short address;
	unsigned char data;

	unsigned char opcode;

	char t;


	static void (CPU::*addr[])();
	static void (CPU::*exec[])();

	void subsequentCycle();

	void read();
	void write();
	void execute();
	void done();

	unsigned char pch();
	unsigned char pcl();
	unsigned short sp();
	unsigned char push();
	unsigned char pull();
	void setIndex();
	unsigned char getIndex();
	unsigned short ad();
	unsigned short ia();
	unsigned short ba();
	unsigned short combine(const unsigned char lo, const unsigned char hi);
	void setP(const unsigned char mask, const unsigned char val);
	void setStatusRegisterNZ(const unsigned char val);
	void setFlagCarry(const short val);
	void setFlagBorrow(const short val);
	unsigned char shiftLeft(unsigned char byt);
	unsigned char shiftRight(unsigned char byt);
	unsigned char rotateLeft(unsigned char byt);
	unsigned char rotateRight(unsigned char byt);

	void addr_SINGLE();
	void addr_INTERNAL_IMMEDIATE();
	void addr_INTERNAL_ZERO_PAGE();
	void addr_INTERNAL_ABSOLUTE();
	void addr_INTERNAL_INDIRECT_X();
	void addr_INTERNAL_ABSOLUTE_XY();
	void addr_INTERNAL_ZERO_PAGE_XY();
	void addr_INTERNAL_INDIRECT_Y();
	void addr_STORE_ZERO_PAGE();
	void addr_STORE_ABSOLUTE();
	void addr_STORE_INDIRECT_X();
	void addr_STORE_ABSOLUTE_XY();
	void addr_STORE_ZERO_PAGE_XY();
	void addr_STORE_INDIRECT_Y();
	void addr_RMW_ZERO_PAGE();
	void addr_RMW_ABSOLUTE();
	void addr_RMW_ZERO_PAGE_X();
	void addr_RMW_ABSOLUTE_X();
	void addr_MISC_PUSH();
	void addr_MISC_PULL();
	void addr_MISC_JSR();
	void addr_MISC_BREAK();
	void addr_MISC_RTI();
	void addr_JMP_ABSOLUTE();
	void addr_JMP_INDIRECT();
	void addr_RTS();
	void addr_BRANCH();
	void addr_IRQ();
	void addr_RESET();
	void addr_NMI();

	void LDA();
	void LDX();
	void LDY();
	void STA();
	void STX();
	void STY();
	void CMP();
	void CPX();
	void CPY();
	void AND();
	void ORA();
	void EOR();
	void ASL();
	void ASL_A();
	void LSR();
	void LSR_A();
	void ROL();
	void ROL_A();
	void ROR();
	void ROR_A();
	void ADC();
	void SBC();
	void INC();
	void DEC();
	void INX();
	void INY();
	void DEX();
	void DEY();
	void BIT();
	void PHA();
	void PHP();
	void PLA();
	void PLP();
	void BRK();
	void RTI();
	void JMP();
	void RTS();
	void JSR();
	void BNE();
	void BEQ();
	void BVC();
	void BVS();
	void BCC();
	void BCS();
	void BPL();
	void BMI();
	void TAX();
	void TXA();
	void TAY();
	void TYA();
	void TXS();
	void TSX();
	void CLC();
	void SEC();
	void CLI();
	void SEI();
	void CLV();
	void CLD();
	void SED();
	void NOP();
	void Unoff();
	void Unoff1();
	void Unoff2();
	void Unoff3();
	void Hang();

public:
	CPU(AddressBus& addressBus);
	~CPU();
};

#endif
