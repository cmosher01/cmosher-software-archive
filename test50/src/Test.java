import java.util.Iterator;

import nu.mine.mosher.core.StringFieldizer;

public class Test
{
	public static void main(String[] args)
	{
		ItemType x = new ItemType("madeup");
		ItemType y = new ItemType(ItemTypeEnum.a);

		System.out.println(x);
		System.out.println(y);

		if (y.isa(ItemTypeEnum.a))
		{
			System.out.println("yes");
		}
		else
		{
			System.out.println("no");
		}
	}
}
