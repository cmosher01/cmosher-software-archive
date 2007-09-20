package chipset;
import chipset.CPU6502;
import chipset.Memory;
import junit.framework.TestCase;

/*
 * Created on Sep 15, 2007
 */
public class CPU6502Test extends TestCase
{
	private CPU6502 cpu;
	private Memory memory;
	
	protected void setUp() throws Exception
	{
		this.memory = new Memory(null,null);
		this.cpu = new CPU6502(this.memory);
	}

	protected void tearDown() throws Exception
	{
	}

	public void wr(final int addr, final int data)
	{
		wr(addr,(byte)data);
	}

	public void test_INTERNAL_INDIRECT_X()
	{
		// LDA ($FF,X)
		wr(0x80,0xA1);
		wr(0x81,0xFF);

		this.cpu.x = 0x90;

		wr(0x8F,0x01);
		wr(0x90,0x20);

		wr(0x2001,0xA5);

		this.cpu.a = 0;

		this.cpu.pc = 0x80;
		this.cpu.step();

		assertEquals(0xA5,this.cpu.a);
	}

	public void test_INTERNAL_INDIRECT_Y()
	{
		// LDA ($FF),Y
		wr(0x80,0xB1);
		wr(0x81,0xFF);

		this.cpu.y = 3;

		wr(0xFF,0x01);
		wr(0x00,0x20);

		wr(0x2004,0xA5);

		this.cpu.a = 0;

		this.cpu.pc = 0x80;
		this.cpu.step();

		assertEquals(0xA5,this.cpu.a);
	}

	public void test_INTERNAL_ABSOLUTE_XY()
	{
		// LDA $1FFE,Y
		wr(0x80,0xB9);
		wr(0x81,0xFE);
		wr(0x82,0x1F);

		this.cpu.y = 5;

		wr(0x2003,0xA5);

		this.cpu.a = 0;

		this.cpu.pc = 0x80;
		this.cpu.step();

		assertEquals(0xA5,this.cpu.a);
	}

	public void test_INTERNAL_ZERO_PAGE_XY()
	{
		// LDA $FE,X
		wr(0x80,0xB5);
		wr(0x81,0xFE);

		this.cpu.x = 4;

		wr(0x02,0xA5);

		this.cpu.a = 0;

		this.cpu.pc = 0x80;
		this.cpu.step();

		assertEquals(0xA5,this.cpu.a);
	}

	public void test_STORE_INDIRECT_X()
	{
		
	}

	public void test_LSR()
	{
		// LSR $20
		wr(0x80,0x46);
		wr(0x81,0x20);

		wr(0x20,0x84);

		this.cpu.c = true;
		this.cpu.n = true;
		this.cpu.z = true;

		this.cpu.pc = 0x80;
		this.cpu.step();

		assertEquals(0x42,this.memory.read(0x20));
		assertFalse(this.cpu.c);
		assertFalse(this.cpu.n);
		assertFalse(this.cpu.z);
	}
}
