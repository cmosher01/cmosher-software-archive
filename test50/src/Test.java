import java.util.Iterator;

public class Test
{
	public static void main(String[] args)
	{
		String test = "this,is,a,test";
		StringFieldizer sf = new StringFieldizer(test);

		for (String s : sf)
		{
			System.out.println(s);
		}
	}
}
