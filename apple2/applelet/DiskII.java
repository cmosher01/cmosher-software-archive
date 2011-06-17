public class DiskII extends Peripheral
{
	private static final int NUM_DRIVES = 2;
	private static final int NUM_TRACKS = 35;
	private static final int TRACK_SIZE = 0x1a00;
	private static final int SECTOR_SIZE = 383;

	private static final int PROM[] =
	{
		0xA2, 0x20, 0xA0, 0x00, 0xA2, 0x03, 0x86, 0x3C, 0x8A, 0x0A, 0x24, 0x3C, 0xF0, 0x10, 0x05, 0x3C, 0x49, 0xFF, 0x29, 0x7E, 0xB0, 0x08, 0x4A, 0xD0, 0xFB, 0x98, 0x9D, 0x56, 0x03, 0xC8, 0xE8, 0x10,
		0xE5, 0x20, 0x58, 0xFF, 0xBA, 0xBD, 0x00, 0x01, 0x0A, 0x0A, 0x0A, 0x0A, 0x85, 0x2B, 0xAA, 0xBD, 0x8E, 0xC0, 0xBD, 0x8C, 0xC0, 0xBD, 0x8A, 0xC0, 0xBD, 0x89, 0xC0, 0xA0, 0x50, 0xBD, 0x80, 0xC0,
		0x98, 0x29, 0x03, 0x0A, 0x05, 0x2B, 0xAA, 0xBD, 0x81, 0xC0, 0xA9, 0x56, 0xa9, 0x00, 0xea, 0x88, 0x10, 0xEB, 0x85, 0x26, 0x85, 0x3D, 0x85, 0x41, 0xA9, 0x08, 0x85, 0x27, 0x18, 0x08, 0xBD, 0x8C,
		0xC0, 0x10, 0xFB, 0x49, 0xD5, 0xD0, 0xF7, 0xBD, 0x8C, 0xC0, 0x10, 0xFB, 0xC9, 0xAA, 0xD0, 0xF3, 0xEA, 0xBD, 0x8C, 0xC0, 0x10, 0xFB, 0xC9, 0x96, 0xF0, 0x09, 0x28, 0x90, 0xDF, 0x49, 0xAD, 0xF0,
		0x25, 0xD0, 0xD9, 0xA0, 0x03, 0x85, 0x40, 0xBD, 0x8C, 0xC0, 0x10, 0xFB, 0x2A, 0x85, 0x3C, 0xBD, 0x8C, 0xC0, 0x10, 0xFB, 0x25, 0x3C, 0x88, 0xD0, 0xEC, 0x28, 0xC5, 0x3D, 0xD0, 0xBE, 0xA5, 0x40,
		0xC5, 0x41, 0xD0, 0xB8, 0xB0, 0xB7, 0xA0, 0x56, 0x84, 0x3C, 0xBC, 0x8C, 0xC0, 0x10, 0xFB, 0x59, 0xD6, 0x02, 0xA4, 0x3C, 0x88, 0x99, 0x00, 0x03, 0xD0, 0xEE, 0x84, 0x3C, 0xBC, 0x8C, 0xC0, 0x10,
		0xFB, 0x59, 0xD6, 0x02, 0xA4, 0x3C, 0x91, 0x26, 0xC8, 0xD0, 0xEF, 0xBC, 0x8C, 0xC0, 0x10, 0xFB, 0x59, 0xD6, 0x02, 0xD0, 0x87, 0xA0, 0x00, 0xA2, 0x56, 0xCA, 0x30, 0xFB, 0xB1, 0x26, 0x5E, 0x00,
		0x03, 0x2A, 0x5E, 0x00, 0x03, 0x2A, 0x91, 0x26, 0xC8, 0xD0, 0xEE, 0xE6, 0x27, 0xE6, 0x3D, 0xA5, 0x3D, 0xCD, 0x00, 0x08, 0xA6, 0x2B, 0x90, 0xDB, 0x4C, 0x01, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00 
	};




	private int track[]; // track # for each disk
	private byte track_data[][]; // array of track data for each drive
	private boolean read_mode[];
	private boolean write_protect[];
	private int track_index[]; // position of read head along track
	public byte data[][][]; // data in both disks
	private int drive; // current drive
	//private boolean motor; // is motor on?

	public DiskII(EmAppleII theapple)
	{
		super(theapple);
		track = new int[NUM_DRIVES];
		track_index = new int[NUM_DRIVES];
		track_data = new byte[NUM_DRIVES][];
		read_mode = new boolean[NUM_DRIVES];
		write_protect = new boolean[NUM_DRIVES];
		data = new byte[NUM_DRIVES][NUM_TRACKS][];
	}

	int read_latch()
	{
		track_index[drive] = (track_index[drive] + 1) % TRACK_SIZE;
		/*
		 * System.out.println("Read latch @ " +
		 * Integer.toString(track_index[drive], 16) + " track " +
		 * Integer.toString(track[drive]) + " drive " + Integer.toString(drive) + " = " +
		 * Integer.toString(value, 16) );
		 */
		if (track_data[drive] != null)
		{
			return (track_data[drive][track_index[drive]] & 0xff);
		}
		return emu.noise() | 0x80;
	}

	void write_latch(int value)
	{
		track_index[drive] = (track_index[drive] + 1) % TRACK_SIZE;
		if (track_data[drive] != null)
			track_data[drive][track_index[drive]] = (byte)value;
	}

	/*
	 * Implement the Disk II softswitches that perform the same function whether
	 * they are read or written to.
	 */
//	private static final int phaseup[] =
//	{ 3, 5, 7, 1 };
//	private static final int phasedn[] =
//	{ 7, 1, 3, 5 };

	public int doIO(int address, int value)
	{
		switch (address & 0x0f)
		{
			/*
			 * Turn motor phases 0 to 3 on. Turning on the previous phase + 1
			 * increments the track position, turning on the previous phase - 1
			 * decrements the track position. In this scheme phase 0 and 3 are
			 * considered to be adjacent. The previous phase number can be
			 * computed as the track number % 4.
			 */
			case 0x1:
			case 0x3:
			case 0x5:
			case 0x7:
				int new_track = track[drive];
				int phase = (address >> 1) & 3;
				// if new phase is even and current phase is odd
				if (phase == ((new_track - 1) & 3))
				{
					if (new_track > 0)
						new_track--;
				}
				else if (phase == ((new_track + 1) & 3))
				{
					if (new_track < NUM_TRACKS * 2 - 1)
						new_track++;
				}
				if ((new_track & 1) == 0)
				{
					track_data[drive] = data[drive][new_track >> 1];
				}
				else
					track_data[drive] = null;
				track[drive] = new_track;
				System.out.println("phase " + Integer.toString(address,16) + " track = " + Float.toString((float)track[drive] / 2));
			break;
			/*
			 * Turn drive motor off.
			 */
			case 0x8:
				//motor = false;
			break;
			/*
			 * Turn drive motor on.
			 */
			case 0x9:
				//motor = true;
			break;
			/*
			 * Select drive 1.
			 */
			case 0xa:
				drive = 0;
			break;
			/*
			 * Select drive 2.
			 */
			case 0xb:
				drive = 1;
			break;
			/*
			 * Select write mode.
			 */
			case 0xf:
				read_mode[drive] = false;
				/*
				 * Read a disk byte if read mode is active.
				 */
			case 0xC:
				if (read_mode[drive])
					return read_latch();
			break;
			/*
			 * Select read mode and read the write protect status.
			 */
			case 0xE:
				read_mode[drive] = true;
				/*
				 * Write a disk byte if write mode is active and the disk is not
				 * write protected.
				 */
			case 0xD:
				if (value >= 0 && !read_mode[drive] && !write_protect[drive])
					write_latch(value);
				/*
				 * Read the write protect status only.
				 */
				return write_protect[drive] ? 0x80 : 0x00;
		}
		return emu.noise();
	}

	public int doHighIO(int address, int value)
	{
		return PROM[address & 0xff];
	}

	/* --------------- TRACK CONVERSION ROUTINES ---------------------- */
	/*
	 * Normal byte (lower six bits only) -> disk byte translation table.
	 */
	private static final int byte_translation[] =
	{ 0x96, 0x97, 0x9a, 0x9b, 0x9d, 0x9e, 0x9f, 0xa6, 0xa7, 0xab, 0xac, 0xad, 0xae, 0xaf, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb9, 0xba, 0xbb, 0xbc, 0xbd, 0xbe, 0xbf, 0xcb, 0xcd, 0xce, 0xcf, 0xd3,
		0xd6, 0xd7, 0xd9, 0xda, 0xdb, 0xdc, 0xdd, 0xde, 0xdf, 0xe5, 0xe6, 0xe7, 0xe9, 0xea, 0xeb, 0xec, 0xed, 0xee, 0xef, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf9, 0xfa, 0xfb, 0xfc, 0xfd, 0xfe, 0xff };
	/*
	 * Sector skewing table.
	 */
	private static final int skewing_table[] =
	{ 0, 7, 14, 6, 13, 5, 12, 4, 11, 3, 10, 2, 9, 1, 8, 15 };

	/*
	 * Encode a 256-byte sector as SECTOR_SIZE disk bytes as follows:
	 * 
	 * 14 sync bytes 3 address header bytes 8 address block bytes 3 address
	 * trailer bytes 6 sync bytes 3 data header bytes 343 data block bytes 3
	 * data trailer bytes
	 */
	public static void nibblizeSector(int vol, int trk, int sector, byte in[], int in_ofs, byte out[], int i)
	{
		int loop, checksum, prev_value, value;
		int sector_buffer[] = new int[258];
		value = 0;
		/*
		 * Step 1: write 6 sync bytes (0xff's). Normally these would be written
		 * as 10-bit bytes with two extra zero bits, but for the purpose of
		 * emulation normal 8-bit bytes will do, since the emulated drive will
		 * always be in sync.
		 */
		for (loop = 0; loop < 14; loop++)
			out[i++] = (byte)0xff;
		/*
		 * Step 2: write the 3-byte address header (0xd5 0xaa 0x96).
		 */
		out[i++] = (byte)0xd5;
		out[i++] = (byte)0xaa;
		out[i++] = (byte)0x96;
		/*
		 * Step 3: write the address block. Use 4-and-4 encoding to convert the
		 * volume, track and sector and checksum into 2 disk bytes each. The
		 * checksum is a simple exclusive OR of the first three values.
		 */
		out[i++] = (byte)((vol >> 1) | 0xaa);
		out[i++] = (byte)(vol | 0xaa);
		checksum = vol;
		out[i++] = (byte)((trk >> 1) | 0xaa);
		out[i++] = (byte)(trk | 0xaa);
		checksum ^= trk;
		out[i++] = (byte)((sector >> 1) | 0xaa);
		out[i++] = (byte)(sector | 0xaa);
		checksum ^= sector;
		out[i++] = (byte)((checksum >> 1) | 0xaa);
		out[i++] = (byte)(checksum | 0xaa);
		/*
		 * Step 4: write the 3-byte address trailer (0xde 0xaa 0xeb).
		 */
		out[i++] = (byte)(0xde);
		out[i++] = (byte)(0xaa);
		out[i++] = (byte)(0xeb);
		/*
		 * Step 5: write another 6 sync bytes.
		 */
		for (loop = 0; loop < 6; loop++)
			out[i++] = (byte)(0xff);
		/*
		 * Step 6: write the 3-byte data header.
		 */
		out[i++] = (byte)(0xd5);
		out[i++] = (byte)(0xaa);
		out[i++] = (byte)(0xad);
		/*
		 * Step 7: read the next 256-byte sector from the old disk image file,
		 * and add two zero bytes to bring the number of bytes up to a multiple
		 * of 3.
		 */
		for (loop = 0; loop < 256; loop++)
			sector_buffer[loop] = in[loop + in_ofs] & 0xff;
		sector_buffer[256] = 0;
		sector_buffer[257] = 0;
		/*
		 * Step 8: write the first 86 disk bytes of the data block, which
		 * encodes the bottom two bits of each sector byte into six-bit values
		 * as follows:
		 * 
		 * disk byte n, bit 0 = sector byte n, bit 1 disk byte n, bit 1 = sector
		 * byte n, bit 0 disk byte n, bit 2 = sector byte n + 86, bit 1 disk
		 * byte n, bit 3 = sector byte n + 86, bit 0 disk byte n, bit 4 = sector
		 * byte n + 172, bit 1 disk byte n, bit 5 = sector byte n + 172, bit 0
		 * 
		 * The scheme allows each pair of bits to be shifted to the right out of
		 * the disk byte, then shifted to the left into the sector byte.
		 * 
		 * Before the 6-bit value is translated to a disk byte, it is exclusive
		 * ORed with the previous 6-bit value, hence the values written are
		 * really a running checksum.
		 */
		prev_value = 0;
		for (loop = 0; loop < 86; loop++)
		{
			value = (sector_buffer[loop] & 0x01) << 1;
			value |= (sector_buffer[loop] & 0x02) >> 1;
			value |= (sector_buffer[loop + 86] & 0x01) << 3;
			value |= (sector_buffer[loop + 86] & 0x02) << 1;
			value |= (sector_buffer[loop + 172] & 0x01) << 5;
			value |= (sector_buffer[loop + 172] & 0x02) << 3;
			out[i++] = (byte)(byte_translation[value ^ prev_value]);
			prev_value = value;
		}
		/*
		 * Step 9: write the last 256 disk bytes of the data block, which
		 * encodes the top six bits of each sector byte. Again, each value is
		 * exclusive ORed with the previous value to create a running checksum
		 * (the first value is exclusive ORed with the last value of the
		 * previous step).
		 */
		for (loop = 0; loop < 256; loop++)
		{
			value = (sector_buffer[loop] >> 2);
			out[i++] = (byte)(byte_translation[value ^ prev_value]);
			prev_value = value;
		}
		/*
		 * Step 10: write the last value as the checksum.
		 */
		out[i++] = (byte)(byte_translation[value]);
		/*
		 * Step 11: write the 3-byte data trailer.
		 */
		out[i++] = (byte)(0xde);
		out[i++] = (byte)(0xaa);
		out[i++] = (byte)(0xeb);
	}

	public static byte[] nibblizeTrack(int vol, int trk, byte in[])
	{
		byte out[] = new byte[TRACK_SIZE];
		int out_pos = 0;
		for (int sector = 0; sector < 16; sector++)
		{
			nibblizeSector(vol,trk,sector,in,skewing_table[sector] << 8,out,out_pos);
			out_pos += SECTOR_SIZE;
		}
		while (out_pos < TRACK_SIZE)
			out[out_pos++] = (byte)(0xff);
		/*
		 * for (int i=0; i<TRACK_SIZE; i++)
		 * System.out.print(Integer.toString(out[i] & 0xff,16)+" ");
		 */
		return out;
	}
}
