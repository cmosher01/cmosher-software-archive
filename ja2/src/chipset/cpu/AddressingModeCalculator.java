package chipset.cpu;
/*
 * Created on Sep 11, 2007
 */
class AddressingModeCalculator
{
	public static int getMode(final int opcode)
    {
		return modes[opcode];
		// TODO undocumented instructions not yet implemented
	}

	private static final int[] modes = new int[]
  	{
		Addressing.MISC_BREAK,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.RMW_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.MISC_PUSH,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.RMW_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.RMW_ZERO_PAGE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.RMW_ABSOLUTE_X,
		Addressing.SINGLE,
		Addressing.MISC_JSR,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.RMW_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.MISC_PULL,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.RMW_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.RMW_ZERO_PAGE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.RMW_ABSOLUTE_X,
		Addressing.SINGLE,
		Addressing.MISC_RTI,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.RMW_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.MISC_PUSH,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.JMP_ABSOLUTE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.RMW_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.RMW_ZERO_PAGE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.RMW_ABSOLUTE_X,
		Addressing.SINGLE,
		Addressing.RTS,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.RMW_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.MISC_PULL,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.JMP_INDIRECT,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.RMW_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.RMW_ZERO_PAGE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.RMW_ABSOLUTE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.STORE_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.STORE_ZERO_PAGE,
		Addressing.STORE_ZERO_PAGE,
		Addressing.STORE_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.STORE_ABSOLUTE,
		Addressing.STORE_ABSOLUTE,
		Addressing.STORE_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.STORE_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.STORE_ZERO_PAGE_XY,
		Addressing.STORE_ZERO_PAGE_XY,
		Addressing.STORE_ZERO_PAGE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.STORE_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.STORE_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.RMW_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.RMW_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.RMW_ZERO_PAGE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.RMW_ABSOLUTE_X,
		Addressing.SINGLE,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.INTERNAL_INDIRECT_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.INTERNAL_ZERO_PAGE,
		Addressing.RMW_ZERO_PAGE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_IMMEDIATE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.INTERNAL_ABSOLUTE,
		Addressing.RMW_ABSOLUTE,
		Addressing.SINGLE,
		Addressing.BRANCH,
		Addressing.INTERNAL_INDIRECT_Y,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ZERO_PAGE_XY,
		Addressing.RMW_ZERO_PAGE_X,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.SINGLE,
		Addressing.INTERNAL_ABSOLUTE_XY,
		Addressing.RMW_ABSOLUTE_X,
		Addressing.SINGLE,
		Addressing.NMI,
		Addressing.RESET,
		Addressing.IRQ
	};
}
