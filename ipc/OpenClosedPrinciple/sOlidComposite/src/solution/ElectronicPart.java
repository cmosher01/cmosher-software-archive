/*
 * Created on Jul 22, 2008
 */
package solution;

public class ElectronicPart extends Part
{
	private int price;
	public ElectronicPart(int price)
	{
		this.price = price;
	}
	@Override
	public int getPrice()
	{
		return this.price;
	}
}
