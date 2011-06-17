#include "stdafx.h"
#include "aplmode.h"

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
