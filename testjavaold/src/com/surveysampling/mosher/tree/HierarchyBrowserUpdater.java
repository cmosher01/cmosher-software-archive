package com.surveysampling.mosher.tree;

import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;

public class HierarchyBrowserUpdater implements TreeExpansionListener
{
    FileTreeNode mRoot;
    JTree mTree;
    DefaultTreeModel mModel;
    JPanel mFrame;

    public HierarchyBrowserUpdater(JTree tree, FileTreeNode node, DefaultTreeModel tmodel, JPanel theFrame)
    {
        mRoot = node;
        mTree = tree;
        mModel = tmodel;
        mFrame = theFrame;
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
        beginWait();

        FileTreeNode node = (FileTreeNode)event.getPath().getLastPathComponent();

        if (node.readTree())
        {
            mModel.nodesWereInserted(node,node.getChildrenIndicies());
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
