package solution;

import java.util.ArrayList;
import java.util.List;

public class Order
{
	private final List<Part> parts = new ArrayList<Part>();
	// ...
	public int getTotalPrice()
	{
		int price = 0;
		for (final Part part: this.parts)
		{
			price += part.getPrice();
		}
		return price;
	}
}
