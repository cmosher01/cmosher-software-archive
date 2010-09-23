package problem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompositePart
{
	private final List<ElectronicPart> components = new ArrayList<ElectronicPart>();
	private final int assemblyPrice;
	public CompositePart(Collection<ElectronicPart> components, int assemblyPrice)
	{
		this.assemblyPrice = assemblyPrice;
		this.components.addAll(components);
	}
	public int getPrice()
	{
		int price = 0;
		for (final ElectronicPart part: this.components)
		{
			price += part.getPrice();
		}
		price += this.assemblyPrice;
		return price;
	}
}
