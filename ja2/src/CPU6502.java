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
	private int s;

	private int t; // clock cycle within instruction
	private int opcode;
	private int adl;
	private int adh;

	private Memory memory;

	public void tick()
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
						read(); // ignored
						execute();
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
					break;
				}
			break;
		}
		++this.t;
	}

	private int ad()
	{
		return this.adh << 8 | this.adl;
	}

	private void execute()
	{
		this.t = -1;
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
