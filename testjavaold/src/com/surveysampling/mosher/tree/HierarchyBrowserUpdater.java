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
        beginWait();

        TreePath path = event.getPath();
        FileTreeNode filetreenode = (FileTreeNode)path.getLastPathComponent();

        if (filetreenode.readTree())
        {
            int rc[] = new int[filetreenode.getChildCount()];
            int i = 0;
            for ((Enumeration e = filetreenode.children(), int i = 0); e.hasMoreElements();)
            {
                TreeNode node = (TreeNode)e.nextElement();
                rc[i++] = node.getIndex(node);
            }
            mModel.nodesWereInserted(filetreenode,rc);
        }

        endWait();
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
    }

    private void beginWait()
    {
        mFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        mFrame.setEnabled(false);
    }

    private void endWait()
    {
        mFrame.setEnabled(true);
        mFrame.setCursor(Cursor.getDefaultCursor());
    }
}
