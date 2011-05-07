/*
 * Created on Nov 28, 2007
 */
package cards;

import gui.GUI;
import javax.swing.JPanel;

public class EmptySlot extends Card
{
	@Override
	public JPanel getPanel(@SuppressWarnings("unused") final GUI gui)
	{
		return null;
	}

	/**
	 * @return
	 */
	@Override
	public String getTypeName()
	{
		return "[empty slot]";
	}
}
