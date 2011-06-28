/*
 * Created on Aug 9, 2005
 *
 */
package com.surveysampling.emailpanel.counts.model;

import javax.swing.table.AbstractTableModel;

import com.surveysampling.emailpanel.counts.data.RowData;

/**
 * The table model is used for the JTable where results
 * are shown.
 * 
 * @author james
 *
 */
public class CountsTableModel extends AbstractTableModel
{

    RowData rowData;
    
    /**
     * Construct using RowData, which contains
     * all the data found in this table.
     *
     */
    public CountsTableModel()
    {
        rowData = new RowData();
    }

    /** 
     * TableModel implementation. The number of columns
     * will always be the same.
     */
    public int getColumnCount()
    {
        return 2;
    }

    /**
     * @return the number of rows
     */
    public int getRowCount()
    {
        return rowData.getLength();
    }

    /**
     * @return the value at (row, col)
     */
    public Object getValueAt(int row, int col)
    {
        return rowData.getValueAt(row,col);
    }
    
    /**
     * Set the value of (row, col) to value
     */
    public void setValueAt(Object value, int row, int col)
    {
        if (row > rowData.getLength())
            throw new ArrayIndexOutOfBoundsException();
        if (row == rowData.getLength())
        {
            rowData.addRowToEnd(value, col);//add a row at the end
            fireTableRowsInserted(row,row);
        }
        else
            rowData.setValueAt(value, row, col);
    }

    /**
     * Removes all the data from the table
     */
    public void removeAllData()
    {
    	rowData.removeAllData();
        fireTableRowsDeleted(0, rowData.getLength());
    }
    
    /**
     * The ColumnName can only be either "Critiria"
     * or "Panelists" depending on the col number of
     * 0 or 1;
     */
    public String getColumnName(int col)
    {
        if (col == 0)
            return "Criteria";
        else
            return "Panelists";
    }
}
