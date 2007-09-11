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

    private boolean IRQ;
    private boolean NMI;

    private byte btmp;
    private int opL;
    private int opH;
    private int ptr;
    private int ptrH;
    private int ptrL;
    private int tmp;

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
						//this.bah += this.c; // TODO
						this.address = ba();
						read();
						if (this.bal < 0x80)
						{
							execute();
							done();
						}
					break;
					case 4:
						if (!this.c)
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
						//this.bah += this.c; // TODO
						this.address = ba();
						read();
						if (this.bal < 0x80)
						{
							execute();
							done();
						}
					break;
					case 5:
						if (!this.c)
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
						//this.bah += this.c; // TODO
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
						//this.bah += this.c; // TODO
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
						// this.bah += this.c; // TODO
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
						this.pc = (hi << 8) | (this.pc & 0xFF);
						read();
						done();
					break;
				}
			break;
		}
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




























	private void handleInterrupts() throws InterruptedException
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

    public void reset() throws InterruptedException
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

	private void executeOpcode() throws InterruptedException
    {
        final int opcode = memory.read(pc++);        

        switch (opcode)
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
            Imm();
            AND();
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
            Imm();
            AND();
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
            Imm();
            SBC();
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

    private void setStatusRegisterByte(int p)
    {
        n = ((p & 0x80) != 0);
        v = ((p & 0x40) != 0);
        m = ((p & 0x20) != 0);
        b = ((p & 0x10) != 0);
        d = ((p & 0x08) != 0);
        i = ((p & 0x04) != 0);
        z = ((p & 0x02) != 0);
        c = ((p & 0x01) != 0);
    }

    private void setStatusRegisterNZ(byte val)
    {
        n = val < 0;
        z = val == 0;
    }

    private void setFlagCarry(int val)
    {
        c = (val & 0x100) != 0;
    }

    private void setFlagBorrow(int val)
    {
        c = (val & 0x100) == 0;
    }

    private int getStatusRegisterByte()
    {
        int p = 0;
        if(n)
            p |= 0x80;
        if(v)
            p |= 0x40;
        if(m)
            p |= 0x20;
        if(b)
            p |= 0x10;
        if(d)
            p |= 0x08;
        if(i)
            p |= 0x04;
        if(z)
            p |= 0x02;
        if(c)
            p |= 0x01;
        return p;
    }

    private void Imp()
    {
    }

    private void Imm()
    {
        opcode = pc++;
    }

    private void Zero() throws InterruptedException
    {
        opcode = memory.read(pc++);
    }

    private void ZeroX() throws InterruptedException
    {
        opcode = memory.read(pc++) + x & 0xff;
    }

    private void ZeroY() throws InterruptedException
    {
        opcode = memory.read(pc++) + y & 0xff;
    }

    private void Abs() throws InterruptedException
    {
        opcode = memReadAbsolute(pc);
        pc += 2;
    }

    private void AbsX() throws InterruptedException
    {
        opL = memory.read(pc++) + x;
        opH = memory.read(pc++) << 8;
        opcode = opH + opL;
    }

    private void AbsY() throws InterruptedException
    {
        opL = memory.read(pc++) + y;
        opH = memory.read(pc++) << 8;
        opcode = opH + opL;
    }

    private void Ind() throws InterruptedException
    {
        ptrL = memory.read(pc++);
        ptrH = memory.read(pc++) << 8;
        opcode = memory.read(ptrH + ptrL);
        ptrL = ptrL + 1 & 0xff;
        opcode += memory.read(ptrH + ptrL) << 8;
    }

    private void IndZeroX() throws InterruptedException
    {
        ptr = x + memory.read(pc++);
        opcode = memory.read(ptr);
        opcode += memory.read(ptr + 1 & 0xff) << 8;
    }

    private void IndZeroY() throws InterruptedException
    {
        ptr = memory.read(pc++);
        opL = memory.read(ptr) + y;
        opH = (memory.read(ptr + 1) & 0xff) << 8;
        opcode = opH + opL;
    }

    private void Rel() throws InterruptedException
    {
        opcode = memory.read(pc++);
        if(opcode >= 128)
            opcode = -(256 - opcode);
        opcode = opcode + pc & 0xffff;
    }

    private void WAbsX() throws InterruptedException
    {
        opL = memory.read(pc++) + x;
        opH = memory.read(pc++) << 8;
        opcode = opH + opL;
    }

    private void WAbsY() throws InterruptedException
    {
        opL = memory.read(pc++) + y;
        opH = memory.read(pc++) << 8;
        opcode = opH + opL;
    }

    private void WIndZeroY() throws InterruptedException
    {
        ptr = memory.read(pc++);
        opL = memory.read(ptr) + y;
        opH = memory.read(ptr + 1 & 0xff) << 8;
        opcode = opH + opL;
    }

    private void LDA() throws InterruptedException
    {
        a = memory.read(opcode);
        setStatusRegisterNZ((byte)a);
    }

    private void LDX() throws InterruptedException
    {
        x = memory.read(opcode);
        setStatusRegisterNZ((byte)x);
    }

    private void LDY() throws InterruptedException
    {
        y = memory.read(opcode);
        setStatusRegisterNZ((byte)y);
    }

    private void STA()
    {
        memory.write(opcode, a & 0xff);
    }

    private void STX()
    {
        memory.write(opcode, x & 0xff);
    }

    private void STY()
    {
        memory.write(opcode, y & 0xff);
    }

    private void ADC() throws InterruptedException
    {
        int Op1 = a;
        int Op2 = memory.read(opcode);
        if(d)
        {
            z = (Op1 + Op2 + (c ? 1 : 0) & 0xff) == 0;
            tmp = (Op1 & 0xf) + (Op2 & 0xf) + (c ? 1 : 0);
            tmp = tmp >= 10 ? tmp + 6 : tmp;
            a = tmp;
            tmp = (Op1 & 0xf0) + (Op2 & 0xf0) + (tmp & 0xf0);
            n = (byte)tmp < 0;
            v = ((Op1 ^ tmp) & ~(Op1 ^ Op2) & 0x80) != 0;
            tmp = a & 0xf | (tmp >= 160 ? tmp + 96 : tmp);
            c = tmp >= 256;
            a = tmp & 0xff;
        } else
        {
            tmp = Op1 + Op2 + (c ? 1 : 0);
            a = tmp & 0xff;
            v = ((Op1 ^ a) & ~(Op1 ^ Op2) & 0x80) != 0;
            setFlagCarry(tmp);
            setStatusRegisterNZ((byte)a);
        }
    }

    private void SBC() throws InterruptedException
    {
        int Op1 = a;
        int Op2 = memory.read(opcode);
        if(d)
        {
            tmp = (Op1 & 0xf) - (Op2 & 0xf) - (c ? 0 : 1);
            tmp = (tmp & 0x10) != 0 ? tmp - 6 : tmp;
            a = tmp;
            tmp = (Op1 & 0xf0) - (Op2 & 0xf0) - (a & 0x10);
            a = a & 0xf | ((tmp & 0x100) != 0 ? tmp - 96 : tmp);
            tmp = Op1 - Op2 - (c ? 0 : 1);
            setFlagBorrow(tmp);
            setStatusRegisterNZ((byte)tmp);
        } else
        {
            tmp = Op1 - Op2 - (c ? 0 : 1);
            a = tmp & 0xff;
            v = ((Op1 ^ Op2) & (Op1 ^ a) & 0x80) != 0;
            setFlagBorrow(tmp);
            setStatusRegisterNZ((byte)a);
        }
    }

    private void CMP() throws InterruptedException
    {
        tmp = a - memory.read(opcode);
        setFlagBorrow(tmp);
        setStatusRegisterNZ((byte)tmp);
    }

    private void CPX() throws InterruptedException
    {
        tmp = x - memory.read(opcode);
        setFlagBorrow(tmp);
        setStatusRegisterNZ((byte)tmp);
    }

    private void CPY() throws InterruptedException
    {
        tmp = y - memory.read(opcode);
        setFlagBorrow(tmp);
        setStatusRegisterNZ((byte)tmp);
    }

    private void AND() throws InterruptedException
    {
        a &= memory.read(opcode) & 0xff;
        setStatusRegisterNZ((byte)a);
    }

    private void ORA() throws InterruptedException
    {
        a |= memory.read(opcode) & 0xff;
        setStatusRegisterNZ((byte)a);
    }

    private void EOR() throws InterruptedException
    {
        a ^= memory.read(opcode) & 0xff;
        setStatusRegisterNZ((byte)a);
    }

    private void ASL() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        memory.write(opcode, btmp);
        c = btmp < 0;
        btmp <<= 1;
        setStatusRegisterNZ(btmp);
        memory.write(opcode, btmp & 0xff);
    }

    private void ASL_A()
    {
        tmp = a << 1;
        a = tmp & 0xff;
        setFlagCarry(tmp);
        setStatusRegisterNZ((byte)a);
    }

    private void LSR() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        c = (btmp & 1) != 0;
        btmp = (byte)((btmp & 0xff) >> 1);
        setStatusRegisterNZ(btmp);
        memory.write(opcode, btmp & 0xff);
    }

    private void LSR_A()
    {
        c = (a & 1) != 0;
        a >>= 1;
        setStatusRegisterNZ((byte)a);
    }

    private void ROL() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        boolean newCarry = btmp < 0;
        btmp = (byte)((btmp & 0xff) << 1 | (c ? 1 : 0));
        c = newCarry;
        setStatusRegisterNZ(btmp);
        memory.write(opcode, btmp & 0xff);
    }

    private void ROL_A()
    {
        tmp = a << 1 | (c ? 1 : 0);
        a = tmp & 0xff;
        setFlagCarry(tmp);
        setStatusRegisterNZ((byte)a);
    }

    private void ROR() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        boolean newCarry = (btmp & 1) != 0;
        btmp = (byte)((btmp & 0xff) >> 1 | (c ? 0x80 : 0));
        c = newCarry;
        setStatusRegisterNZ(btmp);
        memory.write(opcode, btmp & 0xff);
    }

    private void ROR_A()
    {
        tmp = a | (c ? 0x100 : 0);
        c = (a & 1) != 0;
        a = tmp >> 1;
        setStatusRegisterNZ((byte)a);
    }

    private void INC() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        memory.write(opcode, btmp);
        btmp++;
        setStatusRegisterNZ(btmp);
        memory.write(opcode, btmp & 0xff);
    }

    private void DEC() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        memory.write(opcode, btmp);
        btmp--;
        setStatusRegisterNZ(btmp);
        memory.write(opcode, btmp & 0xff);
    }

    private void INX()
    {
        x = x + 1 & 0xff;
        setStatusRegisterNZ((byte)x);
    }

    private void INY()
    {
        y = y + 1 & 0xff;
        setStatusRegisterNZ((byte)y);
    }

    private void DEX()
    {
        x = x - 1 & 0xff;
        setStatusRegisterNZ((byte)x);
    }

    private void DEY()
    {
        y = y - 1 & 0xff;
        setStatusRegisterNZ((byte)y);
    }

    private void BIT() throws InterruptedException
    {
        btmp = (byte)(memory.read(opcode) & 0xff);
        v = (btmp & 0x40) != 0;
        n = btmp < 0;
        z = (btmp & a) == 0;
    }

    private void PHA()
    {
        memory.write(256 + s, a & 0xff);
        s = (s - 1) & 0xff;
    }

    private void PHP()
    {
        final int p = getStatusRegisterByte();
        memory.write(256 + s, p & 0xff);
        s = (s - 1) & 0xff;
    }

    private void PLA() throws InterruptedException
    {
        memory.read(s + 256);
        s = (s + 1) & 0xff;
        a = memory.read(s + 256) & 0xff;
        setStatusRegisterNZ((byte)a);
    }

    private void PLP() throws InterruptedException
    {
        memory.read(s + 256);
        s = (s + 1) & 0xff;
        final int p = memory.read(s + 256) & 0xff;
        setStatusRegisterByte(p);
    }

    private void BRK() throws InterruptedException
    {
        memory.read(opcode);
        pushProgramCounter();
        PHP();
        //I = true;
        b = true;
        pc = memReadAbsolute(0xFFFE);
    }

    private void RTI() throws InterruptedException
    {
        memory.read(s + 256);
        PLP();
        popProgramCounter();
    }

    private void JMP()
    {
        pc = opcode;
    }

    private void RTS() throws InterruptedException
    {
        memory.read(s + 256);
        popProgramCounter();
        memory.read(pc++);
    }

    private void JSR() throws InterruptedException
    {
        opL = memory.read(pc++) & 0xff;
        memory.read(s + 256);
        pushProgramCounter();
        pc = opL + ((memory.read(pc) & 0xff) << 8);
    }

    private void branch()
    {
        pc = opcode;
    }

    private void BNE()
    {
        if(!z)
            branch();
    }

    private void BEQ()
    {
        if(z)
            branch();
    }

    private void BVC()
    {
        if(!v)
            branch();
    }

    private void BVS()
    {
        if(v)
            branch();
    }

    private void BCC()
    {
        if(!c)
            branch();
    }

    private void BCS()
    {
        if(c)
            branch();
    }

    private void BPL()
    {
        if(!n)
            branch();
    }

    private void BMI()
    {
        if(n)
            branch();
    }

    private void TAX()
    {
        x = a;
        setStatusRegisterNZ((byte)a);
    }

    private void TXA()
    {
        a = x;
        setStatusRegisterNZ((byte)a);
    }

    private void TAY()
    {
        y = a;
        setStatusRegisterNZ((byte)a);
    }

    private void TYA()
    {
        a = y;
        setStatusRegisterNZ((byte)a);
    }

    private void TXS()
    {
        s = x;
    }

    private void TSX()
    {
        x = s;
        setStatusRegisterNZ((byte)x);
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
        pc++;
    }

    private void Unoff3()
    {
        pc += 2;
    }

    private void Hang()
    {
        pc--;
    }

    private void handleIRQ() throws InterruptedException
    {
        pushProgramCounter();
        memory.write(256 + s, (byte)(getStatusRegisterByte() & 0xffffffef));
        s = (s - 1) & 0xff;
        i = true;
        pc = memReadAbsolute(0xFFFE);
    }

    private void handleNMI() throws InterruptedException
    {
        pushProgramCounter();
        memory.write(256 + s, (byte)(getStatusRegisterByte() & 0xffffffef));
        s = (s - 1) & 0xff;
        i = true;
        NMI = false;
        pc = memReadAbsolute(0xFFFA);
    }

    private int memReadAbsolute(int adr) throws InterruptedException
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

    private void popProgramCounter() throws InterruptedException
    {
        s = (s + 1) & 0xff;
        pc = memory.read(s + 256) & 0xff;
        s = (s + 1) & 0xff;
        pc += (memory.read(s + 256) & 0xff) << 8;
    }
}
