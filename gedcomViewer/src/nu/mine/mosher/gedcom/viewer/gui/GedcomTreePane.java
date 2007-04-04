/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;
import nu.mine.mosher.gedcom.viewer.tree.GedcomTreeModel;

class GedcomTreePane extends JPanel
{
	public GedcomTreePane(final GedcomTreeModel model)
	{
		super(new GridLayout(1,1),true);

		setOpaque(true);
		addNotify();

		final JTree jtree = createTreeControl(model);
		final JScrollPane scrollpane = createScrollPane(jtree);

		add(scrollpane);
	}

	private static JTree createTreeControl(final GedcomTreeModel model)
	{
		final JTree jtree = new JTree(model);

		jtree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		jtree.setShowsRootHandles(true);
		jtree.setRootVisible(false);

		return jtree;
	}

	private static JScrollPane createScrollPane(final JTree jtree)
	{
		final JScrollPane scrollpane = new JScrollPane(jtree);

		scrollpane.setPreferredSize(new Dimension(640,480));

		return scrollpane;
	}
}
