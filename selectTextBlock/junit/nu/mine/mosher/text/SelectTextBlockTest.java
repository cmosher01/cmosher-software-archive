/*
 * Created on July 29, 2005
 */
package nu.mine.mosher.text;

import junit.framework.TestCase;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class SelectTextBlockTest extends TestCase
{
	/**
	 * Test for text containing 1 block.
	 */
	public void testBlockCount1()
	{
		final String blocks = "test";
		final String block1 = "test";

		assertEquals("",SelectTextBlock.selectTextBlock(blocks,-1));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,0));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,1));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,2));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,3));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,4));
		assertEquals("",SelectTextBlock.selectTextBlock(blocks,5));

		assert blocks.length() == 4;
	}

	/**
	 * Test for text containing 2 blocks.
	 */
	public void testBlockCount2()
	{
		final String blocks = "test\n\nthis";
		final String block1 = "test";
		final String block2 = "this";

		assertEquals("",SelectTextBlock.selectTextBlock(blocks,-1));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,0));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,1));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,2));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,3));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,4));
		assertEquals("",SelectTextBlock.selectTextBlock(blocks,5)); // ???
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,6));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,7));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,8));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,9));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,10));
		assertEquals("",SelectTextBlock.selectTextBlock(blocks,11));

		assert blocks.length() == 10;
	}

	/**
	 * Test for text containing 3 blocks.
	 */
	public void testBlockCount3()
	{
		final String blocks = "test\n\nthis\n\ncode";
		final String block1 = "test";
		final String block2 = "this";
		final String block3 = "code";

		assertEquals("",SelectTextBlock.selectTextBlock(blocks,-1));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,0));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,1));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,2));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,3));
		assertEquals(block1,SelectTextBlock.selectTextBlock(blocks,4));
		assertEquals("",SelectTextBlock.selectTextBlock(blocks,5)); // ???
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,6));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,7));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,8));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,9));
		assertEquals(block2,SelectTextBlock.selectTextBlock(blocks,10));
		assertEquals("",SelectTextBlock.selectTextBlock(blocks,11)); // ???
		assertEquals(block3,SelectTextBlock.selectTextBlock(blocks,12));
		assertEquals(block3,SelectTextBlock.selectTextBlock(blocks,13));
		assertEquals(block3,SelectTextBlock.selectTextBlock(blocks,14));
		assertEquals(block3,SelectTextBlock.selectTextBlock(blocks,15));
		assertEquals(block3,SelectTextBlock.selectTextBlock(blocks,16));
		assertEquals("",SelectTextBlock.selectTextBlock(blocks,17));

		assert blocks.length() == 16;
	}

	/**
	 * Test some strange cases.
	 */
	public void testStrangeCases()
	{
		assertEquals("",SelectTextBlock.selectTextBlock("test",Integer.MIN_VALUE));
		assertEquals("",SelectTextBlock.selectTextBlock("test",Integer.MAX_VALUE));

		assertEquals("",SelectTextBlock.selectTextBlock("",Integer.MIN_VALUE));
		assertEquals("",SelectTextBlock.selectTextBlock("",-1));
		assertEquals("",SelectTextBlock.selectTextBlock("",0));
		assertEquals("",SelectTextBlock.selectTextBlock("",1));
		assertEquals("",SelectTextBlock.selectTextBlock("",Integer.MAX_VALUE));

		assertEquals("",SelectTextBlock.selectTextBlock("\n",-1));
		assertEquals("\n",SelectTextBlock.selectTextBlock("\n",0));
		assertEquals("\n",SelectTextBlock.selectTextBlock("\n",1));
		assertEquals("",SelectTextBlock.selectTextBlock("\n",2));

		assertEquals("",SelectTextBlock.selectTextBlock("\n\n",-1));
		assertEquals("\n\n",SelectTextBlock.selectTextBlock("\n\n",0));
		assertEquals("",SelectTextBlock.selectTextBlock("\n\n",1));
		assertEquals("\n\n",SelectTextBlock.selectTextBlock("\n\n",2));
		assertEquals("",SelectTextBlock.selectTextBlock("\n\n",3));
	}
}
