/*
 * Created on Jan 4, 2008
 */
package gui;

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
		final JLabel label = new JLabel(name);
		add(label,BorderLayout.CENTER);
	}
}
