/*
 * Created on April 19, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.monitor;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Some tests of the <code>ChangeMonitor</code>.
 * 
 * @author Chris Mosher
 */
public class Main
{
    static final Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    static boolean end;

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException
    {
        final MySet myset = new MySet();
        final ChangeMonitor mon = new ChangeMonitor(myset,3);

        final ChangeListener listener = new ChangePrinter();
        mon.addChangeListener(listener);

        Thread.sleep(8000);

        System.out.println();
        System.out.println("forcing check at "+format.format(new Date()));
        mon.forceCheckNow();

        synchronized (Main.class)
        {
            while (!end)
            {
                Main.class.wait();
            }
        }
        System.out.println("closing monitor...");
        mon.close();
        System.out.println("done closing monitor.");
    }

    private static final class MySet implements MonitorableSet
    {
        private static int timesCalled;
        private static MyItem item1 = new MyItem(1);
        private static MyItem item2 = new MyItem(2);
        private static MyItem item3 = new MyItem(3);
        private static MyItem item4 = new MyItem(4);
        private static MyItem item5 = new MyItem(5);

        /**
         * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableSet#getChangesSince(com.surveysampling.emailpanel.counts.api.list.monitor.ChangeSerialNumber, java.util.Set)
         */
        public void getChangesSince(final ChangeSerialNumber lastMod, final Set setOfChangedItems)
        {
            System.out.println();
            System.out.println("looking for changes since "+lastMod.toString()+" at "+format.format(new Date()));

            ++timesCalled;
            switch (timesCalled)
            {
                case 1:
                    item1.modify();
                    setOfChangedItems.add(item1);
                    item2.modify();
                    setOfChangedItems.add(item2);
                break;

                case 2:
                    item2.delete();
                    setOfChangedItems.add(item2);
                break;

                case 3:
                    item5.modify();
                    setOfChangedItems.add(item5);
                    item4.modify();
                    setOfChangedItems.add(item4);
                break;

                case 4:
                    item4.delete();
                    setOfChangedItems.add(item4);
                break;

                case 5:
                    item3.modify();
                    setOfChangedItems.add(item3);
                break;

                default:
                    synchronized (Main.class)
                    {
                        Main.end = true;
                        Main.class.notifyAll();
                    }
            }
        }
    }

    private static final class MyItem implements MonitorableItem
    {
        private static long serialNumber = 0;
        private final int id;
        private ChangeSerialNumber lastMod;
        private boolean deleted;
        MyItem(final int id)
        {
            this.id = id;
        }
        void modify()
        {
            this.lastMod = new ChangeSerialNumber(++serialNumber);
        }
        void delete()
        {
            this.deleted = true;
            modify();
        }
        /**
         * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem#equals(java.lang.Object)
         */
        public boolean equals(final Object obj)
        {
            if (!(obj instanceof MyItem))
            {
                return false;
            }
            final MyItem that = (MyItem)obj;
            return this.id == that.id;
        }
        /**
         * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem#hashCode()
         */
        public int hashCode()
        {
            return id;
        }
        /**
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return "item #"+id+" (last mod: "+format.format(this.lastMod)+")";
        }
        /**
         * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem#isDeleted()
         */
        public boolean isDeleted()
        {
            return this.deleted;
        }
        /**
         * @see com.surveysampling.emailpanel.counts.api.list.monitor.MonitorableItem#getLastChangeSerialNumber()
         */
        public ChangeSerialNumber getLastChangeSerialNumber()
        {
            return this.lastMod;
        }
    }
}
