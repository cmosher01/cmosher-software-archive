import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Provides a minimal LinkedList that's backed by a file.
 * Access is not synchronized.
 * Upon crash and restart, may again return entries that
 * were already returned (prior to the crash).
 * Even if methods throw exceptions, the BackedList object
 * will still be valid, and will just work as an in-memory list.
 */
public class BackedList
{
    private final LinkedList mList;
    private final File mFile;
    private ObjectOutputStream mOut;

    public BackedList(File file) throws IOException, ClassNotFoundException
    {
        mList = new LinkedList();
        mFile = file;
        recover();
    }

    private void recover() throws IOException, ClassNotFoundException
    {
        if (!mFile.exists())
            return;

        LinkedList listLocal = new LinkedList();

        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(mFile);
            ois = new ObjectInputStream(fis);

            while (fis.available() > 0)
            {
                Object o = ois.readObject();
                listLocal.addLast(o);
            }
        }
        finally
        {
            if (ois != null)
                try
                {
                    ois.close();
                    fis = null;
                }
                catch (Exception ignore) {}
            if (fis != null)
                try
                {
                    fis.close();
                }
                catch (Exception ignore) {}
        }

        mFile.delete();
        // if we crash here, then we can lose data
        for (Iterator i = listLocal.iterator(); i.hasNext();)
        {
            Object o = i.next();
            addLast(o);
        }
        // from here on, crashes are OK
        System.err.println("BackedList recovered from "+mFile.getAbsolutePath());
    }

    public void addLast(Object o) throws IOException
    {
        mList.addLast(o);

        if (mOut == null)
            mOut = new ObjectOutputStream(new FileOutputStream(mFile));
        mOut.writeObject(o);
        mOut.flush();
    }

    public Object removeFirst() throws IOException
    {
        Object o = mList.removeFirst();

        if (mList.isEmpty())
        {
            close();
            mFile.delete();
        }

        return o;
    }

    public Object getFirst()
    {
        return mList.getFirst();
    }

    public boolean isEmpty()
    {
        return mList.isEmpty();
    }

    public int size()
    {
        return mList.size();
    }

    public void close() throws IOException
    {
        if (mOut != null)
        {
            mOut.close();
            mOut = null;
        }
    }
}
