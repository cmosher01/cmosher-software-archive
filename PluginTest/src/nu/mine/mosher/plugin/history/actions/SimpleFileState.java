/*
 * Created on Feb 10, 2005
 */
package nu.mine.mosher.plugin.history.actions;

import java.io.InputStream;

import org.eclipse.core.resources.IEncodedStorage;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

/**
 * @author Chris Mosher
 */
public class SimpleFileState implements IFileState, IAdaptable, IEncodedStorage
{
    private final InputStream mContents;
    private final IPath mPath;
    private final long mTime;

    public SimpleFileState(IPath path, InputStream contents, long time)
    {
        this.mPath = path;
        this.mContents = contents;
        this.mTime = time;
    }

    public boolean exists()
    {
        return true;
    }

    public InputStream getContents()
    {
        return mContents;
    }

    public IPath getFullPath()
    {
        return mPath;
    }

    public long getModificationTime()
    {
        return mTime;
    }

    public String getName()
    {
        return mPath.toFile().getName();
    }

    public boolean isReadOnly()
    {
        return true;
    }

    public String getCharset()
    {
        return null;
    }

    public Object getAdapter(Class adapter)
    {
        return null;
    }
}
