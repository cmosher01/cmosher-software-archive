/*
 * Created on Jul 20, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.Main;
import com.surveysampling.emailpanel.counts.model.CountsTableModel;
import com.surveysampling.emailpanel.counts.renderer.CountsTableCellRenderer;

/**
 * @author james
 *
 */
public class ResultsPanel extends JPanel
{
    ImageIcon deleteIcon = Main.createImageIcon("/icon/delete.gif");
    JButton deleteButton = new JButton("Delete", deleteIcon);
    ImageIcon undoIcon = Main.createImageIcon("/icon/undo.gif");
    JButton cancelButton = new JButton("Cancel", undoIcon);
    ImageIcon saveIcon = Main.createImageIcon("/icon/save.gif");
    JButton saveButton = new JButton("Save", saveIcon);
    JTable table;
    CountsTableModel tableModel = new CountsTableModel();
    private JButton fullReportButton;
    private JButton tabDelimButton;
    public ResultsPanel(CountsGUI cg)
    {
        super();
        table = new JTable();
        table.setModel(tableModel);
        table.setDefaultRenderer(Object.class, new CountsTableCellRenderer());
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(100,100));
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
 
        JScrollPane sp = new JScrollPane(table);
        panel.add(sp, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        
        JPanel reportPanel = new JPanel();
        ImageIcon reportIcon = Main.createImageIcon("/icon/paste.gif");
        fullReportButton = new JButton("Full Report", reportIcon);
        reportPanel.add(fullReportButton);
        tabDelimButton = new JButton("Tab Delim", reportIcon);
        reportPanel.add(tabDelimButton);
        
        JPanel controlPanel = new JPanel();
        controlPanel.add(deleteButton);
        controlPanel.add(cancelButton);
        controlPanel.add(saveButton);
        
        buttonPanel.add(reportPanel, BorderLayout.WEST);
        buttonPanel.add(controlPanel, BorderLayout.EAST);
        JScrollPane bpScroll = new JScrollPane(buttonPanel);
        bpScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        bpScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        panel.add(buttonPanel, BorderLayout.PAGE_END);
        setPreferredSize(new Dimension(1,100));
        add(panel, BorderLayout.CENTER);
    }


    /**
     * Set the ActionListener to listener
     * @param listener
     */
    public void setActionListener(ActionListener listener)
    {
        saveButton.setActionCommand("Save");
        saveButton.addActionListener(listener);
        
        deleteButton.setActionCommand("Delete");
        deleteButton.addActionListener(listener);
        
        cancelButton.setActionCommand("Cancel");
        cancelButton.addActionListener(listener);
        
        fullReportButton.setActionCommand("Full");
        fullReportButton.addActionListener(listener);
        
        tabDelimButton.setActionCommand("Tab");
        tabDelimButton.addActionListener(listener);
    }
    /**
     * @return Returns the table.
     */
    public JTable getTable()
    {
        return table;
    }

    /**
     * Enable or disable the buttons
     * @param enable
     */
    public void setButtonsEnable(boolean enable)
    {
        saveButton.setEnabled(enable);
        cancelButton.setEnabled(enable);
    }

}
