import java.util.Random;

/*
 * Created on Aug 1, 2007
 */
public class CPU6502 implements Clock.Timed
{
	private static final int BRK_VECTOR = 0xFFFE;

	private int address;
	private int data;

	private volatile int a;
	private volatile int x;
	private volatile int y;
	private volatile int pc;
	private volatile int s;

	private boolean n;
	private boolean v;
	private boolean m;
	private boolean b;
	private boolean d;
	private boolean i;
	private boolean z;
	private boolean c;

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
	private int sc;
	private boolean wc;

    private volatile boolean IRQ;
    private volatile boolean NMI;

    private Memory memory;


	CPU6502(final Memory memory)
	{
		this.memory = memory;
		reset();
	}

	public void tick()
	{
		realtick();
	}

	private void checkSane()
	{
		if ((a & 0xFFFFFF00) != 0)
		{
			throw new IllegalStateException();
		}
		if ((x & 0xFFFFFF00) != 0)
		{
			throw new IllegalStateException();
		}
		if ((y & 0xFFFFFF00) != 0)
		{
			throw new IllegalStateException();
		}
		if ((s & 0xFFFFFF00) != 0)
		{
			throw new IllegalStateException();
		}
		if ((data & 0xFFFFFF00) != 0)
		{
			throw new IllegalStateException();
		}
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
//			System.out.print(Integer.toHexString(this.pc));
//			System.out.print(" a:");
//			System.out.print(Integer.toHexString(this.a));
//			System.out.print(" x:");
//			System.out.print(Integer.toHexString(this.x));
//			System.out.print(" y:");
//			System.out.print(Integer.toHexString(this.y));
//			System.out.println();
			if (this.pc == 0xf8aa)
			{
				System.out.println("*************************************************************");
			}
			this.address = this.pc++;
			read();
			this.opcode = this.data;
			//System.out.println("PC: "+Integer.toHexString(this.pc-1)+" ("+Integer.toHexString(this.opcode)+")");
			++this.t;
		}
		else
		{
			subseq();
		}
	}

	private void subseq()
	{
		switch (addressing())
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
						this.address &= 0xFF;
						read();
						this.adl = this.data;
					break;
					case 4:
						++this.address;
						this.address &= 0xFF;
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
						this.wc = (this.bal >= 0x100);
						if (this.wc)
						{
							this.bal -= 0x100;
						}
						this.address = ba();
						read();
						if (!this.wc)
						{
							execute();
							done();
						}
					break;
					case 4:
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
						this.bal &= 0xFF; // doesn't leave page zero
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
						this.address &= 0xFF; // doesn't leave page zero
						read();
						this.bah = this.data;
					break;
					case 4:
						setIndex();
						this.bal += this.idx;
						this.wc = (this.bal >= 0x100);
						if (this.wc)
						{
							this.bal -= 0x100;
						}
						this.address = ba();
						read();
						if (!this.wc)
						{
							execute();
							done();
						}
					break;
					case 5:
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
						this.address &= 0xFF;
						read(); // discard
					break;
					case 3:
						this.address += this.x;
						this.address &= 0xFF;
						read();
						this.adl = this.data;
					break;
					case 4:
						++this.address;
						this.address &= 0xFF;
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
						this.address = ba();
						this.address += this.idx;
						read(); // discard (assume this is the right address, manual is ambiguous)
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
						this.bal &= 0xFF; // doesn't leave page zero
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
						this.address &= 0xFF;
						read();
						this.bah = this.data;
					break;
					case 4:
						this.address = ba();
						this.address += this.y;
						read(); // discard
						execute();
					break;
					case 5:
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
						this.bal += this.idx; // doesn't leave page zero
						this.bal &= 0xFF;
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
						this.address = ba();
						this.address += this.idx;
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
						this.data = getStatusRegisterByte();
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
						setStatusRegisterByte(this.data);
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
					break;
					case 4:
						++this.address; // can leave the page
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
						this.adl = this.data;
					break;
					case 4:
						this.address = spPull();
						read();
						this.adh = this.data;
					break;
					case 5:
						this.pc = ad();
						this.address = this.pc;
						read(); // discard
						++this.pc;
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
						this.sc = 0;
						if (lo < 0)
						{
							lo += 0x100;
							this.sc = -1;
						}
						else if (lo >= 0x100)
						{
							lo -= 0x100;
							this.sc = 1;
						}
						this.pc = (this.pc & 0xFF00) | lo;
						this.address = this.pc;
						read();
						if (this.sc == 0)
						{
							done();
						}
					break;
					case 3:
						int hi = pch() + this.sc;
						this.pc = (hi << 8) | pcl();
						read();
						done();
					break;
				}
			break;
		}
		checkSane();
		++this.t;
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
		this.s--;
		this.s &= 0xFF;
		return sp;
	}

	private int spPull()
	{
		++this.s;
		this.s &= 0xFF;
		return sp();
	}

	private void setIndex()
	{
		this.idx = getIndex();
	}

	private int getIndex()
	{
		int aaa = (this.opcode & 0xE0) >> 5;
		int bbb = (this.opcode & 0x1C) >> 2;
		int  cc = (this.opcode & 0x03);
		if (bbb == 0)
		{
			return this.x;
		}
		if (bbb == 4 || bbb == 6)
		{
			return this.y;
		}
		if (bbb == 5 || bbb == 7)
		{
			if (cc == 2 && (aaa == 4 || aaa == 5))
			{
				return this.y;
			}
			return this.x;
		}
		assert false : "not an index instruction";
		return 0;
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

	private Addressing addressing()
	{
		return AddressingModeCalculator.getMode(this.opcode);
	}

	private void read()
	{
		this.data = this.memory.read(this.address);
	}

	private void write()
	{
		this.memory.write(this.address,this.data);
	}


























	// TODO fix interrupt routines
	// TODO fix startup cycles (see prog manual)
	// TODO fix reset

	private void handleInterrupts()
	{
		if (!i && IRQ)
		{
		    handleIRQ();
		}
		if (NMI)
		{
		    handleNMI();
		}
	}

    public void reset()
    {
        i = true;
        s = 0xFF;
        pc = memReadAbsolute(0xFFFC);
    }

    public void IRQ(boolean state)
    {
        IRQ = state;
    }

    public void NMI()
    {
        NMI = true;
    }

	private void execute()
    {
		// TODO undocumented instructions not yet implemented
        switch (this.opcode)
        {
        case 0:
            Imm();
            BRK();
            break;

        case 1:
            IndZeroX();
            ORA();
            break;

        case 2:
            Hang();
            break;

        case 3:
            Unoff();
            break;

        case 4:
            Unoff2();
            break;

        case 5:
            Zero();
            ORA();
            break;

        case 6:
            Zero();
            ASL();
            break;

        case 7:
            Unoff();
            break;

        case 8:
            Imp();
            PHP();
            break;

        case 9:
            Imm();
            ORA();
            break;

        case 10:
            Imp();
            ASL_A();
            break;

        case 11:
            Unoff(); // FIXED
            break;

        case 12:
            Unoff3();
            break;

        case 13:
            Abs();
            ORA();
            break;

        case 14:
            Abs();
            ASL();
            break;

        case 15:
            Unoff();
            break;

        case 16:
            Rel();
            BPL();
            break;

        case 17:
            IndZeroY();
            ORA();
            break;

        case 18:
            Hang();
            break;

        case 19:
            Unoff();
            break;

        case 20:
            Unoff2();
            break;

        case 21:
            ZeroX();
            ORA();
            break;

        case 22:
            ZeroX();
            ASL();
            break;

        case 23:
            Unoff();
            break;

        case 24:
            Imp();
            CLC();
            break;

        case 25:
            AbsY();
            ORA();
            break;

        case 26:
            Unoff1();
            break;

        case 27:
            Unoff();
            break;

        case 28:
            Unoff3();
            break;

        case 29:
            AbsX();
            ORA();
            break;

        case 30:
            WAbsX();
            ASL();
            break;

        case 31:
            Unoff();
            break;

        case 32:
            JSR();
            break;

        case 33:
            IndZeroX();
            AND();
            break;

        case 34:
            Hang();
            break;

        case 35:
            Unoff();
            break;

        case 36:
            Zero();
            BIT();
            break;

        case 37:
            Zero();
            AND();
            break;

        case 38:
            Zero();
            ROL();
            break;

        case 39:
            Unoff();
            break;

        case 40:
            Imp();
            PLP();
            break;

        case 41:
            Imm();
            AND();
            break;

        case 42:
            Imp();
            ROL_A();
            break;

        case 43:
            Unoff();
            break;

        case 44:
            Abs();
            BIT();
            break;

        case 45:
            Abs();
            AND();
            break;

        case 46:
            Abs();
            ROL();
            break;

        case 47:
            Unoff();
            break;

        case 48:
            Rel();
            BMI();
            break;

        case 49:
            IndZeroY();
            AND();
            break;

        case 50:
            Hang();
            break;

        case 51:
            Unoff();
            break;

        case 52:
            Unoff2();
            break;

        case 53:
            ZeroX();
            AND();
            break;

        case 54:
            ZeroX();
            ROL();
            break;

        case 55:
            Unoff();
            break;

        case 56:
            Imp();
            SEC();
            break;

        case 57:
            AbsY();
            AND();
            break;

        case 58:
            Unoff1();
            break;

        case 59:
            Unoff();
            break;

        case 60:
            Unoff3();
            break;

        case 61:
            AbsX();
            AND();
            break;

        case 62:
            WAbsX();
            ROL();
            break;

        case 63:
            Unoff();
            break;

        case 64:
            Imp();
            RTI();
            break;

        case 65:
            IndZeroX();
            EOR();
            break;

        case 66:
            Hang();
            break;

        case 67:
            Unoff();
            break;

        case 68:
            Unoff2();
            break;

        case 69:
            Zero();
            EOR();
            break;

        case 70:
            Zero();
            LSR();
            break;

        case 71:
            Unoff();
            break;

        case 72:
            Imp();
            PHA();
            break;

        case 73:
            Imm();
            EOR();
            break;

        case 74:
            Imp();
            LSR_A();
            break;

        case 75:
            Unoff();
            break;

        case 76:
            Abs();
            JMP();
            break;

        case 77:
            Abs();
            EOR();
            break;

        case 78:
            Abs();
            LSR();
            break;

        case 79:
            Unoff();
            break;

        case 80:
            Rel();
            BVC();
            break;

        case 81:
            IndZeroY();
            EOR();
            break;

        case 82:
            Hang();
            break;

        case 83:
            Unoff();
            break;

        case 84:
            Unoff2();
            break;

        case 85:
            ZeroX();
            EOR();
            break;

        case 86:
            ZeroX();
            LSR();
            break;

        case 87:
            Unoff();
            break;

        case 88:
            Imp();
            CLI();
            break;

        case 89:
            AbsY();
            EOR();
            break;

        case 90:
            Unoff1();
            break;

        case 91:
            Unoff();
            break;

        case 92:
            Unoff3();
            break;

        case 93:
            AbsX();
            EOR();
            break;

        case 94:
            WAbsX();
            LSR();
            break;

        case 95:
            Unoff();
            break;

        case 96:
            Imp();
            RTS();
            break;

        case 97:
            IndZeroX();
            ADC();
            break;

        case 98:
            Hang();
            break;

        case 99:
            Unoff();
            break;

        case 100:
            Unoff2();
            break;

        case 101:
            Zero();
            ADC();
            break;

        case 102:
            Zero();
            ROR();
            break;

        case 103:
            Unoff();
            break;

        case 104:
            Imp();
            PLA();
            break;

        case 105:
            Imm();
            ADC();
            break;

        case 106:
            Imp();
            ROR_A();
            break;

        case 107:
            Unoff();
            break;

        case 108:
            Ind();
            JMP();
            break;

        case 109:
            Abs();
            ADC();
            break;

        case 110:
            Abs();
            ROR();
            break;

        case 111:
            Unoff();
            break;

        case 112:
            Rel();
            BVS();
            break;

        case 113:
            IndZeroY();
            ADC();
            break;

        case 114:
            Hang();
            break;

        case 115:
            Unoff();
            break;

        case 116:
            Unoff2();
            break;

        case 117:
            ZeroX();
            ADC();
            break;

        case 118:
            ZeroX();
            ROR();
            break;

        case 119:
            Unoff();
            break;

        case 120:
            Imp();
            SEI();
            break;

        case 121:
            AbsY();
            ADC();
            break;

        case 122:
            Unoff1();
            break;

        case 123:
            Unoff();
            break;

        case 124:
            Unoff3();
            break;

        case 125:
            AbsX();
            ADC();
            break;

        case 126:
            WAbsX();
            ROR();
            break;

        case 127:
            Unoff();
            break;

        case 128:
            Unoff2();
            break;

        case 129:
            IndZeroX();
            STA();
            break;

        case 130:
            Unoff2();
            break;

        case 131:
            Unoff();
            break;

        case 132:
            Zero();
            STY();
            break;

        case 133:
            Zero();
            STA();
            break;

        case 134:
            Zero();
            STX();
            break;

        case 135:
            Unoff();
            break;

        case 136:
            Imp();
            DEY();
            break;

        case 137:
            Unoff2();
            break;

        case 138:
            Imp();
            TXA();
            break;

        case 139:
            Unoff();
            break;

        case 140:
            Abs();
            STY();
            break;

        case 141:
            Abs();
            STA();
            break;

        case 142:
            Abs();
            STX();
            break;

        case 143:
            Unoff();
            break;

        case 144:
            Rel();
            BCC();
            break;

        case 145:
            WIndZeroY();
            STA();
            break;

        case 146:
            Hang();
            break;

        case 147:
            Unoff();
            break;

        case 148:
            ZeroX();
            STY();
            break;

        case 149:
            ZeroX();
            STA();
            break;

        case 150:
            ZeroY();
            STX();
            break;

        case 151:
            Unoff();
            break;

        case 152:
            Imp();
            TYA();
            break;

        case 153:
            WAbsY();
            STA();
            break;

        case 154:
            Imp();
            TXS();
            break;

        case 155:
            Unoff();
            break;

        case 156:
            Unoff();
            break;

        case 157:
            WAbsX();
            STA();
            break;

        case 158:
            Unoff();
            break;

        case 159:
            Unoff();
            break;

        case 160:
            Imm();
            LDY();
            break;

        case 161:
            IndZeroX();
            LDA();
            break;

        case 162:
            Imm();
            LDX();
            break;

        case 163:
            Unoff();
            break;

        case 164:
            Zero();
            LDY();
            break;

        case 165:
            Zero();
            LDA();
            break;

        case 166:
            Zero();
            LDX();
            break;

        case 167:
            Unoff();
            break;

        case 168:
            Imp();
            TAY();
            break;

        case 169:
            Imm();
            LDA();
            break;

        case 170:
            Imp();
            TAX();
            break;

        case 171:
            Unoff();
            break;

        case 172:
            Abs();
            LDY();
            break;

        case 173:
            Abs();
            LDA();
            break;

        case 174:
            Abs();
            LDX();
            break;

        case 175:
            Unoff();
            break;

        case 176:
            Rel();
            BCS();
            break;

        case 177:
            IndZeroY();
            LDA();
            break;

        case 178:
            Hang();
            break;

        case 179:
            Unoff();
            break;

        case 180:
            ZeroX();
            LDY();
            break;

        case 181:
            ZeroX();
            LDA();
            break;

        case 182:
            ZeroY();
            LDX();
            break;

        case 183:
            Unoff();
            break;

        case 184:
            Imp();
            CLV();
            break;

        case 185:
            AbsY();
            LDA();
            break;

        case 186:
            Imp();
            TSX();
            break;

        case 187:
            Unoff();
            break;

        case 188:
            AbsX();
            LDY();
            break;

        case 189:
            AbsX();
            LDA();
            break;

        case 190:
            AbsY();
            LDX();
            break;

        case 191:
            Unoff();
            break;

        case 192:
            Imm();
            CPY();
            break;

        case 193:
            IndZeroX();
            CMP();
            break;

        case 194:
            Unoff2();
            break;

        case 195:
            Unoff();
            break;

        case 196:
            Zero();
            CPY();
            break;

        case 197:
            Zero();
            CMP();
            break;

        case 198:
            Zero();
            DEC();
            break;

        case 199:
            Unoff();
            break;

        case 200:
            Imp();
            INY();
            break;

        case 201:
            Imm();
            CMP();
            break;

        case 202:
            Imp();
            DEX();
            break;

        case 203:
            Unoff();
            break;

        case 204:
            Abs();
            CPY();
            break;

        case 205:
            Abs();
            CMP();
            break;

        case 206:
            Abs();
            DEC();
            break;

        case 207:
            Unoff();
            break;

        case 208:
            Rel();
            BNE();
            break;

        case 209:
            IndZeroY();
            CMP();
            break;

        case 210:
            Hang();
            break;

        case 211:
            Unoff();
            break;

        case 212:
            Unoff2();
            break;

        case 213:
            ZeroX();
            CMP();
            break;

        case 214:
            ZeroX();
            DEC();
            break;

        case 215:
            Unoff();
            break;

        case 216:
            Imp();
            CLD();
            break;

        case 217:
            AbsY();
            CMP();
            break;

        case 218:
            Unoff1();
            break;

        case 219:
            Unoff();
            break;

        case 220:
            Unoff3();
            break;

        case 221:
            AbsX();
            CMP();
            break;

        case 222:
            WAbsX();
            DEC();
            break;

        case 223:
            Unoff();
            break;

        case 224:
            Imm();
            CPX();
            break;

        case 225:
            IndZeroX();
            SBC();
            break;

        case 226:
            Unoff2();
            break;

        case 227:
            Unoff();
            break;

        case 228:
            Zero();
            CPX();
            break;

        case 229:
            Zero();
            SBC();
            break;

        case 230:
            Zero();
            INC();
            break;

        case 231:
            Unoff();
            break;

        case 232:
            Imp();
            INX();
            break;

        case 233:
            Imm();
            SBC();
            break;

        case 234:
            Imp();
            NOP();
            break;

        case 235:
            Unoff(); // FIXED
            break;

        case 236:
            Abs();
            CPX();
            break;

        case 237:
            Abs();
            SBC();
            break;

        case 238:
            Abs();
            INC();
            break;

        case 239:
            Unoff();
            break;

        case 240:
            Rel();
            BEQ();
            break;

        case 241:
            IndZeroY();
            SBC();
            break;

        case 242:
            Hang();
            break;

        case 243:
            Unoff();
            break;

        case 244:
            Unoff2();
            break;

        case 245:
            ZeroX();
            SBC();
            break;

        case 246:
            ZeroX();
            INC();
            break;

        case 247:
            Unoff();
            break;

        case 248:
            Imp();
            SED();
            break;

        case 249:
            AbsY();
            SBC();
            break;

        case 250:
            Unoff1();
            break;

        case 251:
            Unoff();
            break;

        case 252:
            Unoff3();
            break;

        case 253:
            AbsX();
            SBC();
            break;

        case 254:
            WAbsX();
            INC();
            break;

        case 255:
            Unoff();
            break;
        }
    }

    private void setStatusRegisterByte(final int p)
    {
    	this.n = ((p & 0x80) != 0);
    	this.v = ((p & 0x40) != 0);
    	this.m = ((p & 0x20) != 0);
    	this.b = ((p & 0x10) != 0);
    	this.d = ((p & 0x08) != 0);
    	this.i = ((p & 0x04) != 0);
    	this.z = ((p & 0x02) != 0);
    	this.c = ((p & 0x01) != 0);
    }

    private void setStatusRegisterNZ(final int val)
    {
    	final byte byt = (byte)val;
        this.n = byt < 0;
        this.z = byt == 0;
    }

    private void setFlagCarry(final int val)
    {
    	this.c = (val & 0x100) != 0;
    }

    private void setFlagBorrow(final int val)
    {
    	this.c = (val & 0x100) == 0;
    }

    private int getStatusRegisterByte()
    {
        int p = 0;
        if (this.n)
            p |= 0x80;
        if (this.v)
            p |= 0x40;
        if (this.m)
            p |= 0x20;
        if (this.b)
            p |= 0x10;
        if (this.d)
            p |= 0x08;
        if (this.i)
            p |= 0x04;
        if (this.z)
            p |= 0x02;
        if (this.c)
            p |= 0x01;
        return p;
    }

    private void Imp()
    {
    }

    private void Imm()
    {
//        opcode = pc++;
    }

    private void Zero()
    {
//        opcode = memory.read(pc++);
    }

    private void ZeroX()
    {
//        opcode = memory.read(pc++) + x & 0xff;
    }

    private void ZeroY()
    {
//        opcode = memory.read(pc++) + y & 0xff;
    }

    private void Abs()
    {
//        opcode = memReadAbsolute(pc);
//        pc += 2;
    }

    private void AbsX()
    {
//        opL = memory.read(pc++) + x;
//        opH = memory.read(pc++) << 8;
//        opcode = opH + opL;
    }

    private void AbsY()
    {
//        opL = memory.read(pc++) + y;
//        opH = memory.read(pc++) << 8;
//        opcode = opH + opL;
    }

    private void Ind()
    {
//        ptrL = memory.read(pc++);
//        ptrH = memory.read(pc++) << 8;
//        opcode = memory.read(ptrH + ptrL);
//        ptrL = ptrL + 1 & 0xff;
//        opcode += memory.read(ptrH + ptrL) << 8;
    }

    private void IndZeroX()
    {
//        ptr = x + memory.read(pc++);
//        opcode = memory.read(ptr);
//        opcode += memory.read(ptr + 1 & 0xff) << 8;
    }

    private void IndZeroY()
    {
//        ptr = memory.read(pc++);
//        opL = memory.read(ptr) + y;
//        opH = (memory.read(ptr + 1) & 0xff) << 8;
//        opcode = opH + opL;
    }

    private void Rel()
    {
//        opcode = memory.read(pc++);
//        if(opcode >= 128)
//            opcode = -(256 - opcode);
//        opcode = opcode + pc & 0xffff;
    }

    private void WAbsX()
    {
//        opL = memory.read(pc++) + x;
//        opH = memory.read(pc++) << 8;
//        opcode = opH + opL;
    }

    private void WAbsY()
    {
//        opL = memory.read(pc++) + y;
//        opH = memory.read(pc++) << 8;
//        opcode = opH + opL;
    }

    private void WIndZeroY()
    {
//        ptr = memory.read(pc++);
//        opL = memory.read(ptr) + y;
//        opH = memory.read(ptr + 1 & 0xff) << 8;
//        opcode = opH + opL;
    }


















    private void LDA()
    {
        this.a = this.data;
        this.a &= 0xFF;
        setStatusRegisterNZ(this.a);
//        System.out.print("LDA ["+Integer.toHexString(this.address)+"] "+Integer.toHexString(this.a));
        if (this.address == 0xc0ec && this.a >= 0x80)
        {
        	if (this.a == 0xd5)
        	{
            	//System.out.print("<------------------------------ "+Integer.toHexString(this.a));
        		//System.out.println("      ===========================================");
        	}
//        	else
//            	System.out.println();
        }
//        else
//        	System.out.println();
    }

    private void LDX()
    {
        this.x = this.data;
        this.x &= 0xFF;
        setStatusRegisterNZ(this.x);
    }

    private void LDY()
    {
        this.y = this.data;
        this.y &= 0xFF;
        setStatusRegisterNZ(this.y);
    }

    private void STA()
    {
        this.data = this.a;
    }

    private void STX()
    {
        this.data = this.x;
    }

    private void STY()
    {
        this.data = this.y;
    }

    private void ADC()
    {
        int Op1 = this.a;
        int Op2 = this.data;
        if (this.d)
        {
        	this.z = (Op1 + Op2 + (this.c ? 1 : 0) & 0xff) == 0;
            int tmp = (Op1 & 0xf) + (Op2 & 0xf) + (this.c ? 1 : 0);
            tmp = tmp >= 10 ? tmp + 6 : tmp;
            this.a = tmp;
            tmp = (Op1 & 0xf0) + (Op2 & 0xf0) + (tmp & 0xf0);
            this.n = (byte)tmp < 0;
            this.v = ((Op1 ^ tmp) & ~(Op1 ^ Op2) & 0x80) != 0;
            tmp = this.a & 0xf | (tmp >= 160 ? tmp + 96 : tmp);
            this.c = tmp >= 256;
            this.a = tmp & 0xff;
        }
        else
        {
            int tmp = Op1 + Op2 + (this.c ? 1 : 0);
            this.a = tmp & 0xff;
            this.v = ((Op1 ^ this.a) & ~(Op1 ^ Op2) & 0x80) != 0;
            setFlagCarry(tmp);
            setStatusRegisterNZ(this.a);
        }
    }

    private void SBC()
    {
        int Op1 = this.a;
        int Op2 = this.data;
        if (this.d)
        {
            int tmp = (Op1 & 0xf) - (Op2 & 0xf) - (this.c ? 0 : 1);
            tmp = (tmp & 0x10) != 0 ? tmp - 6 : tmp;
            this.a = tmp;
            tmp = (Op1 & 0xf0) - (Op2 & 0xf0) - (this.a & 0x10);
            this.a = this.a & 0xf | ((tmp & 0x100) != 0 ? tmp - 96 : tmp);
            tmp = Op1 - Op2 - (this.c ? 0 : 1);
            setFlagBorrow(tmp);
            setStatusRegisterNZ(tmp);
        }
        else
        {
            int tmp = Op1 - Op2 - (this.c ? 0 : 1);
            this.a = tmp & 0xff;
            this.v = ((Op1 ^ Op2) & (Op1 ^ this.a) & 0x80) != 0;
            setFlagBorrow(tmp);
            setStatusRegisterNZ(this.a);
        }
    }

    private void CMP()
    {
        final int tmp = this.a - this.data;
        setFlagBorrow(tmp);
        setStatusRegisterNZ(tmp);
    }

    private void CPX()
    {
        final int tmp = this.x - this.data;
        setFlagBorrow(tmp);
        setStatusRegisterNZ(tmp);
    }

    private void CPY()
    {
        final int tmp = this.y - this.data;
        setFlagBorrow(tmp);
        setStatusRegisterNZ(tmp);
    }

    private void AND()
    {
        this.a &= this.data;
        setStatusRegisterNZ(this.a);
    }

    private void ORA()
    {
        this.a |= this.data;
        setStatusRegisterNZ(this.a);
    }

    private void EOR()
    {
        this.a ^= this.data;
        setStatusRegisterNZ(this.a);
    }

    private void ASL()
    {
        byte btmp = (byte)this.data;
        this.c = (btmp & 0x80) != 0;
        btmp <<= 1;
        setStatusRegisterNZ(btmp);
        this.data = btmp;
        this.data &= 0xFF;
    }

    private void ASL_A()
    {
        final int tmp = this.a << 1;
        this.a = (byte)tmp;
        this.a &= 0xFF;
        setFlagCarry(tmp);
        setStatusRegisterNZ(this.a);
    }

    private void LSR()
    {
        byte btmp = (byte)this.data;
        this.c = (btmp & 0x01) != 0;
        btmp >>>= 1;
        setStatusRegisterNZ(btmp);
        this.data = btmp;
        this.data &= 0xFF;
    }

    private void LSR_A()
    {
        this.c = (this.a & 0x01) != 0;
        this.a >>>= 1;
        setStatusRegisterNZ(this.a);
    }

    private void ROL()
    {
        byte btmp = (byte)this.data;
        final boolean newCarry = (btmp < 0);
        btmp = (byte)((btmp << 1) | (this.c ? 0x01 : 0));
        this.c = newCarry;
        setStatusRegisterNZ(btmp);
        this.data = btmp;
        this.data &= 0xFF;
    }

    private void ROL_A()
    {
        final int tmp = (this.a << 1) | (this.c ? 0x01 : 0);
        this.a = (byte)tmp;
        this.a &= 0xFF;
        setFlagCarry(tmp);
        setStatusRegisterNZ(this.a);
    }

    private void ROR()
    {
        byte btmp = (byte)this.data;
        final boolean newCarry = (btmp & 0x01) != 0;
        btmp = (byte)((btmp >> 1) | (this.c ? 0x80 : 0));
        this.c = newCarry;
        setStatusRegisterNZ(btmp);
        this.data = btmp;
        this.data &= 0xFF;
    }

    private void ROR_A()
    {
        this.c = (this.a & 0x01) != 0;
        this.a = (this.a | (this.c ? 0x100 : 0)) >> 1;
        this.a &= 0xFF;
        setStatusRegisterNZ(this.a);
    }

    private void INC()
    {
        ++this.data;
        this.data &= 0xFF;
        setStatusRegisterNZ(this.data);
    }

    private void DEC()
    {
        --this.data;
        this.data &= 0xFF;
        setStatusRegisterNZ(this.data);
    }

    private void INX()
    {
        ++this.x;
        this.x &= 0xFF;
        setStatusRegisterNZ(this.x);
    }

    private void INY()
    {
        ++this.y;
        this.y &= 0xFF;
        setStatusRegisterNZ(this.y);
    }

    private void DEX()
    {
        --this.x;
        this.x &= 0xFF;
        setStatusRegisterNZ(this.x);
    }

    private void DEY()
    {
        --this.y;
        this.y &= 0xFF;
        setStatusRegisterNZ(this.y);
    }

    private void BIT()
    {
        final byte btmp = (byte)this.data;
        this.v = (btmp & 0x40) != 0;
        this.n = (btmp & 0x80) != 0;
        this.z = (btmp & this.a) == 0;
    }

    private void PHA()
    {
    	this.data = this.a;
    }

    private void PHP()
    {
    	this.data = getStatusRegisterByte();
    }

    private void PLA()
    {
    	this.a = this.data;
        setStatusRegisterNZ(this.a);
    }

    private void PLP()
    {
    	setStatusRegisterByte(this.data);
    }

    private void BRK()
    {
//        memory.read(opcode);
//        pushProgramCounter();
//        PHP();
//        //I = true;
//        b = true;
//        pc = memReadAbsolute(0xFFFE);
    }

    private void RTI()
    {
//        memory.read(s + 256);
//        PLP();
//        popProgramCounter();
    }

    private void JMP()
    {
//        pc = opcode;
    }

    private void RTS()
    {
//        memory.read(s + 256);
//        popProgramCounter();
//        memory.read(pc++);
    }

    private void JSR()
    {
//        opL = memory.read(pc++) & 0xff;
//        memory.read(s + 256);
//        pushProgramCounter();
//        pc = opL + ((memory.read(pc) & 0xff) << 8);
    }

//    private void branch()
//    {
//        pc = opcode;
//    }

    private void BNE()
    {
    	this.branch = !this.z;
    }

    private void BEQ()
    {
    	this.branch = this.z;
    }

    private void BVC()
    {
    	this.branch = !this.v;
    }

    private void BVS()
    {
    	this.branch = this.v;
    }

    private void BCC()
    {
    	this.branch = !this.c;
    }

    private void BCS()
    {
    	this.branch = this.c;
    }

    private void BPL()
    {
    	this.branch = !this.n;
    }

    private void BMI()
    {
    	this.branch = this.n;
    }

    private void TAX()
    {
        this.x = this.a;
        setStatusRegisterNZ(this.x);
    }

    private void TXA()
    {
        this.a = this.x;
        setStatusRegisterNZ(this.a);
    }

    private void TAY()
    {
        this.y = this.a;
        setStatusRegisterNZ(this.y);
    }

    private void TYA()
    {
        this.a = this.y;
        setStatusRegisterNZ(this.a);
    }

    private void TXS()
    {
        this.s = this.x;
        //setStatusRegisterNZ(this.s); // ???
    }

    private void TSX()
    {
        this.x = this.s;
        setStatusRegisterNZ(this.x);
    }

    private void CLC()
    {
        c = false;
    }

    private void SEC()
    {
        c = true;
    }

    private void CLI()
    {
        i = false;
    }

    private void SEI()
    {
        i = true;
    }

    private void CLV()
    {
        v = false;
    }

    private void CLD()
    {
        d = false;
    }

    private void SED()
    {
        d = true;
    }

    private void NOP()
    {
    }

    private void Unoff()
    {
    }

    private void Unoff1()
    {
    }

    private void Unoff2()
    {
//        pc++;
    }

    private void Unoff3()
    {
//        pc += 2;
    }

    private void Hang()
    {
        pc--;
    }

    private void handleIRQ()
    {
        pushProgramCounter();
        memory.write(256 + s, (byte)(getStatusRegisterByte() & 0xffffffef));
        s = (s - 1) & 0xff;
        i = true;
        pc = memReadAbsolute(0xFFFE);
    }

    private void handleNMI()
    {
        pushProgramCounter();
        memory.write(256 + s, (byte)(getStatusRegisterByte() & 0xffffffef));
        s = (s - 1) & 0xff;
        i = true;
        NMI = false;
        pc = memReadAbsolute(0xFFFA);
    }

    private int memReadAbsolute(int adr)
    {
        return memory.read(adr) | (memory.read(adr + 1) & 0xff) << 8;
    }

    private void pushProgramCounter()
    {
        memory.write(s + 256, (byte)(pc >> 8));
        s = (s - 1) & 0xff;
        memory.write(s + 256, (byte)pc);
        s = (s - 1) & 0xff;
    }

    private void popProgramCounter()
    {
        s = (s + 1) & 0xff;
        pc = memory.read(s + 256) & 0xff;
        s = (s + 1) & 0xff;
        pc += (memory.read(s + 256) & 0xff) << 8;
    }
}
