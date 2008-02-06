package chipset.cpu;

/*
 * Created on Aug 1, 2007
 */
public final class CPU6502
{
	private static final int MEMORY_LIM = 1 << 0x10;
	private static final int IRQ_VECTOR = MEMORY_LIM-2; // or BRK
	private static final int RESET_VECTOR = IRQ_VECTOR-2; // or power-on
	private static final int NMI_VECTOR = RESET_VECTOR-2;

	int address;
	int data;

	int a;
	int x;
	int y;
	int pc;
	int s;

	boolean n;
	boolean v;
	final boolean m = true;
	boolean b;
	boolean d;
	boolean i;
	boolean z;
	boolean c;

	int t; // clock cycle within instruction
	int opcode;
	int adl;
	int adh;
	int bal;
	int bah;
	int ial;
	int iah;
	int idx;
	int offset;
	boolean branch;
	int sc;
	boolean wc;

    /*volatile*/ boolean IRQ;
    /*volatile*/ boolean NMI;
    volatile boolean reset;

    AddressBus addressBus;

    boolean started;

	public CPU6502(final AddressBus addressBus)
	{
		this.addressBus = addressBus;
	}

	public void powerOn()
	{
		this.started = false;
    	this.reset = false;
    	this.IRQ = false;
    	this.NMI = false;
    	// TODO what else to initialize in CPU?
	}

	public void reset()
    {
    	this.started = true;
    	this.reset = true;
    	this.addressBus.reset();
    }

    public void IRQ()
    {
        this.IRQ = true;
    }

    public void NMI()
    {
        this.NMI = true;
    }

	void step()
	{
		do
		{
			tick();
		}
		while (this.t > 0);
	}

	public void tick()
	{
		if (!this.started)
		{
			return;
		}
		if (this.t == 0)
		{
			firstCycle();
		}
		else
		{
			subsequentCycle();
		}
		++this.t;
	}

	void firstCycle()
	{
		final boolean interrupt = this.NMI || this.reset || (!this.i && this.IRQ);

		if (interrupt)
		{
			this.pc = getInterruptAddress();
		}

		this.address = this.pc++;
		this.pc &= 0xFFFF;

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

	int getInterruptAddress()
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

	int getInterruptPseudoOpCode()
	{
		if (this.NMI)
		{
			return 0x100;
		}
		if (this.reset)
		{
			return 0x101;
		}
		if (!this.i && this.IRQ)
		{
			return 0x102;
		}
		throw new IllegalStateException();
	}

	final Addr[] addrs = new Addr[]
	{
	  	new Addr()
	  	{
	  		public void addr(int cycle)
	  		{
	  			switch (cycle)
	  			{
	  				case 1:
	  					address = pc;
	  					read(); // discard
	  					execute();
	  					done();
	  					break;
	  			}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						adh = 0;
						address = ad();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						address = pc++;
						read();
						adh = data;
					break;
					case 3:
						address = ad();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						address = bal;
						read(); // discard
					break;
					case 3:
						address += x;
						address &= 0xFF;
						read();
						adl = data;
					break;
					case 4:
						++address;
						address &= 0xFF;
						read();
						adh = data;
					break;
					case 5:
						address = ad();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						address = pc++;
						read();
						bah = data;
					break;
					case 3:
						setIndex();
						bal += idx;
						wc = (bal >= 0x100);
						if (wc)
						{
							bal -= 0x100;
						}
						address = ba();
						read();
						if (!wc)
						{
							execute();
							done();
						}
					break;
					case 4:
						++bah;
						address = ba();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						bah = 0;
						address = ba();
						read(); // discard
					break;
					case 3:
						setIndex();
						bal += idx;
						bal &= 0xFF; // doesn't leave page zero
						address = ba();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						ial = data;
					break;
					case 2:
						address = ial;
						read();
						bal = data;
					break;
					case 3:
						++address;
						address &= 0xFF; // doesn't leave page zero
						read();
						bah = data;
					break;
					case 4:
						bal += y;
						wc = (bal >= 0x100);
						if (wc)
						{
							bal -= 0x100;
						}
						address = ba();
						read();
						if (!wc)
						{
							execute();
							done();
						}
					break;
					case 5:
						++bah;
						address = ba();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
						execute();
					break;
					case 2:
						adh = 0;
						address = ad();
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						address = pc++;
						read();
						adh = data;
						execute();
					break;
					case 3:
						address = ad();
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						address = bal;
						address &= 0xFF;
						read(); // discard
					break;
					case 3:
						address += x;
						address &= 0xFF;
						read();
						adl = data;
					break;
					case 4:
						++address;
						address &= 0xFF;
						read();
						adh = data;
						execute();
					break;
					case 5:
						address = ad();
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						address = pc++;
						read();
						bah = data;
					break;
					case 3:
						setIndex();
						address = ba();
						address += idx;
						//read(); // discard (assume this is the right address, manual is ambiguous)
						execute();
					break;
					case 4:
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						bah = 0;
						address = ba();
						read(); // discard
						execute();
					break;
					case 3:
						setIndex();
						bal += idx;
						bal &= 0xFF; // doesn't leave page zero
						address = ba();
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						ial = data;
					break;
					case 2:
						address = ial;
						read();
						bal = data;
					break;
					case 3:
						++address;
						address &= 0xFF;
						read();
						bah = data;
					break;
					case 4:
						address = ba();
						address += y;
						read(); // discard
						execute();
					break;
					case 5:
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						adh = 0;
						address = ad();
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
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						address = pc++;
						read();
						adh = data;
					break;
					case 3:
						address = ad();
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
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						bah = 0;
						address = ba();
						read(); // discard
					break;
					case 3:
						bal += x; // doesn't leave page zero
						bal &= 0xFF;
						address = ba();
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
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						bal = data;
					break;
					case 2:
						address = pc++;
						read();
						bah = data;
					break;
					case 3:
						address = ba();
						address += x;
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
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
						execute();
					break;
					case 2:
						address = push();
						write();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
					break;
					case 2:
						address = sp();
						read(); // discard
					break;
					case 3:
						address = pull();
						read();
						execute();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						address = push();
						read(); // discard
					break;
					case 3:
						data = pch();
						write();
						address = push();
					break;
					case 4:
						data = pcl();
						write();
					break;
					case 5:
						address = pc;
						read();
						adh = data;
						pc = ad();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
					break;
					case 2:
						address = push();
						data = pch();
						write();
					break;
					case 3:
						address = push();
						data = pcl();
						write();
					break;
					case 4:
						address = push();
						b = true;
						data = getStatusRegisterByte();
						write();
					break;
					case 5:
						address = IRQ_VECTOR;
						read();
						adl = data;
					break;
					case 6:
						address = IRQ_VECTOR+1;
						read();
						adh = data;
						pc = ad();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
					break;
					case 2:
						address = sp();
						read(); // discard
					break;
					case 3:
						address = pull();
						read();
						setStatusRegisterByte(data);
					break;
					case 4:
						address = pull();
						read();
						adl = data;
					break;
					case 5:
						address = pull();
						read();
						adh = data;
						pc = ad();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						adl = data;
					break;
					case 2:
						address = pc;
						read();
						adh = data;
						pc = ad();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						ial = data;
					break;
					case 2:
						address = pc;
						read();
						iah = data;
					break;
					case 3:
						address = ia();
						read();
						adl = data;
					break;
					case 4:
						++address; // can leave the page
						read();
						adh = data;
						pc = ad();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
					break;
					case 2:
						address = sp();
						read(); // discard
					break;
					case 3:
						address = pull();
						read();
						adl = data;
					break;
					case 4:
						address = pull();
						read();
						adh = data;
					break;
					case 5:
						pc = ad();
						address = pc;
						read(); // discard
						++pc;
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read();
						offset = data;
						execute();
						if (!branch)
						{
							done();
						}
					break;
					case 2:
						if (offset >= 0x80)
						{
							offset -= 0x100;
						}
						int lo = pcl()+offset;
						sc = 0;
						if (lo < 0)
						{
							lo += 0x100;
							sc = -1;
						}
						else if (lo >= 0x100)
						{
							lo -= 0x100;
							sc = 1;
						}
						pc = (pc & 0xFF00) | lo;
						address = pc;
						read();
						if (sc == 0)
						{
							done();
						}
					break;
					case 3:
						int hi = pch() + sc;
						pc = (hi << 8) | pcl();
						read();
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
					break;
					case 2:
						address = push();
						data = pch();
						write();
					break;
					case 3:
						address = push();
						data = pcl(); // ???
						write();
					break;
					case 4:
						address = push();
						i = true;
						b = false; // ???
						data = getStatusRegisterByte();
						write();
					break;
					case 5:
						address = pc++;
						read();
						adl = data;
					break;
					case 6:
						address = pc;
						read();
						adh = data;
						pc = ad();
						IRQ = false;
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc++;
						read(); // discard
					break;
					case 2:
				        s = 0xFF; // real CPU doesn't do this ???
						address = push();
						data = pch();
						read(); // discard
					break;
					case 3:
						address = push();
						data = pcl();
						read(); // discard
					break;
					case 4:
						address = push();
						i = true;
						data = getStatusRegisterByte();
						read(); // discard
					break;
					case 5:
						address = pc++;
						read();
						adl = data;
					break;
					case 6:
						address = pc;
						read();
						adh = data;
						pc = ad();
						reset = false;
						done();
					break;
				}
			}
		}
		,
		new Addr()
		{
			public void addr(int cycle)
			{
				switch (cycle)
				{
					case 1:
						address = pc;
						read(); // discard
					break;
					case 2:
						address = push();
						data = pch();
						write();
					break;
					case 3:
						address = push();
						data = pcl();
						write();
					break;
					case 4:
						address = push();
						i = true;
						b = false; // ???
	  					data = getStatusRegisterByte();
	  					write();
	  				break;
	  				case 5:
	  					++address;
	  					read();
	  					adl = data;
	  				break;
	  				case 6:
	  					++address;
	  					read();
	  					adh = data;
	  					pc = ad();
	  					NMI = false;
	  					done();
	  				break;
	  			}
	  		}
		}
	};

	void subsequentCycle()
	{
		final int mode = AddressingModeCalculator.getMode(this.opcode);
		final Addr addr = addrs[mode];
		addr.addr(this.t);
	}

	int pch()
	{
		return (this.pc >> 8) & 0xFF;
	}

	int pcl()
	{
		return this.pc & 0xFF;
	}

	int sp()
	{
		return 0x100+this.s;
	}

	int push()
	{
		final int sp = sp();
		--this.s;
		this.s &= 0xFF;
		return sp;
	}

	int pull()
	{
		++this.s;
		this.s &= 0xFF;
		return sp();
	}

	void setIndex()
	{
		this.idx = getIndex();
	}

	int getIndex()
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

	int ad()
	{
		return combine(this.adl,this.adh);
	}

	int ia()
	{
		return combine(this.ial,this.iah);
	}

	static int combine(final int lo, final int hi)
	{
		return ((hi & 0xFF) << 8) | (lo & 0xFF);
	}

	int ba()
	{
		return combine(this.bal,this.bah);
	}

	void done()
	{
		this.t = -1;
	}

	void read()
	{
		this.data = this.addressBus.read(this.address) & 0xFF;
	}

	void write()
	{
		this.addressBus.write(this.address,(byte)this.data);
	}



























    void setStatusRegisterByte(final int p)
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

    void setStatusRegisterNZ(final int val)
    {
    	final byte byt = (byte)val;
        this.n = byt < 0;
        this.z = byt == 0;
    }

    void setFlagCarry(final int val)
    {
    	this.c = val >= 0x100;
    }

    void setFlagBorrow(final int val)
    {
    	this.c = 0 <= val && val < 0x100;
    }

    int getStatusRegisterByte()
    {
        int p = 0;
        if (this.n)
            p |= 0x80;
        if (this.v)
            p |= 0x40;
//        if (this.m) // always true
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







    void LDA()
    {
        this.a = this.data & 0xFF;
        setStatusRegisterNZ(this.a);
    }

    void LDX()
    {
        this.x = this.data & 0xFF;
        setStatusRegisterNZ(this.x);
    }

    void LDY()
    {
        this.y = this.data & 0xFF;
        setStatusRegisterNZ(this.y);
    }

    void STA()
    {
        this.data = this.a;
    }

    void STX()
    {
        this.data = this.x;
    }

    void STY()
    {
        this.data = this.y;
    }

    void CMP()
    {
        final int tmp = this.a - this.data;
        setFlagBorrow(tmp);
        setStatusRegisterNZ(tmp);
    }

    void CPX()
    {
        final int tmp = this.x - this.data;
        setFlagBorrow(tmp);
        setStatusRegisterNZ(tmp);
    }

    void CPY()
    {
        final int tmp = this.y - this.data;
        setFlagBorrow(tmp);
        setStatusRegisterNZ(tmp);
    }

    void AND()
    {
        this.a &= this.data;
        setStatusRegisterNZ(this.a);
    }

    void ORA()
    {
        this.a |= this.data;
        setStatusRegisterNZ(this.a);
    }

    void EOR()
    {
        this.a ^= this.data;
        setStatusRegisterNZ(this.a);
    }






    void ASL()
    {
    	this.data = shiftLeft(this.data);
    }

    void ASL_A()
    {
    	this.a = shiftLeft(this.a);
    }

    void LSR()
    {
        this.data = shiftRight(this.data);
    }

    void LSR_A()
    {
        this.a = shiftRight(this.a);
    }

    void ROL()
    {
    	this.data = rotateLeft(this.data);
    }

    void ROL_A()
    {
    	this.a = rotateLeft(this.a);
    }

    void ROR()
    {
    	this.data = rotateRight(this.data);
    }

    void ROR_A()
    {
    	this.a = rotateRight(this.a);
    }

    int shiftLeft(int byt)
    {
    	byt &= 0xFF;
        this.c = (byt & 0x80) != 0;
        byt <<= 1;
        byt &= 0xFF;
        setStatusRegisterNZ(byt);
        return byt;
    }

    int shiftRight(int byt)
    {
    	byt &= 0xFF;
        this.c = (byt & 0x01) != 0;
        byt >>= 1;
    	byt &= 0xFF;
        setStatusRegisterNZ(byt);
        return byt;
    }

    int rotateLeft(int byt)
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

    int rotateRight(int byt)
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






    void ADC()
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

    void SBC()
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




    void INC()
    {
        ++this.data;
        this.data &= 0xFF;
        setStatusRegisterNZ(this.data);
    }

    void DEC()
    {
        --this.data;
        this.data &= 0xFF;
        setStatusRegisterNZ(this.data);
    }

    void INX()
    {
        ++this.x;
        this.x &= 0xFF;
        setStatusRegisterNZ(this.x);
    }

    void INY()
    {
        ++this.y;
        this.y &= 0xFF;
        setStatusRegisterNZ(this.y);
    }

    void DEX()
    {
        --this.x;
        this.x &= 0xFF;
        setStatusRegisterNZ(this.x);
    }

    void DEY()
    {
        --this.y;
        this.y &= 0xFF;
        setStatusRegisterNZ(this.y);
    }

    void BIT()
    {
        final byte btmp = (byte)this.data;
        this.v = (btmp & 0x40) != 0;
        this.n = (btmp & 0x80) != 0;
        this.z = (btmp & this.a) == 0;
    }

    void PHA()
    {
    	this.data = this.a;
    }

    void PHP()
    {
    	this.data = getStatusRegisterByte();
    }

    void PLA()
    {
    	this.a = this.data;
        setStatusRegisterNZ(this.a);
    }

    void PLP()
    {
    	setStatusRegisterByte(this.data);
    }

//    void BRK()
//    {
//    }
//
//    void RTI()
//    {
//    }
//
//    void JMP()
//    {
//    }
//
//    void RTS()
//    {
//    }
//
//    void JSR()
//    {
//    }

    void BNE()
    {
    	this.branch = !this.z;
    }

    void BEQ()
    {
    	this.branch = this.z;
    }

    void BVC()
    {
    	this.branch = !this.v;
    }

    void BVS()
    {
    	this.branch = this.v;
    }

    void BCC()
    {
    	this.branch = !this.c;
    }

    void BCS()
    {
    	this.branch = this.c;
    }

    void BPL()
    {
    	this.branch = !this.n;
    }

    void BMI()
    {
    	this.branch = this.n;
    }

    void TAX()
    {
        this.x = this.a;
        setStatusRegisterNZ(this.x);
    }

    void TXA()
    {
        this.a = this.x;
        setStatusRegisterNZ(this.a);
    }

    void TAY()
    {
        this.y = this.a;
        setStatusRegisterNZ(this.y);
    }

    void TYA()
    {
        this.a = this.y;
        setStatusRegisterNZ(this.a);
    }

    void TXS()
    {
        this.s = this.x;
        //setStatusRegisterNZ(this.s); // does NOT affect status register
    }

    void TSX()
    {
        this.x = this.s;
        setStatusRegisterNZ(this.x);
    }

    void CLC()
    {
    	this.c = false;
    }

    void SEC()
    {
    	this.c = true;
    }

    void CLI()
    {
    	this.i = false;
    }

    void SEI()
    {
    	this.i = true;
    }

    void CLV()
    {
    	this.v = false;
    }

    void CLD()
    {
    	this.d = false;
    }

    void SED()
    {
    	this.d = true;
    }

//    void NOP()
//    {
//    	// NO OPERATION!
//    }

    void Unoff()
    {
    }

    void Unoff1()
    {
    }

    void Unoff2()
    {
    	this.pc++;
    }

    void Unoff3()
    {
    	this.pc += 2;
    }

    void Hang()
    {
    	this.pc--;
    }

	void execute()
    {
		// TODO undocumented instructions not yet implemented
        final Instr instr = this.instrs[this.opcode];
        instr.exec();
    }

	final Instr[] instrs = new Instr[]
	{
		new Instr()
		{
			public void exec()
			{
//				BRK();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ASL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				PHP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ASL_A();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff(); // FIXED
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ASL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BPL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ASL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CLC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff1();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ORA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ASL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
//				JSR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BIT();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				PLP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROL_A();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BIT();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BMI();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SEC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff1();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				AND();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROL();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
//				RTI();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LSR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				PHA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LSR_A();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
//				JMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LSR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BVC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LSR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CLI();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff1();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				EOR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LSR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
//				RTS();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				PLA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROR_A();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
//				JMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BVS();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SEI();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff1();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ADC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				ROR();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				DEY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				TXA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BCC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				TYA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				TXS();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				STA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				TAY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				TAX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BCS();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CLV();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				TSX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDA();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				LDX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CPY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CPY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				DEC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				INY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				DEX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CPY();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				DEC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BNE();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				DEC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CLD();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff1();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CMP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				DEC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CPX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CPX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				INC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				INX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				//NOP();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff(); // FIXED
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				CPX();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				INC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				BEQ();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Hang();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff2();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				INC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SED();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff1();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff3();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				SBC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				INC();
			}
		}
		,
		new Instr()
		{
			public void exec()
			{
				Unoff();
			}
		}
	};
}
