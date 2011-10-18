package nu.mine.mosher.util;



import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;



/**
 * @author christopher_mosher
 * 
 */
public class OptionalTest
{
	@Test
	public void nominal()
	{
		final Object obj = new Object();
		assertThat(obj, not(nullValue()));

		final Requirement<Object> opt = new Optional<Object>(obj);
		assertTrue(opt.exists());

		final Object objRetrieved = opt.get();
		assertThat(objRetrieved, sameInstance(obj));
	}

	@Test
	public void nominalNothing()
	{
		final Requirement<Object> opt = new Nothing<Object>();
		assertFalse(opt.exists());
	}

	@Test(expected=Requirement.FieldDoesNotExist.class)
	public void accessingNothingShouldThrow()
	{
		final Requirement<Object> opt = new Nothing<Object>();
		opt.get(); /* should throw */
	}

	@Test
	public void nominalEquals()
	{
		final Integer a = new Integer(149);
		final Integer b = new Integer(149);
		assertThat(b,not(sameInstance(a)));
		assertThat(b,equalTo(a));
		assertThat(a,equalTo(b));

		final Requirement<Integer> optA = new Optional<Integer>(a);
		final Requirement<Integer> optB = new Optional<Integer>(b);
		assertThat(optB,not(sameInstance(optA)));

		assertThat(optB,equalTo(optA));
		assertThat(optA,equalTo(optB));

		assertThat(Integer.valueOf(optB.hashCode()),equalTo(Integer.valueOf(optA.hashCode())));
	}

	@Test
	public void nominalNotEquals()
	{
		final Integer a = new Integer(149);
		final Integer b = new Integer(941);
		assertThat(b,not(equalTo(a)));
		assertThat(a,not(equalTo(b)));

		final Requirement<Integer> optA = new Optional<Integer>(a);
		final Requirement<Integer> optB = new Optional<Integer>(b);

		assertThat(optB,not(equalTo(optA)));
		assertThat(optA,not(equalTo(optB)));
	}

	@Test
	public void equalsTwoNulls()
	{
		final Requirement<Object> a = new Optional<Object>(null,true);
		final Requirement<Object> b = new Optional<Object>(null,true);

		assertThat(b,equalTo(a));
		assertThat(a,equalTo(b));
	}

	@Test
	public void notEqualsTwoNulls()
	{
		final Requirement<Object> a = new Optional<Object>(null,false);
		final Requirement<Object> b = new Optional<Object>(null,false);

		assertThat(b,not(equalTo(a)));
		assertThat(a,not(equalTo(b)));
	}

	@Test
	public void notEqualsNullWhenNullsAreNotEqual()
	{
		final Requirement<Object> a = new Optional<Object>(new Object(),false);
		assertTrue(a.exists());
		final Requirement<Object> b = new Optional<Object>(null,false);
		assertFalse(b.exists());

		assertThat(b,not(equalTo(a)));
		assertThat(a,not(equalTo(b)));
	}

	@Test
	public void notEqualsNullWhenNullsAreEqual()
	{
		final Requirement<Object> a = new Optional<Object>(new Object(),true);
		assertTrue(a.exists());
		final Requirement<Object> b = new Optional<Object>(null,true);
		assertFalse(b.exists());

		assertThat(b,not(equalTo(a)));
		assertThat(a,not(equalTo(b)));
	}
}
