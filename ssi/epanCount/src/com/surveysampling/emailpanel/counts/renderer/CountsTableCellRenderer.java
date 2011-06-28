/*
 * Created on Aug 8, 2005
 *
 */
package com.surveysampling.emailpanel.counts.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * @author james
 *
 */
public class CountsTableCellRenderer extends JLabel implements TableCellRenderer
{
    /**
     * 
     */
    public CountsTableCellRenderer()
    {
        super();
    }
    /* (non-Javadoc)
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            this.setOpaque(true);
            this.setBackground(new Color(255,204,102));
            this.setForeground(Color.BLACK);
        }
        else 
        {
            this.setOpaque(false);
            this.setForeground(table.getForeground());
            this.setBackground(table.getBackground());
        }
        if (column == 1)
        {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }
        else if (column == 0)
            setHorizontalAlignment(SwingConstants.LEFT);
        
        setText(" "+value.toString());
        return this;
    }
}
