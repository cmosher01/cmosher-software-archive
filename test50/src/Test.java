import java.util.Iterator;

import nu.mine.mosher.core.StringFieldizer;

public class Test
{
	public static void main(String[] args)
	{
		String test = "this,is,a,,test";

		for (String s : new StringFieldizer(test))
		{
			System.out.println(s);
		}
	}
}
