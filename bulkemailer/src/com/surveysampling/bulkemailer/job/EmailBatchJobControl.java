package com.surveysampling.bulkemailer.job;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMultipart;

import org.xml.sax.SAXException;

import com.surveysampling.bulkemailer.BulkEmailer;
import com.surveysampling.bulkemailer.mta.InvalidEmailException;
import com.surveysampling.email.Email;
import com.surveysampling.email.EmailType;
import com.surveysampling.util.template.TagNotFoundException;
import com.surveysampling.util.template.Template;

/**
 * Handles controlling access to (that is, parsing of) a job spec
 * file (in XML format). Also builds File specifications for the job's
 * various files.
 * 
 * @author Chris Mosher
 */
public class EmailBatchJobControl
{
    private final int mJobID;

    private EmailBatchJobHandler mHandlerJob = new EmailBatchJobHandler();
    private EmailBatchJobDataHandler mHandlerData = new EmailBatchJobDataHandler();
    private EmailHandler mEmailHandler = new EmailHandler();



    /**
     * @param jobID
     */
    public EmailBatchJobControl(int jobID)
    {
        this(jobID,-1);
    }

    /**
     * @param jobID
     * @param maxJobSize
     */
    public EmailBatchJobControl(int jobID, long maxJobSize)
    {
        mJobID = jobID;
        mEmailHandler.setMaxEmailSize(maxJobSize);
    }



    /**
     * @return job ID
     */
    public int getJobID()
    {
        return mJobID;
    }

    /**
     * @return .xml spec file
     */
    public File getSpecFile()
    {
        return buildFile(".xml", false);
    }

    /**
     * @return .log file for this job
     */
    public File getLogFile()
    {
        return buildFile(".log", false);
    }

    /**
     * @return _chk.txt file
     */
    public File getCheckpointFile()
    {
        return buildFile("_chk.txt", false);
    }

    /**
     * @return _chk_bak.txt file
     */
    public File getCheckpointBackupFile()
    {
        return buildFile("_chk_bak.txt", false);
    }

    /**
     * @return .bad file
     */
    public File getBadFile()
    {
        return buildFile(".bad", false);
    }

    /**
     * @return archive .xml spec file
     */
    public File getArchiveSpecFile()
    {
        return buildFile(".xml", true);
    }

    /**
     * @return archive .log file
     */
    public File getArchiveLogFile()
    {
        return buildFile(".log", true);
    }

    /**
     * @return archive .bad file
     */
    public File getArchiveBadFile()
    {
        return buildFile(".bad", true);
    }

    private File buildFile(String suffix, boolean archive)
    {
        File dir;
        if (archive)
            dir = BulkEmailer.getBulkEmailer().getJobArchiveFolder();
        else
            dir = BulkEmailer.getBulkEmailer().getJobFolder();

        StringBuffer s = new StringBuffer(256);
        s.append(Integer.toString(mJobID));
        s.append(suffix);

        return new File(dir, s.toString());
    }



    /**
     * @throws SAXException
     * @throws IOException
     */
    public void readFromFile() throws SAXException, IOException
    {
        /*
         * Parse the <headers>, <message>, and <control>
         * portions of the spec file
         */
        mHandlerJob.parse(getSpecFile());
        mEmailHandler.parse(getSpecFile());
    }

    /**
     * Starts this object's internal thread running, which
     * will start reading rows from the file and filling
     * up a queue (for eventual retrieval
     * by getNextEmail).
     */
    public void parseEmails()
    {
        mHandlerData.parse(getSpecFile());
    }

    /**
     * Skips the given number of emails at the
     * beginning of the file.
     * @param skip
     */
    public void skip (int skip)
    {
        mHandlerData.skip(skip);
    }

    /**
     * Get the next E-Mail message from this object's queue.
     * This method will perform the "merge" that substitues,
     * in the E-Mail message template, the tags with the
     * values from the current row (and the values from the
     * default set). It does the same "merge" for the set
     * of headers. The message text and the headers are
     * put together to form the returned EmailData object.
     * @return the email
     * @throws SAXException
     * @throws InterruptedException the job is being interrupted
     * @throws MessagingException
     * @throws TagNotFoundException
     * @throws InvalidEmailException if one of the email addresses is invalid
     */
    public Email getNextEmail() throws SAXException, InterruptedException, MessagingException, TagNotFoundException, InvalidEmailException
    {
        // this will map all tags to their correct values
        // for the current record
        Map<String,String> mapTags = new HashMap<String,String>();

        // first put the *default* tag values into our tag map
        mapTags.putAll(mHandlerJob.getDefaultTagValues());

        // read the next row from our
        // EmailBatchJobDataHandler's internal queue
        // and add its items to our map
        Map<String,String> m = mHandlerData.getNextRow();
        if (m == null) // eof
            return null;
        mapTags.putAll(m);

        Map<String,String> hdr = new HashMap<String,String>();
        for (final Map.Entry<String,Template> entry : mHandlerJob.getHeaders().entrySet())
        {
            hdr.put(
                //key:
                entry.getKey(),
                //value:
                entry.getValue().getResult(mapTags));
        }

        // create an object that holds the message
        // and headers, and return it to the caller
        return this.buildEmail(mapTags, hdr);
    }

    /**
     * @return current record number
     */
    public int getRecordNumber()
    {
        return mHandlerData.getRecordNumber();
    }

    /**
     * Returns the initial hold state (from the XML file).
     * @return boolean true if the job should start on user hold, 
     * else false.
     */
    public boolean getHold()
    {
        return mHandlerJob.getHold();
    }

    /**
     * Returns the name.
     * @return name
     */
    public String getName()
    {
        return mHandlerJob.getName();
    }

    /**
     * Returns the notify E-Mail.
     * @return notification email
     */
    public String getNotifyEmail()
    {
        return mHandlerJob.getNotify();
    }

    /**
     * Returns the initial priority (from the XML file).
     * @return initial priority value.
     */
    public int getPriority()
    {
        return mHandlerJob.getPriority();
    }

    /**
     * Returns the tier (from the XML file).
     * @return tier.
     */
    public int getTier()
    {
        return mHandlerJob.getTier();
    }

    /**
     * Returns the sample size.
     * @return sample size
     */
    public int getSampleSize()
    {
        return mHandlerJob.getSampleSize();
    }

    /**
     * Returns the send window end.
     * @return end of window
     */
    public String getSendWindowEnd()
    {
        return mHandlerJob.getSendWindowEnd();
    }

    /**
     * Returns the send window start.
     * @return start of window
     */
    public String getSendWindowStart()
    {
        return mHandlerJob.getSendWindowStart();
    }

    /**
     * Returns the ship rate.
     * @return the ship rate
     */
    public int getShipRate()
    {
        return mHandlerJob.getRate();
    }

    /**
     * @return what the ship rate is per
     */
    public int getShipRatePer()
    {
        return mHandlerJob.getRatePer();
    }

    /**
     * 
     */
    public void close()
    {
        mHandlerData.close();

        if (mEmailHandler != null)
        {
            mEmailHandler.close();
            mEmailHandler = null;
        }
    }

    /**
     * 
     */
    public void join()
    {
        mHandlerData.join();
    }

    /**
     * Builds an email message based on the information contained
     * within the two maps. 
     * @param tags A map of tags for creating the message of the email.
     * @param headers A map of email headers
     * @return an email representation. 
     * @throws MessagingException
     * @throws TagNotFoundException
     * @throws InvalidEmailException if one of the email addresses is invalid
     */
    private Email buildEmail(Map<String,String> tags, Map<String,String> headers) throws MessagingException, TagNotFoundException, InvalidEmailException
    {
        // -- get user's email type if not provided then assume client
        // -- accepts Multipart/HTML emails -- 
        String strEmailType = tags.get("EMAIL-TYPE");
        EmailType emailType = (strEmailType == null) ? EmailType.MULTIPART_HTML : EmailType.getEmailType(strEmailType);

        Email email = new Email();
        email.setContentType("text/plain");
        email.setCharacterEncoding("UTF-8");

        // -- see if email is a multipart email -- 
        boolean blnMultipartEmail = mEmailHandler.hasMultipartMessage();
        email.setMultipartEmail(blnMultipartEmail);

        if (blnMultipartEmail)
        {
            // -- get the multipart container -- 
            email.setMultipartContainer(mEmailHandler.getMultipartContainer());
        }

        Template HTMLText = mEmailHandler.getHTMLText();
        Template plainText = mEmailHandler.getPlainText();

        // -- if email contains HTML and user accepts it then add to the email bean -- 
        if (HTMLText != null && (emailType == EmailType.HTML || emailType == EmailType.MULTIPART_HTML || plainText == null))
        {
            email.setContentType("text/html");
            email.setMessage(HTMLText.getResult(tags));

            if (plainText != null)
            {
                // -- need to have multipart/alternative email to include 
                // both HTML and plain text messages -- 
                if (!blnMultipartEmail)
                {
                    email.setMultipartEmail(true);
                    email.setMultipartContainer(new MimeMultipart("alternative"));
                    blnMultipartEmail = true;
                }

                email.setAlternativeMsg(plainText.getResult(tags));
            }
        }
        else if (plainText != null)
        {
            // either no html text or user only wants to receive plain text. 
            email.setMessage(plainText.getResult(tags));
        }
        else
        {
            // -- neither plain text or HTML mesage has been specified. 
            // this should have been caught during parsing of file ---
            throw new MessagingException("This email does not have a plain text or HTML version");
        }

        // -- now add rest of headers to email -- 
        addHeadersToEmail(headers, email);

        return email;
    }

    /**
     * Adds all headers to the email.
     * @param headers The map of headers.
     * @param email The current email.
     * @throws InvalidEmailException if one of the email addresses is invalid
     */
    private static void addHeadersToEmail(Map<String,String> headers, Email email) throws InvalidEmailException
    {
        /*
        * get all the headers, and add them, checking
        * for the special ones: from, reply, to, subject.
        * Others will get added as generic headers.
        */
        for (final Map.Entry<String,String> entry : headers.entrySet())
        {
            final String sHeader = entry.getKey();
            final String sValue = entry.getValue();
            try
            {
                addHeaderToEmail(email,sHeader,sValue);
            }
            catch (AddressException e)
            {
                throw new InvalidEmailException(sValue,e);
            }
        }
    }

    /**
     * @param email
     * @param sHeader
     * @param sValue
     * @throws AddressException
     */
    private static void addHeaderToEmail(Email email, String sHeader, String sValue) throws AddressException
    {
        if (sHeader.equalsIgnoreCase("from"))
        {
            email.setFrom(sValue);
        }
        else if (sHeader.equalsIgnoreCase("reply"))
        {
            email.addReplyTo(sValue);
        }
        else if (sHeader.equalsIgnoreCase("to"))
        {
            email.addRecipient(sValue);
        }
        else if (sHeader.equalsIgnoreCase("cc"))
        {
            email.addCcRecipient(sValue);
        }
        else if (sHeader.equalsIgnoreCase("bcc"))
        {
            email.addBccRecipient(sValue);
        }
        else if (sHeader.equalsIgnoreCase("subject"))
        {
            email.setSubject(sValue);
        }
        else
        {
            email.addHeader(sHeader, sValue);
        }
    }
}
