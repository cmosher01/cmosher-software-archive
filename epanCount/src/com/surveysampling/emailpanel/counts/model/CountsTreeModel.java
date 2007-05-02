/*
 * Created on Aug 2, 2005
 *
 */
package com.surveysampling.emailpanel.counts.model;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.api.EpanCountLibrary;
import com.surveysampling.emailpanel.counts.api.list.EpanCountList;
import com.surveysampling.emailpanel.counts.api.list.EpanCountListItemContents;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeMonitor;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;
import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.data.Folder;
import com.surveysampling.emailpanel.counts.data.OldFolder;
import com.surveysampling.emailpanel.counts.data.RecentFolder;
import com.surveysampling.emailpanel.counts.data.TodayFolder;
import com.surveysampling.emailpanel.counts.exception.BadInputDataException;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * @author james
 *
 */
public class CountsTreeModel extends DefaultTreeModel implements ChangeListener
{

    ChangeMonitor monitor;
    final Object lockListChange = new Object();
    private final Date today;
    private final Date recent;
    private EpanCountLibrary lib;
    private CountsGUI cg;
    private Object prevRequest;
    private final DatalessKeyAccess accessKey;

    /**
     * Construct the tree model
     * 
     * @param root
     * @throws DatalessKeyAccessCreationException
     */
    public CountsTreeModel(DefaultMutableTreeNode root) throws DatalessKeyAccessCreationException
    {
        super(root);
        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.clear();
        cal.set(year,month,day);
        this.today = cal.getTime();
        cal.add(Calendar.DATE,-14);
        this.recent = cal.getTime();
        accessKey = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");
        
    }
    /**
     * @param lib	set the EpanCountLibrary
     */
    public void setLibrary(EpanCountLibrary lib)
    {
        this.lib = lib;
    }

    /**
     * Set the main GUI
     * @param cg
     */
    public void setGUI(CountsGUI cg)
    {
        this.cg = cg;
    }
    
    /**
     * 
     * @return the CountsGUI
     */
    public CountsGUI getGUI()
    {
        return cg;
    }
    /**
     * initialize the startup
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void applicationStartupRequestThread() throws JAXBException, DatalessKeyAccessCreationException
    {
        //      This represents the set of CountRequests that
        // we will be monitoring (see below).
        final EpanCountList setCounts = lib.createEpanCountList();

        /*
         * Create our "list monitoring" thread, which continually
         * queries the database for any CountRequest inserts, updates,
         * or deletes; and notifies our main GUI of them.
         */
        // 
        this.monitor = new ChangeMonitor(setCounts,CountsGUI.CHECK_EVERY_N_SECONDS);
        addChangeListenerToMonitor();
        this.monitor.forceCheckNow();

//      [synchronized (currentItemLock)]
//      current = create new
//      put current to screen
//      add current to list (text, name)
//      add current to listener
//      select current in list
//      dirty = false

        synchronized (this.lockListChange)
        {
            cg.setRequestBeingEdited(cg.getLib().createEpanCountRequest());
            cg.putRequestToScreen();
            addCurrentToList();
            selectCurrentInList(true);
            cg.setDirty(false);
        }
    }
    
    public void addChangeListenerToMonitor()
    {
        this.monitor.addChangeListener(this);
    }
    
    public void addCurrentToList()
    {
        final DatalessKey key = cg.getRequestBeingEdited().getPK();

        final EpanCountListItemContents listItemContents = new EpanCountListItemContents(
            cg.getRequestBeingEdited().getClientName(),
            cg.getRequestBeingEdited().getTopic(),
            cg.getRequestBeingEdited().getDateCreated(),
            cg.getRequestBeingEdited().getQueryCount(),
            0); // 0 counts finished so far; kind of kludgy
        final MonitorableItemDefault item = new MonitorableItemDefault(
            key,
            cg.getRequestBeingEdited().getLastChangeSerialNumber(),
            listItemContents);

        addItemToList(item);
    }
    
    protected void addItemToList(final MonitorableItemDefault item)
    {
        final DefaultMutableTreeNode widgetItem = new DefaultMutableTreeNode(item);
        
        final EpanCountListItemContents listItemContents = (EpanCountListItemContents)item.getContents();

        addItemToSortedList(widgetItem,findTreeForDate(listItemContents.getDateCreated()));
    }
    
    /**
     * 
     * @param widgetItem
     * @param treeFolderNode	the node representing either the new, recent, or old folder
     */
    private void addItemToSortedList(final DefaultMutableTreeNode widgetItem, final DefaultMutableTreeNode treeFolderNode)
    {
        final String sItem = widgetItem.toString();
        int low = 0;
        int high = treeFolderNode.getChildCount()-1;
        Folder folder = (Folder) treeFolderNode.getUserObject();
        JTree tree = cg.getLeftPanel().getTree();
        TreeSelectionListener listener = ((TreeSelectionListener[])tree.getListeners(TreeSelectionListener.class))[0];
        tree.removeTreeSelectionListener(listener);
        while (low <= high)
        {
            final int mid = (low + high) >> 1;
            final Object itemMid = treeFolderNode.getChildAt(mid);
            final String sMid = itemMid.toString();
            final int cmp = sMid.compareToIgnoreCase(sItem);
            
            if (cmp < 0)
            {
                low = mid + 1;
            }
            else if (cmp > 0)
            {
                high = mid - 1;
            }
            else
            {
                // key found at mid; should only happen if two different
                // requests have same client, date, topic, and query count
            	insertItem(widgetItem, mid, treeFolderNode, folder, tree, listener);
                return;
            }
        }
        // key not found; insert at low
        if (low > treeFolderNode.getChildCount())
        {
            low = treeFolderNode.getChildCount(); // append to end of tree
        }
        insertItem(widgetItem, low, treeFolderNode, folder, tree, listener);
    }
	private void insertItem(final DefaultMutableTreeNode widgetItem, int position, final DefaultMutableTreeNode treeFolderNode, Folder folder, JTree tree, TreeSelectionListener listener)
	{
		treeFolderNode.insert(widgetItem, position);
        folder.add((MonitorableItemDefault)widgetItem.getUserObject());

        fireTreeNodesInserted(treeFolderNode, 
                	getPathToRoot(treeFolderNode), 
                	new int[]{getIndexOfChild(treeFolderNode, widgetItem)},
                	new Object[]{widgetItem});
        TreePath path = tree.getSelectionPath();
        reload(treeFolderNode);
        tree.setSelectionPath(path);
        tree.addTreeSelectionListener(listener);
	}
    
    protected DefaultMutableTreeNode findTreeForDate(final Date date)
    {
        if (date.before(this.recent))
        {
            return cg.getLeftPanel().getOldNode();
        }

        if (date.before(this.today))
        {
            return cg.getLeftPanel().getRecentNode();
        }

        return cg.getLeftPanel().getTodayNode();
    }    

    /**
     * 
     * @param key
     * @return	the object representation of a MonitorableItemDefault
     */
    public MonitorableItemDefault findItemInTree(final DatalessKey key)
    {
        final String sKey = DatalessKeyAccessFactory.getDatalessKeyAccess(key).keyAsString(key);

        MonitorableItemDefault item = findItemInTreeOrNull(key);

        if (item == null)
        {
            throw new RuntimeException("cannot find item "+sKey+" in tree.");
        }

        return item;
    }
    
    /**
     * 
     * @param key
     * @return	the object representation of a MonitorableItemDefault
     */
    protected MonitorableItemDefault findItemInTreeOrNull(final DatalessKey key)
    {
        DefaultMutableTreeNode oldNode = cg.getLeftPanel().getOldNode();
        OldFolder oldF = (OldFolder) oldNode.getUserObject();
        MonitorableItemDefault item = oldF.getCountRequest(key);
        if (item == null)
        {
            DefaultMutableTreeNode recentNode = cg.getLeftPanel().getRecentNode();
            RecentFolder recentF = (RecentFolder) recentNode.getUserObject();
            item = recentF.getCountRequest(key);
        }
        if (item == null)
        {
            DefaultMutableTreeNode todayNode = cg.getLeftPanel().getTodayNode();
            TodayFolder todayF = (TodayFolder) todayNode.getUserObject();
            item = todayF.getCountRequest(key);
        }

        return item;
    }
   
    public void updateCurrentInList()
    {
        final EpanCountRequest request = cg.getRequestBeingEdited();
        final EpanCountListItemContents listItemContents = new EpanCountListItemContents(
            request.getClientName(),
            request.getTopic(),
            request.getDateCreated(),
            request.getQueryCount(),
            0); // 0 counts finished so far; kind of kludgy

        final MonitorableItemDefault item = new MonitorableItemDefault(
            request.getPK(),
            request.getLastChangeSerialNumber(),
            listItemContents);

        updateItemTextInList(item);
    }
    
    /**
     * @throws CloneNotSupportedException
     */
    public void makeCloneOfCurrent() throws CloneNotSupportedException
    {
        EpanCountRequest request = cg.getRequestBeingEdited();
        cg.setRequestBeingEdited((EpanCountRequest)request.clone());
        request = cg.getRequestBeingEdited();
        request.setTopic(request.getTopic()+" (Copy)");
    }
    
    /**
     * @param select
     */
    public void selectCurrentInList(boolean select)
    {
        final MonitorableItemDefault item = findItemInTree(cg.getRequestBeingEdited().getPK());
        
        final EpanCountListItemContents listItemContents = (EpanCountListItemContents)item.getContents();
        DefaultMutableTreeNode root = findTreeForDate(listItemContents.getDateCreated());
        
        DefaultMutableTreeNode node = null;
        boolean found = false;
        for (int i = 0; i < root.getChildCount(); i++)
        {
            node = (DefaultMutableTreeNode)root.getChildAt(i);
            if (node.getUserObject().equals(item))
            {
                found = true;
            	break;
            }
        }
        if (!found)
        {
        	System.out.println("Node not found");
        	return;
        }
        TreeNode[] pathNodes = getPathToRoot(node);
        TreePath path = new TreePath (pathNodes);
        setPrevRequest(select ? item : null);
        if (select)
        {
            TreeSelectionListener[] listeners = 
                (TreeSelectionListener[])cg.getLeftPanel().getTree()
                							.getListeners(TreeSelectionListener.class);
            cg.getLeftPanel().getTree().removeTreeSelectionListener(listeners[0]);
            cg.getLeftPanel().getTree().setSelectionPath(path);
            cg.getLeftPanel().getTree().addTreeSelectionListener(listeners[0]);
        }
    }    
    /**
     * Removes the given item from the list of requests.
     * This method would typically be called from a thread other than
     * the dispatch thread.
     * @param item
     */
    public void removeCountList(final MonitorableItem item)
    {
        try
        {
            removeCountListThread(item);
        }
        catch (final Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes the given item from the list of requests.
     * Must be called by the dispatch thread.
     * @param item
     * @throws CloneNotSupportedException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     * @throws BadInputDataException 
     */
    protected void removeCountListThread(final MonitorableItem item) throws CloneNotSupportedException, JAXBException, DatalessKeyAccessCreationException, BadInputDataException
    {
//        remove deleted item from list
//        if deleted PK == current PK
//            if dirty
//                warning dialog
//                get from screen into current
//                current = clone
//            else
//                warning dialog
//                current = create new
//            end if
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//        end if
        synchronized (this.lockListChange)
        {
            final MonitorableItemDefault itemDeleted = (MonitorableItemDefault)item;
            removeItemFromList(itemDeleted);
            if (itemDeleted.getPK().equals(cg.getRequestBeingEdited().getPK()))
            {
                if (cg.getDirty())
                {
                    // TODO put up a warning dialog for other people's modifications
                    cg.getRequestFromScreen();
                    makeCloneOfCurrent();
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Someone else is deleting this count request.","Deleted",JOptionPane.INFORMATION_MESSAGE);
                    cg.setRequestBeingEdited(this.lib.createEpanCountRequest());
                }
                cg.putRequestToScreen();
                addCurrentToList();
                selectCurrentInList(true);
            }
        }
    }
    protected void removeItemFromList(final MonitorableItemDefault itemDeleted)
    {
        DefaultMutableTreeNode folderNode = getFolderNode(itemDeleted);
        
        boolean removed = false;
        for (int i =0; i < folderNode.getChildCount()&&!removed; i++)
        {
        	DefaultMutableTreeNode child = (DefaultMutableTreeNode) folderNode.getChildAt(i);
            if (child.getUserObject().equals(itemDeleted))
            {
                removed = true;
                folderNode.remove(child);
                fireTreeNodesRemoved(folderNode, 
                        getPathToRoot(folderNode), 
                        new int[]{i} , 
                        new Object[]{child} );
            }
        }
        if (!removed)
        {
            System.out.println("The child has not been removed!!");
            Thread.dumpStack();
        }
        
    }
    /**
     * Updates the given item from the list of requests.
     * This method would typically be called from a thread other than
     * the dispatch thread.
     * @param item
     */
    public void updateCountList(final MonitorableItem item)
    {
        try
        {
            updateCountListThread(item);
        }
        catch (final Throwable e)
        {
            throw new RuntimeException(e);
        }
    }

    protected void updateCountListThread(final MonitorableItem item) throws CloneNotSupportedException, SQLException, JAXBException, DatalessKeyAccessCreationException, BadInputDataException
    {
//        set updated item in list (text)
//        if updated PK == current PK
//            if updated mod serial <= current mod serial
//                return
//            end if
//            if dirty
//                warning dialog
//                de-select current in list
//                get from screen into current
//                current = clone
//                put current to screen
//                add current to list (text, name)
//                add current to listener
//                select current in list
//            else
//                warning dialog
//                current = read from database
//                put current to screen
//            end if
//        end if
        synchronized (getLockListChange())
        {
            final MonitorableItemDefault itemUpdated = (MonitorableItemDefault)item;
            cg.callDoGUI(new Runnable()
            {
                public void run()
                {
                    updateItemTextInList(itemUpdated);
                }
            });
            if (itemUpdated.getPK().equals(cg.getRequestBeingEdited().getPK()))
            {
                if (itemUpdated.getLastChangeSerialNumber()
                        .compareTo(cg.getRequestBeingEdited().getLastChangeSerialNumber()) <= 0)
                {
                    return;
                }
                if (cg.getDirty())
                {
                    // TODO put up a warning dialog for other people's modifications
                    selectCurrentInList(false);
                    cg.getRequestFromScreen();
                    makeCloneOfCurrent();
                    cg.putRequestToScreen();
                    addCurrentToList();
                    selectCurrentInList(true);
                }
                else
                {
                    // TODO put up a warning dialog for other people's modifications
                    cg.setRequestBeingEdited(lib.readEpanCountRequest(itemUpdated.getPK()));
                    cg.putRequestToScreen();
                }
            }
        }
    }

    
    protected void updateItemTextInList(final MonitorableItemDefault itemUpdated)
    {
        //TODO make sure this method is correct
        final MonitorableItemDefault widgetItem = findItemInTree(itemUpdated.getPK());

        final String oldText = widgetItem.toString();
        final String newText = itemUpdated.toString();
        
        if (!newText.equalsIgnoreCase(oldText))
        {
            // since the text changed, we may have to move
            // the item within the list in order to maintain
            // the sort
            
            //  use this method, rather than node.getParent();
            final DefaultMutableTreeNode parent = getFolderNode(widgetItem);
            DefaultMutableTreeNode node = null;
            boolean found = false;
            for (int i =0; i< parent.getChildCount(); i++)
            {
                node = (DefaultMutableTreeNode) parent.getChildAt(i);
                if (node.getUserObject().equals(widgetItem))
                {
                	found = true;
                	break;
                }
            }
            if (!found)
            {
            	System.out.println("Node not found");
            	return;
            }
            fireTreeNodesRemoved(node.getParent(), 
                    getPathToRoot(node), 
                    new int[]{parent.getIndex(node)} , 
                    new Object[]{node} );
            node.removeFromParent();

            Folder parentFolder = (Folder) parent.getUserObject();
            parentFolder.remove((MonitorableItemDefault)widgetItem);
            node.setUserObject(itemUpdated);
            addItemToSortedList(node, parent);
        }
    }
    /**
     * @param widgetItem
     * @return
     */
    private DefaultMutableTreeNode getFolderNode(MonitorableItemDefault widgetItem)
    {
        DatalessKey key = widgetItem.getPK();
        
        DefaultMutableTreeNode oldNode = cg.getLeftPanel().getOldNode();
        OldFolder oldF = (OldFolder) oldNode.getUserObject();
        MonitorableItemDefault item = oldF.getCountRequest(key);
        if (item!=null)
            return oldNode;
        
        DefaultMutableTreeNode recentNode = cg.getLeftPanel().getRecentNode();
        RecentFolder recentF = (RecentFolder) recentNode.getUserObject();
        item = recentF.getCountRequest(key);
        if (item!=null)
            return recentNode;

        DefaultMutableTreeNode todayNode = cg.getLeftPanel().getTodayNode();
        TodayFolder todayF = (TodayFolder) todayNode.getUserObject();
        item = todayF.getCountRequest(key);
        if (item!=null)
            return todayNode;
        return null;
    }
    
    
    /* (non-Javadoc)
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#itemChanged(com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem)
     */
    public void itemChanged(final MonitorableItem item)
    {
        final MonitorableItemDefault itemChanged = (MonitorableItemDefault)item;

        cg.callDoGUI(new Runnable()
        {
        	public void run()
        	{
                final MonitorableItemDefault itemInList = findItemInTreeOrNull(itemChanged.getPK());
                if (itemInList != null) // if we already have the item
                {
                    if (item.isDeleted())
                    {
                        // The item has been deleted, so remove it from our list (and fire listeners)
                        removeCountList(item);
                    }
                    else
                    {
                        // The item has changed, so update it in our list (and fire listeners)
                        updateCountList(item);
                    }
                }
                else // if we don't already have the item
                {
                    if (item.isDeleted())
                    {
                        // Received notification of deletion of an item we don't have; ignore.
                    }
                    else
                    {
                        // It is a new item, so add it to our list (and fire listeners)
                        addCountList(item);
                    }
                }
        	}
        });

    }
    /* (non-Javadoc)
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#exception(com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException)
     */
    public void exception(MonitoringException e)
    {
        System.err.println("exception:");
        e.printStackTrace();
    }
    
    /**
     * @param item
     */
    public void addCountList(final MonitorableItem item)
    {
    	addCountListThread(item);
    }

    /**
     * @param item
     */
    protected void addCountListThread(final MonitorableItem item)
    {
//        [synchronized (currentItemLock)]
//            add inserted item to list (text, name)
        synchronized (this.lockListChange)
        {
            final MonitorableItemDefault def = (MonitorableItemDefault)item;
            addItemToList(def);
        }
    }
    
    public boolean isCurrentNew()
    {
        return cg.isCurrentNew();
    }
    
    public void removeCurrentFromList()
    {
        final DatalessKey key = cg.getRequestBeingEdited().getPK();

        // bogus item: just fill in the PK; that's all we need for these two calls
        final MonitorableItemDefault item = new MonitorableItemDefault(key,ChangeSerialNumber.getSerialNumberLowerLimit(),null);
        removeItemFromList(item);
    }

    /**
     * @return Returns the lockListChange.
     */
    public Object getLockListChange()
    {
        return lockListChange;
    }
    /**
     * @return Returns the prevRequest.
     */
    public Object getPrevRequest()
    {
        return prevRequest;
    }
    /**
     * @param prevRequest The prevRequest to set.
     */
    public void setPrevRequest(Object prevRequest)
    {
        this.prevRequest = prevRequest;
    }
    /**
     * @return Returns the lib.
     */
    public EpanCountLibrary getLib()
    {
        return cg.getLib();
    }
    /**
     * @return Returns the accessKey.
     */
    public DatalessKeyAccess getAccessKey()
    {
        return accessKey;
    }
    /**
     * @return Returns the monitor.
     */
    public ChangeMonitor getMonitor()
    {
        return monitor;
    }
    /**
     * @param mon
     */
    public void setMonitor(ChangeMonitor mon)
    {
        this.monitor = mon;
    }
}