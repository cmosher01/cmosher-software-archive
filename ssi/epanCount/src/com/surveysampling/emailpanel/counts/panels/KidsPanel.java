/*
 * Created on Jul 19, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.surveysampling.emailpanel.counts.CountsGUI;

/**
 * The KidsPanel
 * @author james
 *
 */
public class KidsPanel extends JPanel implements ActionListener, KeyListener
{
    JTextField minAgeField = new JTextField();
    JTextField maxAgeField = new JTextField();
    JRadioButton bothButton = new JRadioButton("Both");
    JRadioButton maleButton = new JRadioButton("Male Only");
    JRadioButton femaleButton = new JRadioButton("Female Only");
    JCheckBox withoutBox = new JCheckBox("without");
    boolean without = false;
    CountsGUI cg;
    JLabel minAgeLabel = new JLabel("min");
    JLabel maxAgeLabel = new JLabel("max (years incl)");
    
    public KidsPanel(CountsGUI cg)
    {
        super();
        this.cg = cg;
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        minAgeField.setColumns(5);
        minAgeField.setName("min");
        minAgeField.addKeyListener(this);
        minAgeField.setHorizontalAlignment(JTextField.RIGHT);
        
        maxAgeField.setColumns(5);
        maxAgeField.setName("max");
        maxAgeField.addKeyListener(this);
        maxAgeField.setHorizontalAlignment(JTextField.RIGHT);
        
        panel.add(new JLabel("kids: "));
        panel.add(minAgeLabel);
        panel.add(minAgeField);
        panel.add(maxAgeField);
        panel.add(maxAgeLabel);
        
        ButtonGroup bg = new ButtonGroup();
        
        bothButton.addActionListener(this);
        maleButton.addActionListener(this);
        femaleButton.addActionListener(this);
        withoutBox.addActionListener(this);
        
        panel.add(bothButton);
        panel.add(maleButton);
        panel.add(femaleButton);
        bg.add(bothButton);
        bg.add(maleButton);
        bg.add(femaleButton);
        bothButton.setSelected(true);
        panel.add(withoutBox);
        add(panel, BorderLayout.WEST);
    }

    /**
     * @return	true if both is selected
     * 			false, otherwise
     */
    public boolean isBoth()
    {
        return bothButton.isSelected();
    }
    /**
     * @param select The both to set.
     */
    public void setBoth(boolean select)
    {
        bothButton.setSelected(select);
    }
    /**
     * @return	true, if femaleButton is selected
     * 			false, otherwise
     */
    public boolean isFemale()
    {
        return femaleButton.isSelected();
    }
    /**
     * @param select select/deselect the femaleButton
     */
    public void setFemale(boolean select)
    {
        femaleButton.setSelected(select);
    }
    /**
     * @return	true, if maleButton is selected
     * 			false, otherwise
     */
    public boolean isMale()
    {
        return maleButton.isSelected();
    }
    /**
     * @param select select/deselect maleButton
     */
    public void setMale(boolean select)
    {
        maleButton.setSelected(select);
    }
    /**
     * @return	true, if withoutBox is selected
     * 			false, otherwise
     */
    public boolean isWithout()
    {
        return withoutBox.isSelected();
    }
    /**
     * @param select select/deselect the withoutBox
     */
    public void setWithout(boolean select)
    {
        withoutBox.setSelected(select);
    }
    
    /**
     * @return the text of the maxAge field
     */
    public String getMaxAgeText()
    {
        return maxAgeField.getText();
    }
    /**
     * @param text	set the text of the maxAgeField
     */
    public void setMaxAgeText(String text)
    {
        maxAgeField.setText(text);
    }
    /**
     * @return	text of minAgeField
     */
    public String getMinAgeText()
    {
        return minAgeField.getText();
    }
    /**
     * @param set the text of the minAgeField
     */
    public void setMinAgeText(String text)
    {
        minAgeField.setText(text);
    }
    /**
     * @param enable enable/disable the fields
     */
    public void setFieldsEnable(boolean enable)
    {
        maxAgeField.setEnabled(enable);
        minAgeField.setEnabled(enable);
        withoutBox.setEnabled(enable);
        maleButton.setEnabled(enable);
        femaleButton.setEnabled(enable);
        bothButton.setEnabled(enable);
        minAgeLabel.setEnabled(enable);
        maxAgeLabel.setEnabled(enable);
    }

    /**
     * Implemented as part of KeyListener
     */
	public void keyPressed(KeyEvent e)
	{
		//do nothing
		
	}

	/**
	 * Implemented as part of KeyListener
	 */
	public void keyReleased(KeyEvent e)
	{
		// do nothing
		
	}

	/**
	 * If a key has been typed here, then set dirty flag.
	 */
	public void keyTyped(KeyEvent e)
	{
		cg.setDirty(true);
	}
    /** 
     * Handles the action of this panel.
     * "without" - handles if the without checkbox has been checked
     * 
     */
    public void actionPerformed(ActionEvent arg0)
    {
        cg.setDirty(true);
        if (arg0.getActionCommand().equals("without"))
        {
            JCheckBox cb = (JCheckBox) arg0.getSource();
            if (cb.isSelected())
                without = true;
            else
                without = false;
        }
        
    }
}
