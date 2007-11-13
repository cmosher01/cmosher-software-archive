package chipset;
/*
 * Created on Aug 1, 2007
 */
/**
 * TODO
 *
 * @author Chris Mosher
 */
public class Addressing
{
	public static final int SINGLE                 = 0;
	public static final int INTERNAL_IMMEDIATE     = 1;
	public static final int INTERNAL_ZERO_PAGE     = 2;
	public static final int INTERNAL_ABSOLUTE      = 3;
	public static final int INTERNAL_INDIRECT_X    = 4;
	public static final int INTERNAL_ABSOLUTE_XY   = 5;
	public static final int INTERNAL_ZERO_PAGE_XY  = 6;
	public static final int INTERNAL_INDIRECT_Y    = 7;
	public static final int STORE_ZERO_PAGE        = 8;
	public static final int STORE_ABSOLUTE         = 9;
	public static final int STORE_INDIRECT_X       = 10;
	public static final int STORE_ABSOLUTE_XY      = 11;
	public static final int STORE_ZERO_PAGE_XY     = 12;
	public static final int STORE_INDIRECT_Y       = 13;
	public static final int RMW_ZERO_PAGE          = 14;
	public static final int RMW_ABSOLUTE           = 15;
	public static final int RMW_ZERO_PAGE_X        = 16;
	public static final int RMW_ABSOLUTE_X         = 17;
	public static final int MISC_PUSH              = 18;
	public static final int MISC_PULL              = 19;
	public static final int MISC_JSR               = 20;
	public static final int MISC_BREAK             = 21;
	public static final int MISC_RTI               = 22;
	public static final int JMP_ABSOLUTE           = 23;
	public static final int JMP_INDIRECT           = 24;
	public static final int RTS                    = 25;
	public static final int BRANCH                 = 26;
	public static final int IRQ                    = 27;
	public static final int RESET                  = 28;
	public static final int NMI                    = 29;
}
