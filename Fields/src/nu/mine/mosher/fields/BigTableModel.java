/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.ja2;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * TODO
 * 
 * @author chrism
 */
public class BigTableModel implements TableModel
{
    public int getRowCount()
    {
        return 100000;
    }

    public int getColumnCount()
    {
        return 1000;
    }

    public String getColumnName(int columnIndex)
    {
        return "C"+columnIndex;
    }

    public Class getColumnClass(int columnIndex)
    {
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return Integer.toHexString(rowIndex+columnIndex);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(TableModelListener l)
    {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l)
    {
        // TODO Auto-generated method stub
        
    }
}
