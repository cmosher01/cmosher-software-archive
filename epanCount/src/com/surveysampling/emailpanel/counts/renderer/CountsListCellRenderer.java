/*
 * Created on Aug 15, 2005
 *
 */
package com.surveysampling.emailpanel.counts.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author james
 *
 */
public class CountsListCellRenderer extends JLabel implements ListCellRenderer
{

    /**
     * 
     */
    public CountsListCellRenderer()
    {
        super();
        setOpaque(true);
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
    {
        if (value instanceof JLabel)//the object is in the geoCalcList
        {	
        	JLabel label = (JLabel) value;
        	setIcon(label.getIcon());
        	setText(" "+label.getText());
        }
        else //the object is the geoMatchesList
        {
        	setIcon(null);
        	setText(" "+value.toString());
        }
        if (isSelected)
        {    
            setBackground(new Color(255,204,102));
            setForeground(Color.BLACK);
        }
        else
        {
            setBackground(list.getBackground());
 	       	setForeground(list.getForeground());
        }
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return this;
    }
}
