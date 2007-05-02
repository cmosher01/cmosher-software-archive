/*
 * Created on Jul 19, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.dialogs.XdemDialog;
import com.surveysampling.emailpanel.xdem.XdemCriteria;

/**
 * Panel to display gender and xdem criteria.
 * @author james
 *
 */
public class GenderXdemPanel extends JPanel implements ActionListener
{
    JRadioButton bothButton = new JRadioButton("Both");
    JRadioButton maleButton = new JRadioButton("Male Only");
    JRadioButton femaleButton = new JRadioButton("Female Only");
    JLabel xdemLabel = new JLabel("");//The XdemLabel lets user know the status of the XdemCriteria.
    JButton xdemButton = new JButton("Xdem");
    boolean both, male, female;
    CountsGUI cg;
    public GenderXdemPanel(CountsGUI cg)
    {
        super();
        this.cg = cg;
        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 0));
        ButtonGroup bg = new ButtonGroup();
        bothButton.setSelected(true);
        bg.add(bothButton);
        bothButton.addActionListener(this);
        bg.add(maleButton);
        maleButton.addActionListener(this);
        bg.add(femaleButton);
        femaleButton.addActionListener(this);
        add(new JLabel(" sex: "));
        add(bothButton);
        add(maleButton);
        add(femaleButton);
        add(new JLabel("                              "));
        add(xdemButton);
        xdemButton.addActionListener(this);
        xdemButton.setActionCommand("Xdem");
        add(xdemLabel);
        setPreferredSize(new Dimension(1,30));
    }
    /**
     * @return	true, if bothButton is selected
     * 			false, otherwise
     */
    public boolean isBoth()
    {
        return bothButton.isSelected();
    }
    /**
     * @param select select the bothButton
     */
    public void setBoth(boolean select)
    {
        bothButton.setSelected(select);
    }
    /**
     * @return	true, if the female button is selected
     * 			false, otherwise
     */
    public boolean isFemale()
    {
        return femaleButton.isSelected();
    }
    /**
     * @param select select/deselect the female button
     */
    public void setFemale(boolean select)
    {
        femaleButton.setSelected(select);
    }
    /**
     * @return	true, if the maleButton is selected
     * 			false, otherwise
     */
    public boolean isMale()
    {
        return maleButton.isSelected();
    }
    /**
     * @param select select/deselect the maleButton
     */
    public void setMale(boolean select)
    {
        maleButton.setSelected(select);
    }
    /**
     * @param enable	enable/disable the maleButton
     */
    public void setMaleEnable(boolean enable)
    {
        maleButton.setEnabled(enable);
    }
    /**
     * @param enable	enable/disable the femaleButton
     */
    public void setFemaleEnable(boolean enable)
    {
        femaleButton.setEnabled(enable);
    }
    /**
     * @param enable	enable/disable bothButton
     */
    public void setBothEnabled(boolean enable)
    {
        bothButton.setEnabled(enable);
    }
    /**
     * The XdemLabel lets user know the status of the
     * XdemCriteria.
     * @param text	set the label to this text
     */
    public void setXdemLabel(String text)
    {
        xdemLabel.setText(text);
    }
    /**
     * @param enableXdem	enable/disable the xdemButton
     */
    public void setXdemEnable(boolean enableXdem)
    {
        xdemButton.setEnabled(enableXdem);
    }
    /** 
     * Handles the action for the panel
     * "Xdem" - Xdem is chosen
     */
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equalsIgnoreCase("Xdem"))
            xdem();
        else
        	cg.setDirty(true);
    }
    
    /**
     * Called when the user presses the XDem button.
     */
    public void xdem()
    {
        final XdemCriteria criteria = cg.getRequestBeingEdited().getXdemCriteria();
        criteria.setShowingCommands(criteria.isEmpty());
        final XdemDialog xdem = XdemDialog.create(null,criteria);
        xdem.show();

        if (cg.getRequestBeingEdited().isFrozen())
        {
            return;
        }

        XdemCriteria criteriaMod = xdem.getCriteria();
        if (criteriaMod == null) // just to be extra cautious
        {
            criteriaMod = new XdemCriteria();
        }
        cg.getRequestBeingEdited().setXdemCriteria(criteriaMod);
        
        cg.getRightPanel().getGenderXdemPanel().setXdemLabel(criteriaMod.isEmpty() ? "" : "(has XDem)");
        cg.setDirty(true);
    }
}
