#pragma once
#include "apltypes.h"

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
