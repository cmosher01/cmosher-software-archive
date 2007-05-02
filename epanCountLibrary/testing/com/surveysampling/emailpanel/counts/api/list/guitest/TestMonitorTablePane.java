/*
 * Created on April 22, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.guitest;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 * Main content pane for <code>TestMonitorTableGUI</code>.
 * 
 * @author Chris Mosher
 */
public class TestMonitorTablePane extends JPanel
{
    /**
     * @param model
     * 
     */
    public TestMonitorTablePane(final ListModel model)
    {
        super(new BorderLayout(),true);

        setOpaque(true);

        setPreferredSize(new Dimension(320,240));

        addNotify();

        JList jlist = new JList(model);

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(jlist);

        add(scrollpane,BorderLayout.CENTER);
    }

}
