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
        StringBuffer sb = new StringBuffer(13);
        sb.append("C");
        sb.append(Integer.toString(columnIndex+1));
        return sb.toString();
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
        StringBuffer sb = new StringBuffer(13);
        sb.append("R");
        sb.append(Integer.toString(rowIndex+1));
        sb.append(" C");
        sb.append(Integer.toString(columnIndex+1));
        return sb.toString();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
    }

    public void addTableModelListener(TableModelListener l)
    {
    }

    public void removeTableModelListener(TableModelListener l)
    {
    }
}
