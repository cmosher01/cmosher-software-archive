package com.surveysampling.mosher.tree;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class HierarchyBrowserUpdater implements TreeExpansionListener
{
    FileTreeNode mRoot;
    JTree jtree;
    DefaultTreeModel treemodel;
    Frame frame;

    public HierarchyBrowserUpdater(JTree tree, FileTreeNode node, DefaultTreeModel tmodel, Frame theFrame)
    {
        mRoot = node;
        jtree = tree;
        treemodel = tmodel;
        frame = theFrame;
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
        TreePath path = event.getPath();

        System.out.print("Received expansion event on ");
        System.out.println(path);

        FileTreeNode node = (FileTreeNode)path.getLastPathComponent();
        System.out.println("Node level: " + node.getLevel());
        System.out.println("Children " + node.getChildCount());
        System.out.println("Reading subtree " + node.toString());

        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.setEnabled(false);

        if (node.readTree())
        {
            System.out.println("Calling nodesWereInserted");
            int childrenIdx[] = new int[node.getChildCount()];
            int i = 0;
            for (Enumeration e = node.children(); e.hasMoreElements();)
            {
                Object obj = e.nextElement();
                childrenIdx[i] = node.getIndex((TreeNode)obj);
                i++;
            }
            treemodel.nodesWereInserted(node, childrenIdx);
        }
        frame.setEnabled(true);
        frame.setCursor(Cursor.getDefaultCursor());
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
    }
}
