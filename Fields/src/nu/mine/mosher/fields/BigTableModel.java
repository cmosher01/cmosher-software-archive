/*
 * Created on Feb 4, 2005
 */
package nu.mine.mosher.ja2;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * TODO
 * 
 * @author chrism
 */
public class BigTableModel extends DefaultTableModel
{

    /**
     * 
     */
    public BigTableModel()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param rowCount
     * @param columnCount
     */
    public BigTableModel(int rowCount, int columnCount)
    {
        super(rowCount,columnCount);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param columnNames
     * @param rowCount
     */
    public BigTableModel(Vector columnNames, int rowCount)
    {
        super(columnNames,rowCount);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param columnNames
     * @param rowCount
     */
    public BigTableModel(Object[] columnNames, int rowCount)
    {
        super(columnNames,rowCount);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param data
     * @param columnNames
     */
    public BigTableModel(Vector data, Vector columnNames)
    {
        super(data,columnNames);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param data
     * @param columnNames
     */
    public BigTableModel(Object[][] data, Object[] columnNames)
    {
        super(data,columnNames);
        // TODO Auto-generated constructor stub
    }

}
