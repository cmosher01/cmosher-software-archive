/*
 * Created on Jul 25, 2005
 *
 */
package com.surveysampling.emailpanel.counts.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.CountsGUI;
import com.surveysampling.emailpanel.counts.api.criteria.AgeType;
import com.surveysampling.emailpanel.counts.api.criteria.BreakOutType;
import com.surveysampling.emailpanel.counts.api.criteria.EducationEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.EducationType;
import com.surveysampling.emailpanel.counts.api.criteria.EpanCountCriteria;
import com.surveysampling.emailpanel.counts.api.criteria.EthnicityEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.EthnicityType;
import com.surveysampling.emailpanel.counts.api.criteria.GenderEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GenderType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeType;
import com.surveysampling.emailpanel.counts.api.criteria.MarriedEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.MarriedType;
import com.surveysampling.emailpanel.counts.api.criteria.ObjectFactory;
import com.surveysampling.emailpanel.counts.api.criteria.UserGeoType;
import com.surveysampling.emailpanel.counts.api.criteria.WithKidsType;
import com.surveysampling.emailpanel.counts.api.geography.GeographicArea;
import com.surveysampling.emailpanel.counts.api.geography.GeographicAreaCode;
import com.surveysampling.emailpanel.counts.api.geography.GeographicCodeParser;
import com.surveysampling.emailpanel.counts.api.geography.GeographicNameParser;
import com.surveysampling.emailpanel.counts.api.request.EpanCount;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.exception.BadInputDataException;
import com.surveysampling.emailpanel.counts.model.CountsTableModel;
import com.surveysampling.emailpanel.counts.panels.AgePanel;
import com.surveysampling.emailpanel.counts.panels.ClientInfoPanel;
import com.surveysampling.emailpanel.counts.panels.EducationPanel;
import com.surveysampling.emailpanel.counts.panels.EthnicityPanel;
import com.surveysampling.emailpanel.counts.panels.GenderXdemPanel;
import com.surveysampling.emailpanel.counts.panels.GeoPanel;
import com.surveysampling.emailpanel.counts.panels.IncomePanel;
import com.surveysampling.emailpanel.counts.panels.KidsPanel;
import com.surveysampling.emailpanel.counts.panels.MarriedPanel;
import com.surveysampling.emailpanel.counts.panels.ResultsPanel;
import com.surveysampling.emailpanel.counts.panels.RunPanel;
import com.surveysampling.util.Flag;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * @author james
 *
 */
public class CriterionBuilder
{
    
    private CountsGUI cg;
    EpanCountRequest requestBeingEdited;
    private final ObjectFactory factory = new ObjectFactory();
    
    protected final List rUnparsedEntry = Collections.synchronizedList(new ArrayList(100)); // <String>
    private final Map mapResolution = Collections.synchronizedMap(new HashMap(100)); // <String,DatalessKey>

    private GeographyEnumType geoTypeLastChosen;
    private final DatalessKeyAccess accessKeyGeo;
    
    final List rRunningCounts = Collections.synchronizedList(new LinkedList());
    protected final Flag isCalcGeoRunning = new Flag();
    
    private final ImageIcon iconQuestion = createImageIcon("/icon/unknown.gif");
    private final ImageIcon iconCheck = createImageIcon("/icon/resolved.gif");
    private final ImageIcon iconEx = createImageIcon("/icon/bad.gif");
    
    private static final int MAX_CLIENT_LENGTH = 64;
    private static final int MAX_TOPIC_LENGTH = 64;

    /**
     * Constructor
     * @throws DatalessKeyAccessCreationException
     */
    public CriterionBuilder(CountsGUI counts) throws DatalessKeyAccessCreationException
    {
        accessKeyGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
        this.cg = counts;
    }
    
    /**
     * Get the current request's criteria and
     * put to the screen.
     *
     */
    public void putRequestToScreen()
    {
        final boolean enable = !cg.getRequestBeingEdited().isFrozen();
        requestBeingEdited = cg.getRequestBeingEdited();
        final EpanCountCriteria criteria = cg.getRequestBeingEdited().getCriteria();

        putBasicInformation(enable);
        putAgeCriteria(enable, criteria);
        putKidsCriteria(enable, criteria);
        putIncomeCriteria(enable, criteria);
        putEthnicityCriteria(enable, criteria);
        putEducationCriteria(enable, criteria);
        putMarriedCriteria(enable, criteria);
        putGenderXdemCriteria(enable, criteria);
        putGeoCriteria(enable, criteria);
        putBreakoutCriteria(enable, criteria);

        addDataToTable(enable);
        
        modifyRunPanelButtons(enable);

        if (enable)
        {
            groupGeo();
        }
        else //request is frozen
        {
            try
            {
                calcGeo(false);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the request being editted from the
     * screen and save it. 
     * @throws JAXBException
     * @throws BadInputDataException
     */
    public void getRequestFromScreen() throws JAXBException, BadInputDataException
    {
        getGeoFromScreen();
        
        getBasicCriteria();

        final EpanCountCriteria criteria = this.requestBeingEdited.getCriteria();

        getAgeCriteria(criteria);
        getKidsCriteria(criteria);
        getIncomeCriteria(criteria);
        getEthnicityCriteria(criteria);
        getEducationCriteria(criteria);
        getMarriedCriteria(criteria);
        getGenderCriteria(criteria);
        getGeoCriteria(criteria);
        getBreakOutCriteria(criteria);
        
    }
    
    /**
     * Get the text of the client and topic fields
     * @throws BadInputDataException
     */
	private void getBasicCriteria() throws BadInputDataException
	{
		ClientInfoPanel cip = cg.getRightPanel().getClientInfoPanel();
		final String clientName = cip.getClientText();
        if (clientName.length() == 0) 
        {
        	String text = "Client name has not been inputted. Enter a client name.";
        	throw new BadInputDataException(text);
        }
        else if (MAX_CLIENT_LENGTH < clientName.length())
        {
        	String text = "Client name is too long ("+MAX_CLIENT_LENGTH+" characters maximum).";
        	throw new BadInputDataException(text);
        }
        this.requestBeingEdited.setClientName(clientName);
        
        final String topic = cip.getTopicText();
        if (topic.length() == 0)
        {
        	String text = "Topic has not been inputted. Enter a topic.";
        	throw new BadInputDataException(text);
        }
        else if (MAX_TOPIC_LENGTH < topic.length())
        {
        	String text = "Topic is too long (" +MAX_TOPIC_LENGTH+ " characters maximum).";
        	throw new BadInputDataException(text);
        }
        this.requestBeingEdited.setTopic(topic);

	}    
    /**
     * Enable or disable the runPanel buttons
     * @param enable
     */
    private void modifyRunPanelButtons(final boolean enable)
    {
        final int iRunning = this.rRunningCounts.indexOf(this.requestBeingEdited);
        final boolean isCurrentlyRunning = iRunning >= 0;

        RunPanel runPanel = cg.getRightPanel().getRunPanel();
        runPanel.setRunEnable(enable && !isCurrentlyRunning);
        runPanel.setAbortEnable(isCurrentlyRunning);
    }

    /**
     * Add data to the table. Enable or Disable
     * the resultsPanel buttons.
     * 
     * @param enable
     */
    private void addDataToTable(final boolean enable)
    {
        ResultsPanel rp = cg.getRightPanel().getResultsPanel();
        // display child count queries
        JTable table = rp.getTable();
        CountsTableModel tableModel =(CountsTableModel) table.getModel();
        tableModel.removeAllData();
        for (final Iterator iCount = this.requestBeingEdited.iterator(); iCount.hasNext(); )
        {
            final EpanCount count = (EpanCount)iCount.next();

            final String sName = count.getName();

            String sCountOrErrMsg = "";
            if (count.completedSuccessfully())
            {
                sCountOrErrMsg = ""+count.getCount();
            }
            else if (count.completedWithError())
            {
                final BufferedReader rdr = new BufferedReader(new StringReader(count.getErrorMessage()));
                try
                {
                    sCountOrErrMsg = rdr.readLine();
                    rdr.close();
                }
                catch (final IOException shouldNotHappen)
                {
                    shouldNotHappen.printStackTrace();
                    sCountOrErrMsg = count.getErrorMessage();
                }
            }
            
            int rowIndex = tableModel.getRowCount();
            tableModel.setValueAt(sName, rowIndex, 0);
            tableModel.setValueAt(sCountOrErrMsg, rowIndex,1);
        }
        table.repaint();
        rp.setButtonsEnable(enable);
    }

    /**
     * Put the selected geos' to screen. This will also
     * select the radio button to use.
     */
    public void groupGeo()
    {
    	GeoPanel gp = cg.getRightPanel().getGeoPanel();
        GeographyEnumType geoType = gp.getSelectedGeoType();

        boolean isEnterableGeo = !(geoType.equals(GeographyEnumType.CONTINENTAL) || geoType.equals(GeographyEnumType.USA));
        /*
         * If we're switching to a type of geography that doesn't allow
         * any user entered codes or names (that is, the two US geographies),
         * then we are going to disable the user entry text area, so we need
         * to clear it out. But if there is already some text in it, it
         * would be lost, so we want the user to confirm this loss.
         */
        if (!isEnterableGeo)
        {
            final String sUser = gp.getGeoUser();
            boolean okToClear = true;
            if (sUser.length() > 0)
            {
                okToClear = false;
                final Object[] options = {"Discard geography","Cancel"};
                final int choice = JOptionPane.showOptionDialog(null,"This will lose your geography.",
                    "Warning",JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE,null,options,options[1]);
                if (choice == 0)
                {
                    okToClear = true;
                }
            }
            if (okToClear)
            {
                if (this.geoTypeLastChosen != null && !this.geoTypeLastChosen.equals(geoType))
                {
                    cg.setDirty(true);
                }
                clearGeoLists();
                clearGeoUserArea();
            }
            else
            {
                // oops, go back to the previous geography type selection
            	geoType = this.geoTypeLastChosen;
                gp.setButton(geoType, true);
                isEnterableGeo = !(geoType.equals(GeographyEnumType.CONTINENTAL) || geoType.equals(GeographyEnumType.USA));
            }
        }
        else // DMA, MSA, STATE, ZIP, FIP
        {
            try
            {
                calcGeo(false);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        gp.setGeoUserEnable(isEnterableGeo);
        this.geoTypeLastChosen = geoType;
    }
    
    /**
     * Put the geoCriteria of the selected request
     * to the screen
     * @param enable	enable or disable the buttons
     * @param criteria	the criteria of the request
     */
    private void putGeoCriteria(final boolean enable, final EpanCountCriteria criteria)
    {
        GeoPanel gp = cg.getRightPanel().getGeoPanel();
        // geography type
        gp.setZip(false);
        gp.setFips(false);
        gp.setCounty(false);
        gp.setMsa(false);
        gp.setDma(false);
        gp.setState(false);
        gp.setCont(false);
        gp.setFull(false);
        UserGeoType userGeo = criteria.getUserGeo();
        if (userGeo == null)
        {
            try
            {
                criteria.setUserGeo(factory.createUserGeoType());
            }
            catch (JAXBException e)
            {
                e.printStackTrace();
            }
            userGeo = criteria.getUserGeo();
            userGeo.setGeoType(GeographyEnumType.CONTINENTAL);
        }
        final GeographyEnumType geoType = userGeo.getGeoType();
        if (geoType.equals(GeographyEnumType.ZIP))
        {
            gp.setZip(true);
        }
        else if (geoType.equals(GeographyEnumType.FIPS))
        {
            gp.setFips(true);
        }
        else if (geoType.equals(GeographyEnumType.COUNTY))
        {
            gp.setCounty(true);
        }
        else if (geoType.equals(GeographyEnumType.MSA))
        {
            gp.setMsa(true);
        }
        else if (geoType.equals(GeographyEnumType.DMA))
        {
            gp.setDma(true);
        }
        else if (geoType.equals(GeographyEnumType.STATE))
        {
            gp.setState(true);
        }
        else if (geoType.equals(GeographyEnumType.CONTINENTAL))
        {
            gp.setCont(true);
        }
        else if (geoType.equals(GeographyEnumType.USA))
        {
            gp.setFull(true);
        }
        
        gp.setFieldsEnable(enable);

        
        clearGeoLists();
        clearGeoUserArea();

        GeographyType geography = criteria.getGeography();
        if (geography == null)
        {
            try
            {
                criteria.setGeography(factory.createGeographyType());
            }
            catch (JAXBException e)
            {
                e.printStackTrace();
            }
            geography = criteria.getGeography();
        }
        final List rID = geography.getId();
        final Iterator iID = rID.iterator();

        final StringBuffer sUserFixed = new StringBuffer(2048);

        final Set setUnparsedEntry = new HashSet();
        final List rUserGeoItem = userGeo.getUserGeoItem();

        this.geoTypeLastChosen = null;
        for (final Iterator iUserGeoItem = rUserGeoItem.iterator(); iUserGeoItem.hasNext();)
        {
            String s = (String)iUserGeoItem.next();
            s = s.trim();
            if (s.length() == 0)
            {
                continue;
            }

            while (setUnparsedEntry.contains(s))
            {
                s += " [DUPLICATE]";
            }

            setUnparsedEntry.add(s);

            sUserFixed.append(s);
            sUserFixed.append("\n");

            final String sID = (String)iID.next();
            if (sID.length() > 0)
            {
                final DatalessKey key = this.accessKeyGeo.createKeyFromString(sID);
                this.mapResolution.put(s,key);
            }
            else
            {
                this.mapResolution.put(s,null);
            }
        }
        gp.setGeoUser(sUserFixed.toString());
        gp.setGeoUserEnable(enable);
    }

    /**
     * Put the gender criteria to the screen
     * as well as the xdem criteria.
     * @param enable
     * @param criteria
     */
    private void putGenderXdemCriteria(final boolean enable, final EpanCountCriteria criteria)
    {
        // sex
        GenderXdemPanel gxp = cg.getRightPanel().getGenderXdemPanel();
        boolean specifiedMale = false;
        boolean specifiedFemale = false;

        final GenderType genderType = criteria.getGender();
        if (genderType != null)
        {
            final List rGender = genderType.getId();
            for (Iterator iGender = rGender.iterator(); iGender.hasNext();)
            {
                final GenderEnumType gender = (GenderEnumType)iGender.next();
                if (gender.equals(GenderEnumType.MALE))
                {
                    specifiedMale = true;
                }
                else if (gender.equals(GenderEnumType.FEMALE))
                {
                    specifiedFemale = true;
                }
            }
        }
        if (specifiedMale && specifiedFemale)
        {
            throw new IllegalStateException("both male and female were specified; shouldn't happen");
        }

        gxp.setMale(specifiedMale);
        gxp.setMaleEnable(enable);

        gxp.setFemale(specifiedFemale);
        gxp.setFemaleEnable(enable);

        gxp.setBoth(!specifiedMale && !specifiedFemale);
        gxp.setBothEnabled(enable);
        
        
        final boolean emptyXdem = requestBeingEdited.getXdemCriteria().isEmpty();
        final boolean badXdem = requestBeingEdited.getXdemCriteria().hasErrors();
        gxp.setXdemLabel(emptyXdem ? "" : badXdem ? "(has invalid XDem)" : "(has XDem)");
        final boolean enableXdem = enable || !emptyXdem;
        gxp.setXdemEnable(enableXdem);
    }

    /**
     * Select all the marriedPanel's checkBoxes
     * Put the marriedCriteria to the screen
     * 
     * @param enable
     * @param criteria
     */
    private void putMarriedCriteria(final boolean enable, final EpanCountCriteria criteria)
    {
        // married
        MarriedPanel mp = cg.getRightPanel().getMarriedPanel();
        mp.setSingle(false);
        mp.setMarried(false);
        mp.setDivorce(false);
        mp.setPartner(false);
        final MarriedType marriedType = criteria.getMarried();
        if (marriedType != null)
        {
            final List rMarried = marriedType.getId();
            for (final Iterator iMarried = rMarried.iterator(); iMarried.hasNext();)
            {
                final MarriedEnumType Married = (MarriedEnumType)iMarried.next();
                if (Married.equals(MarriedEnumType.SINGLE_NEVER_MARRIED))
                {
                    mp.setSingle(true);
                }
                if (Married.equals(MarriedEnumType.MARRIED))
                {
                    mp.setMarried(true);
                }
                if (Married.equals(MarriedEnumType.SEPARATED_DIVORCED_WIDOWED))
                {
                    mp.setDivorce(true);
                }
                if (Married.equals(MarriedEnumType.DOMESTIC_PARTNERSHIP))
                {
                    mp.setPartner(true);
                }
            }
        }
        mp.setEditable(enable);
    }

    /**
     * Select the educationCriteria checkBoxes
     * Put the selected request to screen
     * @param enable
     * @param criteria
     */
    private void putEducationCriteria(final boolean enable, final EpanCountCriteria criteria)
    {
        EducationPanel edp = cg.getRightPanel().getEducationPanel();
        edp.setSomeHS(false);
        edp.setHsGrad(false);
        edp.setSmCollege(false);
        edp.setColDeg(false);
        edp.setSmGrad(false);
        edp.setMasters(false);
        edp.setPro(false);
        final EducationType educationType = criteria.getEducation();
        if (educationType != null)
        {
            final List rEducation = educationType.getId();
            for (final Iterator iEducation = rEducation.iterator(); iEducation.hasNext();)
            {
                final EducationEnumType education = (EducationEnumType)iEducation.next();
                if (education.equals(EducationEnumType.COMPLETED_SOME_HIGH_SCHOOL))
                {
                    edp.setSomeHS(true);
                }
                if (education.equals(EducationEnumType.HIGH_SCHOOL_GRADUATE))
                {
                    edp.setHsGrad(true);
                }
                if (education.equals(EducationEnumType.COMPLETED_SOME_COLLEGE))
                {
                    edp.setSmCollege(true);
                }
                if (education.equals(EducationEnumType.COLLEGE_DEGREE))
                {
                    edp.setColDeg(true);
                }
                if (education.equals(EducationEnumType.COMPLETED_SOME_POSTGRADUATE))
                {
                    edp.setSmGrad(true);
                }
                if (education.equals(EducationEnumType.MASTERS_DEGREE))
                {
                    edp.setMasters(true);
                }
                if (education.equals(EducationEnumType.DOCTORATE_LAW_OR_PROFESSIONAL_DEGREE))
                {
                    edp.setPro(true);
                }
            }
        }
        edp.setEditable(enable);
    }

    /**
     * Select the ethnicityCriteria checkBoxes
     * Put the selected request to screen
     * @param enable
     * @param criteria
     */
    private void putEthnicityCriteria(final boolean enable, final EpanCountCriteria criteria)
    {
        EthnicityPanel ep = cg.getRightPanel().getEthnicityPanel();
        ep.setBlack(false);
        ep.setAsian(false);
        ep.setHisp(false);
        ep.setInd(false);
        ep.setPac(false);
        ep.setOther(false);
        ep.setWhite(false);
            
        final EthnicityType ethnicityType = criteria.getEthnicity();
        if (ethnicityType != null)
        {
            final List rEthnicity = ethnicityType.getId();
            for (final Iterator iEthnicity = rEthnicity.iterator(); iEthnicity.hasNext();)
            {
                final EthnicityEnumType ethnicity = (EthnicityEnumType)iEthnicity.next();
                if (ethnicity.equals(EthnicityEnumType.BLACK))
                {
                    ep.setBlack(true);
                }
                if (ethnicity.equals(EthnicityEnumType.HISPANIC))
                {
                    ep.setHisp(true);
                }
                if (ethnicity.equals(EthnicityEnumType.WHITE))
                {
                    ep.setWhite(true);
                }
                if (ethnicity.equals(EthnicityEnumType.ASIAN))
                {
                    ep.setAsian(true);
                }
                if (ethnicity.equals(EthnicityEnumType.PACIFIC))
                {
                    ep.setPac(true);
                }
                if (ethnicity.equals(EthnicityEnumType.INDIAN))
                {
                    ep.setInd(true);
                }
                if (ethnicity.equals(EthnicityEnumType.OTHER))
                {
                    ep.setOther(true);
                }
            }
        }
        ep.setFieldsEnabled(enable);
     }

    /**
     * Select the incomeCriteria checkBoxes
     * Put the selected request to screen
     * @param enable	enable or disable the fields
     * @param criteria	
     */
    private void putIncomeCriteria(final boolean enable, final EpanCountCriteria criteria)
    {
        // income
        IncomePanel ip = cg.getRightPanel().getIncomePanel(); 
        ip.setInc0(false);
        ip.setInc20(false);
        ip.setInc30(false);
        ip.setInc40(false);
        ip.setInc50(false);
        ip.setInc60(false);
        ip.setInc75(false);
        ip.setInc100(false);
        ip.setInc150(false);
        ip.setIncPNA(false);

        final IncomeType incomeType = criteria.getIncome();
        if (incomeType != null)
        {
            final List rIncome = incomeType.getId();
            for (Iterator iIncome = rIncome.iterator(); iIncome.hasNext();)
            {
                final IncomeEnumType income = (IncomeEnumType)iIncome.next();
                if (income.equals(IncomeEnumType.MIN_0_K))
                {
                    ip.setInc0(true);
                }
                else if (income.equals(IncomeEnumType.MIN_20_K))
                {
                    ip.setInc20(true);
                }
                else if (income.equals(IncomeEnumType.MIN_30_K))
                {
                    ip.setInc30(true);
                }
                else if (income.equals(IncomeEnumType.MIN_40_K))
                {
                    ip.setInc40(true);
                }
                else if (income.equals(IncomeEnumType.MIN_50_K))
                {
                    ip.setInc50(true);
                }
                else if (income.equals(IncomeEnumType.MIN_60_K))
                {
                    ip.setInc60(true);
                }
                else if (income.equals(IncomeEnumType.MIN_75_K))
                {
                    ip.setInc75(true);
                }
                else if (income.equals(IncomeEnumType.MIN_100_K))
                {
                    ip.setInc100(true);
                }
                else if (income.equals(IncomeEnumType.MIN_150_K))
                {
                    ip.setInc150(true);
                }
            }
            if (incomeType.isPna())
            {
                ip.setIncPNA(true);
            }
        }
        
        ip.setFieldsEnable(enable);
        
    }

    /**
     * Put the kids criteria to the kids panel
     * 
     * @param enable
     * @param criteria
     */
    private void putKidsCriteria(boolean enable, final EpanCountCriteria criteria)
    {
        final WithKidsType withKids = criteria.getWithKids();
        KidsPanel kp = cg.getRightPanel().getKidsPanel();
        kp.setMaxAgeText("");
        kp.setMinAgeText("");
        kp.setWithout(false);
        
        boolean specifiedMale = false;
        boolean specifiedFemale = false;
        if (withKids != null)
        {
            // ages of kids
            final AgeType ageType = withKids.getAge();
            if (ageType == null)
            {
            }
            else
            {
                final BigInteger ageMin = ageType.getMin();
                if (ageMin == null)
                {
                }
                else
                {
                    kp.setMinAgeText(ageMin.toString());
                }

                final BigInteger ageMax = ageType.getMax();
                if (ageMax == null)
                {
                }
                else
                {
                    kp.setMaxAgeText(ageMax.toString());
                }
            }
            // sexes of kids

	        final GenderType genderType = withKids.getGender();
	        if (genderType != null)
	        {
	            final List rGender = genderType.getId();
	            for (Iterator iGender = rGender.iterator(); iGender.hasNext();)
	            {
	                final GenderEnumType gender = (GenderEnumType)iGender.next();
	                if (gender.equals(GenderEnumType.MALE))
	                {
	                    specifiedMale = true;
	                }
	                else if (gender.equals(GenderEnumType.FEMALE))
	                {
	                    specifiedFemale = true;
	                }
	            }
	            if (specifiedMale && specifiedFemale)
	            {
	                throw new IllegalStateException("both male and female were specified; shouldn't happen");
	            }
	        }
	        // no kids
	        kp.setWithout(withKids.isNoKids());
        }
        
        kp.setMale(specifiedMale);
        kp.setFemale(specifiedFemale);
        kp.setBoth(!specifiedMale && !specifiedFemale);        
        kp.setFieldsEnable(enable);
        
        
    }


    /**
     * Put client topic information to the ClientInfoPanel
     * 
     * @param enable	enable or disable the fields
     */
    private void putBasicInformation(boolean enable)
    {
        ClientInfoPanel cip = cg.getRightPanel().getClientInfoPanel();
        final String createdBy = requestBeingEdited.getCreatedBy();
        JLabel aeLabel = cip.getAeLabel();
        if (createdBy.length() > 0)
        {
            aeLabel.setText("created by: "+createdBy);
        }
        else
        {
            aeLabel.setText(" ");//just enough to keep sizes consistent
        }
        cip.setClientText(requestBeingEdited.getClientName());
        cip.setTopicText(requestBeingEdited.getTopic());
        cip.setFieldsEnable(enable);
    }

    /**
     * Put age criteria to the AgePanel
     * 
     * @param enable	enable/disable the fields
     * @param criteria	the criteria that is used
     */
    private void putAgeCriteria(boolean enable, final EpanCountCriteria criteria)
    {
        AgePanel ap = cg.getRightPanel().getAgePanel();
        final AgeType ageType = criteria.getAge();
        if (ageType == null)
        {
            ap.setMaxAge("");
            ap.setMinAge("");
        }
        else
        {
            {
                final BigInteger ageMin = ageType.getMin();
                if (ageMin == null)
                {
                    ap.setMinAge("");
                }
                else
                {
                    ap.setMinAge(ageMin.toString());
                }
            }

            {
                final BigInteger ageMax = ageType.getMax();
                if (ageMax == null)
                {
                    ap.setMaxAge("");
                }
                else
                {
                    ap.setMaxAge(ageMax.toString());
                }
            }
        }
        ap.setFieldsEnable(enable);
    }

    /**
     *	Clears the geo lists. The geoCalc and geoMatches lists.
     */
    public void clearGeoLists()
    {
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                cg.getRightPanel().getGeoPanel().removeFromLists();
            }
        });
    }
    
    /**
     * Clear the geoUser textArea
     *
     */
    public void clearGeoUserArea()
    {
        this.mapResolution.clear();
        this.rUnparsedEntry.clear();
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                cg.getRightPanel().getGeoPanel().setGeoUser("");
            }
        });
    }
    
    /**
     *	Put the breakout criteria. Select either the geography
     *	or gender checkboxes.
     *
     * @param enable
     * @param criteria
     */
    private void putBreakoutCriteria(boolean enable, EpanCountCriteria criteria)
    {
        // break-out
        RunPanel rp = cg.getRightPanel().getRunPanel();
        rp.setGender(false);
        rp.setGeography(false);
        final BreakOutType breakOutType = criteria.getBreakOut();
        if (breakOutType != null)
        {
            if (breakOutType.isGeography())
            {
                rp.setGeography(true);
            }
            if (breakOutType.isGender())
            {
                rp.setGender(true);
            }
        }
        
        rp.setFieldsEnable(enable);
    }
    

	/****************************************************************************************
	 *	                           GETREQUESTFROMSCREEN()									*
	 ****************************************************************************************/
    
    /**
     * Gets whatever breakOutCriteria has been selected.
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getBreakOutCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        // break-out
        RunPanel rp = cg.getRightPanel().getRunPanel();
        BreakOutType breakOutType = criteria.getBreakOut();
        if (breakOutType == null)
        {
            criteria.setBreakOut(this.factory.createBreakOutType());
            breakOutType = criteria.getBreakOut();
        }
        breakOutType.setGeography(false);
        breakOutType.setGender(false);
        if (rp.isGeographySelected())
        {
            breakOutType.setGeography(true);
        }
        if (rp.isGenderSelected())
        {
            breakOutType.setGender(true);
        }
    }

    /**
     * Gets the geoCriteria.
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getGeoCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
            GeoPanel gp = cg.getRightPanel().getGeoPanel();
            UserGeoType userGeo = criteria.getUserGeo();
            if (userGeo == null)
            {
                criteria.setUserGeo(this.factory.createUserGeoType());
                userGeo = criteria.getUserGeo();
            }
            // geography type
            if (gp.isZip())
            {
                userGeo.setGeoType(GeographyEnumType.ZIP);
            }
            else if (gp.isFips())
            {
                userGeo.setGeoType(GeographyEnumType.FIPS);
            }
            else if (gp.isCounty())
            {
                userGeo.setGeoType(GeographyEnumType.COUNTY);
            }
            else if (gp.isMsa())
            {
                userGeo.setGeoType(GeographyEnumType.MSA);
            }
            else if (gp.isDma())
            {
                userGeo.setGeoType(GeographyEnumType.DMA);
            }
            else if (gp.isState())
            {
                userGeo.setGeoType(GeographyEnumType.STATE);
            }
            else if (gp.isCont())
            {
                userGeo.setGeoType(GeographyEnumType.CONTINENTAL);
            }
            else if (gp.isFull())
            {
                userGeo.setGeoType(GeographyEnumType.USA);
            }
            
            
            // user-entered geography items
            final List userGeoItem = userGeo.getUserGeoItem();
            userGeoItem.clear();
            for (final Iterator iUserGeo = this.rUnparsedEntry.iterator(); iUserGeo.hasNext();)
            {
                final String sUserGeo = (String)iUserGeo.next();
                userGeoItem.add(sUserGeo);
            }
            GeographyType geography = criteria.getGeography();
            if (geography == null)
            {
                criteria.setGeography(this.factory.createGeographyType());
                geography = criteria.getGeography();
            }
            final List rGeo = geography.getId();
            rGeo.clear();
            for (final Iterator iUserGeo = this.rUnparsedEntry.iterator(); iUserGeo.hasNext();)
            {
                final String sUserGeo = (String)iUserGeo.next();
                final DatalessKey keyGeo = (DatalessKey)this.mapResolution.get(sUserGeo);
                if (keyGeo == null)
                {
                    rGeo.add("");
                }
                else
                {
                    rGeo.add(DatalessKeyAccessFactory.getDatalessKeyAccess(keyGeo).keyAsString(keyGeo));
                }
            }
    }

    /**
     * Get the genderCriteria from the screen.
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getGenderCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        //sex
        GenderXdemPanel gxp = cg.getRightPanel().getGenderXdemPanel();
        GenderType genderType = criteria.getGender();
        if (genderType == null)
        {
            criteria.setGender(this.factory.createGenderType());
            genderType = criteria.getGender();
        }
        final List rGender = genderType.getId();
        rGender.clear();
        if (gxp.isMale())
        {
            rGender.add(GenderEnumType.MALE);
        }
        else if (gxp.isFemale())
        {
            rGender.add(GenderEnumType.FEMALE);
        }
        if (rGender.isEmpty())
        {
            criteria.setGender(null);
        }
    }

    /**
     * Get the married criteria from the screen.
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getMarriedCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        //      married
        MarriedPanel mp = cg.getRightPanel().getMarriedPanel();
        MarriedType marriedType = criteria.getMarried();
        if (marriedType == null)
        {
            criteria.setMarried(this.factory.createMarriedType());
            marriedType = criteria.getMarried();
        }
        final List rMarried = marriedType.getId();
        rMarried.clear();
        if (mp.isSingle())
        {
            rMarried.add(MarriedEnumType.SINGLE_NEVER_MARRIED);
        }
        if (mp.isMarried())
        {
            rMarried.add(MarriedEnumType.MARRIED);
        }
        if (mp.isDivorce())
        {
            rMarried.add(MarriedEnumType.SEPARATED_DIVORCED_WIDOWED);
        }
        if (mp.isPartner())
        {
            rMarried.add(MarriedEnumType.DOMESTIC_PARTNERSHIP);
        }
        if (rMarried.isEmpty())
        {
            criteria.setMarried(null);
        }
    }

    /**
     * Get the EducationCriteria from the screen
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getEducationCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        //education
        EducationPanel eduPanel = cg.getRightPanel().getEducationPanel();
        EducationType educationType = criteria.getEducation();
        if (educationType == null)
        {
            criteria.setEducation(this.factory.createEducationType());
            educationType = criteria.getEducation();
        }
        final List rEducation = educationType.getId();
        rEducation.clear();
        if (eduPanel.isSomeHS())
        {
            rEducation.add(EducationEnumType.COMPLETED_SOME_HIGH_SCHOOL);
        }
        if (eduPanel.isHsGrad())
        {
            rEducation.add(EducationEnumType.HIGH_SCHOOL_GRADUATE);
        }
        if (eduPanel.isSmCollege())
        {
            rEducation.add(EducationEnumType.COMPLETED_SOME_COLLEGE);
        }
        if (eduPanel.isColDeg())
        {
            rEducation.add(EducationEnumType.COLLEGE_DEGREE);
        }
        if (eduPanel.isSmGrad())
        {
            rEducation.add(EducationEnumType.COMPLETED_SOME_POSTGRADUATE);
        }
        if (eduPanel.isMasters())
        {
            rEducation.add(EducationEnumType.MASTERS_DEGREE);
        }
        if (eduPanel.isPro())
        {
            rEducation.add(EducationEnumType.DOCTORATE_LAW_OR_PROFESSIONAL_DEGREE);
        }
        if (rEducation.isEmpty())
        {
            criteria.setEducation(null);
        }
    }

    /**
     * Get the ethnicityCriteria from the screen.
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getEthnicityCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        //ethnicity
        EthnicityPanel ep = cg.getRightPanel().getEthnicityPanel(); 
        EthnicityType ethnicityType = criteria.getEthnicity();
        if (ethnicityType == null)
        {
            criteria.setEthnicity(this.factory.createEthnicityType());
            ethnicityType = criteria.getEthnicity();
        }
        final List rEthnicity = ethnicityType.getId();
        rEthnicity.clear();
        if (ep.isBlack())
        {
            rEthnicity.add(EthnicityEnumType.BLACK);
        }
        if (ep.isHisp())
        {
            rEthnicity.add(EthnicityEnumType.HISPANIC);
        }
        if (ep.isWhite())
        {
            rEthnicity.add(EthnicityEnumType.WHITE);
        }
        if (ep.isAsian())
        {
            rEthnicity.add(EthnicityEnumType.ASIAN);
        }
        if (ep.isPac())
        {
            rEthnicity.add(EthnicityEnumType.PACIFIC);
        }
        if (ep.isInd())
        {
            rEthnicity.add(EthnicityEnumType.INDIAN);
        }
        if (ep.isOther())
        {
            rEthnicity.add(EthnicityEnumType.OTHER);
        }
        if (rEthnicity.isEmpty())
        {
            criteria.setEthnicity(null);
        }
    }

    /**
     * Get the incomeCriteria from the screen.
     * 
     * @param criteria
     * @throws JAXBException
     */
    private void getIncomeCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        //income
        IncomePanel ip = cg.getRightPanel().getIncomePanel();
        IncomeType incomeType = criteria.getIncome();
        if (incomeType == null)
        {
            criteria.setIncome(this.factory.createIncomeType());
            incomeType = criteria.getIncome();
        }
        final List rIncome = incomeType.getId();
        rIncome.clear();
        if (ip.isInc0())
        {
            rIncome.add(IncomeEnumType.MIN_0_K);
        }
        if (ip.isInc20())
        {
            rIncome.add(IncomeEnumType.MIN_20_K);
        }
        if (ip.isInc30())
        {
            rIncome.add(IncomeEnumType.MIN_30_K);
        }
        if (ip.isInc40())
        {
            rIncome.add(IncomeEnumType.MIN_40_K);
        }
        if (ip.isInc50())
        {
            rIncome.add(IncomeEnumType.MIN_50_K);
        }
        if (ip.isInc60())
        {
            rIncome.add(IncomeEnumType.MIN_60_K);
        }
        if (ip.isInc75())
        {
            rIncome.add(IncomeEnumType.MIN_75_K);
        }
        if (ip.isInc100())
        {
            rIncome.add(IncomeEnumType.MIN_100_K);
        }
        if (ip.isInc150())
        {
            rIncome.add(IncomeEnumType.MIN_150_K);
        }
        boolean pna = ip.isIncPNA();
        incomeType.setPna(pna);
        if (rIncome.isEmpty() && !pna)
        {
            criteria.setIncome(null);
        }
    }

    /**
     * Get the kidsCriteria from the screen.
     * 
     * @param criteria
     * @throws JAXBException
     * @throws BadInputDataException 
     */
    private void getKidsCriteria(final EpanCountCriteria criteria) throws JAXBException, BadInputDataException
    {
        //kids
        KidsPanel kp = cg.getRightPanel().getKidsPanel();
        boolean haveKidCriteria = false;
        WithKidsType withKids = this.factory.createWithKidsType();
        {
            // ages of kids
            final String sMinAge = kp.getMinAgeText();
            final String sMaxAge = kp.getMaxAgeText();
            if (sMinAge.length() > 0 || sMaxAge.length() > 0)
            {
                final AgeType age = this.factory.createAgeType();

                if (sMinAge.length() > 0)
                {
                    final BigInteger biAge = new BigInteger(sMinAge);

                    final int iAge = biAge.intValue();
                    if (iAge < 2 || 18 <= iAge)
                    {
                    	String text = "Kids' age must be between 2 and 17 inclusive.";
                    	JOptionPane.showMessageDialog(null, text);
                        throw new BadInputDataException(text);
                    }
                    age.setMin(biAge);
                    haveKidCriteria = true;
                }
                if (sMaxAge.length() > 0)
                {
                    final BigInteger biAge = new BigInteger(sMaxAge);

                    final int iAge = biAge.intValue();
                    if (iAge < 2 || 18 <= iAge)
                    {
                    	String text = "Kids' age must be between 2 and 17 inclusive.";
                    	JOptionPane.showMessageDialog(null, text);
                        throw new BadInputDataException(text);
                    }
                    age.setMax(biAge);
                    haveKidCriteria = true;
                }

                withKids.setAge(age);
            }
        }
        {
            // sexes of kids
            GenderType genderType = withKids.getGender();
            if (genderType == null)
            {
                withKids.setGender(this.factory.createGenderType());
                genderType = withKids.getGender();
            }
            final List rGender = genderType.getId();
            rGender.clear();
            if (kp.isMale())
            {
                rGender.add(GenderEnumType.MALE);
                haveKidCriteria = true;
            }
            else if (kp.isFemale())
            {
                rGender.add(GenderEnumType.FEMALE);
                haveKidCriteria = true;
            }
            if (rGender.isEmpty())
            {
                withKids.setGender(null);
            }
        }
        // no kids
        if (kp.isWithout())
        {
            withKids.setNoKids(true);
            haveKidCriteria = true;
        }
        if (haveKidCriteria)
        {
            criteria.setWithKids(withKids);
        }
        else
        {
            criteria.setWithKids(null);
        }
    }

    /**
     * Get the AgeCriteria from the screen
     * 
     * @param criteria
     * @throws JAXBException
     * @throws BadInputDataException 
     */
    private void getAgeCriteria(final EpanCountCriteria criteria) throws JAXBException, BadInputDataException
    {
        AgePanel ap = cg.getRightPanel().getAgePanel();
        final String sMinAge = ap.getMinAge();
        final String sMaxAge = ap.getMaxAge();
        if (sMinAge.length() > 0 || sMaxAge.length() > 0)
        {
            final AgeType age = this.factory.createAgeType();

            if (sMinAge.length() > 0)
            {
                final BigInteger biAge = new BigInteger(sMinAge);

                final int iAge = biAge.intValue();
                if (iAge < 18 || 200 <= iAge)
                {
                	String text = "Age must be between 18 and 199 inclusive.";
                	JOptionPane.showMessageDialog(null, text);
                    throw new BadInputDataException(text);
                }
                age.setMin(biAge);
            }
            if (sMaxAge.length() > 0)
            {
                final BigInteger biAge = new BigInteger(sMaxAge);

                final int iAge = biAge.intValue();
                if (iAge < 18 || 200 <= iAge)
                {
                	String text = "Age must be between 18 and 199 inclusive.";
                	JOptionPane.showMessageDialog(null, text);
                    throw new BadInputDataException(text);
                }
                age.setMax(biAge);
            }

            criteria.setAge(age);
        }
        else
        {
            criteria.setAge(null);
        }
    }

    /**
     * Gets the geocriteria specified on screen
     * and initializes the geoType.
     *
     */
    private void getGeoFromScreen()
    {
    	GeoPanel gp = cg.getRightPanel().getGeoPanel();
        final GeographyEnumType geoType = gp.getSelectedGeoType();
        if (geoType.equals(GeographyEnumType.COUNTY) ||
            geoType.equals(GeographyEnumType.MSA) ||
            geoType.equals(GeographyEnumType.DMA) ||
            geoType.equals(GeographyEnumType.STATE))
        {
            initGeoListsNames(geoType);
        }
        else if (geoType.equals(GeographyEnumType.ZIP) ||
        		 geoType.equals(GeographyEnumType.FIPS))
        {
            initGeoListsCodes(geoType);
        }
        else if (geoType.equals(GeographyEnumType.CONTINENTAL) ||
        		 geoType.equals(GeographyEnumType.USA))
        {
            clearGeoLists();
        }
        else // can't happen since at least one of these radio buttons will be selected
        	assert false;
    }
    
    /**
     * If the geoType is County, MSA, DMA, or State, use this
     * method to initialize the geoType. Read the geoUser text
     * and parse the entry.
     * 
     * @param geoType
     */
    private void initGeoListsNames(final GeographyEnumType geoType)
    {
        final GeoPanel gp = cg.getRightPanel().getGeoPanel();
        
        if (!cg.isSaving()&& gp.isGeoTypeHasChanged())
        {
            this.mapResolution.clear();
        }

        this.rUnparsedEntry.clear();
        final String sUser = gp.getGeoUser();
        final StringBuffer sUserFixed = new StringBuffer(sUser.length());

        final Set setUnparsedEntry = new HashSet();
        final BufferedReader inUser = new BufferedReader(new StringReader(sUser));
        try
        {
            for (String s = inUser.readLine(); s != null; s = inUser.readLine())
            {
                s = s.trim();
                if (s.length() == 0)
                {
                    continue;
                }
    
                while (setUnparsedEntry.contains(s))
                {
                    s += " [DUPLICATE]";
                }
    
                setUnparsedEntry.add(s);
    
                sUserFixed.append(s);
                sUserFixed.append("\n");
    
                this.rUnparsedEntry.add(s);
                if (!this.mapResolution.containsKey(s))
                {
                    this.mapResolution.put(s,null);
                    // TODO this could leave orphaned keys in mapResolution (is this OK?)
                }
            }
        }
        catch (final IOException shouldNotHappen)
        {
            throw new RuntimeException(shouldNotHappen);
        }
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                gp.setGeoUser(sUserFixed.toString());
            }
        });
    }
    
    /**
     * If the geoType is zip or fips, then use this method
     * initialize the geoType. Essentially the same as 
     * initGeoListsNames, except makes sure that user entered
     * geoType text is only numerical
     * 
     * @param geoType
     */
    private void initGeoListsCodes(final GeographyEnumType geoType)
    {
        final GeoPanel gp = cg.getRightPanel().getGeoPanel();

        if (!cg.isSaving()&& gp.isGeoTypeHasChanged())
        {
            this.mapResolution.clear();
        }
        
        this.rUnparsedEntry.clear();
        String sUser = gp.getGeoUser();

        sUser = sUser.replaceAll(" \\[DUPLICATE\\]","");



        final StringBuffer sbCleaned = new StringBuffer(sUser.length());
        cleanGeoCodes(sUser,sbCleaned);

        final StringTokenizer st = new StringTokenizer(sbCleaned.toString());

        final StringBuffer sUserFixed = new StringBuffer(sUser.length());
        final Set setUnparsedEntry = new HashSet();
        while (st.hasMoreTokens())
        {
            String code = st.nextToken();

            while (setUnparsedEntry.contains(code))
            {
                code += " [DUPLICATE]";
            }

            setUnparsedEntry.add(code);

            sUserFixed.append(code);
            sUserFixed.append("\n");

            this.rUnparsedEntry.add(code);
            if (!this.mapResolution.containsKey(code))
            {
                this.mapResolution.put(code,null);
                // TODO this could leave orphaned keys in mapResolution (is this OK?)
            }
        }
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                gp.setGeoUser(sUserFixed.toString());
            }
        });
    }

    private void cleanGeoCodes(final String sUser, final StringBuffer sbCleaned)
    {
        final StringTokenizer st = new StringTokenizer(sUser);
        while (st.hasMoreTokens())
        {
            final String code = st.nextToken();

            // if word has any digits in it, or consists of only ILO's
            if (code.matches(".*\\d+.*") || code.matches("[IiLlOo]+"))
            {
                /*
                 * Replace I's and L's with ones, and
                 * O's with zeroes (either upper or lower case)
                 * then replace all non-digits with spaces.
                 */
                sbCleaned.append(code
                    .replace('i','1').replace('I','1')
                    .replace('l','1').replace('L','1')
                    .replace('o','0').replace('O','0')
                    .replaceAll("\\D"," "));

                sbCleaned.append(' ');
            }
        }
    }

    /**
     * Validate the geoType
     * 
     * @throws InterruptedException
     */
    public void calcGeoAndWait(boolean forceCalcGeo) throws InterruptedException
    {
        calcGeo(forceCalcGeo);
        this.isCalcGeoRunning.waitUntilFalse();
    }
    
    /**
     * @throws InterruptedException
     */
    public void calcGeo(boolean forceCalcGeo) throws InterruptedException
    {
        synchronized (this.isCalcGeoRunning)
        {
            /*
             * Prevent calcGeoTask from running more
             * than once at a time. This could happen
             * if they double-click on the button,
             * for example.
             */
            if (this.isCalcGeoRunning.isTrue())
            {
                // already running, so just do nothing
                return;
            }
            this.isCalcGeoRunning.waitToSetTrue();
        }
        
        try
        {
            calcGeoTask(forceCalcGeo);
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }
        try {
			CriterionBuilder.this.isCalcGeoRunning.waitToSetFalse();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Depending on the geoType, validate the geoType.
     * @param forceCalcGeo if true, then the count request was run before saving, so
     *                     force a calc geo of the specified geoType
     *                     otherwise, showGeos()
     */
    protected void calcGeoTask(boolean forceCalcGeo)
    {
        clearGeoLists();

        if (this.requestBeingEdited.isFrozen()&&!forceCalcGeo)
        {
            showGeos();
            return;
        }
        
        GeoPanel gp = cg.getRightPanel().getGeoPanel();

        final GeographyEnumType geoType = gp.getSelectedGeoType();
        if (geoType.equals(GeographyEnumType.COUNTY) ||
            geoType.equals(GeographyEnumType.MSA) ||
            geoType.equals(GeographyEnumType.DMA) ||
            geoType.equals(GeographyEnumType.STATE))
        {
            initGeoListsNames(geoType);
            calcGeoNames(geoType);
        }
        else if (geoType.equals(GeographyEnumType.ZIP) ||
        		 geoType.equals(GeographyEnumType.FIPS))
        {
            initGeoListsCodes(geoType);
            calcGeoCodes(geoType);
        }
        else
        {
            clearGeoUserArea();
        }

        if (!cg.isSaving() && gp.isGeoTypeHasChanged())
            cg.setDirty(true);
    }
    
    /**
     * Show the geos that are potential
     * matches for the selected unresolved geoType.
     * 
     * The matches are shown in the geoMatches List.
     *
     */
    private void showGeos()
    {
        final JList geoCalc = cg.getRightPanel().getGeoPanel().getGeoCalcList();

        final Vector data = new Vector();
        final GeographicNameParser parserName = cg.getLib().getGeographicNameParser();
        final GeographicCodeParser parserCode = cg.getLib().getGeographicCodeParser();
        final UserGeoType userGeo = this.requestBeingEdited.getCriteria().getUserGeo();

        boolean useCode = false;
        if (userGeo.getGeoType().equals(GeographyEnumType.ZIP))
        {
            useCode = true;
        }

        final List rUserGeoItem = userGeo.getUserGeoItem();
        for (final Iterator iUserGeoItem = rUserGeoItem.iterator(); iUserGeoItem.hasNext();)
        {
            final String sUserGeoItem = (String)iUserGeoItem.next();
            final DatalessKey keyGeo = (DatalessKey)this.mapResolution.get(sUserGeoItem);

            final JLabel widgetItem = new JLabel();

            if (keyGeo != null)
            {
                try
                {
                    String geo;
                    if (useCode)
                    {
                        geo = parserCode.lookup(keyGeo);
                    }
                    else
                    {
                        geo = parserName.lookup(keyGeo);
                    }
                    widgetItem.setText(geo);
                }
                catch (final IllegalStateException geoNotFound)
                {
                    geoNotFound.printStackTrace();
                    widgetItem.setText(sUserGeoItem);
                    widgetItem.setIcon(iconQuestion);
                }
            }
            else
            {
                widgetItem.setText(sUserGeoItem);
                widgetItem.setIcon(iconEx);
            }
            
            data.add(widgetItem);
        }
        
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                geoCalc.setListData(data);
            }
        });
    }
    
    /**
     * Using the parsed geoTypes, fill the calcGeo JList
     * with the proper geoType title and also icons
     * This method is used for County, State, DMA, MSA
     * 
     * @param geoType
     */
    private void calcGeoNames(final GeographyEnumType geoType)
    {
    	final GeographicNameParser parser = cg.getLib().getGeographicNameParser();
        final List rrMatch = new ArrayList(this.rUnparsedEntry.size());

        final Vector data = new Vector();
        // parse the user-input geography, and update our progress
        // bar to indicate the progress to the user
        // James - progressBar removed, since its no longer needed
        parser.parseGeo(this.rUnparsedEntry,geoType,rrMatch,5,null);

        final JList geoCalc = cg.getRightPanel().getGeoPanel().getGeoCalcList();
        final Iterator iUnparsedEntry = this.rUnparsedEntry.iterator();
        for (final Iterator irMatch = rrMatch.iterator(); irMatch.hasNext();)
        {
            final List rMatch = (List)irMatch.next();
            final String sUnparsedEntry = (String)iUnparsedEntry.next();

            final JLabel widgetItem = new JLabel();
            if (rMatch.size() == 0)
            {
                widgetItem.setText(sUnparsedEntry);
                widgetItem.setIcon(iconEx);
            }
            else if (rMatch.size() == 1)
            {
                // There is one and only one match. Good!
                DatalessKey keyResolution = (DatalessKey)this.mapResolution.get(sUnparsedEntry);
                if (keyResolution == null) // if we haven't recorded the resolution yet
                {
                    final GeographicArea match = (GeographicArea)rMatch.get(0);
                    keyResolution = match.getKey();
                    this.mapResolution.put(sUnparsedEntry,keyResolution);
                }
                widgetItem.setText(parser.lookup(keyResolution));
            }
            else
            {
                DatalessKey keyResolution = (DatalessKey)this.mapResolution.get(sUnparsedEntry);
                if (keyResolution == null) // if the user hasn't resolved it yet
                {
                    final GeographicArea matchTop = (GeographicArea)rMatch.get(0);
                    widgetItem.setText(parser.lookup(matchTop.getKey()));
                    widgetItem.setIcon(iconQuestion);
                }
                else
                {
                    widgetItem.setText(parser.lookup(keyResolution));
                    widgetItem.setIcon(iconCheck);
                }
            }
            widgetItem.putClientProperty("Matches", rMatch);
            data.add(widgetItem);
        }
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                geoCalc.setListData(data);
            }
        });
    }

    /**
     * Using the parsed geoTypes, fill the calcGeo JList
     * with the proper geoType title and also icons
     * This method is used for ZIP and FIPS
     *  
     * @param geoType
     */
    private void calcGeoCodes(final GeographyEnumType geoType)
    {
        final GeographicCodeParser parser = cg.getLib().getGeographicCodeParser();
        final List rrMatch = new ArrayList(this.rUnparsedEntry.size());

        // parse the user-input geography, and update our progress
        // bar to indicate the progress to the user
        parser.parseGeo(this.rUnparsedEntry,geoType,rrMatch,1,null);

        final Vector data = new Vector();
        final JList geoCalc = cg.getRightPanel().getGeoPanel().getGeoCalcList();
        final Iterator iUnparsedEntry = this.rUnparsedEntry.iterator();
        for (final Iterator irMatch = rrMatch.iterator(); irMatch.hasNext();)
        {
            final List rMatch = (List)irMatch.next();
            final String sUnparsedEntry = (String)iUnparsedEntry.next();

            final JLabel widgetItem = new JLabel();
            if (rMatch.size() == 0)
            {
                widgetItem.setText(sUnparsedEntry);
                widgetItem.setIcon(iconEx);
            }
            else if (rMatch.size() == 1)
            {
                final GeographicAreaCode match = (GeographicAreaCode)rMatch.get(0);
                this.mapResolution.put(sUnparsedEntry,match.getKey());
                widgetItem.setText(match.getCode()+" "+match.getName());
            }
            else
            {
                throw new RuntimeException();
            }
            widgetItem.putClientProperty("Matches",rMatch);
            data.add(widgetItem);
        }
        cg.callDoGUI(new Runnable()
        {
            public void run()
            {
                geoCalc.setListData(data);
            }
        });
    }
    
    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path
     * @return	ImageIcon of the image located at the path
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = CriterionBuilder.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            System.err.println(imgURL);
            return null;
        }
    }

    /**
     * @return Returns the mapResolution.
     */
    public Map getMapResolution()
    {
        return mapResolution;
    }
    /**
     * @return Returns the rUnparsedEntry.
     */
    public List getRUnparsedEntry()
    {
        return rUnparsedEntry;
    }
    /**
     * @return Returns the rRunningCounts.
     */
    public List getRRunningCounts()
    {
        return rRunningCounts;
    }
}
