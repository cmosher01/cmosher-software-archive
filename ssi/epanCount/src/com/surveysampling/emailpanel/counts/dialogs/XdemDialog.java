/*
 * Created on June 14, 2005
 */
package com.surveysampling.emailpanel.counts.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.surveysampling.emailpanel.order.xdem.XdemCriteriaEntryPanel;
import com.surveysampling.emailpanel.xdem.XdemCriteria;

/**
 * A dialog to display the XDem criteria entry page.
 * 
 * @author Chris Mosher
 */
public final class XdemDialog extends JDialog
{
    private XdemCriteriaEntryPanel criteriaPanel;

    /**
     * Creates a new JDialog to display the XDem criteria
     * entry page. Callers would need to call <code>show</code>
     * on the returned dialog to display it.
     * @param owner
     * @param criteria
     * @return the new dialog ready to be shown
     */
    public static XdemDialog create(final Frame owner, final XdemCriteria criteria)
    {
        final XdemDialog dlg = new XdemDialog(owner);

        final XdemCriteriaEntryPanel xdem = new XdemCriteriaEntryPanel(criteria,new ChangeListener()
        {
            public void stateChanged(final ChangeEvent e)
            {
                dlg.dispose();
            }
        });

        dlg.setCriteriaPanel(xdem);
        dlg.setSize(720,520);
        dlg.setLocationRelativeTo(owner);

        return dlg;
    }

    private XdemDialog(final Frame owner) throws HeadlessException
    {
        super(owner,"XDem",true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
    }

    private void setCriteriaPanel(final XdemCriteriaEntryPanel criteriaPanel)
    {
        this.criteriaPanel = criteriaPanel;
        getContentPane().add(this.criteriaPanel,BorderLayout.CENTER);
    }

    /**
     * @return XDem criteria (as modified by the user)
     */
    public XdemCriteria getCriteria()
    {
        return criteriaPanel.getCriteria();
    }
}
