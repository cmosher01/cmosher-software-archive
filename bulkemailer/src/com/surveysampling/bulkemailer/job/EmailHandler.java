package com.surveysampling.bulkemailer.job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.surveysampling.activation.ByteDataSource;
import com.surveysampling.email.BinaryConverter;
import com.surveysampling.email.EmailType;
import com.surveysampling.email.BinaryConverter.EncodingType;
import com.surveysampling.util.template.Template;
import com.surveysampling.xml.XMLUtil;

/**
 * Class for creating an Email from an XML file.
 * The email can be simply text or can be a multipart email. 
 *
 * @author Paul Sideleau
 * @version 1.0
 * @since BulkEmailer 1.1
 */
public class EmailHandler extends DefaultHandler
{

    /** -- data of current element processing **/
    private final StringBuffer currentData = new StringBuffer();

    /**  the current body part that is being configured **/
    private BodyPart bodypart = null;

    /** indicates if in BodyPart Element **/
    private boolean blnInBodypart = false;

    /** indicates if in header element **/
    private boolean blnInHeader = false;

    /** determines if handler is in a nessage element **/
    private boolean blnInMessage = false;

    /** determines if handler is in a email-type element **/
    private boolean blnInEmailType = false;

    /** 
     * indicates if within the First Multipart Element which
     * is the container
     */
    private boolean blnFirstMultipart = true;

    /** content type of the message **/
    private String contentType = "text/plain";

    /** hold value for current body part header, Content-Transfer-Encoding **/
    private String contentTransferEncoding = null;

    /** name of the current mime header **/
    private String currentHeader = null;

    /** email object that will be created **/
    private Multipart multipartContainer = null;

    /** the multipart type that the container is **/
    private String multipartContainerType = null;

    /** current size of email in bytes **/
    private long lngEmailSizeBytes = NO_EMAIL_SIZE_LIMIT;

    /** maximum size that the email can be in bytes **/
    private long lngMaxEmailSizeBytes;

    /** Constant indicating their is no limit on the size of the eamil **/
    public final static byte NO_EMAIL_SIZE_LIMIT = -1;

    /** 
     * An binaryConverter for encoding and decoding binary data to a character
     * representation. It defaults to use BASE64
     */
    private final BinaryConverter binaryConverter = new BinaryConverter(BinaryConverter.EncodingType.BASE64);

    /** 
     * stack used to keep track of what multipart object the handler
     * is working on. 
     */
    private final List<Multipart> multipartStack = new LinkedList<Multipart>();

    /** indicates whether the handler has parsed the XML **/
    private boolean blnHasParsed;

    /** plain text message body **/
    private Template mPlainText;

    /** HTML message body **/
    private Template mHTMLText;

    /** holds all email types for users **/
    private final Set<EmailType> setEmailType = new HashSet<EmailType>();

    /** indicates if EmailHandler should do strict 
     * validation of the XML file. Defaults to true.
     */
    private boolean blnStrictContentValidating = true;



    /**
     * Constructs a new EmailHandler
     */
    public EmailHandler()
    {
        // need to have this constructor
    }

    /**
     * Creates an email handler specifying the maximum
     * number of bytes that an email can be.
     * 
     * @param maxEmailSizeBytes The maximum number of
     * bytes that the email can be. 
     * @see #setMaxEmailSize(long)
     */
    public EmailHandler(long maxEmailSizeBytes)
    {
        this.setMaxEmailSize(maxEmailSizeBytes);
    }

    /**
     * Reads an element's content into a string buffer. 
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
    {
        // -- only bother to read data when in a message 
        // or email-type element --- 
        if (this.blnInMessage || this.blnInEmailType)
        {
            currentData.append(ch, start, length);
        }
    }

    /**
     * Converts XML structure to javax.mail Objects 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {

        String name = qName.toLowerCase().trim();

        if (name.equals("email-type"))
        {
            this.blnInEmailType = true;
        }
        if (name.equals("message"))
        {
            this.blnInMessage = true;
            String strStrict = attributes.getValue("strict-content-validation");
            this.blnStrictContentValidating = (strStrict == null || strStrict.equalsIgnoreCase("true") || strStrict.equals("1"));
        }
        if (name.equals("multipart"))
        {
            this.createMultipart(attributes.getValue("type"));
        }
        else if (name.equals("body-part"))
        {
            this.blnInBodypart = true;
            this.createBodyPart();
        }
        else if (name.equals("body-header") && this.blnInBodypart == true)
        {
            this.blnInHeader = true;
        }
        else if (name.equals("data"))
        {
            // do nothing
        }
        else if (name.equals("plain-text"))
        {
            // do nothing
        }
        else if (name.equals("html-text"))
        {
            // -- do nothing -- 
        }
        else if (this.blnInHeader == true)
        {
            this.currentHeader = qName.trim();
        }
    }

    /** 
     * finish converting XML element into Java Mail object. 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        String name = qName.toLowerCase().trim();

        if (name.equals("message"))
        {
            this.blnInMessage = false;
        }
        if (name.equals("email-type"))
        {
            try
            {
                this.blnInEmailType = false;
                String strType = this.currentData.toString().trim();
                EmailType type = EmailType.getEmailType(strType);
                this.setEmailType.add(type);
            }
            catch (MessagingException iae)
            {
                throw (SAXException)new SAXException("Error in element emailtype: " + iae.getMessage()).initCause(iae);
            }

        }
        else if (name.equals("multipart"))
        {
            // -- pop the current multipart from top of stack
            // since we are finished with it --
            this.multipartStack.remove(this.multipartStack.size() - 1);
        }
        else if (name.equals("body-part"))
        {
            this.blnInBodypart = false;
        }
        else if (name.equals("body-header") && this.blnInBodypart == true)
        {
            this.blnInHeader = false;
            this.currentHeader = null;
        }
        else if (name.equals("data"))
        {
            this.createEmailContent();
        }
        else if (name.equals("plain-text"))
        {
            String plainText = this.currentData.toString().trim();
            if (plainText.length() == 0)
            {
                throw new SAXException("Cannot have a PlainText element " + " with no text in it.");
            }

            // -- ensure that email's size hasn't exceed the max limit. --
            this.lngEmailSizeBytes += plainText.getBytes().length;
            this.isAboveMaxEmailSize();
            try
            {
                this.mPlainText = new Template(plainText,"<field>","</field>");
            }
            catch (Throwable e)
            {
                SAXException ex = new SAXException("Error parsing plain text template");
                ex.initCause(e);
                throw ex;
            }
        }
        else if (name.equals("html-text"))
        {
            String HTMLText = this.currentData.toString().trim();
            if (HTMLText.length() == 0)
            {
                throw new SAXException("Cannot have a HTMLText element " + " with no text in it.");
            }

            // -- ensure that email's size hasn't exceed the max limit. --
            this.lngEmailSizeBytes += HTMLText.getBytes().length;
            this.isAboveMaxEmailSize();
            try
            {
                this.mHTMLText = new Template(HTMLText,"<field>","</field>");
            }
            catch (Throwable e)
            {
                SAXException ex = new SAXException("Error parsing HTML template");
                ex.initCause(e);
                throw ex;
            }
        }
        else if (this.blnInHeader == true)
        {
            this.addHeader();
        }

        // -- always reset the current StringBuffer when end of
        // element has been reached. 
        this.currentData.setLength(0);

    }

    /** 
     * Gets a shallow copy of the Multipart Container that has all multipart 
     * body parts if the handler processe an Multipart email. A 
     * shallow copy is returned so that the Multipart's parts can
     * be repositioned or removed or new parts can be added to the
     * Multipart container without affecting the orignial Multipart
     * container. 
     * @return the Multipart container or null if the handler
     * did not process a multipart email. 
     * @throws MessagingException 
     * @throws IllegalStateException if method is called
     * before the XML file is parsed. 
     */
    public Multipart getMultipartContainer() throws MessagingException
    {
        this.hasParsed();

        if (this.multipartContainer != null)
        {
            // create a shallow clone. Create a new Multipart object and
            // add the container's body parts to it. 
            Multipart shallowClone = new MimeMultipart(this.multipartContainerType);
            int count = this.multipartContainer.getCount();

            for (int i = 0; i < count; i++)
            {
                shallowClone.addBodyPart(this.multipartContainer.getBodyPart(i));
            }

            return shallowClone;
        }
        return null;
    }

    /**
     * Gets the plain text content.
     * The value will not be null
     * when either a text element was found
     * or a multipart body with a content
     * type of text/plain.
     * @return the plain text or null if
     * there is none. 
     * @throws IllegalStateException if method is called
     * before the XML file is parsed. 
     */
    public Template getPlainText()
    {
        this.hasParsed();
        return this.mPlainText;
    }

    /**
    * Gets the HTML text.
    * The value will not be null
    * only when the invitation is a multipart
    * email and one of its body parts has a
    * content type of text/html. 
    * @return the HTML text or null if
    * there is none. 
    * @throws IllegalStateException if method is called
    * before the XML file is parsed. 
    */
    public Template getHTMLText()
    {
        this.hasParsed();
        return this.mHTMLText;
    }

    /**
     * Creates a Multipart Object of the specified type. 
     * @param multipartType The type of multipart object to create
     * (alternative, mixed, related)
     * @throws SAXException if multipartType is null or empty String.
     */
    private void createMultipart(String multipartType) throws SAXException
    {
        // -- first ensure type is not null and not empty string -- 
        String errorMsg = "Multipart cannot be created without specifying a type.";

        if (multipartType == null)
        {
            throw new SAXException(errorMsg);
        }

        multipartType = multipartType.trim();

        if (multipartType.length() == 0)
        {
            throw new SAXException(errorMsg);
        }

        // -- create multipart -- 
        Multipart multipart = new MimeMultipart(multipartType);

        // if multipart is being created witin a BodyPart
        // then it must be the body part's content
        if (this.blnInBodypart == true)
        {
            try
            {
                assert(this.bodypart != null) : "bodypart was null while blnInBodypart is true";
                this.bodypart.setContent(multipart);
            }
            catch (MessagingException me)
            {
                throw (SAXException)new SAXException(me).initCause(me);
            }
        }

        // -- then this is the container that will
        // hold all the body parts -- 
        if (this.blnFirstMultipart == true)
        {
            this.multipartContainer = multipart;
            this.multipartContainerType = multipartType;
            this.blnFirstMultipart = false;
        }

        // -- place current multipart on top of stack --
        this.multipartStack.add(multipart);
    }

    /**
     * Creates A MimeBodyPart object and adds it to the 
     * latest Multipart object that has been created. 
     * @throws SAXException if error occurs creating the 
     * body part. 
     */
    private void createBodyPart() throws SAXException
    {
        this.bodypart = new MimeBodyPart();

        try
        {
            // add bodypart to its Multipart Container by getting
            // the last multipart created from the top of the stack. 
            int stackIdx = this.multipartStack.size() - 1;
            assert(stackIdx >= 0) : "programming error. Multipart stack is empty";

            Multipart multi = this.multipartStack.get(stackIdx);
            multi.addBodyPart(this.bodypart);
        }
        catch (MessagingException me)
        {
            throw (SAXException)new SAXException("Error creating body part: " + me.getMessage()).initCause(me);
        }
    }

    /**
     * Creates the content of the email.
     * If the current content type is text, such as text/plain
     * or text/html then the data is trimmed and added to the
     * current Body. Otherwise, it is assumed that the data 
     * is binary data from a file or image and that it is 
     * encoded in Base64. The data will then be decoded
     * and add to the body using a DataHandler. The data 
     * must be decoded because the Java Mail will reencode it
     * in Base64 when the message is sent. There is no way
     * to control this. 
     * @throws SAXException if error occurs adding content
     * to the current body part or if an error occurs decoding
     * binary data in Base64 format. 
     */
    private void createEmailContent() throws SAXException
    {
        // -- assert preassumptions -- 
        assert(this.currentData != null) : "CurrentData was null.";
        assert(this.bodypart != null) : "BodyPart was null.";
        assert(this.contentType != null) : "ContentType was null.";

        String data = this.currentData.toString().trim();

        try
        {
            String tempContentType = this.contentType.toLowerCase();
            if (tempContentType.startsWith("text"))
            {
                // -- data is just text data then -- 
                this.bodypart.setContent(data, this.contentType);

                //-- ensure that email's size hasn't exceed the max limit. --
                this.lngEmailSizeBytes += data.getBytes().length;
                this.isAboveMaxEmailSize();
            }
            else
            {
                // -- data must be binary, such as a file or image -- 

                // -- convert data into bytes so it can be decoded. -- 
                ByteArrayInputStream byteInputStream = new ByteArrayInputStream(data.getBytes());

                // -- ensure that a content transfer encoding has been provided -- 
                if (this.contentTransferEncoding == null)
                {
                    throw new SAXException(
                        "For BodyParts that have a content type of "
                            + this.contentType
                            + ", the header, Content-Transfer-Encoding must be specified.");
                }

                // -- decode the encoded bytes -- 
                this.binaryConverter.setEncodingType(EncodingType.getEncodingType(this.contentTransferEncoding));
                byte[] decodedBytes = this.binaryConverter.decode(byteInputStream);

                // -- ensure that email's size hasn't exceed the max limit. --
                this.lngEmailSizeBytes += decodedBytes.length;
                this.isAboveMaxEmailSize();

                //-- add information to current body part using
                // a byte data source
                ByteDataSource source = new ByteDataSource(decodedBytes, this.contentType);

                this.bodypart.setDataHandler(new DataHandler(source));
            }
        }
        catch (IllegalArgumentException iae)
        {
            // -- content transfer encoding has invalid encoding -- 
            throw (SAXException)new SAXException(this.contentTransferEncoding + " is an invalid email encoding type.").initCause(iae);
        }
        catch (BinaryConverter.DecodingException de)
        {
            // -- error decoding data -- 
            throw (SAXException)new SAXException().initCause(de);
        }
        catch (MessagingException me)
        {
            // -- error adding data to the body part -- 
            throw (SAXException)new SAXException().initCause(me);
        }
    }

    /**
     * Adds a header to the current body part
     * @throws SAXException if error occurs adding
     * header to the current body part. 
     */
    private void addHeader() throws SAXException
    {
        assert(this.currentHeader != null) : "currentHeader was null";
        assert(this.currentData != null) : "currentData was null";
        assert(this.bodypart != null) : "bodyPart was null";

        // -- get value of the header -- 
        String value = this.currentData.toString().trim();

        // --- need to keep track of the content type to
        // properly handle reading the data of the body part -- 
        if (this.currentHeader.equalsIgnoreCase("Content-Type"))
        {
            this.contentType = value;
        }
        else if (this.currentHeader.equalsIgnoreCase("Content-Transfer-Encoding"))
        {
            this.contentTransferEncoding = value;
        }

        try
        {
            this.bodypart.addHeader(this.currentHeader, value);
        }
        catch (MessagingException me)
        {
            throw (SAXException)new SAXException("Error adding header in method addHeader(): " + me.getMessage()).initCause(me);
        }
    }

    /**
     * Parses the specified XML file with the specified
     * schema using this handler as the parser's
     * data handler. 
     * @param xmlfile The xml file to parse against. 
     * @throws SAXException if error occurs parsing the file. 
     * @throws IOException if error occurs reading the 
     * specified files. 
     */
    public void parse(File xmlfile) throws SAXException, IOException
    {
        URL url = getClass().getClassLoader().getResource("com/surveysampling/bulkemailer/job/job.xsd");
        XMLUtil.parse(xmlfile, url.toString(), this);
    }

    /**
     * Checks if the handler has processed a Multipart
     * email. 
     * @return ture if the handler processed a Multipart
     * email. 
     * @throws IllegalStateException if method is called
     * before the XML file is parsed. 
     */
    public boolean hasMultipartMessage()
    {
        this.hasParsed();
        return (this.multipartContainer != null);
    }

    /**
     * Gets the maximum email size in bytes that an email
     * can be. 
     * @return the maximum email size that an email
     * can be or NO_EMAIL_SIZE_LIMIT if there is no
     * limit. 
     */
    public long getMaxEmailSize()
    {
        return this.lngMaxEmailSizeBytes;
    }

    /**
     * Set the maximum email size that an email can be in
     * bytes.
     * @param maxBytes The maximum email size. If it is &alt; 0 then
     * it will be set to NO_EMAIL_SIZE_LIMIT indicating there there
     * is no restrictions on the size of the email. 
     */
    public void setMaxEmailSize(long maxBytes)
    {
        this.lngMaxEmailSizeBytes = (maxBytes < 0) ? NO_EMAIL_SIZE_LIMIT : maxBytes;
    }

    /**
     * Gets whether strict content validating is turned on.
     * 
     * @return true if handler will perform strict content
     * validation. 
     * 
     * @throws IllegalStateException if method is called before the
     * handler has succesfully parsed the information.
     */
    public boolean isStrictContentValidatingOn()
    {
        this.hasParsed();
        return this.blnStrictContentValidating;
    }

    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    @Override
    public void endDocument() throws SAXException
    {
        // -- if strict content validating is on, then
        // ensure that every user will be able to receive the email -- 
        if (this.blnStrictContentValidating == true)
        {
            // -- if this is a multipart email but 
            // -- one or more people cannot accept multipart
            // -- emails then throw SAX exception
            boolean noMultipart = (this.setEmailType.contains(EmailType.TEXT) || this.setEmailType.contains(EmailType.HTML));

            // -- if this email contains HTML text and no Plain text
            // -- and one ore more people only want to view plain text
            // -- then throw SAX Exception
            boolean noHTML = (this.setEmailType.contains(EmailType.TEXT) || this.setEmailType.contains(EmailType.MULTIPART_TEXT));

            if (this.multipartContainer != null && noMultipart == true)
            {
                throw new SAXException(
                    "The email in this XML file is a"
                        + " Multipart Email. However, one or more email address(es) has(have) an EmailType"
                        + " of "
                        + EmailType.TEXT
                        + " or "
                        + EmailType.HTML
                        + " which means the user cannot receive"
                        + " a multipart email. Therefore, this file is invalid.");
            }
            else if (this.mHTMLText != null && noHTML == true && this.mPlainText == null)
            {
                throw new SAXException(
                    "The email in this XML file contains HTML"
                        + " text without a plain text message. However, one or more "
                        + "email addresses only will accept plain text messages. "
                        + "Therefore, the file is invalid.");
            }
            else if (this.mHTMLText == null && this.mPlainText == null)
            {
                throw new SAXException("Cannot have an email without a Message. No " + "HTML or plain text was provided.");
            }

        } // -- end if strict validating is true -- 

        this.blnHasParsed = true;
    }

    /**
     * Ensures that the XML file has been parsed.
     * @throws IllegalStateException if XML file has
     * not been parsed. 
     */
    private void hasParsed()
    {
        if (this.blnHasParsed == false)
        {
            throw new IllegalStateException("XML file must be successfully parsed before calling this method.");
        }
    }

    /**
     * Checks if the maximum email size has been exceeded
     * if the maximum email size check is turned on. 
     * @return false if the maximum email size has <b>not</b>
     * been exceeded or if the maximum email size checks is
     * turned <b>off</b>/
     * 
     * @throws SAXException if the maximum email size has been
     * exceeded. 
     */
    private boolean isAboveMaxEmailSize() throws SAXException
    {
        if (this.lngMaxEmailSizeBytes != NO_EMAIL_SIZE_LIMIT && this.lngEmailSizeBytes > this.lngMaxEmailSizeBytes)
        {
            throw new SAXException(
                "The email has exceeded the maximum "
                    + " size limit of "
                    + this.lngMaxEmailSizeBytes
                    + " bytes. The email is at least "
                    + this.lngEmailSizeBytes
                    + " bytes in length.");
        }

        return false;
    }

    /**
     * Closes the emailhandler. This will free all resources
     * that the EmailHandler has in memory.
     * Once this method has been called, the email handler will
     * need to reparse that data if it still needs to be
     * used. Hence, it should be called only when the
     * EmailHandler is no longer needed.
     */
    public void close()
    {
        this.blnHasParsed = false;
        this.bodypart = null;
        this.multipartContainer = null;
        this.setEmailType.clear();
        this.multipartStack.clear();
        this.currentData.setLength(0);
        this.blnFirstMultipart = false;
        this.blnInBodypart = false;
        this.blnInHeader = false;
        this.blnStrictContentValidating = true;
        this.contentTransferEncoding = null;
        this.contentType = null;
        this.currentHeader = null;
        this.mHTMLText = null;
        this.multipartContainerType = null;
        this.mPlainText = null;
    }

}
