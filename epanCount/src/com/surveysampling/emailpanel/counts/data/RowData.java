/*
 * Created on Aug 9, 2005
 *
 */
package com.surveysampling.emailpanel.counts.data;

import java.util.ArrayList;

/**
 * This class holds all the data
 * that is contained in the results
 * table of the Counts application
 * 
 * @author james
 *
 */
public class RowData
{

    ArrayList rows;
    
    /**
     * Constructor
     * rows represent the individual rows. Each row
     * contains 2 columns. The 2 columns are represented
     * also with an ArrayList
     */
    public RowData()
    {
        rows = new ArrayList();
    }
    /**
     * This is used primarily for the table model
     * 
     * @return the number of rows
     */
    public int getLength()
    {
        return rows.size();
    }
    
    /**
     * Add a row to the end of the list of rows. 
     * Each row consists of an ArrayList of length 2.
     * 
     * @param value
     * @param col
     */
    public void addRowToEnd(Object value, int col)
    {
        ArrayList columns = new ArrayList(2);
        columns.add("");
        columns.add("");
        columns.set(col,value);
        rows.add(columns);
    }
    
    /**
     * 
     * @param row
     * @param col
     * @return	the value at row, col
     */
    public Object getValueAt(int row, int col)
    {
        ArrayList dataRow = (ArrayList) rows.get(row);//get the row
        return dataRow.get(col);//return row, col
    }
    
    /**
     * At (row,col) set the value.
     * 
     * @param value
     * @param row
     * @param col
     */
    public void setValueAt(Object value, int row, int col)
    {
        ArrayList column = (ArrayList) rows.get(row);
        column.set(col, value);
    }

    /**
     * Construct a brand new ArrayList.
     */
    public void removeAllData()
    {
        rows = new ArrayList();
    }
}
