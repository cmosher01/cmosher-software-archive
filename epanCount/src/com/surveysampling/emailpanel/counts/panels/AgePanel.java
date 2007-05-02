/*
 * Created on Jul 18, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.surveysampling.emailpanel.counts.CountsGUI;

/**
 * The AgePanel
 * @author james
 *
 */
public class AgePanel extends JPanel implements KeyListener
{
    private JTextField minAgeField, maxAgeField;
    CountsGUI cg;
    JLabel minAgeLabel = new JLabel("min");
    JLabel maxAgeLabel = new JLabel("max (years incl)");
    
    public AgePanel(CountsGUI cg)
    {
        this.cg = cg;
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.add(new JLabel("age: "));
        minAgeField = new JTextField();
        minAgeField.setColumns(5);
        minAgeField.addKeyListener(this);
        minAgeField.setName("min");
        minAgeField.setHorizontalAlignment(JTextField.RIGHT);
        
        minAgeLabel.setLabelFor(minAgeField);
        maxAgeField = new JTextField();
        maxAgeField.setColumns(5);
        maxAgeField.addKeyListener(this);
        maxAgeField.setName("max");
        maxAgeField.setHorizontalAlignment(JTextField.RIGHT);
        
        panel.add(minAgeLabel);
        panel.add(minAgeField);
        panel.add(maxAgeField);
        panel.add(maxAgeLabel);
        setPreferredSize(new Dimension(1,5));
        JPanel dummy = new JPanel();
        dummy.setLayout(new BorderLayout());
        dummy.add(panel, BorderLayout.WEST);
        add(dummy, BorderLayout.CENTER);
        setPreferredSize(new Dimension(1,30));
    }
    /**
     * @return Returns the text of the maxAgeField
     */
    public String getMaxAge()
    {
        return maxAgeField.getText();
    }
    /**
     * @param set the text of the maximumAgeField
     */
    public void setMaxAge(String max)
    {
        maxAgeField.setText(max);
    }
    /**
     * @return Returns the text of the minAgeField
     */
    public String getMinAge()
    {
        
        return minAgeField.getText();
    }
    /**
     * Set the text of the minimumAge field
     */
    public void setMinAge(String min)
    {
        minAgeField.setText(min);
    }


    /**
     * @param enable	true, enable all fields
     * 					false, disable all fields
     */
    public void setFieldsEnable(boolean enable)
    {
        minAgeField.setEnabled(enable);
        maxAgeField.setEnabled(enable);
        minAgeLabel.setEnabled(enable);
        maxAgeLabel.setEnabled(enable);
    }
    
    /**
     * Implemented for KeyListener interface
     */
	public void keyPressed(KeyEvent e)
	{
		// Does nothing
	}

	/**
	 * Implemented for KeyListener interface
	 */
	public void keyReleased(KeyEvent e)
	{
		// Does nothing
	}

	/**
	 * If a key has been typed in this field, then set the request
	 * to dirty.
	 */
	public void keyTyped(KeyEvent e)
	{
		
		cg.setDirty(true);
	}
}
