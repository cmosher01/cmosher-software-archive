/*
 * Created on Jul 20, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.surveysampling.emailpanel.counts.CountsGUI;

/**
 * @author james
 *
 */
public class RightPanel extends JPanel
{
    RunPanel runPanel;
    InformationPanel ip;
    ResultsPanel resultsPanel;
    CountsGUI cg;
    GeoPanel geoPanel;
    JSplitPane bottomSplit;
    
    public RightPanel(CountsGUI countsGUI)
    {
    	setLayout(new BorderLayout());
    	cg = countsGUI;
        runPanel = new RunPanel(cg);
        ip = new InformationPanel(cg);
        resultsPanel = new ResultsPanel(cg);
        
        geoPanel = new GeoPanel(cg);
        
        JPanel runResultPanel = new JPanel();
        runResultPanel.setLayout(new BorderLayout());
        runResultPanel.add(runPanel, BorderLayout.NORTH);
        runResultPanel.add(resultsPanel, BorderLayout.CENTER);

        bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, geoPanel, runResultPanel);
        DividerAdjuster.addAdjusterToSplitPane(bottomSplit,.5);

        JScrollPane ipScroll = new JScrollPane(ip);
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, ipScroll,bottomSplit);
        
        mainSplit.setDividerLocation(ip.getPreferredSize().height+mainSplit.getDividerSize());
        add(mainSplit, BorderLayout.CENTER);
        setPreferredSize(new Dimension(1,1));
        
    }
    /**
     * 
     * @return	the AgePanel from Information Panel
     */
    public AgePanel getAgePanel()
    {
        return ip.getAgePanel();
    }
    
    /**
     * @return Returns the clientInfoPanel.
     */
    public ClientInfoPanel getClientInfoPanel()
    {
        return ip.getClientInfoPanel();
    }
    /**
     * @return Returns the educationPanel.
     */
    public EducationPanel getEducationPanel()
    {
        return ip.getEducationPanel();
    }
    /**
     * @return Returns the ethnicityPanel.
     */
    public EthnicityPanel getEthnicityPanel()
    {
        return ip.getEthnicityPanel();
    }
    /**
     * @return Returns the genderXdemPanel.
     */
    public GenderXdemPanel getGenderXdemPanel()
    {
        return ip.getGenderXdemPanel();
    }
    /**
     * @return Returns the geoPanel.
     */
    public GeoPanel getGeoPanel()
    {
        return geoPanel;
    }
    /**
     * @return Returns the incomePanel.
     */
    public IncomePanel getIncomePanel()
    {
        return ip.getIncomePanel();
    }
    /**
     * @return Returns the kidsPanel.
     */
    public KidsPanel getKidsPanel()
    {
        return ip.getKidsPanel();
    }
    /**
     * @return Returns the marriedPanel.
     */
    public MarriedPanel getMarriedPanel()
    {
        return ip.getMarriedPanel();
    }
    
    /**
     * 
     * @return	the runPanel
     */
    public RunPanel getRunPanel()
    {
        return runPanel;
    }
    
    public void setActionListener(ActionListener listener)
    {
        runPanel.setActionListener(listener);
        resultsPanel.setActionListener(listener);
    }
    /**
     * @return	the resultsPanel
     */
    public ResultsPanel getResultsPanel()
    {
        return resultsPanel;
    }

    /**
     * Adjusts a splitter window so that its divider is at a given
     * proportion of its total height, any time its total height changes.
     */
    private static class DividerAdjuster extends ComponentAdapter implements ComponentListener
    {
        static void addAdjusterToSplitPane(final JSplitPane splitter, final double proportion)
        {
            splitter.addComponentListener(new DividerAdjuster(splitter,proportion));
        }



        private final JSplitPane splitter;
        private final double proportion;

        private int heightPrevious;

        private DividerAdjuster(final JSplitPane splitter, final double proportion)
        {
            this.splitter = splitter;
            this.proportion = proportion;
            this.heightPrevious = this.splitter.getHeight();
        }

        public void componentResized(final ComponentEvent e)
        {
            final int heightCurrent = this.splitter.getHeight();
            if (heightCurrent != this.heightPrevious)
            {
                this.heightPrevious = heightCurrent;
                this.splitter.setDividerLocation(this.proportion);
            }
        }
    }
}
