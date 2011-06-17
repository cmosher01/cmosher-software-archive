/*
 * Created on Aug 12, 2005
 */
package nu.mine.mosher.play.immutable;

public class Test
{
	static
	{
		// this works:
		assert Immute.class.isAssignableFrom(Struct.class) : "Struct is not immutable";

		// this doesn't work
		//assert Struct.class.isInstance(Immute.class) : "Struct is not immutable";
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("ok");

		final Abc abc = new Abc() {
			public void something()
			{
			} };

			abc.toString();
	}
}
