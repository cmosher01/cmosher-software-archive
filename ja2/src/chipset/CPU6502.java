package chipset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import other.HexUtil;

/*
 * Created on Aug 1, 2007
 */
public final class CPU6502 implements Clock.Timed
{
	private static final int MEMORY_LIM = 0x10000;
	private static final int IRQ_VECTOR = MEMORY_LIM-2; // or BRK
	private static final int RESET_VECTOR = IRQ_VECTOR-2; // or power-on
	private static final int NMI_VECTOR = RESET_VECTOR-2;

	private int address;
	private int data;

	protected int a;
	protected int x;
	protected int y;
	protected int pc;
	protected int s;

	protected boolean n;
	protected boolean v;
	private final boolean m = true;
	protected boolean b;
	protected boolean d;
	protected boolean i;
	protected boolean z;
	protected boolean c;

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
    private volatile boolean reset;

    private Memory memory;


	public CPU6502(final Memory memory)
	{
		this.memory = memory;
		reset();
	}

    public void reset()
    {
    	// BEGIN Apple ][ specific:
    	this.memory.read(0xC0E8); // turn off disk drive
    	// END   Apple ][ specific.
//        this.i = true;
//        this.pc = memReadAbsolute(RESET_VECTOR);
//
//        this.s = 0xFF; // real CPU doesn't do this ???
    	this.reset = true;
    }

    public void IRQ()
    {
        this.IRQ = true;
    }

    public void NMI()
    {
        this.NMI = true;
    }

	public void step()
	{
		do
		{
			tick();
		}
		while (this.t > 0);
	}

	public void stopped()
	{
//		dumpTrace();
	}

	public void tick()
	{
		if (this.t == 0)
		{
//			trace();
			firstCycle();
		}
		else
		{
			subsequentCycle();
		}
//		checkSane();
		++this.t;
	}

	private void firstCycle()
	{
		final boolean interrupt = isInterrupted();

		if (interrupt)
		{
			this.pc = getInterruptAddress();
		}

		this.address = this.pc++;
		read();

		if (interrupt)
		{
			this.opcode = getInterruptPseudoOpCode();
		}
		else
		{
			this.opcode = this.data;
		}
	}

	private boolean isInterrupted()
	{
		return
			this.NMI ||
			this.reset ||
			(!this.i && this.IRQ);
	}

	private int getInterruptAddress()
	{
		if (this.NMI)
		{
			return NMI_VECTOR-2;
		}
		if (this.reset)
		{
			return RESET_VECTOR-2;
		}
		if (!this.i && this.IRQ)
		{
			return IRQ_VECTOR-2;
		}
		throw new IllegalStateException();
	}

	private int getInterruptPseudoOpCode()
	{
		if (this.NMI)
		{
			return 0x80000001;
		}
		if (this.reset)
		{
			return 0x80000002;
		}
		if (!this.i && this.IRQ)
		{
			return 0x80000003;
		}
		throw new IllegalStateException();
	}

//	private Map<Integer,Integer> mapCoverageCount = new HashMap<Integer,Integer>();
//	private void trace()
//	{
//		if (!this.mapCoverageCount.containsKey(this.pc))
//		{
//			this.mapCoverageCount.put(this.pc,0);
//		}
//		int c = this.mapCoverageCount.get(this.pc);
//		++c;
//		this.mapCoverageCount.put(this.pc,c);
//	}
//
//	private static class CompareValues implements Comparator<Entry<Integer,Integer>>
//	{
//		public int compare(Entry<Integer,Integer> e1, Entry<Integer,Integer> e2)
//		{
//			if (e1.getValue() < e2.getValue())
//			{
//				return -1;
//			}
//			if (e2.getValue() < e1.getValue())
//			{
//				return +1;
//			}
//			return 0;
//		}
//		
//	}
//
//	public void dumpTrace()
//	{
//		final SortedSet<Entry<Integer,Integer>> trace = new TreeSet<Entry<Integer,Integer>>(Collections.<Entry<Integer,Integer>>reverseOrder(new CompareValues()));
//		trace.addAll(this.mapCoverageCount.entrySet());
//		for (final Map.Entry<Integer,Integer> e : trace)
//		{
//			System.out.println(HexUtil.word(e.getKey())+": "+e.getValue());
//		}
//	}

	private void subsequentCycle()
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
						//read(); // discard (assume this is the right address, manual is ambiguous) (it's messing up disk writing, so I'm omitting it)
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
						this.b = true;
						this.data = getStatusRegisterByte();
						write();
					break;
					case 5:
						this.address = IRQ_VECTOR;
						read();
						this.adl = this.data;
					break;
					case 6:
						this.address = IRQ_VECTOR+1;
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






			case RESET:
				switch (this.t)
				{
					case 1:
						this.address = this.pc++;
						read(); // discard
					break;
					case 2:
				        this.s = 0xFF; // real CPU doesn't do this ???
						this.address = spPush();
						this.data = pch();
						read(); // discard
					break;
					case 3:
						this.address = spPush();
						this.data = pcl();
						read(); // discard
					break;
					case 4:
						this.address = spPush();
						this.i = true;
						this.data = getStatusRegisterByte();
						read(); // discard
					break;
					case 5:
						this.address = this.pc++;
						read();
						this.adl = this.data;
					break;
					case 6:
						this.address = this.pc;
						read();
						this.adh = this.data;
						this.pc = ad();
						this.reset = false;
						done();
					break;
				}
			break;
			case IRQ:
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
						this.data = pcl(); // ???
						write();
					break;
					case 4:
						this.address = spPush();
						this.i = true;
						this.b = false; // ???
						this.data = getStatusRegisterByte();
						write();
					break;
					case 5:
						this.address = this.pc++;
						read();
						this.adl = this.data;
					break;
					case 6:
						this.address = this.pc;
						read();
						this.adh = this.data;
						this.pc = ad();
						this.IRQ = false;
						done();
					break;
				}
			break;
			case NMI:
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
						this.i = true;
						this.b = false; // ???
						this.data = getStatusRegisterByte();
						write();
					break;
					case 5:
						++this.address;
						read();
						this.adl = this.data;
					break;
					case 6:
						++this.address;
						read();
						this.adh = this.data;
						this.pc = ad();
						this.NMI = false;
						done();
					break;
				}
			break;
		}
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
		// opcode: aaabbbcc
		int aaa = (this.opcode & 0xE0) >> 5;
		int bbb = (this.opcode & 0x1C) >> 2;
		int cc = (this.opcode & 0x03);
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
		this.data &= 0xFF;
	}

	private void write()
	{
		this.memory.write(this.address,(byte)this.data);
	}



























    private void setStatusRegisterByte(final int p)
    {
    	this.n = ((p & 0x80) != 0);
    	this.v = ((p & 0x40) != 0);
//    	this.m = ((p & 0x20) != 0); can't clear this flag
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







    private void LDA()
    {
        this.a = this.data & 0xFF;
        setStatusRegisterNZ(this.a);
    }

    private void LDX()
    {
        this.x = this.data & 0xFF;
        setStatusRegisterNZ(this.x);
    }

    private void LDY()
    {
        this.y = this.data & 0xFF;
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
    	this.data = shiftLeft(this.data);
    }

    private void ASL_A()
    {
    	this.a = shiftLeft(this.a);
    }

    private void LSR()
    {
        this.data = shiftRight(this.data);
    }

    private void LSR_A()
    {
        this.a = shiftRight(this.a);
    }

    private void ROL()
    {
    	this.data = rotateLeft(this.data);
    }

    private void ROL_A()
    {
    	this.a = rotateLeft(this.a);
    }

    private void ROR()
    {
    	this.data = rotateRight(this.data);
    }

    private void ROR_A()
    {
    	this.a = rotateRight(this.a);
    }

    private int shiftLeft(int byt)
    {
    	byt &= 0xFF;
        this.c = (byt & 0x80) != 0;
        byt <<= 1;
        byt &= 0xFF;
        setStatusRegisterNZ(byt);
        return byt;
    }

    private int shiftRight(int byt)
    {
    	byt &= 0xFF;
        this.c = (byt & 0x01) != 0;
        byt >>= 1;
    	byt &= 0xFF;
        setStatusRegisterNZ(byt);
        return byt;
    }

    private int rotateLeft(int byt)
    {
    	byt &= 0xFF;

    	final boolean newCarry = (byt & 0x80) != 0;

        byt <<= 1;
    	byt &= 0xFF;

    	if (this.c)
    	{
    		byt |= 0x01;
    	}

    	this.c = newCarry;
        setStatusRegisterNZ(byt);

        return byt;
    }

    private int rotateRight(int byt)
    {
    	byt &= 0xFF;

    	final boolean newCarry = (byt & 0x01) != 0;

        byt >>= 1;
    	byt &= 0xFF;

    	if (this.c)
    	{
    		byt |= 0x80;
    	}

    	this.c = newCarry;
        setStatusRegisterNZ(byt);

        return byt;
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
            this.a &= 0xFF;
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
    }

    private void RTI()
    {
    }

    private void JMP()
    {
    }

    private void RTS()
    {
    }

    private void JSR()
    {
    }

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
        //setStatusRegisterNZ(this.s); // does NOT affect status register
    }

    private void TSX()
    {
        this.x = this.s;
        setStatusRegisterNZ(this.x);
    }

    private void CLC()
    {
    	this.c = false;
    }

    private void SEC()
    {
    	this.c = true;
    }

    private void CLI()
    {
    	this.i = false;
    }

    private void SEI()
    {
    	this.i = true;
    }

    private void CLV()
    {
    	this.v = false;
    }

    private void CLD()
    {
    	this.d = false;
    }

    private void SED()
    {
    	this.d = true;
    }

    private void NOP()
    {
    	// NO OPERATION!
    }

    private void Unoff()
    {
    }

    private void Unoff1()
    {
    }

    private void Unoff2()
    {
    	this.pc++;
    }

    private void Unoff3()
    {
    	this.pc += 2;
    }

    private void Hang()
    {
    	this.pc--;
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
	private void execute()
    {
		// TODO undocumented instructions not yet implemented
        switch (this.opcode)
		{
			case 0:
				BRK();
			break;
			case 1:
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
				ORA();
			break;
			case 6:
				ASL();
			break;
			case 7:
				Unoff();
			break;
			case 8:
				PHP();
			break;
			case 9:
				ORA();
			break;
			case 10:
				ASL_A();
			break;
			case 11:
				Unoff(); // FIXED
			break;
			case 12:
				Unoff3();
			break;
			case 13:
				ORA();
			break;
			case 14:
				ASL();
			break;
			case 15:
				Unoff();
			break;
			case 16:
				BPL();
			break;
			case 17:
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
				ORA();
			break;
			case 22:
				ASL();
			break;
			case 23:
				Unoff();
			break;
			case 24:
				CLC();
			break;
			case 25:
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
				ORA();
			break;
			case 30:
				ASL();
			break;
			case 31:
				Unoff();
			break;
			case 32:
				JSR();
			break;
			case 33:
				AND();
			break;
			case 34:
				Hang();
			break;
			case 35:
				Unoff();
			break;
			case 36:
				BIT();
			break;
			case 37:
				AND();
			break;
			case 38:
				ROL();
			break;
			case 39:
				Unoff();
			break;
			case 40:
				PLP();
			break;
			case 41:
				AND();
			break;
			case 42:
				ROL_A();
			break;
			case 43:
				Unoff();
			break;
			case 44:
				BIT();
			break;
			case 45:
				AND();
			break;
			case 46:
				ROL();
			break;
			case 47:
				Unoff();
			break;
			case 48:
				BMI();
			break;
			case 49:
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
				AND();
			break;
			case 54:
				ROL();
			break;
			case 55:
				Unoff();
			break;
			case 56:
				SEC();
			break;
			case 57:
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
				AND();
			break;
			case 62:
				ROL();
			break;
			case 63:
				Unoff();
			break;
			case 64:
				RTI();
			break;
			case 65:
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
				EOR();
			break;
			case 70:
				LSR();
			break;
			case 71:
				Unoff();
			break;
			case 72:
				PHA();
			break;
			case 73:
				EOR();
			break;
			case 74:
				LSR_A();
			break;
			case 75:
				Unoff();
			break;
			case 76:
				JMP();
			break;
			case 77:
				EOR();
			break;
			case 78:
				LSR();
			break;
			case 79:
				Unoff();
			break;
			case 80:
				BVC();
			break;
			case 81:
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
				EOR();
			break;
			case 86:
				LSR();
			break;
			case 87:
				Unoff();
			break;
			case 88:
				CLI();
			break;
			case 89:
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
				EOR();
			break;
			case 94:
				LSR();
			break;
			case 95:
				Unoff();
			break;
			case 96:
				RTS();
			break;
			case 97:
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
				ADC();
			break;
			case 102:
				ROR();
			break;
			case 103:
				Unoff();
			break;
			case 104:
				PLA();
			break;
			case 105:
				ADC();
			break;
			case 106:
				ROR_A();
			break;
			case 107:
				Unoff();
			break;
			case 108:
				JMP();
			break;
			case 109:
				ADC();
			break;
			case 110:
				ROR();
			break;
			case 111:
				Unoff();
			break;
			case 112:
				BVS();
			break;
			case 113:
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
				ADC();
			break;
			case 118:
				ROR();
			break;
			case 119:
				Unoff();
			break;
			case 120:
				SEI();
			break;
			case 121:
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
				ADC();
			break;
			case 126:
				ROR();
			break;
			case 127:
				Unoff();
			break;
			case 128:
				Unoff2();
			break;
			case 129:
				STA();
			break;
			case 130:
				Unoff2();
			break;
			case 131:
				Unoff();
			break;
			case 132:
				STY();
			break;
			case 133:
				STA();
			break;
			case 134:
				STX();
			break;
			case 135:
				Unoff();
			break;
			case 136:
				DEY();
			break;
			case 137:
				Unoff2();
			break;
			case 138:
				TXA();
			break;
			case 139:
				Unoff();
			break;
			case 140:
				STY();
			break;
			case 141:
				STA();
			break;
			case 142:
				STX();
			break;
			case 143:
				Unoff();
			break;
			case 144:
				BCC();
			break;
			case 145:
				STA();
			break;
			case 146:
				Hang();
			break;
			case 147:
				Unoff();
			break;
			case 148:
				STY();
			break;
			case 149:
				STA();
			break;
			case 150:
				STX();
			break;
			case 151:
				Unoff();
			break;
			case 152:
				TYA();
			break;
			case 153:
				STA();
			break;
			case 154:
				TXS();
			break;
			case 155:
				Unoff();
			break;
			case 156:
				Unoff();
			break;
			case 157:
				STA();
			break;
			case 158:
				Unoff();
			break;
			case 159:
				Unoff();
			break;
			case 160:
				LDY();
			break;
			case 161:
				LDA();
			break;
			case 162:
				LDX();
			break;
			case 163:
				Unoff();
			break;
			case 164:
				LDY();
			break;
			case 165:
				LDA();
			break;
			case 166:
				LDX();
			break;
			case 167:
				Unoff();
			break;
			case 168:
				TAY();
			break;
			case 169:
				LDA();
			break;
			case 170:
				TAX();
			break;
			case 171:
				Unoff();
			break;
			case 172:
				LDY();
			break;
			case 173:
				LDA();
			break;
			case 174:
				LDX();
			break;
			case 175:
				Unoff();
			break;
			case 176:
				BCS();
			break;
			case 177:
				LDA();
			break;
			case 178:
				Hang();
			break;
			case 179:
				Unoff();
			break;
			case 180:
				LDY();
			break;
			case 181:
				LDA();
			break;
			case 182:
				LDX();
			break;
			case 183:
				Unoff();
			break;
			case 184:
				CLV();
			break;
			case 185:
				LDA();
			break;
			case 186:
				TSX();
			break;
			case 187:
				Unoff();
			break;
			case 188:
				LDY();
			break;
			case 189:
				LDA();
			break;
			case 190:
				LDX();
			break;
			case 191:
				Unoff();
			break;
			case 192:
				CPY();
			break;
			case 193:
				CMP();
			break;
			case 194:
				Unoff2();
			break;
			case 195:
				Unoff();
			break;
			case 196:
				CPY();
			break;
			case 197:
				CMP();
			break;
			case 198:
				DEC();
			break;
			case 199:
				Unoff();
			break;
			case 200:
				INY();
			break;
			case 201:
				CMP();
			break;
			case 202:
				DEX();
			break;
			case 203:
				Unoff();
			break;
			case 204:
				CPY();
			break;
			case 205:
				CMP();
			break;
			case 206:
				DEC();
			break;
			case 207:
				Unoff();
			break;
			case 208:
				BNE();
			break;
			case 209:
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
				CMP();
			break;
			case 214:
				DEC();
			break;
			case 215:
				Unoff();
			break;
			case 216:
				CLD();
			break;
			case 217:
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
				CMP();
			break;
			case 222:
				DEC();
			break;
			case 223:
				Unoff();
			break;
			case 224:
				CPX();
			break;
			case 225:
				SBC();
			break;
			case 226:
				Unoff2();
			break;
			case 227:
				Unoff();
			break;
			case 228:
				CPX();
			break;
			case 229:
				SBC();
			break;
			case 230:
				INC();
			break;
			case 231:
				Unoff();
			break;
			case 232:
				INX();
			break;
			case 233:
				SBC();
			break;
			case 234:
				NOP();
			break;
			case 235:
				Unoff(); // FIXED
			break;
			case 236:
				CPX();
			break;
			case 237:
				SBC();
			break;
			case 238:
				INC();
			break;
			case 239:
				Unoff();
			break;
			case 240:
				BEQ();
			break;
			case 241:
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
				SBC();
			break;
			case 246:
				INC();
			break;
			case 247:
				Unoff();
			break;
			case 248:
				SED();
			break;
			case 249:
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
				SBC();
			break;
			case 254:
				INC();
			break;
			case 255:
				Unoff();
			break;
			default:
				throw new IllegalStateException();
		}
    }
}
