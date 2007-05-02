/*
 * Created on Jul 19, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import com.surveysampling.emailpanel.counts.CountsGUI;

/**
 * The InformationPanel
 * @author james
 *
 */
public class InformationPanel extends JPanel
{

    CountsGUI cg;
    ClientInfoPanel clientInfoPanel;
    GenderXdemPanel genderXdemPanel;
    MarriedPanel marriedPanel;
    EducationPanel educationPanel;
    EthnicityPanel ethnicityPanel;
    IncomePanel incomePanel;
    KidsPanel kidsPanel;
    AgePanel agePanel;
    
    
    public InformationPanel(CountsGUI countsGUI)
    {
        cg = countsGUI;
        clientInfoPanel = new ClientInfoPanel(cg);
        genderXdemPanel = new GenderXdemPanel(cg);
        marriedPanel = new MarriedPanel(cg);
        educationPanel = new EducationPanel(cg);
        ethnicityPanel = new EthnicityPanel(cg);
        incomePanel = new IncomePanel(cg);
        kidsPanel = new KidsPanel(cg);
        agePanel = new AgePanel(cg);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = 0;
        c.ipady = 0;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        add(clientInfoPanel, c);
        c.gridy = 1;
        add(agePanel, c);
        c.gridy = 2;
        add(kidsPanel, c);
        c.gridy = 3;
        add(incomePanel, c);
        c.gridy = 4;
        add(ethnicityPanel, c);
        c.gridy = 5;
        add(educationPanel, c);
        c.gridy = 6;
        add(marriedPanel, c);
        c.gridy = 7;
        add(genderXdemPanel, c);
    }
    
    /**
     * @return Returns the agePanel.
     */
    public AgePanel getAgePanel()
    {
        return agePanel;
    }
    /**
     * @return Returns the clientInfoPanel.
     */
    public ClientInfoPanel getClientInfoPanel()
    {
        return clientInfoPanel;
    }
    /**
     * @return Returns the educationPanel.
     */
    public EducationPanel getEducationPanel()
    {
        return educationPanel;
    }
    /**
     * @return Returns the ethnicityPanel.
     */
    public EthnicityPanel getEthnicityPanel()
    {
        return ethnicityPanel;
    }
    /**
     * @return Returns the genderXdemPanel.
     */
    public GenderXdemPanel getGenderXdemPanel()
    {
        return genderXdemPanel;
    }

    /**
     * @return Returns the incomePanel.
     */
    public IncomePanel getIncomePanel()
    {
        return incomePanel;
    }
    /**
     * @return Returns the kidsPanel.
     */
    public KidsPanel getKidsPanel()
    {
        return kidsPanel;
    }
    /**
     * @return Returns the marriedPanel.
     */
    public MarriedPanel getMarriedPanel()
    {
        return marriedPanel;
    }
}
