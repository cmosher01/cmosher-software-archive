/*
 * Created on Sep 11, 2007
 */
public class AddressingModeCalculator
{
	public static Addressing getMode(final int opcode)
    {
		// TODO undocumented instructions not yet implemented
        switch (opcode)
        {
        case 0x00:
            return Addressing.MISC_BREAK;

        case 0x01:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0x02:
            return Hang();

        case 0x03:
            return Unoff();

        case 0x04:
            return Unoff2();

        case 0x05:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0x06:
            return Addressing.RMW_ZERO_PAGE;

        case 0x07:
            return Unoff();

        case 0x08:
            return Addressing.MISC_PUSH;

        case 0x09:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0x0A:
            return Addressing.SINGLE;

        case 0x0B:
            return Unoff();

        case 0x0C:
            return Unoff3();

        case 0x0D:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0x0E:
            return Addressing.RMW_ABSOLUTE;

        case 0x0F:
            return Unoff();

        case 0x10:
            return Addressing.BRANCH;

        case 0x11:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0x12:
            return Hang();

        case 0x13:
            return Unoff();

        case 0x14:
            return Unoff2();

        case 0x15:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0x16:
            return Addressing.RMW_ZERO_PAGE_X;

        case 0x17:
            return Unoff();

        case 0x18:
            return Addressing.SINGLE;

        case 0x19:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x1A:
            return Unoff1();

        case 0x1B:
            return Unoff();

        case 0x1C:
            return Unoff3();

        case 0x1D:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x1E:
            return Addressing.RMW_ABSOLUTE_X;

        case 0x1F:
            return Unoff();

        case 0x20:
            return Addressing.MISC_JSR;

        case 0x21:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0x22:
            return Hang();

        case 0x23:
            return Unoff();

        case 0x24:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0x25:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0x26:
            return Addressing.RMW_ZERO_PAGE;

        case 0x27:
            return Unoff();

        case 0x28:
            return Addressing.MISC_PULL;

        case 0x29:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0x2A:
            return Addressing.SINGLE;

        case 0x2B:
            return Unoff();

        case 0x2C:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0x2D:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0x2E:
            return Addressing.RMW_ABSOLUTE;

        case 0x2F:
            return Unoff();

        case 0x30:
            return Addressing.BRANCH;

        case 0x31:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0x32:
            return Hang();

        case 0x33:
            return Unoff();

        case 0x34:
            return Unoff2();

        case 0x35:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0x36:
            return Addressing.RMW_ZERO_PAGE_X;

        case 0x37:
            return Unoff();

        case 0x38:
            return Addressing.SINGLE;

        case 0x39:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x3A:
            return Unoff1();

        case 0x3B:
            return Unoff();

        case 0x3C:
            return Unoff3();

        case 0x3D:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x3E:
            return Addressing.RMW_ABSOLUTE_X;

        case 0x3F:
            return Unoff();

        case 0x40:
            return Addressing.MISC_RTI;

        case 0x41:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0x42:
            return Hang();

        case 0x43:
            return Unoff();

        case 0x44:
            return Unoff2();

        case 0x45:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0x46:
            return Addressing.RMW_ZERO_PAGE;

        case 0x47:
            return Unoff();

        case 0x48:
            return Addressing.MISC_PUSH;

        case 0x49:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0x4A:
            return Addressing.SINGLE;

        case 0x4B:
            return Unoff();

        case 0x4C:
            return Addressing.JMP_ABSOLUTE;

        case 0x4D:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0x4E:
            return Addressing.RMW_ABSOLUTE;

        case 0x4F:
            return Unoff();

        case 0x50:
            return Addressing.BRANCH;

        case 0x51:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0x52:
            return Hang();

        case 0x53:
            return Unoff();

        case 0x54:
            return Unoff2();

        case 0x55:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0x56:
            return Addressing.RMW_ZERO_PAGE_X;

        case 0x57:
            return Unoff();

        case 0x58:
            return Addressing.SINGLE;

        case 0x59:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x5A:
            return Unoff1();

        case 0x5B:
            return Unoff();

        case 0x5C:
            return Unoff3();

        case 0x5D:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x5E:
            return Addressing.RMW_ABSOLUTE_X;

        case 0x5F:
            return Unoff();

        case 0x60:
            return Addressing.RTS;

        case 0x61:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0x62:
            return Hang();

        case 0x63:
            return Unoff();

        case 0x64:
            return Unoff2();

        case 0x65:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0x66:
            return Addressing.RMW_ZERO_PAGE;

        case 0x67:
            return Unoff();

        case 0x68:
            return Addressing.MISC_PULL;

        case 0x69:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0x6A:
            return Addressing.SINGLE;

        case 0x6B:
            return Unoff();

        case 0x6C:
            return Addressing.JMP_INDIRECT;

        case 0x6D:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0x6E:
            return Addressing.RMW_ABSOLUTE;

        case 0x6F:
            return Unoff();

        case 0x70:
            return Addressing.BRANCH;

        case 0x71:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0x72:
            return Hang();

        case 0x73:
            return Unoff();

        case 0x74:
            return Unoff2();

        case 0x75:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0x76:
            return Addressing.RMW_ZERO_PAGE_X;

        case 0x77:
            return Unoff();

        case 0x78:
            return Addressing.SINGLE;

        case 0x79:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x7A:
            return Unoff1();

        case 0x7B:
            return Unoff();

        case 0x7C:
            return Unoff3();

        case 0x7D:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0x7E:
            return Addressing.RMW_ABSOLUTE_X;

        case 0x7F:
            return Unoff();

        case 0x80:
            return Unoff2();

        case 0x81:
            return Addressing.STORE_INDIRECT_X;

        case 0x82:
            return Unoff2();

        case 0x83:
            return Unoff();

        case 0x84:
            return Addressing.STORE_ZERO_PAGE;

        case 0x85:
            return Addressing.STORE_ZERO_PAGE;

        case 0x86:
            return Addressing.STORE_ZERO_PAGE;

        case 0x87:
            return Unoff();

        case 0x88:
            return Addressing.SINGLE;

        case 0x89:
            return Unoff2();

        case 0x8A:
            return Addressing.SINGLE;

        case 0x8B:
            return Unoff();

        case 0x8C:
            return Addressing.STORE_ABSOLUTE;

        case 0x8D:
            return Addressing.STORE_ABSOLUTE;

        case 0x8E:
            return Addressing.STORE_ABSOLUTE;

        case 0x8F:
            return Unoff();

        case 0x90:
            return Addressing.BRANCH;

        case 0x91:
            return Addressing.STORE_INDIRECT_Y;

        case 0x92:
            return Hang();

        case 0x93:
            return Unoff();

        case 0x94:
            return Addressing.STORE_ZERO_PAGE_XY;

        case 0x95:
            return Addressing.STORE_ZERO_PAGE_XY;

        case 0x96:
            return Addressing.STORE_ZERO_PAGE_XY;

        case 0x97:
            return Unoff();

        case 0x98:
            return Addressing.SINGLE;

        case 0x99:
            return Addressing.STORE_ABSOLUTE_XY;

        case 0x9A:
            return Addressing.SINGLE;

        case 0x9B:
            return Unoff();

        case 0x9C:
            return Unoff();

        case 0x9D:
            return Addressing.STORE_ABSOLUTE_XY;

        case 0x9E:
            return Unoff();

        case 0x9F:
            return Unoff();

        case 0xA0:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xA1:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0xA2:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xA3:
            return Unoff();

        case 0xA4:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xA5:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xA6:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xA7:
            return Unoff();

        case 0xA8:
            return Addressing.SINGLE;

        case 0xA9:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xAA:
            return Addressing.SINGLE;

        case 0xAB:
            return Unoff();

        case 0xAC:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xAD:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xAE:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xAF:
            return Unoff();

        case 0xB0:
            return Addressing.BRANCH;

        case 0xB1:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0xB2:
            return Hang();

        case 0xB3:
            return Unoff();

        case 0xB4:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0xB5:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0xB6:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0xB7:
            return Unoff();

        case 0xB8:
            return Addressing.SINGLE;

        case 0xB9:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xBA:
            return Addressing.SINGLE;

        case 0xBB:
            return Unoff();

        case 0xBC:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xBD:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xBE:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xBF:
            return Unoff();

        case 0xC0:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xC1:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0xC2:
            return Unoff2();

        case 0xC3:
            return Unoff();

        case 0xC4:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xC5:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xC6:
            return Addressing.RMW_ZERO_PAGE;

        case 0xC7:
            return Unoff();

        case 0xC8:
            return Addressing.SINGLE;

        case 0xC9:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xCA:
            return Addressing.SINGLE;

        case 0xCB:
            return Unoff();

        case 0xCC:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xCD:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xCE:
            return Addressing.RMW_ABSOLUTE;

        case 0xCF:
            return Unoff();

        case 0xD0:
            return Addressing.BRANCH;

        case 0xD1:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0xD2:
            return Hang();

        case 0xD3:
            return Unoff();

        case 0xD4:
            return Unoff2();

        case 0xD5:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0xD6:
            return Addressing.RMW_ZERO_PAGE_X;

        case 0xD7:
            return Unoff();

        case 0xD8:
            return Addressing.SINGLE;

        case 0xD9:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xDA:
            return Unoff1();

        case 0xDB:
            return Unoff();

        case 0xDC:
            return Unoff3();

        case 0xDD:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xDE:
            return Addressing.RMW_ABSOLUTE_X;

        case 0xDF:
            return Unoff();

        case 0xE0:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xE1:
            return Addressing.INTERNAL_INDIRECT_X;

        case 0xE2:
            return Unoff2();

        case 0xE3:
            return Unoff();

        case 0xE4:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xE5:
            return Addressing.INTERNAL_ZERO_PAGE;

        case 0xE6:
            return Addressing.RMW_ZERO_PAGE;

        case 0xE7:
            return Unoff();

        case 0xE8:
            return Addressing.SINGLE;

        case 0xE9:
            return Addressing.INTERNAL_IMMEDIATE;

        case 0xEA:
            return Addressing.SINGLE;

        case 0xEB:
            return Unoff();

        case 0xEC:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xED:
            return Addressing.INTERNAL_ABSOLUTE;

        case 0xEE:
            return Addressing.RMW_ABSOLUTE;

        case 0xEF:
            return Unoff();

        case 0xF0:
            return Addressing.BRANCH;

        case 0xF1:
            return Addressing.INTERNAL_INDIRECT_Y;

        case 0xF2:
            return Hang();

        case 0xF3:
            return Unoff();

        case 0xF4:
            return Unoff2();

        case 0xF5:
            return Addressing.INTERNAL_ZERO_PAGE_XY;

        case 0xF6:
            return Addressing.RMW_ZERO_PAGE_X;

        case 0xF7:
            return Unoff();

        case 0xF8:
            return Addressing.SINGLE;

        case 0xF9:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xFA:
            return Unoff1();

        case 0xFB:
            return Unoff();

        case 0xFC:
            return Unoff3();

        case 0xFD:
            return Addressing.INTERNAL_ABSOLUTE_XY;

        case 0xFE:
            return Addressing.RMW_ABSOLUTE_X;

        case 0xFF:
            return Unoff();


        case 0x80000001:
        	return Addressing.NMI;
        case 0x80000002:
        	return Addressing.RESET;
        case 0x80000003:
        	return Addressing.IRQ;
        }
        return Addressing.SINGLE;
    }

	private static Addressing Unoff()
	{
		return Addressing.SINGLE;
	}

	private static Addressing Unoff1()
	{
		return Addressing.SINGLE;
	}

	private static Addressing Unoff2()
	{
		return Addressing.SINGLE;
	}

	private static Addressing Unoff3()
	{
		return Addressing.SINGLE;
	}

	private static Addressing Hang()
	{
		return Addressing.SINGLE;
	}
}
