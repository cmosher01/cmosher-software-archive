import java.util.Iterator;

public class Test
{
	public static void main(String[] args)
	{
		String test = "this,is,a,test";
		StringFieldizer sf = new StringFieldizer(test);
		Iterator i = sf.iterator();
		while (i.hasNext())
		{
			String s = i.next()l
			System.out.println(s);
		}
	}
}
