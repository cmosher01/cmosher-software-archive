package nu.mine.mosher.charsets;

import java.util.HashMap;

class GedcomAnselTable
{
	static final byte[] rcAnsel=
	{
		(byte)0xA1,
		(byte)0xA2,
		(byte)0xA3,
		(byte)0xA4,
		(byte)0xA5,
		(byte)0xA6,
		(byte)0xA7,
		(byte)0xA8,
		(byte)0xA9,
		(byte)0xAA,
		(byte)0xAB,
		(byte)0xAC,
		(byte)0xAD,
		(byte)0xAE,
		(byte)0xB0,
		(byte)0xB1,
		(byte)0xB2,
		(byte)0xB3,
		(byte)0xB4,
		(byte)0xB5,
		(byte)0xB6,
		(byte)0xB7,
		(byte)0xB8,
		(byte)0xB9,
		(byte)0xBA,
		(byte)0xBC,
		(byte)0xBD,
		(byte)0xC0,
		(byte)0xC1,
		(byte)0xC2,
		(byte)0xC3,
		(byte)0xC4,
		(byte)0xC5,
		(byte)0xC6,
		(byte)0xCF,
		(byte)0xE0,
		(byte)0xE1,
		(byte)0xE2,
		(byte)0xE3,
		(byte)0xE4,
		(byte)0xE5,
		(byte)0xE6,
		(byte)0xE7,
		(byte)0xE8,
		(byte)0xE9,
		(byte)0xEA,
		(byte)0xEB,
		(byte)0xEC,
		(byte)0xED,
		(byte)0xEE,
		(byte)0xEF,
		(byte)0xF0,
		(byte)0xF1,
		(byte)0xF2,
		(byte)0xF3,
		(byte)0xF4,
		(byte)0xF5,
		(byte)0xF6,
		(byte)0xF7,
		(byte)0xF8,
		(byte)0xF9,
		(byte)0xFA,
		(byte)0xFB,
		(byte)0xFE
	};

	static final char[] rcUTF16 =
	{
		(char)0x0141,
		(char)0x00D8,
		(char)0x0110,
		(char)0x00DE,
		(char)0x00C6,
		(char)0x0152,
		(char)0x02B9,
		(char)0x00B7,
		(char)0x266D,
		(char)0x00AE,
		(char)0x00B1,
		(char)0x01A0,
		(char)0x01AF,
		(char)0x02BE,
		(char)0x02BB,
		(char)0x0142,
		(char)0x00F8,
		(char)0x0111,
		(char)0x00FE,
		(char)0x00E6,
		(char)0x0153,
		(char)0x02BA,
		(char)0x0131,
		(char)0x00A3,
		(char)0x00F0,
		(char)0x01A1,
		(char)0x01B0,
		(char)0x00B0,
		(char)0x2113,
		(char)0x2117,
		(char)0x00A9,
		(char)0x266F,
		(char)0x00BF,
		(char)0x00A1,
		(char)0x00DF,
		(char)0x0309,
		(char)0x0300,
		(char)0x0301,
		(char)0x0302,
		(char)0x0303,
		(char)0x0304,
		(char)0x0306,
		(char)0x0307,
		(char)0x0308,
		(char)0x030C,
		(char)0x030A,
		(char)0xFE20,
		(char)0xFE21,
		(char)0x0315,
		(char)0x030B,
		(char)0x0310,
		(char)0x0327,
		(char)0x0328,
		(char)0x0323,
		(char)0x0324,
		(char)0x0325,
		(char)0x0333,
		(char)0x0332,
		(char)0x0326,
		(char)0x031C,
		(char)0x032E,
		(char)0xFE22,
		(char)0xFE23,
		(char)0x0313
	};

	private static final HashMap mapDecoder = new HashMap();
	private static final HashMap mapEncoder = new HashMap();

	public static HashMap getDecoder()
	{
		assert rcAnsel.length == rcUTF16.length;
		assert rcAnsel.length > 0;

		if (mapDecoder.isEmpty())
		{
			for (int i = 0; i < rcAnsel.length; i++)
            {
                byte b = rcAnsel[i];
                char c = rcUTF16[i];
				mapDecoder.put(new Byte(b),new Character(c));
            }
		}

		return mapDecoder;
	}

	public static HashMap getEncoder()
	{
		assert rcAnsel.length == rcUTF16.length;
		assert rcAnsel.length > 0;

		if (mapEncoder.isEmpty())
		{
			for (int i = 0; i < rcAnsel.length; i++)
			{
				byte b = rcAnsel[i];
				char c = rcUTF16[i];
				mapEncoder.put(new Character(c),new Byte(b));
			}
		}

		return mapEncoder;
	}

	private GedcomAnselTable()
	{
	}
}
