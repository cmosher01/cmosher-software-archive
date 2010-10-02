import static org.junit.Assert.*;

import org.junit.Test;


public class WeakMapTest
{
	@Test
	public void empty()
	{
		final WeakMap<String,String> map = new WeakMap<String,String>(16,1.0f);
		assertEquals(0,map.size());
		assertNull(map.get("FOO"));
		assertTrue(map.isEmpty());
	}

	@Test
	public void nominal()
	{
		final WeakMap<String,String> map = new WeakMap<String,String>(16,1.0f);
		final String KEY = "KEY";
		final String VALUE = "VALUE";
		map.put(KEY,VALUE);
		assertEquals(VALUE,map.get(KEY));
		assertEquals(1,map.size());
		assertFalse(map.isEmpty());
	}

	@Test
	public void weakNominal()
	{
		final WeakMap<String,Object> map = new WeakMap<String,Object>(16,1.0f);
		final String KEY = "KEY";

		Object obj = new Object();

		map.put(KEY,obj);
		assertSame(obj,map.get(KEY));
		System.gc();
		assertSame(obj,map.get(KEY));

		obj = null;
		System.gc();
		assertNull(map.get(KEY));
		assertTrue(map.isEmpty());
	}
}
