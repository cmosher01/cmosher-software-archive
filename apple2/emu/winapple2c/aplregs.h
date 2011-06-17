#pragma once
#include "apltypes.h"

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
