/*
 * Created on April 6, 2005
 */
package com.surveysampling.emailpanel.counts.api;

import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.surveysampling.emailpanel.counts.api.criteria.EpanCountCriteria;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeType;
import com.surveysampling.emailpanel.counts.api.criteria.ObjectFactory;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.counts.api.request.exception.ConcurrentModificationException;
import com.surveysampling.emailpanel.xdem.metadata.AddFailedException;
import com.surveysampling.sql.LookupException;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Test code only.
 * 
 * @author Chris Mosher
 */
public class Main
{
    /**
     * @param args
     * @throws DatalessKeyAccessCreationException
     * @throws AddFailedException
     * @throws ConcurrentModificationException
     * @throws JAXBException
     * @throws SQLException
     * @throws LookupException
     * @throws ClassCastException
     */
    public static void main(String[] args) throws DatalessKeyAccessCreationException, AddFailedException, ClassCastException, ConcurrentModificationException, JAXBException, SQLException, LookupException
    {
        System.setProperty("jdbc.drivers","oracle.jdbc.driver.OracleDriver");

        final EpanCountLibrary lib = new EpanCountLibrary(",alpha,ChrisM","majic4u");

        EpanCountRequest request = lib.createEpanCountRequest();

        request.setTopic("testing");
        request.setClientName("Surveys");

        final EpanCountCriteria criteria = request.getCriteria();

        final IncomeType income = new ObjectFactory().createIncomeType();
        final List rIncome = income.getId();
        rIncome.add(IncomeEnumType.MIN_100_K);
        rIncome.add(IncomeEnumType.MIN_150_K);

        criteria.setIncome(income);

        request.buildEpanCountChildRecords();
        request = request.saveToDatabase();

        System.out.println(request.getLastChangeSerialNumber().toString());

        lib.close();
    }
}
