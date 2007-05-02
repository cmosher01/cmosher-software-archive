/*
 * Created on May 11, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.surveysampling.emailpanel.counts.api.criteria.EpanCountCriteria;

/**
 * Provides static methods to read, write, and clone
 * an <code>EpanCountCriteria</code> object.
 * 
 * @author Chris Mosher
 */
public final class EpanCountCriteriaAccess
{
    private EpanCountCriteriaAccess()
    {
        assert false;
    }



    /**
     * 32K, length of a fairly large criteria XML text
     */
    private static final int FAIRLY_LARGE_CRITERIA_LENGTH = 0x8000;



    /**
     * Gets a new <code>EpanCountCriteria</code> object by unmarshalling
     * the XML from the given column in a <code>RecordSet</code>. Uses JAXB.
     * @param rs
     * @param column
     * @return the <code>EpanCountCriteria</code> object read from <code>rs</code>
     * @throws SQLException
     * @throws JAXBException
     */
    public static EpanCountCriteria getCriteriaFromResultSet(final ResultSet rs, final int column) throws SQLException, JAXBException
    {
        /*
         * Get the XML from the RecordSet into a StringBuffer.
         */
        final StringBuffer sbCriteria = new StringBuffer(FAIRLY_LARGE_CRITERIA_LENGTH);
        OracleClobAccess.getClobFromResultSet(rs,column,sbCriteria);

        /*
         * Use a JAXB Unmarshaller to read from the XML and
         * create a new EpanCountCriteria object representing
         * all the XML data.
         */
        final JAXBContext jaxb = JAXBContext.newInstance(EpanCountCriteria.class.getPackage().getName());
        final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
        return (EpanCountCriteria)unmarshaller.unmarshal(new StreamSource(new StringReader(sbCriteria.toString())));
    }

    /**
     * Puts an existing <code>EpanCountCriteria</code> object into a given
     * <code>PreparedStatement</code>. Uses JAXB.
     * @param criteria
     * @param st
     * @param parameterIndex
     * @throws SQLException
     * @throws JAXBException
     */
    public static void putCriteriaToPreparedStatement(final EpanCountCriteria criteria, final PreparedStatement st, final int parameterIndex) throws SQLException, JAXBException
    {
        /*
         * Use a JAXB Marshaller to get data from the
         * EpanCountCriteria object and write it to a StringWriter.
         */
        final JAXBContext jaxb = JAXBContext.newInstance(EpanCountCriteria.class.getPackage().getName());
        final Marshaller marshaller = jaxb.createMarshaller();
        final StringWriter swXML = new StringWriter(FAIRLY_LARGE_CRITERIA_LENGTH);
        marshaller.setProperty("jaxb.formatted.output",Boolean.TRUE);
        marshaller.marshal(criteria,new StreamResult(swXML));

        /*
         * Get the XML from the StringWriter and
         * put it to the PreparedStatement.
         */
        OracleClobAccess.putClobToPreparedStatement(swXML.getBuffer(),st,parameterIndex);
    }

    /**
     * Clones an <code>EpanCountCriteria</code> object. Uses JAXB.
     * @param criteria
     * @return clone of <code>criteria</code>
     * @throws JAXBException
     */
    public static EpanCountCriteria cloneCriteria(final EpanCountCriteria criteria) throws JAXBException
    {
        final JAXBContext jaxb = JAXBContext.newInstance(EpanCountCriteria.class.getPackage().getName());

        final Marshaller marshaller = jaxb.createMarshaller();
        final Unmarshaller unmarshaller = jaxb.createUnmarshaller();

        final StringWriter swXML = new StringWriter(EpanCountCriteriaAccess.FAIRLY_LARGE_CRITERIA_LENGTH);

        /*
         * Use a JAXB Marshaller to get data from the
         * EpanCountCriteria object and write it to a StringWriter,
         * then use a JAXB Unmarshaller to read from the StringWriter and
         * create a new EpanCountCriteria object (which will be a
         * clone of the original object).
         */
        marshaller.marshal(criteria,new StreamResult(swXML));
        return (EpanCountCriteria)unmarshaller.unmarshal(new StreamSource(new StringReader(swXML.toString())));
    }
}
