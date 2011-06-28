/*
 * Created on Jul 21, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.surveysampling.emailpanel.counts.data.OldFolder;
import com.surveysampling.emailpanel.counts.data.RecentFolder;
import com.surveysampling.emailpanel.counts.data.TodayFolder;
import com.surveysampling.emailpanel.counts.listeners.CountsTreeListener;
import com.surveysampling.emailpanel.counts.model.CountsTreeModel;
import com.surveysampling.emailpanel.counts.renderer.CountsTreeCellRenderer;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * The LeftPanel holds the tree.
 * @author james
 *
 */
public class LeftPanel extends JPanel
{
    CountsTreeListener atl;
    CountsTreeModel countsTreeModel;
    JTree tree;
    DefaultMutableTreeNode todayNode = new DefaultMutableTreeNode(new TodayFolder());
    DefaultMutableTreeNode recentNode = new DefaultMutableTreeNode(new RecentFolder());
    DefaultMutableTreeNode oldNode = new DefaultMutableTreeNode(new OldFolder());
    
    
    public LeftPanel() throws DatalessKeyAccessCreationException
    {
        setLayout(new BorderLayout());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(todayNode);
        root.add(recentNode);
        root.add(oldNode);
        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        countsTreeModel = new CountsTreeModel(root);
        atl = new CountsTreeListener(countsTreeModel);
        tree.addTreeSelectionListener(atl);
        tree.addTreeWillExpandListener(atl);
        tree.setModel(countsTreeModel);
        tree.setToggleClickCount(0);
        CountsTreeCellRenderer treeRend = new CountsTreeCellRenderer();
        treeRend.setBackgroundSelectionColor(new Color(255,204,102));
        treeRend.setTextSelectionColor(Color.BLACK);
        tree.setCellRenderer(treeRend);
        JScrollPane sp = new JScrollPane(tree);
        add(sp, BorderLayout.CENTER);
        setSize(new Dimension(400,600));
    }

   
    public JTree getTree()
    {
        return tree;
    }
    /**
     * @return Returns the oldNode.
     */
    public DefaultMutableTreeNode getOldNode()
    {
        return oldNode;
    }
    /**
     * @return Returns the recentNode.
     */
    public DefaultMutableTreeNode getRecentNode()
    {
        return recentNode;
    }
    /**
     * @return Returns the todayNode.
     */
    public DefaultMutableTreeNode getTodayNode()
    {
        return todayNode;
    }

}
