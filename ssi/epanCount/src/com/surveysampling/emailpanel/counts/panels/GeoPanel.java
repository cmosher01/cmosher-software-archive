/*
 * Created on Jul 18, 2005
 *
 */
package com.surveysampling.emailpanel.counts.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.Main;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.geography.GeographicArea;
import com.surveysampling.emailpanel.counts.api.geography.GeographicAreaCode;
import com.surveysampling.emailpanel.counts.api.geography.GeographicNameParser;
import com.surveysampling.emailpanel.counts.data.CriterionBuilder;
import com.surveysampling.emailpanel.counts.renderer.CountsListCellRenderer;
import com.surveysampling.util.key.DatalessKey;

/**
 * The GeoPanel
 * @author james
 *
 */
public class GeoPanel extends JPanel implements ActionListener, ItemListener, ListSelectionListener
{
    private final ImageIcon iconQuestion = Main.createImageIcon("/icon/unknown.gif");
    private final ImageIcon iconCheck = Main.createImageIcon("/icon/resolved.gif");
    JRadioButton zipButton = new JRadioButton("ZIP");
    JRadioButton fipsButton = new JRadioButton("FIPS");
    JRadioButton countyButton = new JRadioButton("County Name");
    JRadioButton msaButton = new JRadioButton("MSA");
    JRadioButton dmaButton = new JRadioButton("DMA");
    JRadioButton stateButton = new JRadioButton("State name");
    JRadioButton continentalUSButton = new JRadioButton("Continental U.S.");
    JRadioButton fullButton = new JRadioButton("Full U.S.");
    JButton calcGeoButton = new JButton(">>");
    
    JLabel geoUserLabel = new JLabel(" enter geos (or Ctrl+V to paste) ");
    JLabel geoCalcLabel = new JLabel("geos matched ");
    JLabel geoMatchesLabel = new JLabel("choose geo ");
	
    JList geoCalcList = new JList();
    JTextArea geoUserTextArea = new JTextArea(5,10);
    JList geoMatchesList = new JList();
    JScrollPane textScroll;
    CountsGUI cg;
    private boolean geoTypeHasChanged;
    private boolean internalChange;
	
    public GeoPanel(CountsGUI countsGUI)
    {
        super();
        cg = countsGUI;
        setLayout(new BorderLayout());
        
        JPanel first = new JPanel();
        first.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTHWEST;
        c.ipadx = 0;
        c.ipady = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.BOTH;
        first.add(new JLabel(" geo: "), c);
        c.gridx = 1;
        first.add(zipButton, c);
        c.gridx = 2;
        first.add(fipsButton, c);
        c.gridx = 3;
        first.add(countyButton, c);
        c.gridx = 4;
        first.add(msaButton, c);
        c.gridx = 5;
        first.add(dmaButton, c);
        c.gridx = 6;
        first.add(stateButton, c);
        c.gridx = 7;
        first.add(continentalUSButton, c);
        c.gridx = 8;
        first.add(fullButton, c);
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(zipButton);
        bg.add(fipsButton);
        bg.add(countyButton);
        bg.add(msaButton);
        bg.add(dmaButton);
        bg.add(stateButton);
        bg.add(continentalUSButton);
        bg.add(fullButton);
        continentalUSButton.setSelected(true);
        

        zipButton.addItemListener(this);
        fipsButton.addItemListener(this);
        countyButton.addItemListener(this);
        msaButton.addItemListener(this);
        dmaButton.addItemListener(this);
        stateButton.addItemListener(this);
        continentalUSButton.addItemListener(this);
        fullButton.addItemListener(this);
        
        JPanel area1Panel = new JPanel();
        area1Panel.setLayout(new BorderLayout());
        area1Panel.add(geoUserLabel, BorderLayout.NORTH);
        textScroll = new JScrollPane (geoUserTextArea);
        geoUserTextArea.setFont(geoCalcList.getFont());
        geoUserTextArea.setMargin(new Insets(1,2,0,0));
        textScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        area1Panel.add(textScroll, BorderLayout.CENTER);
        area1Panel.add(calcGeoButton, BorderLayout.EAST);
        calcGeoButton.addActionListener(this);
        
        JPanel area2Panel = new JPanel();
        area2Panel.setLayout(new BorderLayout());
        area2Panel.add(geoCalcLabel, BorderLayout.NORTH);
        JScrollPane geoCalcScroll = new JScrollPane (geoCalcList);
        area2Panel.add(geoCalcScroll, BorderLayout.CENTER);
        geoCalcScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        CountsListCellRenderer clcr = new CountsListCellRenderer();
    
        geoCalcList.addListSelectionListener(this);
        geoCalcList.setCellRenderer(clcr);
        geoCalcList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel area3Panel = new JPanel();
        area3Panel.setLayout(new BorderLayout());
        area3Panel.add(geoMatchesLabel, BorderLayout.NORTH);
        
        JScrollPane geoMatchesScroll = new JScrollPane (geoMatchesList);
        area3Panel.add(geoMatchesScroll, BorderLayout.CENTER);
        geoMatchesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        geoMatchesList.addListSelectionListener(this);
        geoMatchesList.setCellRenderer(clcr);
        geoMatchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1,3));
        bottom.add(area1Panel);
        bottom.add(area2Panel);
        bottom.add(area3Panel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(bottom);//lists

        JPanel dummyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0));
        dummyPanel.add(first);//buttons, keep to the left
        
        add(dummyPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }
    
    /**
     * Change the button status. Also change the boolean that this is an internal
     * change. The boolean is used in the itemStateChanged method, to check if 
     * the change was internal or done by the user. If it is internal, the no reason
     * to go through the itemStateChanged method.
     * 
     * @param selected  button is selected
     * @param button    the button to change
     */
    public void changeButtonStatus(boolean selected, JRadioButton button)
    {
        internalChange = true;
        button.setSelected(selected);
        internalChange = false;
    }
    /**
     * @return Returns true if continentalUS is selected
     */
    public boolean isCont()
    {
        return continentalUSButton.isSelected();
    }
    /**
     * @param cont Set the continentalUSButton to
     * 				true or false
     */
    public void setCont(boolean cont)
    {
        changeButtonStatus(cont, continentalUSButton);
    }
    /**
     * @return Returns if county has been selected.
     */
    public boolean isCounty()
    {
        return countyButton.isSelected();
    }
    /**
     * @param country Set the country to true or false
     */
    public void setCounty(boolean country)
    {
        changeButtonStatus(country, countyButton);
    }
    /**
     * @return Returns if dma has been selected
     */
    public boolean isDma()
    {
        return dmaButton.isSelected();
    }
    /**
     * @param dma Set the dma to true or false
     */
    public void setDma(boolean dma)
    {
        changeButtonStatus(dma, dmaButton);
    }
    /**
     * @return Returns if fips is selected
     */
    public boolean isFips()
    {
        return fipsButton.isSelected();
    }
    /**
     * @param fips Set the fips to true or false
     */
    public void setFips(boolean fips)
    {
        changeButtonStatus(fips, fipsButton);
    }
    /**
     * @return Returns if full has been selected
     */
    public boolean isFull()
    {
        return fullButton.isSelected();
    }
    /**
     * @param full Set the full to true or false
     */
    public void setFull(boolean full)
    {
        changeButtonStatus(full, fullButton);
    }
    /**
     * @return Returns if MSA has been selected
     */
    public boolean isMsa()
    {
        return msaButton.isSelected();
    }
    /**
     * @param msa Set the msa to true or false
     */
    public void setMsa(boolean msa)
    {
        changeButtonStatus(msa, msaButton);
    }
    /**
     * @return Returns if state has been selected
     */
    public boolean isState()
    {
        return stateButton.isSelected();
    }
    /**
     * @param state set the state to true or false
     */
    public void setState(boolean state)
    {
        changeButtonStatus(state, stateButton);
    }
    /**
     * @return Returns if the zip has been selected
     */
    public boolean isZip()
    {
        return zipButton.isSelected();
    }
    /**
     * @param zip Set the zip to true or false
     */
    public void setZip(boolean zip)
    {
        changeButtonStatus(zip, zipButton);
    }

    /**
     * Remove all the data from both set of Lists
     * geoMatchesList and geoCalcList
     */
    public void removeFromLists()
    {
    	/* remove the listener since when clearing the list, a selection change
    	 * is fired and we know this is not a selection event.
    	 */
    	geoMatchesList.removeListSelectionListener(this);
        geoMatchesList.setListData(new Vector());
        geoMatchesList.addListSelectionListener(this);
        geoCalcList.setListData(new Vector());
        geoCalcList.repaint();
        geoCalcList.revalidate();
    }
    
    /**
     * Set the text of geoUserTextArea. This is the textArea
     * where users enter geoTypes.
     * @param text
     */
    public void setGeoUser(String text)
    {
        geoUserTextArea.setText(text);
        geoUserTextArea.setCaretPosition(0);
    }
    
    /**
     * @return	the text of the geoUser
     */
    public String getGeoUser()
    {
        return geoUserTextArea.getText();
    }

    /**
     * @param enable	enable or disable the 
     * 					geoUserTextArea
     */
    public void setGeoUserEnable(boolean enable)
    {
        geoUserTextArea.setEnabled(enable);
        textScroll.setEnabled(enable);
    }

    /**
     * @param enable	enable or disable all 
     * 					the radio buttons
     */
    public void setFieldsEnable(boolean enable)
    {
        zipButton.setEnabled(enable);
        fipsButton.setEnabled(enable);
        countyButton.setEnabled(enable);
        msaButton.setEnabled(enable);
        dmaButton.setEnabled(enable);
        stateButton.setEnabled(enable);
        continentalUSButton.setEnabled(enable);
        fullButton.setEnabled(enable);
        if (enable)
        	geoUserLabel.setText(" enter geos (or Ctrl+V to paste) ");
        else
        	geoUserLabel.setText(" geos entered ");
        geoUserLabel.setEnabled(enable);
        geoCalcLabel.setEnabled(enable);
        geoMatchesLabel.setEnabled(enable);
    }

    /**
     * Set which button is selected
     * @param geoType	The geoType to set
     * @param select	true - select the button
     * 					otherwise, deselect
     */
    public void setButton(GeographyEnumType geoType, boolean select) throws RuntimeException
    {
    	if (geoType == GeographyEnumType.COUNTY)
        {
            countyButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.MSA)
        {
            msaButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.DMA)
        {
            dmaButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.STATE)
        {
            stateButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.ZIP)
        {
            zipButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.FIPS)
        {
            fipsButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.CONTINENTAL)
        {
            continentalUSButton.setSelected(select);
            return;
        }
        else if (geoType == GeographyEnumType.USA)
        {
            fullButton.setSelected(select);
            return;
        }
        throw new RuntimeException();
    }

    /**
     * @return	return which geoType has been selected
     * @throws RuntimeException
     */
    public GeographyEnumType getSelectedGeoType() throws RuntimeException
    {
        if (isCounty())
        {
            return GeographyEnumType.COUNTY;
        }
        else if (isMsa())
        {
            return GeographyEnumType.MSA;
        }
        else if (isDma())
        {
            return GeographyEnumType.DMA;
        }
        else if (isState())
        {
            return GeographyEnumType.STATE;
        }
        else if (isZip())
        {
            return GeographyEnumType.ZIP;
        }
        else if (isFips())
        {
            return GeographyEnumType.FIPS;
        }
        else if (isCont())
        {
            return GeographyEnumType.CONTINENTAL;
        }
        else if (isFull())
        {
            return GeographyEnumType.USA;
        }
        throw new RuntimeException();
    }
    /**
     * @return Returns the geoCalcList.
     */
    public JList getGeoCalcList()
    {
        return geoCalcList;
    }

    /**
     * Handles the action
     * - ">>" - calculate/evaluate the inputted geos from
     * 			the geoUserTextArea
     * - a radioButton - change geoType 
     */
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equalsIgnoreCase(">>"))
            cg.calcGeo(false);
    }

    /**
     * Handles the action if the selected radio button changed
     */
    public void itemStateChanged(ItemEvent itemEvent)
    {
        //internalChange indicates that the radio button was selected
        // through internal methods. The user did not make the change.
        if ((itemEvent.getStateChange()==ItemEvent.SELECTED)&&(!internalChange))
        {
            geoTypeHasChanged = true;
            cg.setDirty(true);
            CriterionBuilder cb = cg.getCb();
            cb.groupGeo();
            geoTypeHasChanged = false;
        }
    }
    /**
     * Handles a selection change on either list
     * geoCalcList and geoMatchesList
     */
    public void valueChanged(ListSelectionEvent selectionEvent)
    {
        JList selectedList = (JList) selectionEvent.getSource();
        if (selectedList.equals(geoCalcList))
            showMatches();
        else if (selectedList.equals(geoMatchesList))
            resolveGeo();
    }

    /**
     * Show the matches that pertain to what was selected
     * in the geoCalcList. geoMatchesList does not use a ListModel.
     * A model is not needed, since the data is stored in a vector
     * and displayed out on the JList. DefaultListModel implements
     * ListModel in the same way.
     */
    private void showMatches()
    {
	    Vector listData = new Vector();
		if (cg.getRequestBeingEdited().isFrozen())
		{
		    return;
		}
		final JLabel match = (JLabel) geoCalcList.getSelectedValue();
		if (match == null)
		{
		    return;
		}

		final List rMatch = (List)match.getClientProperty("Matches");
		if (rMatch == null || rMatch.size() == 0)
		{
		    final String text = "[unknown; will ignore]";
		    listData.add(text);
		    geoMatchesList.setListData(listData);
		}
		else
		{
		    final GeographicNameParser parser = cg.getLib().getGeographicNameParser();
		    boolean needToSelect = true;
		    if (rMatch.size() == 1)
		    {
		        needToSelect = false;
		    }
		    String valueToSelect = null;
		    for (final Iterator iMatch = rMatch.iterator(); iMatch.hasNext();)
		    {
		        final Object geoAreaOrCode = iMatch.next();
		        if (geoAreaOrCode instanceof GeographicArea)
		        {
		            final GeographicArea geo = (GeographicArea)geoAreaOrCode;
		            final DatalessKey keyGeo = geo.getKey();
		            final String nameFull = parser.lookup(keyGeo);
		            listData.add(nameFull);
		            if (needToSelect)
		            {
		                final int iSel = geoCalcList.getSelectedIndex();
		                final String sUnparsedEntry = (String)cg.getCb().getRUnparsedEntry().get(iSel);
		                DatalessKey keyResolution = (DatalessKey)cg.getCb().getMapResolution().get(sUnparsedEntry);
		                if (keyResolution != null)
		                {
		                	/*
		                	 * If a value has already been resolved for this
		                	 * selectedItem, then store the object and select
		                	 * after all the data has been added to the list
		                	 */
		                    if (keyGeo.equals(keyResolution))
		                    {
		                    	valueToSelect = nameFull;
		                        needToSelect = false;
		                    }
		                }
		            }
		        }
		        else
		        {
		            final GeographicAreaCode geo = (GeographicAreaCode)geoAreaOrCode;
		            final String text = geo.getCode()+" "+geo.getName();
		            listData.add(text);
		        }
		    }
		    geoMatchesList.setListData(listData);
		    if (valueToSelect!=null)//select the stored item, now that the data has been added
				geoMatchesList.setSelectedValue(valueToSelect, true);
		}
    }
    
    /**
     * After the user has resolved the geoType, change the
     * text of the selected item in the geoCalcList to the
     * resolved geoType text.
     */
    public void resolveGeo()
    {
    	// find the area the user is resolving; it is the
		// currently selected item in the geoCalc list
		final int iMatch = geoCalcList.getSelectedIndex();
		if (iMatch < 0)
		{
		    // shouldn't happen, but if it does we don't know which
		    // area to resolve anyway, so just do nothing
		    return;
		}

		// get the list of matches
		final JLabel match = (JLabel) geoCalcList.getSelectedValue();
		final List rMatch = (List)match.getClientProperty("Matches");
		if (rMatch==null || rMatch.size() <= 1)
		{
		    // if there's only one, don't let them choose it
		    return;
		}

		// get the corresponding original entry, which is our key
		// into the resolutions map
		final String sUnparsedEntry = (String)cg.getCb().getRUnparsedEntry().get(iMatch);

		// get the index of the match that the user is choosing
		// (this will be the "resolution" for the given original entry)
		
		final int iResolution = geoMatchesList.getSelectedIndex();
		if (iResolution < 0)
		{
		    // -1 means no selection, so clear any pre-exising resolution
		    cg.getCb().getMapResolution().put(sUnparsedEntry,null);
		    //match.setText(parser.lookup(keyTopMatch));
		    match.setIcon(iconQuestion);
		    cg.setDirty(true);
		}
		else if (rMatch.size() <= iResolution)
		{
		    // this can happen for "unknown" matches
		    return;
		}
		else
		{
		    // get the chosen match's geography key, and add it to our map of resolutions
		    final GeographicArea geoArea = (GeographicArea)rMatch.get(iResolution);
		    final DatalessKey keyGeo = geoArea.getKey();
		    cg.getCb().getMapResolution().put(sUnparsedEntry,keyGeo);
		    final GeographicNameParser parser = cg.getLib().getGeographicNameParser();
		    match.setText(parser.lookup(keyGeo));
		    match.setIcon(iconCheck);
		    cg.setDirty(true);
		    geoCalcList.repaint();
		}
    }

	public JLabel getGeoUserLabel()
	{
		return geoUserLabel;
	}

    public boolean isGeoTypeHasChanged()
    {
        return geoTypeHasChanged;
    }
    
}
