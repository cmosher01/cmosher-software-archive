/*
 * Created on Jan 4, 2008
 */
package chipset;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DefaultCardPanel extends JPanel
{
	public DefaultCardPanel(final String name)
	{
		setLayout(new BorderLayout());
		setOpaque(true);
		addNotify();
		setFocusable(false);
		add(new JLabel(name),BorderLayout.CENTER);
	}
}
