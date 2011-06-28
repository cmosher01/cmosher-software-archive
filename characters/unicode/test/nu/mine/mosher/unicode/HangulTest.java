package nu.mine.mosher.unicode;

import junit.framework.TestCase;

/**
 * @author Chris Mosher
 */
public class HangulTest extends TestCase
{
    public HangulTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(HangulTest.class);
    }

    public void testCompose()
    {
    	int[] r = new int[3];
    	r[0] = 0x1111;
    	r[1] = 0x1171;
    	r[2] = 0x11b6;
		String32 s32 = new String32(r);
    	s32 = Hangul.compose(s32);
		r = s32.get();
    	assertEquals(r[0],0xd4db);
		assertEquals(r.length,1);

		r = new int[2];
		r[0] = 0x1100;
		r[1] = 0x1161;
		s32 = new String32(r);
		s32 = Hangul.compose(s32);
		r = s32.get();
		assertEquals(r[0],0xac00);
		assertEquals(r.length,1);
    }

	public void testDecompose()
	{
		int[] r = new int[1];
		r[0] = 0xd4db;
		String32 s32 = new String32(r);
		s32 = Hangul.decompose(s32);
		r = s32.get();
		assertEquals(r[0],0x1111);
		assertEquals(r[1],0x1171);
		assertEquals(r[2],0x11b6);
		assertEquals(r.length,3);

		r = new int[1];
		r[0] = 0xac00;
		s32 = new String32(r);
		s32 = Hangul.decompose(s32);
		r = s32.get();
		assertEquals(r[0],0x1100);
		assertEquals(r[1],0x1161);
		assertEquals(r.length,2);
	}
}
