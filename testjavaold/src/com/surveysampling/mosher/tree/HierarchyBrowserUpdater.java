package com.surveysampling.mosher.tree;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultTreeModel;

public class HierarchyBrowserUpdater implements TreeExpansionListener
{
    DefaultTreeModel mModel;

    public HierarchyBrowserUpdater(DefaultTreeModel tmodel)
    {
        mModel = tmodel;
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
        FileTreeNode node = (FileTreeNode)event.getPath().getLastPathComponent();

        if (node.readTree())
        {
            mModel.nodesWereInserted(node,node.getChildrenIndicies());
        }
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
    }
}
