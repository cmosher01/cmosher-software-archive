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
    JTree mTree;
    DefaultTreeModel mModel;
    Frame mFrame;

    public HierarchyBrowserUpdater(JTree tree, FileTreeNode node, DefaultTreeModel tmodel, Frame theFrame)
    {
        mRoot = node;
        mTree = tree;
        mModel = tmodel;
        mFrame = theFrame;
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
        TreePath path = event.getPath();
        FileTreeNode node = (FileTreeNode)path.getLastPathComponent();

        mFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mFrame.setEnabled(false);

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
            mModel.nodesWereInserted(node, childrenIdx);
        }

        mFrame.setEnabled(true);
        mFrame.setCursor(Cursor.getDefaultCursor());
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
    }
}
