package com.surveysampling.mosher.tree;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

public class TreeFrame extends Frame implements WindowListener
{

    public TreeFrame(String dir)
    {
        setLayout(new BorderLayout());
        addNotify();
        setSize(getInsets().left + getInsets().right + 485, getInsets().top + getInsets().bottom + 367);
        setTitle("Java Directory Browser");

        FileTreeNode root = (FileTreeNode)buildTree(dir);
        DefaultTreeModel treemodel = new DefaultTreeModel(root);
        JTree tree = new JTree(treemodel);
        tree.setDoubleBuffered(true);

        HierarchyBrowserUpdater upd = new HierarchyBrowserUpdater(tree, root, treemodel, this);
        tree.addTreeExpansionListener(upd);

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(tree);
        scrollpane.setDoubleBuffered(true);

        add("Center", scrollpane);
        addWindowListener(this);
    }

    public TreeNode buildTree(String dir)
    {
        FileTreeNode root = new FileTreeNode(new File(dir));
        root.readTree(false);
        return (TreeNode)root;
    }

    public synchronized void setVisible(boolean show)
    {
        setLocation(50, 50);
        super.setVisible(show);
    }

    public void windowClosed(WindowEvent event)
    {
    }

    public void windowDeiconified(WindowEvent event)
    {
    }

    public void windowIconified(WindowEvent event)
    {
    }

    public void windowActivated(WindowEvent event)
    {
    }

    public void windowDeactivated(WindowEvent event)
    {
    }

    public void windowOpened(WindowEvent event)
    {
    }

    public void windowClosing(WindowEvent event)
    {
        System.exit(0);
    }

    public static void main(String argv[])
    {
        String dir = System.getProperty("user.dir");
        if (argv != null && argv.length == 1)
            dir = argv[0];
        TreeFrame myframe = new TreeFrame(dir);
        myframe.setVisible(true);
    }
}
