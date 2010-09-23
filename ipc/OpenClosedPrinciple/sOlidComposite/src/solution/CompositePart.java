package solution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompositePart extends Part
{
	private final List<Part> components = new ArrayList<Part>();
	private final int assemblyPrice;
	public CompositePart(Collection<Part> components, int assemblyPrice)
	{
		this.components.addAll(components);
		this.assemblyPrice = assemblyPrice;
	}
	public int getAssemblyPrice()
	{
		return this.assemblyPrice;
	}
	@Override
	public int getPrice()
	{
		int price = 0;
		for (final Part part: this.components)
		{
			price += part.getPrice();
		}
		price += getAssemblyPrice();
		return price;
	}
}
