import java.util.Iterator;

public class Test
{
	public static void main(String[] args)
	{
		String test = "this,is,a,test";
		StringFieldizer sf = new StringFieldizer(test);
		java.util.Iterator<String> i = sf.iterator();
		while (i.hasNext())
		{
			String s = i.next();
			System.out.println(s);
		}
	}
}
