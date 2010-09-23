package problem;

import java.util.ArrayList;
import java.util.List;

public class Order
{
	private final List<CompositePart> composites = new ArrayList<CompositePart>();
	private final List<ElectronicPart> looseParts = new ArrayList<ElectronicPart>();
	// ...
	public int getTotalPrice()
	{
		int price = 0;
		for (final CompositePart composite: this.composites)
		{
			price += composite.getPrice();
		}
		for (final ElectronicPart loosePart: this.looseParts)
		{
			price += loosePart.getPrice();
		}
		return price;
	}
}
