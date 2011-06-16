package nu.mosher.mine.a2diskedit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * @author Chris Mosher
 */
public class A2DiskEdit
{
	private JFrame frameMain;
	//private JEditorPane htmlPane = null;
	private DefaultMutableTreeNode top = new DefaultMutableTreeNode("Program");
	private static A2DiskEdit staticApp = new A2DiskEdit();
	private JTree tree;
	private JScrollPane rightView = new JScrollPane();

	public void createComponents()
	{
        createNodes();

        //Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer()
		{
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);
				DefaultMutableTreeNode nodeDef = (DefaultMutableTreeNode)value;
				Object node = nodeDef.getUserObject();
				if (node instanceof TreeNode)
				{
					TreeNode treeNode = (TreeNode)node;
					Icon icon = treeNode.getIcon();
					if (icon != null)
					    setIcon(icon);
				}
			
			    return this;
			}
		};
		tree.setCellRenderer(renderer);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent e)
            {
                DefaultMutableTreeNode nodeDef = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if (nodeDef == null) return;

				Object node = nodeDef.getUserObject();
				if (node instanceof TreeNode)
				{
					if (node instanceof Command)
					{
						((Command)node).dispatch();
					}
					else
					{
				        rightView.setViewportView(((TreeNode)node).getRightPane());
					}
				}
            }
        });

        //Create the scroll pane and add the tree to it. 
        JScrollPane treeView = new JScrollPane(tree);

        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(treeView);
        splitPane.setRightComponent(rightView);

//        Dimension minimumSize = new Dimension(50, 100);
//        paneRight.setMinimumSize(minimumSize);
//        treeView.setMinimumSize(minimumSize);

        splitPane.setDividerLocation(300);
        splitPane.setPreferredSize(new Dimension(600, 400));

		frameMain.getContentPane().add(splitPane,BorderLayout.CENTER);
	}

    private void createNodes()
    {
        top.add(new DefaultMutableTreeNode(new CommandOpen()));
        top.add(new DefaultMutableTreeNode(new CommandNew()));
    }

	public void fileOpen()
	{
		final JFileChooser fc = new JFileChooser();

		int returnVal = fc.showOpenDialog(frameMain);

		if (returnVal != JFileChooser.APPROVE_OPTION)
			return;

		DefaultMutableTreeNode n = new DefaultMutableTreeNode();
		TreeNode d = new Disk(n,fc.getSelectedFile());
		n.setUserObject(d);
		addNode(n,top);
		tree.setSelectionRow(0);

/*
        n.add(new DefaultMutableTreeNode("ProDOS boot loader"));
        n.add(new DefaultMutableTreeNode("SOS boot loader"));
        n.add(new DefaultMutableTreeNode("Volume: merlin"));
*/
	}

	public void fileNew()
	{
		DefaultMutableTreeNode n = new DefaultMutableTreeNode();
		TreeNode d = new Disk(n);
		n.setUserObject(d);
		addNode(n,top);
		tree.setSelectionRow(0);
	}

	public void addNode(DefaultMutableTreeNode n, DefaultMutableTreeNode par)
	{
		((DefaultTreeModel)tree.getModel()).insertNodeInto(n,par,0);
	}

	public static A2DiskEdit getApp()
	{
		return staticApp;
	}

	public static void main(String[] args)
	{
		A2DiskEdit.getApp().mainApp();
	}

	public void mainApp()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			//UIManager.getSystemLookAndFeelClassName());
			//"com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			//"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			//"com.sun.java.swing.plaf.multi.MultiLookAndFeel");
			//"com.sun.java.swing.plaf.basic.BasicLookAndFeel");
			//UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e)
		{
			try
			{
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
			catch (Exception e2)
			{
			}
		}

		frameMain = new JFrame("Apple ][ Disk Editor");
		frameMain.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		//Create the top-level container and add contents to it.
		createComponents();

		frameMain.pack();
		frameMain.setVisible(true);
	}
}
