/*
 * Created on Aug 30, 2004
 */
package nu.mine.mosher;

import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class GDiffCmdListModel extends AbstractListModel implements ListModel
{
    private List rGDiffCmd;

    /**
     * 
     */
    public GDiffCmdListModel(List rGDiffCmd)
    {
        this.rGDiffCmd = rGDiffCmd;
    }

    /**
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return rGDiffCmd.size();
    }

    /**
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return rGDiffCmd.get(index);
    }

    public List list()
    {
        return Collections.unmodifiableList(rGDiffCmd);
    }
}
