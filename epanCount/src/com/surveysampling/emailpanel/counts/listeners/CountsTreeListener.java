/*
 * Created on Jul 21, 2005
 *
 */
package com.surveysampling.emailpanel.counts.listeners;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;
import com.surveysampling.emailpanel.counts.data.Folder;
import com.surveysampling.emailpanel.counts.model.CountsTreeModel;

/**
 * Listens for TreeEvent changes
 * 
 * @author james
 *
 */
public class CountsTreeListener implements TreeSelectionListener, TreeWillExpandListener
{

    CountsTreeModel ctm;
    private boolean pathCollapsed = false;
	private boolean pathHasBeenCollapsed = false;
    
    /**
     * Constructor
     * @param ctm	The model the tree is using
     */
	public CountsTreeListener(CountsTreeModel ctm)
    {
        this.ctm = ctm;
    }
    /**
     * A selection on the tree has been made
     */
    public void valueChanged(TreeSelectionEvent event)
    {
        if (event.getNewLeadSelectionPath()==null)
            return;
        synchronized (ctm.getLockListChange())
        {
            JTree tree = (JTree) event.getSource();
	        final DefaultMutableTreeNode widgetItem = (DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent();

	        if (widgetItem == null)
	        {
	            throw new IllegalStateException("Cannot get currently selected item in any tree.");
	        }
	        
	        if (widgetItem.getUserObject().equals(ctm.getPrevRequest()))//if its been re-selected
        		return;
	        /*
             * If they selected one of the "folders" (today, recent, or old),
             * then restore the old selection (and return).
             */
	        if (widgetItem.getUserObject() instanceof Folder)
	        {
	        	if (pathHasBeenCollapsed)
	        	{
	        		pathHasBeenCollapsed = false;
	        		return;
	        	}
	            tree.removeTreeSelectionListener(this);
	            tree.setSelectionPath(event.getOldLeadSelectionPath());
	            tree.addTreeSelectionListener(this);
	            
                return;
	        }

	        MonitorableItemDefault mid = (MonitorableItemDefault) widgetItem.getUserObject();
	        
            if (ctm.getGUI().getDirty())
            {
                JOptionPane.showMessageDialog(null,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                revertToOldPath(event, tree);
            }
            else
            {
                if (ctm.isCurrentNew())
                {
                	ctm.removeCurrentFromList();
                }
                try
                {
                    ctm.getGUI().setRequestBeingEdited(ctm.getLib().readEpanCountRequest(mid.getPK()));
                    ctm.setPrevRequest(widgetItem.getUserObject());
                }
                catch (final Throwable throwable)
                {
                    throwable.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Error occurred while trying to read from database: "+throwable.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    revertToOldPath(event, tree);
                }
                ctm.getGUI().putRequestToScreen();
            }

        }
        
    }
    
    /**
     * Revert to previously selected item
     * @param event
     * @param tree
     */
	private void revertToOldPath(TreeSelectionEvent event, JTree tree)
	{
		TreePath path = event.getOldLeadSelectionPath();
		if (path==null)
		{
			return;
		}
		Object prevObject = path.getLastPathComponent();//to select prev object
		tree.removeTreeSelectionListener(this);
		tree.setSelectionPath(path);
		DefaultMutableTreeNode o = (DefaultMutableTreeNode) prevObject;
		if (o!=null)
			ctm.setPrevRequest(o.getUserObject());
		tree.addTreeSelectionListener(this);
	}
    /**
     * When a JTree's node collapses, the root node is selected. This fires
     * an TreeSelectionEvent which calls valueChanged. We need 2 booleans to
     * keep track of tree collapses. pathHasBeenCollapsed is used in valueChanged
     * so that no action is performed if this boolean is true. pathCollapsed
     * is used in the treeWillExpand method. If a tree node has been collapsed
     * then the next time a tree node has been expanded, check if the prevRequest
     * is in the node's children. If it is then select it and set the boolean to 
     * false.
     */
    public void treeWillCollapse(TreeExpansionEvent arg0) throws ExpandVetoException
    {
        pathCollapsed = true;
        pathHasBeenCollapsed = true;
    }
    /**
     * If the tree is about to expand, then check if any previous requests had been 
     * selected in this node's children. If there is a node, then "reselect" that node.
     */
    public void treeWillExpand(TreeExpansionEvent arg0) throws ExpandVetoException
    {
    	if (!pathCollapsed)
    		return;
    	DefaultMutableTreeNode folderNode = (DefaultMutableTreeNode) arg0.getPath().getLastPathComponent();
    	Object prevRequest = ctm.getPrevRequest();
    	for (int i =0; i < folderNode.getChildCount(); i++)
    	{
    		DefaultMutableTreeNode node =(DefaultMutableTreeNode)folderNode.getChildAt(i); 
    		Object treeObj = node.getUserObject();
    		if (treeObj.equals(prevRequest))
    		{
    			JTree tree = (JTree) arg0.getSource();
    			TreePath path = new TreePath(ctm.getPathToRoot(node));
    			tree.setSelectionPath(path);
    			pathCollapsed = false;
    			break;
    		}
    	}
    			
    }

}
