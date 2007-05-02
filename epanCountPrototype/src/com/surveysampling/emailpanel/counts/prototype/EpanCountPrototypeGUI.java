/*
 * Created on May 12, 2005
 */
package com.surveysampling.emailpanel.counts.prototype;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;

import thinlet.Thinlet;

import com.surveysampling.emailpanel.counts.api.EpanCountLibrary;
import com.surveysampling.emailpanel.counts.api.criteria.AgeType;
import com.surveysampling.emailpanel.counts.api.criteria.BreakOutType;
import com.surveysampling.emailpanel.counts.api.criteria.EducationEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.EducationType;
import com.surveysampling.emailpanel.counts.api.criteria.EpanCountCriteria;
import com.surveysampling.emailpanel.counts.api.criteria.EthnicityEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.EthnicityType;
import com.surveysampling.emailpanel.counts.api.criteria.GenderEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GenderType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeType;
import com.surveysampling.emailpanel.counts.api.criteria.MarriedEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.MarriedType;
import com.surveysampling.emailpanel.counts.api.criteria.ObjectFactory;
import com.surveysampling.emailpanel.counts.api.criteria.UserGeoType;
import com.surveysampling.emailpanel.counts.api.criteria.WithKidsType;
import com.surveysampling.emailpanel.counts.api.geography.GeographicArea;
import com.surveysampling.emailpanel.counts.api.geography.GeographicAreaCode;
import com.surveysampling.emailpanel.counts.api.geography.GeographicCodeParser;
import com.surveysampling.emailpanel.counts.api.geography.GeographicNameParser;
import com.surveysampling.emailpanel.counts.api.list.EpanCountList;
import com.surveysampling.emailpanel.counts.api.list.EpanCountListItemContents;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeMonitor;
import com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItemDefault;
import com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableSet;
import com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException;
import com.surveysampling.emailpanel.counts.api.list.users.User;
import com.surveysampling.emailpanel.counts.api.list.users.UserAccess;
import com.surveysampling.emailpanel.counts.api.request.EpanCount;
import com.surveysampling.emailpanel.counts.api.request.EpanCountDoneListener;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.api.request.exception.ConcurrentModificationException;
import com.surveysampling.emailpanel.xdem.XdemCriteria;
import com.surveysampling.sql.LookupException;
import com.surveysampling.util.Flag;
import com.surveysampling.util.ProgressWatcher;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * A prototype GUI (implemented as a Thinlet) for
 * the EpanCount system.
 * 
 * @author Chris Mosher
 */
public class EpanCountPrototypeGUI extends Thinlet implements ChangeListener
{
    private static final int CHECK_EVERY_N_SECONDS = 15;
    private static final String GUI_DEFINITION_PATH = "/epanCountPrototype.xml";

    private final Flag flagExit;
    private final EpanCountLibrary lib;
    ChangeMonitor monitor;

    private final ObjectFactory factory = new ObjectFactory();

    private final DatalessKeyAccess accessKeyGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    private final DatalessKeyAccess accessKey = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");

    EpanCountRequest requestBeingEdited;

    final Object lockListChange = new Object();

    private boolean dirty;

    private final Date today;
    private final Date recent;

    final List rRunningCounts = Collections.synchronizedList(new LinkedList());

    /**
     * @param lib
     * @param flagExit
     * @throws IOException
     * @throws DatalessKeyAccessCreationException
     * 
     */
    public EpanCountPrototypeGUI(final EpanCountLibrary lib, final Flag flagExit) throws IOException, DatalessKeyAccessCreationException
    {
        final Object spotCountsComponent = parse(GUI_DEFINITION_PATH);
        add(spotCountsComponent);
        this.flagExit = flagExit;
        this.lib = lib;
        setFont(new Font("Tahoma",Font.PLAIN,11));
        setColors(0xece9d8, 0x000000, 0xffffff, 0x909090, 0xb0b0b0, 0xededed, 0xc7c5b2, 0xe68b2c, 0xf2c977);

        final Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.clear();
        cal.set(year,month,day);
        this.today = cal.getTime();
        cal.add(Calendar.DATE,-14);
        this.recent = cal.getTime();
    }

    /**
     * Called when the user chooses the menu item File/Exit.
     */
    public void exitApplication()
    {
        if (destroy())
        {
            this.flagExit.set(true);
        }
    }

    /**
     * @see thinlet.Thinlet#destroy()
     */
    public boolean destroy()
    {
        if (this.dirty)
        {
            JOptionPane.showMessageDialog(this,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!this.rRunningCounts.isEmpty())
        {
            final int cCountsRunning = this.rRunningCounts.size();
            String sCounts = ""+cCountsRunning+" counts are";
            String sItThem = "them";
            if (cCountsRunning == 1)
            {
                sCounts = "1 count is";
                sItThem = "it";
            }

            final Object[] options = {"Abort counts","Cancel"};
            final int choice = JOptionPane.showOptionDialog(this,sCounts+" currently running. Are you sure you want to ABORT "+sItThem+"?",
                "Warning",JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,null,options,options[1]);
            if (choice != 0)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Called by the application's shutdown hook.
     * @throws CloneNotSupportedException
     * @throws ClassCastException
     * @throws ConcurrentModificationException
     * @throws LookupException
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void applicationShutdownRequest() throws CloneNotSupportedException, ClassCastException, ConcurrentModificationException, LookupException, SQLException, JAXBException, DatalessKeyAccessCreationException
    {
//        stop monitor thread and join
//        if dirty
//            get from screen into current
//            save current to database
//            if concurrent modification
//                current = clone
//                save current to database
//            end if
//        end if
//        flagExit = true
        this.monitor.close();
        if (this.dirty)
        {
            getRequestFromScreen();
            try
            {
                this.requestBeingEdited = this.requestBeingEdited.saveToDatabase();
            }
            catch (final ConcurrentModificationException e)
            {
                e.printStackTrace();
                makeCloneOfCurrent();
                this.requestBeingEdited = this.requestBeingEdited.saveToDatabase();
            }
            this.dirty = false;
        }

        for (final Iterator iRunningCounts = this.rRunningCounts.iterator(); iRunningCounts.hasNext();)
        {
            final EpanCountRequest request = (EpanCountRequest)iRunningCounts.next();
            request.abort();
        }

        this.flagExit.set(true);
    }

    /**
     * @throws CloneNotSupportedException
     */
    private void makeCloneOfCurrent() throws CloneNotSupportedException
    {
        this.requestBeingEdited = (EpanCountRequest)this.requestBeingEdited.clone();
        this.requestBeingEdited.setTopic(this.requestBeingEdited.getTopic()+" (Copy)");
    }

    /**
     * Called when the application is ready to start up.
     */
    public void applicationStartupRequest()
    {
        doGUI(new Runnable()
        {
            public void run()
            {
                try
                {
                    applicationStartupRequestThread();
                }
                catch (final Throwable e)
                {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    protected void applicationStartupRequestThread() throws JAXBException, DatalessKeyAccessCreationException
    {
        // This represents the set of CountRequests that
        // we will be monitoring (see below).
        final EpanCountList setCounts = lib.createEpanCountList();

        /*
         * Create our "list monitoring" thread, which continually
         * queries the database for any CountRequest inserts, updates,
         * or deletes; and notifies our main GUI of them.
         */
        // 
        this.monitor = new ChangeMonitor(setCounts,CHECK_EVERY_N_SECONDS);
        this.monitor.addChangeListener(this);
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
            this.requestBeingEdited = this.lib.createEpanCountRequest();
            putRequestToScreen();
            addCurrentToList();
            selectCurrentInList(true);
            this.dirty = false;
        }
    }

    protected void addCurrentToList()
    {
        final DatalessKey key = this.requestBeingEdited.getPK();

        final EpanCountListItemContents listItemContents = new EpanCountListItemContents(
            this.requestBeingEdited.getClientName(),
            this.requestBeingEdited.getTopic(),
            this.requestBeingEdited.getDateCreated(),
            this.requestBeingEdited.getQueryCount(),
            0); // 0 counts finished so far; kind of kludgy

        final MonitorableItemDefault item = new MonitorableItemDefault(
            key,
            this.requestBeingEdited.getLastChangeSerialNumber(),
            listItemContents);

        addItemToList(item);
    }

    protected void addItemToList(final MonitorableItemDefault item)
    {
        final Object widgetItem = create("node");

        setString(widgetItem,"text",item.toString());

        final DatalessKey key = item.getPK();
        setString(widgetItem,"name",key.getAccess().keyAsString(key));

        final EpanCountListItemContents listItemContents = (EpanCountListItemContents)item.getContents();

        addItemToSortedList(widgetItem,findTreeForDate(listItemContents.getDateCreated()));
    }

    private void addItemToSortedList(final Object widgetItem, final Object tree)
    {
        final String sItem = getString(widgetItem,"text");
        int low = 0;
        int high = getCount(tree)-1;

        while (low <= high)
        {
            final int mid = (low + high) >> 1;
            final Object itemMid = getItem(tree,mid);
            final String sMid = getString(itemMid,"text");
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
                add(tree,widgetItem,mid);
                return;
            }
        }

        // key not found; insert at low
        if (low >= getCount(tree))
        {
            low = -1; // append to end of tree
        }
        add(tree,widgetItem,low);
    }

    protected Object findTreeForDate(final Date date)
    {
        if (date.before(this.recent))
        {
            return find("listOld");
        }

        if (date.before(this.today))
        {
            return find("listRecent");
        }

        return find("listToday");
    }

    protected void updateCurrentInList()
    {
        final EpanCountListItemContents listItemContents = new EpanCountListItemContents(
            this.requestBeingEdited.getClientName(),
            this.requestBeingEdited.getTopic(),
            this.requestBeingEdited.getDateCreated(),
            this.requestBeingEdited.getQueryCount(),
            0); // 0 counts finished so far; kind of kludgy

        final MonitorableItemDefault item = new MonitorableItemDefault(
            this.requestBeingEdited.getPK(),
            this.requestBeingEdited.getLastChangeSerialNumber(),
            listItemContents);

        updateItemTextInList(item);
    }

    protected void removeCurrentFromList()
    {
        final DatalessKey key = this.requestBeingEdited.getPK();

        // bogus item: just fill in the PK; that's all we need for these two calls
        final MonitorableItemDefault item = new MonitorableItemDefault(key,ChangeSerialNumber.getSerialNumberLowerLimit(),null);
        removeItemFromList(item);
    }

    protected void selectCurrentInList(final boolean select)
    {
        final Object widgetItem = findItemInTree(this.requestBeingEdited.getPK());

        setBoolean(widgetItem,"selected",select);
        this.prevRequest = select ? widgetItem : null;
    }

    protected void updateItemTextInList(final MonitorableItemDefault itemUpdated)
    {
        final Object widgetItem = findItemInTree(itemUpdated.getPK());

        final String oldText = getString(widgetItem,"text");
        final String newText = itemUpdated.toString();

        setString(widgetItem,"text",newText);

        if (!newText.equalsIgnoreCase(oldText))
        {
            // since the text changed, we may have to move
            // the item within the list in order to maintain
            // the sort
            final Object tree = getParent(widgetItem);
            remove(widgetItem);
            addItemToSortedList(widgetItem,tree);
        }
    }

    protected void removeItemFromList(final MonitorableItemDefault itemDeleted)
    {
        remove(findItemInTree(itemDeleted.getPK()));
    }

    protected Object findItemInTree(final DatalessKey key)
    {
        final String sKey = key.getAccess().keyAsString(key);

        Object item = findItemInTreeOrNull(key);

        if (item == null)
        {
            throw new RuntimeException("cannot find item "+sKey+" in tree.");
        }

        return item;
    }

    protected Object findItemInTreeOrNull(final DatalessKey key)
    {
        final String sKey = key.getAccess().keyAsString(key);

        Object item = find(find("listOld"),sKey);
        if (item == null)
        {
            item = find(find("listRecent"),sKey);
        }
        if (item == null)
        {
            item = find(find("listToday"),sKey);
        }

        return item;
    }

    /**
     * Removes the given item from the list of requests.
     * This method would typically be called from a thread other than
     * the dispatch thread.
     * @param item
     */
    public void removeCountList(final MonitorableItem item)
    {
        doGUI(new Runnable()
        {
            public void run()
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
        });
    }

    /**
     * Removes the given item from the list of requests.
     * Must be called by the dispatch thread.
     * @param item
     * @throws CloneNotSupportedException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    protected void removeCountListThread(final MonitorableItem item) throws CloneNotSupportedException, JAXBException, DatalessKeyAccessCreationException
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
            if (itemDeleted.getPK().equals(this.requestBeingEdited.getPK()))
            {
                if (this.dirty)
                {
                    // TODO put up a warning dialog for other people's modifications
                    getRequestFromScreen();
                    makeCloneOfCurrent();
                }
                else
                {
                    JOptionPane.showMessageDialog(this,"Someone else is deleting this count request.","Deleted",JOptionPane.INFORMATION_MESSAGE);
                    this.requestBeingEdited = this.lib.createEpanCountRequest();
                }
                putRequestToScreen();
                addCurrentToList();
                selectCurrentInList(true);
            }
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

    protected void updateCountListThread(final MonitorableItem item) throws CloneNotSupportedException, SQLException, JAXBException, DatalessKeyAccessCreationException
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
        synchronized (this.lockListChange)
        {
            final MonitorableItemDefault itemUpdated = (MonitorableItemDefault)item;
            doGUI(new Runnable()
            {
                public void run()
                {
                    updateItemTextInList(itemUpdated);
                }
            });
            if (itemUpdated.getPK().equals(this.requestBeingEdited.getPK()))
            {
                if (itemUpdated.getLastChangeSerialNumber().compareTo(this.requestBeingEdited.getLastChangeSerialNumber()) <= 0)
                {
                    return;
                }
                if (this.dirty)
                {
                    // TODO put up a warning dialog for other people's modifications
                    selectCurrentInList(false);
                    getRequestFromScreen();
                    makeCloneOfCurrent();
                    putRequestToScreen();
                    addCurrentToList();
                    selectCurrentInList(true);
                }
                else
                {
                    // TODO put up a warning dialog for other people's modifications
                    this.requestBeingEdited = lib.readEpanCountRequest(itemUpdated.getPK());
                    putRequestToScreen();
                }
            }
        }
    }

    /**
     * @param item
     */
    public void addCountList(final MonitorableItem item)
    {
        doGUI(new Runnable()
        {
            public void run()
            {
                addCountListThread(item);
            }
        });
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

    private Object prevRequest;

    /**
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     * 
     */
    public void clickCount() throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty
//            "must save or cancel first"
//            restore prior selection in list
//        else
//            if current has never been saved to database
//                remove current from list
//                remove current from listener
//            end if
//            current = read from database
//            prior selection = current
//            put current to screen
//        end if
        synchronized (this.lockListChange)
        {
            final Object widgetItem = getSelectedItem(find("treeRequests"));
            if (widgetItem == null)
            {
                throw new IllegalStateException("Cannot get currently selected item in any tree.");
            }

            /*
             * If they selected on of the "folders" (today, recent, or old),
             * then restore the old selection (and return).
             */
            final String nameOfSelectedItem = getString(widgetItem,"name");
            if (nameOfSelectedItem.startsWith("list"))
            {
                setBoolean(widgetItem,"selected",false);
                if (this.prevRequest == null)
                {
                    System.err.println("prevRequest was null");
                }
                else
                {
                    setBoolean(this.prevRequest,"selected",true);
                }
                return;
            }

            if (this.dirty)
            {
                JOptionPane.showMessageDialog(this,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                setBoolean(widgetItem,"selected",false);
                if (this.prevRequest == null)
                {
                    System.err.println("prevRequest was null");
                }
                else
                {
                    setBoolean(this.prevRequest,"selected",true);
                }
            }
            else
            {
                if (isCurrentNew())
                {
                    removeCurrentFromList();
                }
                final DatalessKey key = this.accessKey.createKeyFromString(nameOfSelectedItem);
                this.requestBeingEdited = lib.readEpanCountRequest(key);
                this.prevRequest = widgetItem;
                putRequestToScreen();
            }
        }
    }

    /**
     * 
     */
    public void save()
    {
        if (!this.dirty)
        {
            return;
        }
        final Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    saveThread(false);
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * @param buildCountChildRecords
     * @throws InterruptedException
     * @throws CloneNotSupportedException
     * @throws ClassCastException
     * @throws ConcurrentModificationException
     * @throws LookupException
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    protected void saveThread(final boolean buildCountChildRecords) throws InterruptedException, CloneNotSupportedException, ClassCastException, ConcurrentModificationException, LookupException, SQLException, JAXBException, DatalessKeyAccessCreationException
    {
        calcGeoAndWait();

//        get from screen into current
//        dirty = false
//        if (buildCounts)
//            build child count records
//        end if
//        current = save current to database
//        if concurrent modification
//            de-select current in list
//            current = clone
//            save current to database
//            add current to list (text, name)
//            add current to listener
//            select current in list
//        else
//            set current item in list (text)
//            (then triggers notification of updated item from listener)
//        end if
//        put current to screen

        synchronized (this.lockListChange)
        {
            getRequestFromScreen();
            this.dirty = false;
    
            if (buildCountChildRecords)
            {
                this.requestBeingEdited.buildEpanCountChildRecords();
            }
    
            try
            {
                this.requestBeingEdited = this.requestBeingEdited.saveToDatabase();
                updateCurrentInList();
            }
            catch (final ConcurrentModificationException e)
            {
                System.out.println("concurrent modification during save");
                selectCurrentInList(false);
                makeCloneOfCurrent();
                this.requestBeingEdited = this.requestBeingEdited.saveToDatabase();
                addCurrentToList();
                selectCurrentInList(true);
            }
            putRequestToScreen();
        }
    }

    /**
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     * 
     */
    public void cancel() throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty, ask user
//        if current has never been saved to database
//            remove current from list
//            remove current from listener
//            current = create new
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//        else
//            current = read from database
//            put current to screen
//            set current in list (text)
//        end if
        synchronized (this.lockListChange)
        {
            if (this.dirty)
            {
                final Object[] options = {"Discard changes","Keep changes"};
                final int choice = JOptionPane.showOptionDialog(null,"Are you sure you want to DISCARD your changes?",
                    "Warning",JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,null,options,options[1]);
                if (choice != 0)
                {
                    return;
                }
            }

            if (isCurrentNew())
            {
                removeCurrentFromList();
                this.requestBeingEdited = this.lib.createEpanCountRequest();
                putRequestToScreen();
                addCurrentToList();
                selectCurrentInList(true);
            }
            else
            {
                this.requestBeingEdited = lib.readEpanCountRequest(this.requestBeingEdited.getPK());
                updateCurrentInList();
                putRequestToScreen();
            }
            this.dirty = false;
        }
    }

    /**
     * Called when the user presses the Delete button.
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void delete() throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty, ask user
//        if current has never been saved to database
//            remove current from list
//            remove current from listener
//        else
//            delete from database
//            (triggers notification of deleted item from listener; see above)
//            de-select current in list
//        end if
//        current = create new
//        put current to screen
//        add current to list (text, name)
//        add current to listener
//        select current in list
//        dirty = false

        synchronized (this.lockListChange)
        {
            final Object[] options = {"Delete request","Keep request"};
            final int choice = JOptionPane.showOptionDialog(null,"Are you sure you want to PERMANENTLY DELETE this request?",
                "Warning",JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,null,options,options[1]);
            if (choice != 0)
            {
                return;
            }
            if (isCurrentNew())
            {
                removeCurrentFromList();
            }
            else
            {
                this.requestBeingEdited.delete();
                this.monitor.forceCheckNow();
                selectCurrentInList(false);
            }
            this.requestBeingEdited = this.lib.createEpanCountRequest();
            putRequestToScreen();
            addCurrentToList();
            selectCurrentInList(true);
            this.dirty = false;
        }
    }

    /**
     * @throws CloneNotSupportedException
     * @throws JAXBException
     */
    public void copyToNew() throws CloneNotSupportedException, JAXBException
    {
//        if dirty
//            "must save or cancel first"
//        else
//            if current has never been saved to database
//                remove current from list
//                remove current from listener
//            else
//                de-select current in list
//            end if
//            get from screen into current
//            current = clone
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//            dirty = true
//        end if
        synchronized (this.lockListChange)
        {
            if (this.dirty)
            {
                JOptionPane.showMessageDialog(this,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isCurrentNew())
            {
                removeCurrentFromList();
            }
            else
            {
                selectCurrentInList(false);
            }

            getRequestFromScreen();
            makeCloneOfCurrent();
            putRequestToScreen();
            addCurrentToList();
            selectCurrentInList(true);
            setDirty();
        }
    }

    /**
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void blankNew() throws JAXBException, DatalessKeyAccessCreationException
    {
//        if dirty
//            "must save or cancel first"
//        else
//            if current not null and current has never been saved to database
//                remove current from list
//                remove current from listener
//            else
//                de-select current in list
//            end if
//            current = create new
//            put current to screen
//            add current to list (text, name)
//            add current to listener
//            select current in list
//        end if
        synchronized (this.lockListChange)
        {
            if (this.dirty)
            {
                JOptionPane.showMessageDialog(this,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            if (isCurrentNew())
            {
                removeCurrentFromList();
            }
            else
            {
                selectCurrentInList(false);
            }

            this.requestBeingEdited = this.lib.createEpanCountRequest();
            putRequestToScreen();
            addCurrentToList();
            selectCurrentInList(true);
        }
    }




    private boolean isCurrentNew()
    {
        return this.requestBeingEdited != null && this.requestBeingEdited.getLastChangeSerialNumber().equals(ChangeSerialNumber.getSerialNumberLowerLimit());
    }

    private void putRequestToScreen()
    {
        doGUI(new Runnable()
        {
            public void run()
            {
                try
                {
                    putRequestToScreenThread();
                }
                catch (final JAXBException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    void putRequestToScreenThread() throws JAXBException
    {
        final boolean enable = !this.requestBeingEdited.isFrozen();

        {
            final String createdBy = this.requestBeingEdited.getCreatedBy();
            if (createdBy.length() > 0)
            {
                setString(find("createdByLabel"),"text","created by:");
                setString(find("createdBy"),"text",createdBy);
            }
            else
            {
                setString(find("createdByLabel"),"text","");
                setString(find("createdBy"),"text","");
            }
        }

        {
            setString(find("clientName"),"text",this.requestBeingEdited.getClientName());
            setBoolean(find("clientName"),"enabled",enable);
        }

        {
            setString(find("topic"),"text",this.requestBeingEdited.getTopic());
            setBoolean(find("topic"),"enabled",enable);
        }

        final EpanCountCriteria criteria = this.requestBeingEdited.getCriteria();

        {
            // age
            final AgeType ageType = criteria.getAge();
            if (ageType == null)
            {
                setString(find("minAge"),"text","");
                setString(find("maxAge"),"text","");
            }
            else
            {
                {
                    final BigInteger ageMin = ageType.getMin();
                    if (ageMin == null)
                    {
                        setString(find("minAge"),"text","");
                    }
                    else
                    {
                        setString(find("minAge"),"text",ageMin.toString());
                    }
                }

                {
                    final BigInteger ageMax = ageType.getMax();
                    if (ageMax == null)
                    {
                        setString(find("maxAge"),"text","");
                    }
                    else
                    {
                        setString(find("maxAge"),"text",ageMax.toString());
                    }
                }
            }
            setBoolean(find("minAge"),"enabled",enable);
            setBoolean(find("maxAge"),"enabled",enable);
            setBoolean(find("minAgeLabel"),"enabled",enable);
            setBoolean(find("maxAgeLabel"),"enabled",enable);
        }

        {
            // kids
            final WithKidsType withKids = criteria.getWithKids();
            setString(find("minAgeKid"),"text","");
            setString(find("maxAgeKid"),"text","");
            setBoolean(find("noKids"),"selected",false);
            boolean specifiedMale = false;
            boolean specifiedFemale = false;
            if (withKids != null)
            {
                {
                    // ages of kids
                    final AgeType ageType = withKids.getAge();
                    if (ageType == null)
                    {
                        setString(find("minAgeKid"),"text","");
                        setString(find("maxAgeKid"),"text","");
                    }
                    else
                    {
                        {
                            final BigInteger ageMin = ageType.getMin();
                            if (ageMin == null)
                            {
                                setString(find("minAgeKid"),"text","");
                            }
                            else
                            {
                                setString(find("minAgeKid"),"text",ageMin.toString());
                            }
                        }
    
                        {
                            final BigInteger ageMax = ageType.getMax();
                            if (ageMax == null)
                            {
                                setString(find("maxAgeKid"),"text","");
                            }
                            else
                            {
                                setString(find("maxAgeKid"),"text",ageMax.toString());
                            }
                        }
                    }
                }
                {
                    // sexes of kids

                    final GenderType genderType = withKids.getGender();
                    if (genderType != null)
                    {
                        final List rGender = genderType.getId();
                        for (Iterator iGender = rGender.iterator(); iGender.hasNext();)
                        {
                            final GenderEnumType gender = (GenderEnumType)iGender.next();
                            if (gender.equals(GenderEnumType.MALE))
                            {
                                specifiedMale = true;
                            }
                            else if (gender.equals(GenderEnumType.FEMALE))
                            {
                                specifiedFemale = true;
                            }
                        }
                        if (specifiedMale && specifiedFemale)
                        {
                            throw new IllegalStateException("both male and female were specified; shouldn't happen");
                        }
                    }
                }
                // no kids
                setBoolean(find("noKids"),"selected",withKids.isNoKids());
            }
            setBoolean(find("minAgeKid"),"enabled",enable);
            setBoolean(find("maxAgeKid"),"enabled",enable);
            setBoolean(find("minAgeKidLabel"),"enabled",enable);
            setBoolean(find("maxAgeKidLabel"),"enabled",enable);

            setBoolean(find("maleKid"),"selected",specifiedMale);
            setBoolean(find("maleKid"),"enabled",enable || specifiedMale);

            setBoolean(find("femaleKid"),"selected",specifiedFemale);
            setBoolean(find("femaleKid"),"enabled",enable || specifiedFemale);

            setBoolean(find("bothSexesKid"),"selected",!specifiedMale && !specifiedFemale);
            setBoolean(find("bothSexesKid"),"enabled",enable || (!specifiedMale && !specifiedFemale));

            setBoolean(find("noKids"),"enabled",enable);
        }

        {
            // income
            setBoolean(find("inc0"),"selected",false);
            setBoolean(find("inc20"),"selected",false);
            setBoolean(find("inc30"),"selected",false);
            setBoolean(find("inc40"),"selected",false);
            setBoolean(find("inc50"),"selected",false);
            setBoolean(find("inc60"),"selected",false);
            setBoolean(find("inc75"),"selected",false);
            setBoolean(find("inc100"),"selected",false);
            setBoolean(find("inc150"),"selected",false);
            setBoolean(find("incPNA"),"selected",false);

            final IncomeType incomeType = criteria.getIncome();
            if (incomeType != null)
            {
                final List rIncome = incomeType.getId();
                for (Iterator iIncome = rIncome.iterator(); iIncome.hasNext();)
                {
                    final IncomeEnumType income = (IncomeEnumType)iIncome.next();
                    if (income.equals(IncomeEnumType.MIN_0_K))
                    {
                        setBoolean(find("inc0"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_20_K))
                    {
                        setBoolean(find("inc20"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_30_K))
                    {
                        setBoolean(find("inc30"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_40_K))
                    {
                        setBoolean(find("inc40"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_50_K))
                    {
                        setBoolean(find("inc50"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_60_K))
                    {
                        setBoolean(find("inc60"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_75_K))
                    {
                        setBoolean(find("inc75"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_100_K))
                    {
                        setBoolean(find("inc100"),"selected",true);
                    }
                    else if (income.equals(IncomeEnumType.MIN_150_K))
                    {
                        setBoolean(find("inc150"),"selected",true);
                    }
                }
                if (incomeType.isPna())
                {
                    setBoolean(find("incPNA"),"selected",true);
                }
            }
            setBoolean(find("incLabel0"),"enabled",enable);
            setBoolean(find("inc0"),"enabled",enable);
            setBoolean(find("inc20"),"enabled",enable);
            setBoolean(find("inc30"),"enabled",enable);
            setBoolean(find("inc40"),"enabled",enable);
            setBoolean(find("inc50"),"enabled",enable);
            setBoolean(find("inc60"),"enabled",enable);
            setBoolean(find("inc75"),"enabled",enable);
            setBoolean(find("inc100"),"enabled",enable);
            setBoolean(find("inc150"),"enabled",enable);
            setBoolean(find("incLabelK"),"enabled",enable);
            setBoolean(find("incPNA"),"enabled",enable);
        }

        {
            // ethnicity
            setBoolean(find("ethBlack"),"selected",false);
            setBoolean(find("ethHispanic"),"selected",false);
            setBoolean(find("ethWhite"),"selected",false);
            setBoolean(find("ethAsian"),"selected",false);
            setBoolean(find("ethPacific"),"selected",false);
            setBoolean(find("ethIndian"),"selected",false);
            setBoolean(find("ethOther"),"selected",false);
            final EthnicityType ethnicityType = criteria.getEthnicity();
            if (ethnicityType != null)
            {
                final List rEthnicity = ethnicityType.getId();
                for (final Iterator iEthnicity = rEthnicity.iterator(); iEthnicity.hasNext();)
                {
                    final EthnicityEnumType ethnicity = (EthnicityEnumType)iEthnicity.next();
                    if (ethnicity.equals(EthnicityEnumType.BLACK))
                    {
                        setBoolean(find("ethBlack"),"selected",true);
                    }
                    if (ethnicity.equals(EthnicityEnumType.HISPANIC))
                    {
                        setBoolean(find("ethHispanic"),"selected",true);
                    }
                    if (ethnicity.equals(EthnicityEnumType.WHITE))
                    {
                        setBoolean(find("ethWhite"),"selected",true);
                    }
                    if (ethnicity.equals(EthnicityEnumType.ASIAN))
                    {
                        setBoolean(find("ethAsian"),"selected",true);
                    }
                    if (ethnicity.equals(EthnicityEnumType.PACIFIC))
                    {
                        setBoolean(find("ethPacific"),"selected",true);
                    }
                    if (ethnicity.equals(EthnicityEnumType.INDIAN))
                    {
                        setBoolean(find("ethIndian"),"selected",true);
                    }
                    if (ethnicity.equals(EthnicityEnumType.OTHER))
                    {
                        setBoolean(find("ethOther"),"selected",true);
                    }
                }
            }
            setBoolean(find("ethBlack"),"enabled",enable);
            setBoolean(find("ethHispanic"),"enabled",enable);
            setBoolean(find("ethWhite"),"enabled",enable);
            setBoolean(find("ethAsian"),"enabled",enable);
            setBoolean(find("ethPacific"),"enabled",enable);
            setBoolean(find("ethIndian"),"enabled",enable);
            setBoolean(find("ethOther"),"enabled",enable);
        }

        {
            // education
            setBoolean(find("eduSomeHS"),"selected",false);
            setBoolean(find("eduHSGrad"),"selected",false);
            setBoolean(find("eduSomeCol"),"selected",false);
            setBoolean(find("eduColDeg"),"selected",false);
            setBoolean(find("eduSomeGrad"),"selected",false);
            setBoolean(find("eduMasters"),"selected",false);
            setBoolean(find("eduPro"),"selected",false);
            final EducationType educationType = criteria.getEducation();
            if (educationType != null)
            {
                final List rEducation = educationType.getId();
                for (final Iterator iEducation = rEducation.iterator(); iEducation.hasNext();)
                {
                    final EducationEnumType education = (EducationEnumType)iEducation.next();
                    if (education.equals(EducationEnumType.COMPLETED_SOME_HIGH_SCHOOL))
                    {
                        setBoolean(find("eduSomeHS"),"selected",true);
                    }
                    if (education.equals(EducationEnumType.HIGH_SCHOOL_GRADUATE))
                    {
                        setBoolean(find("eduHSGrad"),"selected",true);
                    }
                    if (education.equals(EducationEnumType.COMPLETED_SOME_COLLEGE))
                    {
                        setBoolean(find("eduSomeCol"),"selected",true);
                    }
                    if (education.equals(EducationEnumType.COLLEGE_DEGREE))
                    {
                        setBoolean(find("eduColDeg"),"selected",true);
                    }
                    if (education.equals(EducationEnumType.COMPLETED_SOME_POSTGRADUATE))
                    {
                        setBoolean(find("eduSomeGrad"),"selected",true);
                    }
                    if (education.equals(EducationEnumType.MASTERS_DEGREE))
                    {
                        setBoolean(find("eduMasters"),"selected",true);
                    }
                    if (education.equals(EducationEnumType.DOCTORATE_LAW_OR_PROFESSIONAL_DEGREE))
                    {
                        setBoolean(find("eduPro"),"selected",true);
                    }
                }
            }
            setBoolean(find("eduSomeHS"),"enabled",enable);
            setBoolean(find("eduHSGrad"),"enabled",enable);
            setBoolean(find("eduSomeCol"),"enabled",enable);
            setBoolean(find("eduColDeg"),"enabled",enable);
            setBoolean(find("eduSomeGrad"),"enabled",enable);
            setBoolean(find("eduMasters"),"enabled",enable);
            setBoolean(find("eduPro"),"enabled",enable);
        }

        {
            // married
            setBoolean(find("marSingle"),"selected",false);
            setBoolean(find("marMarried"),"selected",false);
            setBoolean(find("marDivorced"),"selected",false);
            setBoolean(find("marPartner"),"selected",false);
            final MarriedType marriedType = criteria.getMarried();
            if (marriedType != null)
            {
                final List rMarried = marriedType.getId();
                for (final Iterator iMarried = rMarried.iterator(); iMarried.hasNext();)
                {
                    final MarriedEnumType Married = (MarriedEnumType)iMarried.next();
                    if (Married.equals(MarriedEnumType.SINGLE_NEVER_MARRIED))
                    {
                        setBoolean(find("marSingle"),"selected",true);
                    }
                    if (Married.equals(MarriedEnumType.MARRIED))
                    {
                        setBoolean(find("marMarried"),"selected",true);
                    }
                    if (Married.equals(MarriedEnumType.SEPARATED_DIVORCED_WIDOWED))
                    {
                        setBoolean(find("marDivorced"),"selected",true);
                    }
                    if (Married.equals(MarriedEnumType.DOMESTIC_PARTNERSHIP))
                    {
                        setBoolean(find("marPartner"),"selected",true);
                    }
                }
            }
            setBoolean(find("marSingle"),"enabled",enable);
            setBoolean(find("marMarried"),"enabled",enable);
            setBoolean(find("marDivorced"),"enabled",enable);
            setBoolean(find("marPartner"),"enabled",enable);
        }

        {
            // sex
            boolean specifiedMale = false;
            boolean specifiedFemale = false;

            final GenderType genderType = criteria.getGender();
            if (genderType != null)
            {
                final List rGender = genderType.getId();
                for (Iterator iGender = rGender.iterator(); iGender.hasNext();)
                {
                    final GenderEnumType gender = (GenderEnumType)iGender.next();
                    if (gender.equals(GenderEnumType.MALE))
                    {
                        specifiedMale = true;
                    }
                    else if (gender.equals(GenderEnumType.FEMALE))
                    {
                        specifiedFemale = true;
                    }
                }
            }
            if (specifiedMale && specifiedFemale)
            {
                throw new IllegalStateException("both male and female were specified; shouldn't happen");
            }

            setBoolean(find("male"),"selected",specifiedMale);
            setBoolean(find("male"),"enabled",enable || specifiedMale);

            setBoolean(find("female"),"selected",specifiedFemale);
            setBoolean(find("female"),"enabled",enable || specifiedFemale);

            setBoolean(find("bothSexes"),"selected",!specifiedMale && !specifiedFemale);
            setBoolean(find("bothSexes"),"enabled",enable || (!specifiedMale && !specifiedFemale));
        }

        {
            // XDem
            final boolean emptyXdem = this.requestBeingEdited.getXdemCriteria().isEmpty();
            final boolean badXdem = this.requestBeingEdited.getXdemCriteria().hasErrors();
            setString(find("xdemLabel"),"text",emptyXdem ? "" : badXdem ? "(has invalid XDem)" : "(has XDem)");
            final boolean enableXdem = enable || !emptyXdem;
            setBoolean(find("xdem"),"enabled",enableXdem);
        }

        {
            {
                // geography type
                setBoolean(find("geoZIP"),"selected",false);
                setBoolean(find("geoFIPS"),"selected",false);
                setBoolean(find("geoCounty"),"selected",false);
                setBoolean(find("geoMSA"),"selected",false);
                setBoolean(find("geoDMA"),"selected",false);
                setBoolean(find("geoState"),"selected",false);
                setBoolean(find("geoUSCont"),"selected",false);
                setBoolean(find("geoUSFull"),"selected",false);
                UserGeoType userGeo = criteria.getUserGeo();
                if (userGeo == null)
                {
                    criteria.setUserGeo(this.factory.createUserGeoType());
                    userGeo = criteria.getUserGeo();
                    userGeo.setGeoType(GeographyEnumType.CONTINENTAL);
                }
                final GeographyEnumType geoType = userGeo.getGeoType();
                if (geoType.equals(GeographyEnumType.ZIP))
                {
                    setBoolean(find("geoZIP"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.FIPS))
                {
                    setBoolean(find("geoFIPS"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.COUNTY))
                {
                    setBoolean(find("geoCounty"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.MSA))
                {
                    setBoolean(find("geoMSA"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.DMA))
                {
                    setBoolean(find("geoDMA"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.STATE))
                {
                    setBoolean(find("geoState"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.CONTINENTAL))
                {
                    setBoolean(find("geoUSCont"),"selected",true);
                }
                else if (geoType.equals(GeographyEnumType.USA))
                {
                    setBoolean(find("geoUSFull"),"selected",true);
                }
                setBoolean(find("geoZIP"),"enabled",enable || getBoolean(find("geoZIP"),"selected"));
                setBoolean(find("geoFIPS"),"enabled",enable || getBoolean(find("geoFIPS"),"selected"));
                setBoolean(find("geoCounty"),"enabled",enable || getBoolean(find("geoCounty"),"selected"));
                setBoolean(find("geoMSA"),"enabled",enable || getBoolean(find("geoMSA"),"selected"));
                setBoolean(find("geoDMA"),"enabled",enable || getBoolean(find("geoDMA"),"selected"));
                setBoolean(find("geoState"),"enabled",enable || getBoolean(find("geoState"),"selected"));
                setBoolean(find("geoUSCont"),"enabled",enable || getBoolean(find("geoUSCont"),"selected"));
                setBoolean(find("geoUSFull"),"enabled",enable || getBoolean(find("geoUSFull"),"selected"));

                clearGeoAreasGUI();
                clearGeoLists();

                GeographyType geography = criteria.getGeography();
                if (geography == null)
                {
                    criteria.setGeography(this.factory.createGeographyType());
                    geography = criteria.getGeography();
                }
                final List rID = geography.getId();
                final Iterator iID = rID.iterator();

                final StringBuffer sUserFixed = new StringBuffer(2048);

                final Set setUnparsedEntry = new HashSet();
                final List rUserGeoItem = userGeo.getUserGeoItem();
                this.lastParsedGeoType = null;
                this.geoTypeLastChosen = null;
                for (final Iterator iUserGeoItem = rUserGeoItem.iterator(); iUserGeoItem.hasNext();)
                {
                    String s = (String)iUserGeoItem.next();
                    s = s.trim();
                    if (s.length() == 0)
                    {
                        continue;
                    }

                    while (setUnparsedEntry.contains(s))
                    {
                        s += " [DUPLICATE]";
                    }

                    setUnparsedEntry.add(s);

                    sUserFixed.append(s);
                    sUserFixed.append("\n");

                    final String sID = (String)iID.next();
                    if (sID.length() > 0)
                    {
                        final DatalessKey key = this.accessKeyGeo.createKeyFromString(sID);
                        this.mapResolution.put(s,key);
                    }
                    else
                    {
                        this.mapResolution.put(s,null);
                    }
                }
                setString(find("geoUser"),"text",sUserFixed.toString());

                setBoolean(find("geoUser"),"enabled",enable);
                //setBoolean(find("geoCalc"),"enabled",enable); // don't disable because user can't scroll
                setBoolean(find("geoMatches"),"enabled",enable);
            }
        }

        {
            // break-out
            setBoolean(find("breakGeo"),"selected",false);
            setBoolean(find("breakSex"),"selected",false);
            final BreakOutType breakOutType = criteria.getBreakOut();
            if (breakOutType != null)
            {
                if (breakOutType.isGeography())
                {
                    setBoolean(find("breakGeo"),"selected",true);                    
                }
                if (breakOutType.isGender())
                {
                    setBoolean(find("breakSex"),"selected",true);
                }
            }
            setBoolean(find("breakGeo"),"enabled",enable);
            setBoolean(find("breakSex"),"enabled",enable);
        }

        {
            // display child count queries
            removeAll(find("tableCount"));

            final Object colName = create("column");
            setString(colName,"text","Criteria");
            setInteger(colName,"width",300);// TODO don't hard-code width of count column
            final Object colCount = create("column");
            setString(colCount,"text","Panelists");
            setChoice(colCount,"alignment","right");
            final Object headerCount = create("header");
            add(headerCount,colName);
            add(headerCount,colCount);
            add(find("tableCount"),headerCount);

            for (final Iterator iCount = this.requestBeingEdited.iterator(); iCount.hasNext(); )
            {
                final EpanCount count = (EpanCount)iCount.next();

                final String sName = count.getName();

                String sCountOrErrMsg = "";
                if (count.completedSuccessfully())
                {
                    sCountOrErrMsg = ""+count.getCount();
                }
                else if (count.completedWithError())
                {
                    final BufferedReader rdr = new BufferedReader(new StringReader(count.getErrorMessage()));
                    try
                    {
                        sCountOrErrMsg = rdr.readLine();
                        rdr.close();
                    }
                    catch (final IOException shouldNotHappen)
                    {
                        shouldNotHappen.printStackTrace();
                        sCountOrErrMsg = count.getErrorMessage();
                    }
                }

                final Object cellName = create("cell");
                setString(cellName,"text",sName);
                final Object cellCount = create("cell");
                setString(cellCount,"text",sCountOrErrMsg);
                setChoice(cellCount,"alignment","right");

                final Object rowCount = create("row");
                add(rowCount,cellName);
                add(rowCount,cellCount);

                add(find("tableCount"),rowCount);
            }
        }

        setBoolean(find("save"),"enabled",enable);
        setBoolean(find("cancel"),"enabled",enable);

        final int iRunning = this.rRunningCounts.indexOf(this.requestBeingEdited);
        final boolean isCurrentlyRunning = iRunning >= 0;

        setBoolean(find("runCount"),"enabled",enable && !isCurrentlyRunning);
        setBoolean(find("abortCount"),"enabled",isCurrentlyRunning);

        if (enable)
        {
            groupGeo();
        }
    }

    private void getRequestFromScreen() throws JAXBException
    {
        getGeoFromScreen();

        try
        {
            final String topic = getString(find("topic"),"text");
            if (topic.length() == 0 || 64 < topic.length())
            {
                throw new IllegalArgumentException("A topic must be entered (64 characters maximum).");
            }
            this.requestBeingEdited.setTopic(topic);
    
            final String clientName = getString(find("clientName"),"text");
            if (clientName.length() == 0 || 64 < clientName.length())
            {
                throw new IllegalArgumentException("A client name must be entered (64 characters maximum).");
            }
            this.requestBeingEdited.setClientName(clientName);
        }
        catch (final Throwable e)
        {
            showException(e);
            throw new RuntimeException(e);
        }

        final EpanCountCriteria criteria = this.requestBeingEdited.getCriteria();

        {
            // age
            final String sMinAge = getString(find("minAge"),"text");
            final String sMaxAge = getString(find("maxAge"),"text");
            if (sMinAge.length() > 0 || sMaxAge.length() > 0)
            {
                final AgeType age = this.factory.createAgeType();

                if (sMinAge.length() > 0)
                {
                    try
                    {
                        final BigInteger biAge = new BigInteger(sMinAge);
    
                        final int iAge = biAge.intValue();
                        if (iAge < 18 || 200 <= iAge)
                        {
                            throw new IllegalArgumentException("age must be between 18 and 199 inclusive.");
                        }
                        age.setMin(biAge);
                    }
                    catch (final Throwable e)
                    {
                        showException(e);
                        throw new RuntimeException(e);
                    }
                }
                if (sMaxAge.length() > 0)
                {
                    try
                    {
                        final BigInteger biAge = new BigInteger(sMaxAge);

                        final int iAge = biAge.intValue();
                        if (iAge < 18 || 200 <= iAge)
                        {
                            throw new IllegalArgumentException("age must be between 18 and 199 inclusive.");
                        }
                        age.setMax(biAge);
                    }
                    catch (final Throwable e)
                    {
                        showException(e);
                        throw new RuntimeException(e);
                    }
                }

                criteria.setAge(age);
            }
            else
            {
                criteria.setAge(null);
            }
        }

        {
            // kids
            boolean haveKidCriteria = false;
            WithKidsType withKids = this.factory.createWithKidsType();
            {
                // ages of kids
                final String sMinAge = getString(find("minAgeKid"),"text");
                final String sMaxAge = getString(find("maxAgeKid"),"text");
                if (sMinAge.length() > 0 || sMaxAge.length() > 0)
                {
                    final AgeType age = this.factory.createAgeType();

                    if (sMinAge.length() > 0)
                    {
                        try
                        {
                            final BigInteger biAge = new BigInteger(sMinAge);
        
                            final int iAge = biAge.intValue();
                            if (iAge < 2 || 18 <= iAge)
                            {
                                throw new IllegalArgumentException("age must be between 2 and 17 inclusive.");
                            }
                            age.setMin(biAge);
                            haveKidCriteria = true;
                        }
                        catch (final Throwable e)
                        {
                            showException(e);
                            throw new RuntimeException(e);
                        }
                    }
                    if (sMaxAge.length() > 0)
                    {
                        try
                        {
                            final BigInteger biAge = new BigInteger(sMaxAge);

                            final int iAge = biAge.intValue();
                            if (iAge < 2 || 18 <= iAge)
                            {
                                throw new IllegalArgumentException("age must be between 2 and 17 inclusive.");
                            }
                            age.setMax(biAge);
                            haveKidCriteria = true;
                        }
                        catch (final Throwable e)
                        {
                            showException(e);
                            throw new RuntimeException(e);
                        }
                    }

                    withKids.setAge(age);
                }
            }
            {
                // sexes of kids
                GenderType genderType = withKids.getGender();
                if (genderType == null)
                {
                    withKids.setGender(this.factory.createGenderType());
                    genderType = withKids.getGender();
                }
                final List rGender = genderType.getId();
                rGender.clear();
                if (getBoolean(find("maleKid"),"selected"))
                {
                    rGender.add(GenderEnumType.MALE);
                    haveKidCriteria = true;
                }
                else if (getBoolean(find("femaleKid"),"selected"))
                {
                    rGender.add(GenderEnumType.FEMALE);
                    haveKidCriteria = true;
                }
                if (rGender.isEmpty())
                {
                    withKids.setGender(null);
                }
            }
            // no kids
            if (getBoolean(find("noKids"),"selected"))
            {
                withKids.setNoKids(true);
                haveKidCriteria = true;
            }
            if (haveKidCriteria)
            {
                criteria.setWithKids(withKids);
            }
            else
            {
                criteria.setWithKids(null);
            }
        }

        {
            // income
            IncomeType incomeType = criteria.getIncome();
            if (incomeType == null)
            {
                criteria.setIncome(this.factory.createIncomeType());
                incomeType = criteria.getIncome();
            }
            final List rIncome = incomeType.getId();
            rIncome.clear();
            if (getBoolean(find("inc0"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_0_K);
            }
            if (getBoolean(find("inc20"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_20_K);
            }
            if (getBoolean(find("inc30"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_30_K);
            }
            if (getBoolean(find("inc40"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_40_K);
            }
            if (getBoolean(find("inc50"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_50_K);
            }
            if (getBoolean(find("inc60"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_60_K);
            }
            if (getBoolean(find("inc75"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_75_K);
            }
            if (getBoolean(find("inc100"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_100_K);
            }
            if (getBoolean(find("inc150"),"selected"))
            {
                rIncome.add(IncomeEnumType.MIN_150_K);
            }
            boolean pna = getBoolean(find("incPNA"),"selected");
            incomeType.setPna(pna);
            if (rIncome.isEmpty() && !pna)
            {
                criteria.setIncome(null);
            }
        }

        {
            // ethnicity
            EthnicityType ethnicityType = criteria.getEthnicity();
            if (ethnicityType == null)
            {
                criteria.setEthnicity(this.factory.createEthnicityType());
                ethnicityType = criteria.getEthnicity();
            }
            final List rEthnicity = ethnicityType.getId();
            rEthnicity.clear();
            if (getBoolean(find("ethBlack"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.BLACK);
            }
            if (getBoolean(find("ethHispanic"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.HISPANIC);
            }
            if (getBoolean(find("ethWhite"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.WHITE);
            }
            if (getBoolean(find("ethAsian"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.ASIAN);
            }
            if (getBoolean(find("ethPacific"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.PACIFIC);
            }
            if (getBoolean(find("ethIndian"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.INDIAN);
            }
            if (getBoolean(find("ethOther"),"selected"))
            {
                rEthnicity.add(EthnicityEnumType.OTHER);
            }
            if (rEthnicity.isEmpty())
            {
                criteria.setEthnicity(null);
            }
        }

        {
            // education
            EducationType educationType = criteria.getEducation();
            if (educationType == null)
            {
                criteria.setEducation(this.factory.createEducationType());
                educationType = criteria.getEducation();
            }
            final List rEducation = educationType.getId();
            rEducation.clear();
            if (getBoolean(find("eduSomeHS"),"selected"))
            {
                rEducation.add(EducationEnumType.COMPLETED_SOME_HIGH_SCHOOL);
            }
            if (getBoolean(find("eduHSGrad"),"selected"))
            {
                rEducation.add(EducationEnumType.HIGH_SCHOOL_GRADUATE);
            }
            if (getBoolean(find("eduSomeCol"),"selected"))
            {
                rEducation.add(EducationEnumType.COMPLETED_SOME_COLLEGE);
            }
            if (getBoolean(find("eduColDeg"),"selected"))
            {
                rEducation.add(EducationEnumType.COLLEGE_DEGREE);
            }
            if (getBoolean(find("eduSomeGrad"),"selected"))
            {
                rEducation.add(EducationEnumType.COMPLETED_SOME_POSTGRADUATE);
            }
            if (getBoolean(find("eduMasters"),"selected"))
            {
                rEducation.add(EducationEnumType.MASTERS_DEGREE);
            }
            if (getBoolean(find("eduPro"),"selected"))
            {
                rEducation.add(EducationEnumType.DOCTORATE_LAW_OR_PROFESSIONAL_DEGREE);
            }
            if (rEducation.isEmpty())
            {
                criteria.setEducation(null);
            }
        }

        {
            // married
            MarriedType marriedType = criteria.getMarried();
            if (marriedType == null)
            {
                criteria.setMarried(this.factory.createMarriedType());
                marriedType = criteria.getMarried();
            }
            final List rMarried = marriedType.getId();
            rMarried.clear();
            if (getBoolean(find("marSingle"),"selected"))
            {
                rMarried.add(MarriedEnumType.SINGLE_NEVER_MARRIED);
            }
            if (getBoolean(find("marMarried"),"selected"))
            {
                rMarried.add(MarriedEnumType.MARRIED);
            }
            if (getBoolean(find("marDivorced"),"selected"))
            {
                rMarried.add(MarriedEnumType.SEPARATED_DIVORCED_WIDOWED);
            }
            if (getBoolean(find("marPartner"),"selected"))
            {
                rMarried.add(MarriedEnumType.DOMESTIC_PARTNERSHIP);
            }
            if (rMarried.isEmpty())
            {
                criteria.setMarried(null);
            }
        }

        {
            // sex
            GenderType genderType = criteria.getGender();
            if (genderType == null)
            {
                criteria.setGender(this.factory.createGenderType());
                genderType = criteria.getGender();
            }
            final List rGender = genderType.getId();
            rGender.clear();
            if (getBoolean(find("male"),"selected"))
            {
                rGender.add(GenderEnumType.MALE);
            }
            else if (getBoolean(find("female"),"selected"))
            {
                rGender.add(GenderEnumType.FEMALE);
            }
            if (rGender.isEmpty())
            {
                criteria.setGender(null);
            }
        }

        {
            {
                {
                    UserGeoType userGeo = criteria.getUserGeo();
                    if (userGeo == null)
                    {
                        criteria.setUserGeo(this.factory.createUserGeoType());
                        userGeo = criteria.getUserGeo();
                    }
                    // geography type
                    if (getBoolean(find("geoZIP"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.ZIP);
                    }
                    else if (getBoolean(find("geoFIPS"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.FIPS);
                    }
                    else if (getBoolean(find("geoCounty"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.COUNTY);
                    }
                    else if (getBoolean(find("geoMSA"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.MSA);
                    }
                    else if (getBoolean(find("geoDMA"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.DMA);
                    }
                    else if (getBoolean(find("geoState"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.STATE);
                    }
                    else if (getBoolean(find("geoUSCont"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.CONTINENTAL);
                    }
                    else if (getBoolean(find("geoUSFull"),"selected"))
                    {
                        userGeo.setGeoType(GeographyEnumType.USA);
                    }
                    // user-entered geography items
                    final List userGeoItem = userGeo.getUserGeoItem();
                    userGeoItem.clear();
                    for (final Iterator iUserGeo = this.rUnparsedEntry.iterator(); iUserGeo.hasNext();)
                    {
                        final String sUserGeo = (String)iUserGeo.next();
                        userGeoItem.add(sUserGeo);
                    }
                }
                {
                    GeographyType geography = criteria.getGeography();
                    if (geography == null)
                    {
                        criteria.setGeography(this.factory.createGeographyType());
                        geography = criteria.getGeography();
                    }
                    final List rGeo = geography.getId();
                    rGeo.clear();
                    for (final Iterator iUserGeo = this.rUnparsedEntry.iterator(); iUserGeo.hasNext();)
                    {
                        final String sUserGeo = (String)iUserGeo.next();
                        final DatalessKey keyGeo = (DatalessKey)this.mapResolution.get(sUserGeo);
                        if (keyGeo == null)
                        {
                            rGeo.add("");
                        }
                        else
                        {
                            rGeo.add(keyGeo.getAccess().keyAsString(keyGeo));
                        }
                    }
                }
            }
        }

        {
            // break-out
            BreakOutType breakOutType = criteria.getBreakOut();
            if (breakOutType == null)
            {
                criteria.setBreakOut(this.factory.createBreakOutType());
                breakOutType = criteria.getBreakOut();
            }
            breakOutType.setGeography(false);
            breakOutType.setGender(false);
            if (getBoolean(find("breakGeo"),"selected"))
            {
                breakOutType.setGeography(true);
            }
            if (getBoolean(find("breakSex"),"selected"))
            {
                breakOutType.setGender(true);
            }
        }
    }

    private void showException(final Throwable e)
    {
        doGUI(new Runnable()
        {
            public void run()
            {
                try
                {
                    final StringWriter writer = new StringWriter();
                    e.printStackTrace(new PrintWriter(writer));
                    final String trace = writer.toString().replace('\r',' ').replace('\t',' ');

                    String thrclass = e.getClass().getName();
                    thrclass = thrclass.substring(thrclass.lastIndexOf('.') + 1);

                    final Object exceptiondialog = parse("/exception.xml");
                    setString(exceptiondialog,"text",thrclass);
                    setString(find(exceptiondialog,"message"),"text",e.getMessage());
                    setString(find(exceptiondialog,"stacktrace"),"text",trace);

                    add(exceptiondialog);
                }
                catch (final Throwable e2)
                {
                    e2.printStackTrace();
                }
            }
        });
    }

    /**
     * @param dialog
     */
    public void closeDialog(final Object dialog)
    {
        remove(dialog);
    }

    protected GeographyEnumType lastParsedGeoType;
    protected final List rUnparsedEntry = Collections.synchronizedList(new ArrayList(100)); // <String>
    private final Map mapResolution = Collections.synchronizedMap(new HashMap(100)); // <String,DatalessKey>

    protected final Flag isCalcGeoRunning = new Flag();

    /**
     * @throws InterruptedException
     */
    public void calcGeo() throws InterruptedException
    {
        synchronized (this.isCalcGeoRunning)
        {
            /*
             * Prevent calcGeoTask from running more
             * than once at a time. This could happen
             * if they double-click on the button,
             * for example.
             */
            if (this.isCalcGeoRunning.isTrue())
            {
                // already running, so just do nothing
                return;
            }
            this.isCalcGeoRunning.waitToSetTrue();
        }
        final Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    calcGeoTask();
                    EpanCountPrototypeGUI.this.isCalcGeoRunning.waitToSetFalse();
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /**
     * @throws InterruptedException
     */
    public void calcGeoAndWait() throws InterruptedException
    {
        calcGeo();
        this.isCalcGeoRunning.waitUntilFalse();
    }

    private final Image iconQuestion = Toolkit.getDefaultToolkit().createImage(EpanCountPrototype.class.getResource("/icon/unknown.gif"));
    private final Image iconCheck = Toolkit.getDefaultToolkit().createImage(EpanCountPrototype.class.getResource("/icon/resolved.gif"));
    private final Image iconEx = Toolkit.getDefaultToolkit().createImage(EpanCountPrototype.class.getResource("/icon/bad.gif"));

    protected void calcGeoTask()
    {
        clearGeoAreasGUI();

        if (this.requestBeingEdited.isFrozen())
        {
            showGeos();
            return;
        }

        final GeographyEnumType geoType = getSelectedGeoType();
        if (
            geoType.equals(GeographyEnumType.COUNTY) ||
            geoType.equals(GeographyEnumType.MSA) ||
            geoType.equals(GeographyEnumType.DMA) ||
            geoType.equals(GeographyEnumType.STATE)
            )
        {
            initGeoLists(geoType);
            calcGeoNames(geoType);
        }
        else if (
            geoType.equals(GeographyEnumType.ZIP) ||
            geoType.equals(GeographyEnumType.FIPS)
            )
        {
            initGeoListsCodes(geoType);
            calcGeoCodes(geoType);
        }
        else
        {
            clearGeoLists();
        }

        setDirty();
        this.lastParsedGeoType = geoType;
    }

    private void getGeoFromScreen()
    {
        final GeographyEnumType geoType = getSelectedGeoType();
        if (
            geoType.equals(GeographyEnumType.COUNTY) ||
            geoType.equals(GeographyEnumType.MSA) ||
            geoType.equals(GeographyEnumType.DMA) ||
            geoType.equals(GeographyEnumType.STATE)
            )
        {
            initGeoLists(geoType);
        }
        else if (
            geoType.equals(GeographyEnumType.ZIP) ||
            geoType.equals(GeographyEnumType.FIPS)
            )
        {
            initGeoListsCodes(geoType);
        }
        else
        {
            clearGeoLists();
        }
    }

    private void showGeos()
    {
        final Object geoCalc = find("geoCalc");

        final GeographicNameParser parserName = lib.getGeographicNameParser();
        final GeographicCodeParser parserCode = lib.getGeographicCodeParser();
        final UserGeoType userGeo = this.requestBeingEdited.getCriteria().getUserGeo();

        boolean useCode = false;
        if (userGeo.getGeoType().equals(GeographyEnumType.ZIP))
        {
            useCode = true;
        }

        final List rUserGeoItem = userGeo.getUserGeoItem();
        for (final Iterator iUserGeoItem = rUserGeoItem.iterator(); iUserGeoItem.hasNext();)
        {
            final String sUserGeoItem = (String)iUserGeoItem.next();
            final DatalessKey keyGeo = (DatalessKey)this.mapResolution.get(sUserGeoItem);

            final Object widgetItem = create("item");

            if (keyGeo != null)
            {
                try
                {
                    String geo;
                    if (useCode)
                    {
                        geo = parserCode.lookup(keyGeo);
                    }
                    else
                    {
                        geo = parserName.lookup(keyGeo);
                    }
                    setString(widgetItem,"text",geo);
                }
                catch (final IllegalStateException geoNotFound)
                {
                    geoNotFound.printStackTrace();
                    setString(widgetItem,"text",sUserGeoItem);
                    setIcon(widgetItem,"icon",iconQuestion);
                }
            }
            else
            {
                setString(widgetItem,"text",sUserGeoItem);
                setIcon(widgetItem,"icon",iconEx);
            }

            doGUI(new Runnable()
            {
                public void run()
                {
                    add(geoCalc,widgetItem);
                }
            });
        }
    }

    /**
     * @return
     * @throws RuntimeException
     */
    private GeographyEnumType getSelectedGeoType() throws RuntimeException
    {
        if (getBoolean(find("geoCounty"),"selected"))
        {
            return GeographyEnumType.COUNTY;
        }
        else if (getBoolean(find("geoMSA"),"selected"))
        {
            return GeographyEnumType.MSA;
        }
        else if (getBoolean(find("geoDMA"),"selected"))
        {
            return GeographyEnumType.DMA;
        }
        else if (getBoolean(find("geoState"),"selected"))
        {
            return GeographyEnumType.STATE;
        }
        else if (getBoolean(find("geoZIP"),"selected"))
        {
            return GeographyEnumType.ZIP;
        }
        else if (getBoolean(find("geoFIPS"),"selected"))
        {
            return GeographyEnumType.FIPS;
        }
        else if (getBoolean(find("geoUSCont"),"selected"))
        {
            return GeographyEnumType.CONTINENTAL;
        }
        else if (getBoolean(find("geoUSFull"),"selected"))
        {
            return GeographyEnumType.USA;
        }
        throw new RuntimeException();
    }

    private Object getGeoButton(final GeographyEnumType geoType) throws RuntimeException
    {
        if (geoType == GeographyEnumType.COUNTY)
        {
            return find("geoCounty");
        }
        else if (geoType == GeographyEnumType.MSA)
        {
            return find("geoMSA");
        }
        else if (geoType == GeographyEnumType.DMA)
        {
            return find("geoDMA");
        }
        else if (geoType == GeographyEnumType.STATE)
        {
            return find("geoState");
        }
        else if (geoType == GeographyEnumType.ZIP)
        {
            return find("geoZIP");
        }
        else if (geoType == GeographyEnumType.FIPS)
        {
            return find("geoFIPS");
        }
        else if (geoType == GeographyEnumType.CONTINENTAL)
        {
            return find("geoUSCont");
        }
        else if (geoType == GeographyEnumType.USA)
        {
            return find("geoUSFull");
        }

        throw new RuntimeException();
    }

    /**
     * 
     */
    private void clearGeoAreasGUI()
    {
        doGUI(new Runnable()
        {
            public void run()
            {
                removeAll(find("geoCalc"));
                removeAll(find("geoMatches"));
            }
        });
    }

    private void clearGeoLists()
    {
        this.mapResolution.clear();
        this.rUnparsedEntry.clear();
        doGUI(new Runnable()
        {
            public void run()
            {
                setString(find("geoUser"),"text","");
            }
        });
    }

    private void calcGeoNames(final GeographyEnumType geoType)
    {
        final Object progressCalcGeo = find("progressCalcGeo");

        final GeographicNameParser parser = lib.getGeographicNameParser();
        final List rrMatch = new ArrayList(this.rUnparsedEntry.size());

        // parse the user-input geography, and update our progress
        // bar to indicate the progress to the user
        parser.parseGeo(this.rUnparsedEntry,geoType,rrMatch,5,
            new ProgressWatcher()
            {
                public void setProgress(final int progress, final String message)
                {
                    doGUI(new Runnable()
                    {
                        public void run()
                        {
                            setInteger(progressCalcGeo,"value",progress);
                        }
                    });
                }

                public void setTotalSteps(final int totalSteps)
                {
                    doGUI(new Runnable()
                        {
                            public void run()
                            {
                                setInteger(progressCalcGeo,"maximum",Math.max(totalSteps,1));
                            }
                        });
                }
            });

        final Object geoCalc = find("geoCalc");
        final Iterator iUnparsedEntry = this.rUnparsedEntry.iterator();
        for (final Iterator irMatch = rrMatch.iterator(); irMatch.hasNext();)
        {
            final List rMatch = (List)irMatch.next();
            final String sUnparsedEntry = (String)iUnparsedEntry.next();

            final Object widgetItem = create("item");
            if (rMatch.size() == 0)
            {
                setString(widgetItem,"text",sUnparsedEntry);
                setIcon(widgetItem,"icon",iconEx);
            }
            else if (rMatch.size() == 1)
            {
                // There is one and only one match. Good!
                DatalessKey keyResolution = (DatalessKey)this.mapResolution.get(sUnparsedEntry);
                if (keyResolution == null) // if we haven't recorded the resolution yet
                {
                    final GeographicArea match = (GeographicArea)rMatch.get(0);
                    keyResolution = match.getKey();
                    this.mapResolution.put(sUnparsedEntry,keyResolution);
                }
                setString(widgetItem,"text",parser.lookup(keyResolution));
            }
            else
            {
                DatalessKey keyResolution = (DatalessKey)this.mapResolution.get(sUnparsedEntry);
                if (keyResolution == null) // if the user hasn't resolved it yet
                {
                    final GeographicArea matchTop = (GeographicArea)rMatch.get(0);
                    setString(widgetItem,"text",parser.lookup(matchTop.getKey()));
                    setIcon(widgetItem,"icon",iconQuestion);
                }
                else
                {
                    setString(widgetItem,"text",parser.lookup(keyResolution));
                    setIcon(widgetItem,"icon",iconCheck);
                }
            }
            putProperty(widgetItem,"Matches",rMatch);
            doGUI(new Runnable()
            {
                public void run()
                {
                    add(geoCalc,widgetItem);
                }
            });
        }
        doGUI(new Runnable()
        {
            public void run()
            {
                setInteger(progressCalcGeo,"value",0);
            }
        });
    }

    /**
     * @param geoType
     */
    private void calcGeoCodes(final GeographyEnumType geoType)
    {
        final Object progressCalcGeo = find("progressCalcGeo");

        final GeographicCodeParser parser = lib.getGeographicCodeParser();
        final List rrMatch = new ArrayList(this.rUnparsedEntry.size());

        // parse the user-input geography, and update our progress
        // bar to indicate the progress to the user
        parser.parseGeo(this.rUnparsedEntry,geoType,rrMatch,1,
            new ProgressWatcher()
            {
                public void setProgress(final int progress, final String message)
                {
                    doGUI(new Runnable()
                    {
                        public void run()
                        {
                            setInteger(progressCalcGeo,"value",progress);
                        }
                    });
                }

                public void setTotalSteps(final int totalSteps)
                {
                    doGUI(new Runnable()
                        {
                            public void run()
                            {
                                setInteger(progressCalcGeo,"maximum",Math.max(totalSteps,1));
                            }
                        });
                }
            });

        final Object geoCalc = find("geoCalc");
        final Iterator iUnparsedEntry = this.rUnparsedEntry.iterator();
        for (final Iterator irMatch = rrMatch.iterator(); irMatch.hasNext();)
        {
            final List rMatch = (List)irMatch.next();
            final String sUnparsedEntry = (String)iUnparsedEntry.next();

            final Object widgetItem = create("item");
            if (rMatch.size() == 0)
            {
                setString(widgetItem,"text",sUnparsedEntry);
                setIcon(widgetItem,"icon",iconEx);
            }
            else if (rMatch.size() == 1)
            {
                final GeographicAreaCode match = (GeographicAreaCode)rMatch.get(0);
                this.mapResolution.put(sUnparsedEntry,match.getKey());
                setString(widgetItem,"text",match.getCode()+" "+match.getName());
            }
            else
            {
                throw new RuntimeException();
            }
            putProperty(widgetItem,"Matches",rMatch);
            doGUI(new Runnable()
            {
                public void run()
                {
                    add(geoCalc,widgetItem);
                }
            });
        }
        doGUI(new Runnable()
        {
            public void run()
            {
                setInteger(progressCalcGeo,"value",0);
            }
        });
    }

    private void initGeoLists(final GeographyEnumType geoType)
    {
        if (lastParsedGeoType != null && !lastParsedGeoType.equals(geoType))
        {
            // Oops, they changed geography types on us... so their
            // old resolutions are no longer valid
            this.mapResolution.clear();
        }

        this.rUnparsedEntry.clear();

        final String sUser = getString(find("geoUser"),"text");
        final StringBuffer sUserFixed = new StringBuffer(sUser.length());

        final Set setUnparsedEntry = new HashSet();
        final BufferedReader inUser = new BufferedReader(new StringReader(sUser));
        try
        {
            for (String s = inUser.readLine(); s != null; s = inUser.readLine())
            {
                s = s.trim();
                if (s.length() == 0)
                {
                    continue;
                }
    
                while (setUnparsedEntry.contains(s))
                {
                    s += " [DUPLICATE]";
                }
    
                setUnparsedEntry.add(s);
    
                sUserFixed.append(s);
                sUserFixed.append("\n");
    
                this.rUnparsedEntry.add(s);
                if (!this.mapResolution.containsKey(s))
                {
                    this.mapResolution.put(s,null);
                    // TODO this could leave orphaned keys in mapResolution (is this OK?)
                }
            }
        }
        catch (final IOException shouldNotHappen)
        {
            throw new RuntimeException(shouldNotHappen);
        }
        doGUI(new Runnable()
        {
            public void run()
            {
                setString(find("geoUser"),"text",sUserFixed.toString());
            }
        });
    }

    private void initGeoListsCodes(final GeographyEnumType geoType)
    {
        if (lastParsedGeoType != null && !lastParsedGeoType.equals(geoType))
        {
            // Oops, they changed geography types on us... so their
            // old resolutions are no longer valid
            this.mapResolution.clear();
        }
        this.rUnparsedEntry.clear();

        String sUser = getString(find("geoUser"),"text");

        sUser = sUser
            .replace('i','1').replace('I','1')
            .replace('l','1').replace('L','1')
            .replace('o','0').replace('O','0')
            .replaceAll("[^0-9]"," ");

        final StringTokenizer st = new StringTokenizer(sUser);

        final StringBuffer sUserFixed = new StringBuffer(sUser.length());
        final Set setUnparsedEntry = new HashSet();
        while (st.hasMoreTokens())
        {
            String code = st.nextToken();

            while (setUnparsedEntry.contains(code))
            {
                code += " [DUPLICATE]";
            }

            setUnparsedEntry.add(code);

            sUserFixed.append(code);
            sUserFixed.append("\n");

            this.rUnparsedEntry.add(code);
            if (!this.mapResolution.containsKey(code))
            {
                this.mapResolution.put(code,null);
                // TODO this could leave orphaned keys in mapResolution (is this OK?)
            }
        }
        doGUI(new Runnable()
        {
            public void run()
            {
                setString(find("geoUser"),"text",sUserFixed.toString());
            }
        });
    }

    /**
     * 
     */
    public void showMatches()
    {
        if (this.requestBeingEdited.isFrozen())
        {
            return;
        }
        final Object geoMatches = find("geoMatches");
        removeAll(geoMatches);

        final Object geoCalc = find("geoCalc");

        final Object match = getSelectedItem(geoCalc);
        if (match == null)
        {
            return;
        }

        final List rMatch = (List)getProperty(match,"Matches");
        if (rMatch.size() == 0)
        {
            final Object widgetMatch = create("item");
            setString(widgetMatch,"text","[unknown; will ignore]");
            add(geoMatches,widgetMatch);
        }
        else
        {
            final GeographicNameParser parser = this.lib.getGeographicNameParser();
            boolean needToSelect = true;
            if (rMatch.size() == 1)
            {
                needToSelect = false;
            }
            for (final Iterator iMatch = rMatch.iterator(); iMatch.hasNext();)
            {
                final Object geoAreaOrCode = iMatch.next();
                if (geoAreaOrCode instanceof GeographicArea)
                {
                    final GeographicArea geo = (GeographicArea)geoAreaOrCode;
                    final DatalessKey keyGeo = geo.getKey();
                    final String nameFull = parser.lookup(keyGeo);
                    final Object widgetMatch = create("item");
                    setString(widgetMatch,"text",nameFull);
                    add(geoMatches,widgetMatch);
                    if (needToSelect)
                    {
                        final int iSel = getSelectedIndex(geoCalc);
                        final String sUnparsedEntry = (String)this.rUnparsedEntry.get(iSel);
                        DatalessKey keyResolution = (DatalessKey)this.mapResolution.get(sUnparsedEntry);
                        if (keyResolution != null)
                        {
                            if (keyGeo.equals(keyResolution))
                            {
                                setBoolean(widgetMatch,"selected",true);
                                needToSelect = false;
                            }
                        }
                    }
                }
                else
                {
                    final GeographicAreaCode geo = (GeographicAreaCode)geoAreaOrCode;
                    final Object widgetMatch = create("item");
                    setString(widgetMatch,"text",geo.getCode()+" "+geo.getName());
                    add(geoMatches,widgetMatch);
                }
            }
        }
    }

    /**
     * 
     */
    public void resolveGeo()
    {
        // find the area the user is resolving; it is the
        // currently selected item in the geoCalc list
        final Object geoCalc = find("geoCalc");
        final int iMatch = getSelectedIndex(geoCalc);
        if (iMatch < 0)
        {
            // shouldn't happen, but if it does we don't know which
            // area to resolve anyway, so just do nothing
            return;
        }

        // get the list of matches
        final Object match = getSelectedItem(geoCalc);
        final List rMatch = (List)getProperty(match,"Matches");
        if (rMatch.size() <= 1)
        {
            // if there's only one, don't let them choose it
            return;
        }

        // get the corresponding original entry, which is our key
        // into the resolutions map
        final String sUnparsedEntry = (String)this.rUnparsedEntry.get(iMatch);

        // get the index of the match that the user is choosing
        // (this will be the "resolution" for the given original entry)
        final Object geoMatches = find("geoMatches");
        final int iResolution = getSelectedIndex(geoMatches);
        if (iResolution < 0)
        {
            // -1 means no selection, so clear any pre-exising resolution
            this.mapResolution.put(sUnparsedEntry,null);
            // TODO setString(match,"text",parser.lookup(keyTopMatch));
            setIcon(match,"icon",iconQuestion);
            setDirty();
        }
        else if (rMatch.size() <= iResolution)
        {
            // this can happen for "unknown" matches
            return;
        }
        else
        {
            // get the chosen match's geography key, and add it to our map of resolutions
            final GeographicArea geoArea = (GeographicArea)rMatch.get(iResolution);
            final DatalessKey keyGeo = geoArea.getKey();
            this.mapResolution.put(sUnparsedEntry,keyGeo);
            final GeographicNameParser parser = this.lib.getGeographicNameParser();
            setString(match,"text",parser.lookup(keyGeo));
            setIcon(match,"icon",iconCheck);
            setDirty();
        }
    }

    /**
     * Executes the given task on Swing's Event Dispatch
     * thread, and waits for the task to finish. Note
     * that if the current thread <i>is</i> the Event
     * Dispatch thread, then this method simply calls
     * <code>task.run()</code>.
     * @param task
     */
    protected static void doGUI(final Runnable task)
    {
        try
        {
            if (SwingUtilities.isEventDispatchThread())
            {
                task.run();
            }
            else
            {
                SwingUtilities.invokeAndWait(task);
            }
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when the user presses the DebugInfo button.
     */
    public void debugInfo()
    {
        final Runtime runtime = Runtime.getRuntime();
        System.out.println("total memory: "+runtime.totalMemory());
        System.out.println(" free memory: "+runtime.freeMemory());
        System.out.println("  difference: "+(runtime.totalMemory()-runtime.freeMemory()));

        System.err.println("mapResolution: ("+this.mapResolution.size()+"): ");
        for (final Iterator i = this.mapResolution.entrySet().iterator(); i.hasNext();)
        {
            final Map.Entry entry = (Map.Entry)i.next();
            String s = (String)entry.getKey();
            DatalessKey k = (DatalessKey)entry.getValue();
            System.err.println(s+": "+(k==null?"null":k.getAccess().keyAsString(k)));
        }
    }

    private GeographyEnumType geoTypeLastChosen;

    /**
     * 
     */
    public void groupGeo()
    {
        GeographyEnumType geoType = getSelectedGeoType();

        boolean isEnterableGeo = !(geoType.equals(GeographyEnumType.CONTINENTAL) || geoType.equals(GeographyEnumType.USA));
        /*
         * If we're switching to a type of geography that doesn't allow
         * any user entered codes or names (that is, the two US geographies),
         * then we are going to disable the user entry text area, so we need
         * to clear it out. But if there is already some text in it, it
         * would be lost, so we want the user to confirm this loss.
         */
        if (!isEnterableGeo)
        {
            final String sUser = getString(find("geoUser"),"text");
            boolean okToClear = true;
            if (sUser.length() > 0)
            {
                okToClear = false;
                final Object[] options = {"Discard geography","Cancel"};
                final int choice = JOptionPane.showOptionDialog(null,"This will lose your geography.",
                    "Warning",JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,null,options,options[1]);
                if (choice == 0)
                {
                    okToClear = true;
                }
            }
            if (okToClear)
            {
                if (this.geoTypeLastChosen != null && !this.geoTypeLastChosen.equals(geoType))
                {
                    setDirty();
                }
                clearGeoAreasGUI();
                clearGeoLists();
            }
            else
            {
                // oops, go back to the previous geography type selection
                setBoolean(getGeoButton(geoType),"selected",false);
                geoType = this.geoTypeLastChosen;
                setBoolean(getGeoButton(geoType),"selected",true);
                isEnterableGeo = !(geoType.equals(GeographyEnumType.CONTINENTAL) || geoType.equals(GeographyEnumType.USA));
            }
        }
        else
        {
            if (this.geoTypeLastChosen != null && !this.geoTypeLastChosen.equals(geoType))
            {
                setDirty();
            }
        }

        setBoolean(find("geoUser"),"enabled",isEnterableGeo);

        this.geoTypeLastChosen = geoType;
    }

    /**
     * Submits the count queries to the database.
     */
    public void runCount()
    {
        final EpanCountRequest requestToRun = this.requestBeingEdited;
        this.rRunningCounts.add(requestToRun);
        final Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    runCountThread(requestToRun);
                }
                catch (final Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    protected void runCountThread(final EpanCountRequest requestToRun) throws ClassCastException, InterruptedException, CloneNotSupportedException, ConcurrentModificationException, JAXBException, DatalessKeyAccessCreationException, SQLException, LookupException
    {
        saveThread(true);

        requestToRun.run(new EpanCountDoneListener()
        {
            public void done(final EpanCount epanCount)
            {
                EpanCountPrototypeGUI.this.monitor.forceCheckNow();
            }
            public void allDone(final EpanCountRequest epanCountRequest)
            {
                EpanCountPrototypeGUI.this.rRunningCounts.remove(epanCountRequest);
            }
        });
    }

    /**
     * Marks the current request as "dirty" (changed).
     */
    public void setDirty()
    {
        if (this.requestBeingEdited.isFrozen())
        {
            return;
        }

        this.dirty = true;
    }

    /**
     * Called when the user presses the "Full report" button
     * @throws JAXBException
     */
    public void reportToClipboard() throws JAXBException
    {
        if (this.dirty)
        {
            getRequestFromScreen();
        }
        final StringBuffer sb = new StringBuffer(8*1024);
        appendReport(sb);
        copyToClipboard(sb);
    }

    /**
     * Called when the user presses the "Tab delim." button
     * @throws JAXBException
     */
    public void csvToClipboard() throws JAXBException
    {
        if (this.dirty)
        {
            getRequestFromScreen();
        }
        final StringBuffer sb = new StringBuffer(2*1024);
        appendCounts(sb);
        copyToClipboard(sb);
    }

    protected void appendReport(final StringBuffer sb)
    {
        this.lib.getCountReportBuilder().appendReport(this.requestBeingEdited,sb);
    }

    protected void appendCounts(final StringBuffer sb)
    {
        for (final Iterator iCount = this.requestBeingEdited.iterator(); iCount.hasNext();)
        {
            final EpanCount count = (EpanCount)iCount.next();

            final String nameOrig = count.getName().trim();
            final String nameDoubleUpQuotes = nameOrig.replaceAll("\"","\"\"");

            // format: "nameWithAnyQuotesDoubled"\tcount\n
            sb.append("\"");
            sb.append(nameDoubleUpQuotes);
            sb.append("\"\t");
            if (count.completedSuccessfully())
            {
                sb.append(Long.toString(count.getCount()));
            }
            sb.append("\n");
        }
    }

    protected void copyToClipboard(final StringBuffer source)
    {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        final StringSelection csv = new StringSelection(source.toString());
        clipboard.setContents(csv,csv);
    }

    /**
     * Called when the user presses the Abort button.
     */
    public void abort()
    {
        int iRunning = this.rRunningCounts.indexOf(this.requestBeingEdited);
        if (iRunning < 0)
        {
            System.err.println("Cannot find request to abort.");
            return;
        }

        setBoolean(find("abortCount"),"enabled",false);
        final EpanCountRequest requestRunning = (EpanCountRequest)this.rRunningCounts.get(iRunning);
        requestRunning.abort();
    }

    /**
     * Called when the user presses the XDem button.
     */
    public void xdem()
    {
        final XdemCriteria criteria = this.requestBeingEdited.getXdemCriteria();
        criteria.setShowingCommands(criteria.isEmpty());
        final XdemDialog xdem = XdemDialog.create((Frame)getParent(),criteria);
        xdem.show();

        if (this.requestBeingEdited.isFrozen())
        {
            return;
        }

        XdemCriteria criteriaMod = xdem.getCriteria();
        if (criteriaMod == null) // just to be extra cautious
        {
            criteriaMod = new XdemCriteria();
        }
        this.requestBeingEdited.setXdemCriteria(criteriaMod);
        setString(find("xdemLabel"),"text",criteriaMod.isEmpty() ? "" : "(has XDem)");
        setDirty();
    }

    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#itemChanged(com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem)
     */
    public void itemChanged(final MonitorableItem item)
    {
        final MonitorableItemDefault itemChanged = (MonitorableItemDefault)item;
        final Object itemInList = findItemInTreeOrNull(itemChanged.getPK());

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

    /**
     * @see com.surveysampling.emailpanel.counts.api.list.monitor.ChangeListener#exception(com.surveysampling.emailpanel.counts.api.list.monitor.exception.MonitoringException)
     */
    public void exception(final MonitoringException e)
    {
        System.err.println("exception:");
        e.printStackTrace();
    }

    /**
     * Called when the user chooses the menu item File/Preferences.
     * @throws IOException
     * @throws SQLException
     */
    public void preferences() throws IOException, SQLException
    {
        if (this.dirty)
        {
            JOptionPane.showMessageDialog(this,"You must SAVE or CANCEL your changes first.","Save or Cancel",JOptionPane.WARNING_MESSAGE);
            return;
        }

        final Object dlgPrefs = parse("/prefs.xml");

        final Object listUsers = find(dlgPrefs,"users");

        final UserAccess userAccess = this.lib.getUserAccess();
        final SortedSet rUser = new TreeSet();
        userAccess.getUsers(rUser);

        for (final Iterator iUser = rUser.iterator(); iUser.hasNext();)
        {
            final User user = (User)iUser.next();

            final Object widgetUser = create("item");

            final StringBuffer text = new StringBuffer(64);
            text.append(user.getNameLast());
            text.append(", ");
            text.append(user.getNameFirst());
            text.append(" (");
            text.append(user.getDept());
            text.append(")");
            setString(widgetUser,"text",text.toString());

            setBoolean(widgetUser,"selected",user.isSeen());

            putProperty(widgetUser,"userObj",user);

            add(listUsers,widgetUser);
        }

        add(dlgPrefs);
    }

    /**
     * Called when the users presses the OK button on the Preferences dialog.
     * @param dlg
     * @throws SQLException
     * @throws JAXBException
     * @throws DatalessKeyAccessCreationException
     */
    public void prefsOK(final Object dlg) throws SQLException, JAXBException, DatalessKeyAccessCreationException
    {
        this.monitor.close();

        final Object listUsers = find(dlg,"users");

        final Object[] rItemUser = getItems(listUsers);
        final List rUser = new ArrayList(rItemUser.length);
        for (int iItem = 0; iItem < rItemUser.length; ++iItem)
        {
            final Object widgetUser = rItemUser[iItem];
            final User user = (User)getProperty(widgetUser,"userObj");
            user.setSeen(getBoolean(widgetUser,"selected"));
            rUser.add(user);
        }
        remove(dlg);

        final UserAccess userAccess = this.lib.getUserAccess();
        userAccess.setSeenUsers(rUser);

        synchronized (this.lockListChange)
        {
            removeAll(find("listToday"));
            removeAll(find("listRecent"));
            removeAll(find("listOld"));

            final MonitorableSet monset = this.monitor.getMonitorableSet();
            this.monitor = new ChangeMonitor(monset,CHECK_EVERY_N_SECONDS);
            this.monitor.addChangeListener(this);
            this.monitor.forceCheckNow();

            this.requestBeingEdited = this.lib.createEpanCountRequest();
            putRequestToScreen();
            addCurrentToList();
            selectCurrentInList(true);
            this.dirty = false;
        }
    }

    /**
     * Called when the users presses the Cancel button on the Preferences dialog.
     * @param dlg
     */
    public void prefsCancel(final Object dlg)
    {
        remove(dlg);
    }
}
