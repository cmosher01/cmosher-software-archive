#include "stdafx.h"
#include "apple2c.h"

void apple_ii_c::run()
{
	mem.setup();
	vid.setup(mem,cpu);
	cpu.setup(mem);

	cpu.reset();
	cpu.run();
}
