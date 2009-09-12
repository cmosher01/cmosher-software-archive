#include "stdafx.h"
#include "opcode.h"

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
