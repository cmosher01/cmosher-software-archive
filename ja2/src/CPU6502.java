import java.util.Random;

/*
 * Created on Aug 1, 2007
 */
public class CPU6502 implements Clock.Timed
{
	private int address;
	private int data;

	private int a;
	private int x;
	private int y;
	private int pc;
	private int s = 0x100;

	private int n;
	private int v;
	private int b;
	private int d;
	private int i;
	private int z;
	private int c;

	private int t; // clock cycle within instruction
	private int opcode;
	private int adl;
	private int adh;
	private int bal;
	private int bah;
	private int ial;

	private Memory memory;
	private int idx;

	CPU6502(final Memory memory)
	{
		this.memory = memory;
	}

	public void tick()
	{
		testtick();
	}

	public void testtick()
	{
		++this.t;
		if (this.t >= 1000)
		{
			this.t = 0;
			Random rnd = new Random();
			int c = rnd.nextInt(0x100);
			int a = rnd.nextInt(0x400);
			this.memory.write(0x400+a,c);
		}
	}

	public void realtick()
	{
		if (this.t == 0)
		{
			this.address = this.pc++;
			read();
			this.opcode = this.data;
			++this.t;
		}
		else
		{
			subseq();
		}
	}

	private void subseq()
	{
		switch (addressing(this.opcode))
		{
			case SINGLE:
				switch (this.t)
				{
					case 1:
						this.address = this.pc;
						read(); // discard
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_IMMEDIATE:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_ZERO_PAGE:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.adl = this.data;
					break;
					case 2:
						this.adh = 0;
						this.address = ad();
						read();
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_ABSOLUTE:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.adl = this.data;
					break;
					case 2:
						this.address = this.pc++;
						read();
						this.adh = this.data;
					break;
					case 3:
						this.address = ad();
						read();
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_INDIRECT_X:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.bal = this.data;
					break;
					case 2:
						this.address = this.bal;
						read(); // discard
					break;
					case 3:
						this.address += this.x;
						read();
						this.adl = this.data;
					break;
					case 4:
						++this.address;
						read();
						this.adh = this.data;
					break;
					case 5:
						this.address = ad();
						read();
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_ABSOLUTE_XY:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.bal = this.data;
					break;
					case 2:
						this.address = this.pc++;
						read();
						this.bah = this.data;
					break;
					case 3:
						//TODO set idx
						this.bal += this.idx;
						this.bah += this.c;
						this.address = ba();
						read();
						if (this.bal < 0x80)
						{
							execute();
							done();
						}
					break;
					case 4:
						if (this.c == 0)
							++this.bah;
						this.address = ba();
						read();
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_ZERO_PAGE_XY:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.bal = this.data;
					break;
					case 2:
						this.bah = 0;
						this.address = ba();
						read(); // discard
					break;
					case 3:
						//TODO set idx
						this.bal += this.idx;
						this.address = ba();
						read();
						execute();
						done();
					break;
				}
			break;
			case INTERNAL_INDIRECT_Y:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.ial = this.data;
					break;
					case 2:
						this.address = this.ial;
						read();
						this.bal = this.data;
					break;
					case 3:
						++this.address;
						read();
						this.bah = this.data;
					break;
					case 4:
						this.bal += this.idx;
						this.bah += this.c;
						this.address = ba();
						read();
						if (this.bal < 0x80)
						{
							execute();
							done();
						}
					break;
					case 5:
						if (this.c == 0)
							++this.bah;
						this.address = ba();
						read();
						execute();
						done();
					break;
				}
			break;
		}
		++this.t;
	}

	private int ad()
	{
		return combine(this.adh,this.adl);
	}

	private static int combine(final int lo, final int hi)
	{
		return ((hi & 0xFF) << 8) | (lo & 0xFF);
	}

	private int ba()
	{
		return combine(this.bah,this.bal);
	}

	private void done()
	{
		this.t = -1;
	}

	private void execute()
	{
		switch (this.opcode)
		{
		}
	}

	private Addressing addressing(int op)
	{
		return Addressing.SINGLE;
	}

	private void read()
	{
		this.data = this.memory.read(this.address);
	}

	private void write()
	{
		this.memory.write(this.address,this.data);
	}
}
