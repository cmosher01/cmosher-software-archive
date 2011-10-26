/*
 * Created on Aug 28, 2004
 */
package nu.mine.mosher;

import javax.swing.DefaultListSelectionModel;



/**
 * TODO
 * 
 * @author Chris
 */
public abstract class SingleSelectionModel extends DefaultListSelectionModel
{
    /**
     * 
     */
    public SingleSelectionModel()
    {
        setSelectionMode(SINGLE_SELECTION);
    }

    @Override
	public void setSelectionInterval(int index0, int index1)
    {
        int oldIndex = getMinSelectionIndex();
        super.setSelectionInterval(index0,index1);
        int newIndex = getMinSelectionIndex();
        if (oldIndex != newIndex)
        {
            updateSingleSelection(oldIndex,newIndex);
        }
    }

    public abstract void updateSingleSelection(int oldIndex, int newIndex);
}
