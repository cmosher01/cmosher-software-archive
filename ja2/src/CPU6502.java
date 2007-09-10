import java.util.Random;

/*
 * Created on Aug 1, 2007
 */
public class CPU6502 implements Clock.Timed
{
	private static final int BRK_VECTOR = 0xFFFE;

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
	private int iah;
	private int idx;
	private int offset;
	private boolean branch;

	private Memory memory;


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
						setIndex();
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
						setIndex();
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
						setIndex();
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
			case STORE_ZERO_PAGE:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.adl = this.data;
						execute();
					break;
					case 2:
						this.adh = 0;
						this.address = ad();
						write();
						done();
					break;
				}
			break;
			case STORE_ABSOLUTE:
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
						execute();
					break;
					case 3:
						this.address = ad();
						write();
						done();
					break;
				}
			break;
			case STORE_INDIRECT_X:
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
						execute();
					break;
					case 5:
						this.address = ad();
						write();
						done();
					break;
				}
			break;
			case STORE_ABSOLUTE_XY:
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
						setIndex();
						this.bal += this.idx;
						this.bah += this.c; //  ???
						this.address = ba();
						read(); // discard
						execute();
					break;
					case 4:
						write();
						done();
					break;
				}
			break;
			case STORE_ZERO_PAGE_XY:
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
						execute();
					break;
					case 3:
						setIndex();
						this.bal += this.idx;
						this.address = ba();
						write();
						done();
					break;
				}
			break;
			case STORE_INDIRECT_Y:
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
						setIndex();
						this.bal += this.idx;
						this.bah += this.c; // ???
						this.address = ba();
						read(); // discard
					break;
					case 5:
						execute();
						write();
						done();
					break;
				}
			break;
			case RMW_ZERO_PAGE:
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
					break;
					case 3:
						write();
						execute();
					break;
					case 4:
						write();
						done();
					break;
				}
			break;
			case RMW_ABSOLUTE:
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
					break;
					case 4:
						write();
						execute();
					break;
					case 5:
						write();
						done();
					break;
				}
			break;
			case RMW_ZERO_PAGE_X:
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
						setIndex();
						this.bal += this.idx;
						this.address = ba();
						read();
					break;
					case 4:
						write();
						execute();
					break;
					case 5:
						write();
						done();
					break;
				}
			break;
			case RMW_ABSOLUTE_X:
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
						setIndex();
						this.bal += this.idx;
						this.bah += this.c;
						this.address = ba();
						read(); // discard
					break;
					case 4:
						read();
					break;
					case 5:
						write();
						execute();
					break;
					case 6:
						write();
						done();
					break;
				}
			break;
			case MISC_PUSH:
				switch (this.t)
				{
					case 1:
						this.address = this.pc;
						read(); // discard
						execute();
					break;
					case 2:
						this.address = spPush();
						write();
						done();
					break;
				}
			break;
			case MISC_PULL:
				switch (this.t)
				{
					case 1:
						this.address = this.pc;
						read(); // discard
					break;
					case 2:
						this.address = sp();
						read(); // discard
					break;
					case 3:
						this.address = spPull();
						read();
						execute();
						done();
					break;
				}
			break;
			case MISC_JSR:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.adl = this.data;
					break;
					case 2:
						this.address = spPush();
						read(); // discard
					break;
					case 3:
						this.data = pch();
						write();
						this.address = spPush();
					break;
					case 4:
						this.data = pcl();
						write();
					break;
					case 5:
						this.address = this.pc;
						read();
						this.adh = this.data;
						this.pc = ad();
						done();
					break;
				}
			break;
			case MISC_BREAK:
				switch (this.t)
				{
					case 1:
						this.address = this.pc;
						read(); // discard
					break;
					case 2:
						this.address = spPush();
						this.data = pch();
						write();
					break;
					case 3:
						this.address = spPush();
						this.data = pcl();
						write();
					break;
					case 4:
						this.address = spPush();
						this.data = p();
						write();
					break;
					case 5:
						this.address = BRK_VECTOR;
						read();
						this.adl = this.data;
					break;
					case 6:
						this.address = BRK_VECTOR+1;
						read();
						this.adh = this.data;
						this.pc = ad();
						done();
					break;
				}
			break;
			case MISC_RTI:
				switch (this.t)
				{
					case 1:
						this.address = this.pc;
						read(); // discard
					break;
					case 2:
						this.address = sp();
						read(); // discard
					break;
					case 3:
						this.address = spPull();
						read();
						setP(this.data);
					break;
					case 4:
						this.address = spPull();
						read();
						this.adl = this.data;
					break;
					case 5:
						this.address = spPull();
						read();
						this.adh = this.data;
						this.pc = ad();
						done();
					break;
				}
			break;
			case JMP_ABSOLUTE:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.adl = this.data;
					break;
					case 2:
						this.address = this.pc;
						read();
						this.adh = this.data;
						this.pc = ad();
						done();
					break;
				}
			break;
			case JMP_INDIRECT:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.ial = this.data;
					break;
					case 2:
						this.address = this.pc;
						read();
						this.iah = this.data;
					break;
					case 3:
						this.address = ia();
						read();
						this.adl = this.data;
						++this.ial; // ???
					break;
					case 4:
						this.address = ia();
						read();
						this.adh = this.data;
						this.pc = ad();
						done();
					break;
				}
			break;
			case RTS:
				switch (this.t)
				{
					case 1:
						this.address = this.pc;
						read(); // discard
					break;
					case 2:
						this.address = sp();
						read(); // discard
					break;
					case 3:
						this.address = spPull();
						read();
						execute();
						done();
					break;
				}
			break;
			case BRANCH:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read();
						this.offset = this.data;
						execute();
						if (!this.branch)
						{
							done();
						}
					break;
					case 2:
						if (this.offset >= 0x80)
						{
							this.offset -= 0x100;
						}
						int lo = pcl()+this.offset;
						this.c = 0;
						if (lo < 0)
						{
							lo += 0x100;
							this.c = -1;
						}
						else if (lo >= 0x100)
						{
							lo -= 0x100;
							this.c = 1;
						}
						this.pc = (this.pc & 0xFF00) | lo;
						this.address = this.pc;
						read();
						if (this.c == 0)
						{
							done();
						}
					break;
					case 3:
						int hi = pch() + this.c;
						this.pc = (hi << 8) | (this.pc & 0xFF);
						read();
						done();
					break;
				}
			break;
		}
		++this.t;
	}

	private void setP(int data2)
	{
		// TODO
	}

	private int p()
	{
		return 0; // TODO
	}

	private int pch()
	{
		return (this.pc & 0xFF00) >> 8;
	}

	private int pcl()
	{
		return this.pc & 0xFF;
	}

	private int sp()
	{
		return 0x100+this.s;
	}

	private int spPush()
	{
		final int sp = sp();
		--this.s;
		if (this.s < 0)
		{
			this.s += 0x100;
		}
		return sp;
	}

	private int spPull()
	{
		++this.s;
		if (this.s >= 0x100)
		{
			this.s -= 0x100;
		}
		return sp();
	}

	private void setIndex()
	{
		// TODO
	}

	private int ad()
	{
		return combine(this.adl,this.adh);
	}

	private int ia()
	{
		return combine(this.ial,this.iah);
	}

	private static int combine(final int lo, final int hi)
	{
		return ((hi & 0xFF) << 8) | (lo & 0xFF);
	}

	private int ba()
	{
		return combine(this.bal,this.bah);
	}

	private void done()
	{
		this.t = -1;
	}

	private void execute()
	{
		switch (this.opcode)
		{
			// TODO
		}
	}

	private Addressing addressing(int op)
	{
		return Addressing.SINGLE; // TODO
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
