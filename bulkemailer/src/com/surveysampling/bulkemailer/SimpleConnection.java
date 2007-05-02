package com.surveysampling.bulkemailer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.surveysampling.bulkemailer.job.EmailBatchJobControl;
import com.surveysampling.bulkemailer.job.Job;
import com.surveysampling.bulkemailer.mta.Mta;
import com.surveysampling.bulkemailer.mta.MtaManager;
import com.surveysampling.bulkemailer.mta.MtaState;
import com.surveysampling.bulkemailer.util.DailyWindow;
import com.surveysampling.bulkemailer.util.HttpUtil;
import com.surveysampling.bulkemailer.util.ThrottlePrecise;
import com.surveysampling.bulkemailer.util.TimeOfDay;
import com.surveysampling.bulkemailer.util.TimeOfDayParser;
import com.surveysampling.util.DeltaTime;
import com.surveysampling.util.ThreadUtil;
import com.surveysampling.xml.XMLUtil;

/**
 * Handles a client connection.
 * 
 * @author Chris Mosher
 */
final class SimpleConnection
{
    // TODO Internet Explorer (as of V6.0 SP1), can't handle application/xhtml+xml content type
//    private static final String CONTENT_TYPE = "application/xhtml+xml";
    private static final String CONTENT_TYPE = "text/html";

    private final Socket socket;
    private boolean quit;

    private final Thread thread = new Thread(new Runnable()
    {
        public void run()
        {
            try
            {
                doRun();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    });



    /**
     * Initializes a connection, given a client's socket. The
     * connection processing will be handled in a new thread,
     * internal to this object.
     * @param s the client's socket
     */
    public SimpleConnection(Socket s)
    {
        this.socket = s;
        this.thread.start();
    }



    /**
     * Handles the connection to the client. Reads commands
     * from the client and sends response(s) back.
     * @throws IOException
     */
    protected void doRun() throws IOException
    {
        // set thread name
        StringBuffer sbThreadName = new StringBuffer(256);
        sbThreadName.append(getClass().getName());
        sbThreadName.append(" (");
        sbThreadName.append(this.socket.getInetAddress().getHostAddress());
        sbThreadName.append(")");
        Thread.currentThread().setName(sbThreadName.toString());

        BufferedReader in = null;
        PrintWriter out = null;
        try
        {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(),"UTF-8"));
            out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(),"UTF-8"),true);

            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            if (!in.ready())
            {
                out.println(BulkEmailer.getHerald());
                out.println("READY");
            }

            while (doCommand(in, out))
            {
                // keep processing commands
            }
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
            try
            {
                this.socket.close();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }

    private boolean doCommand(BufferedReader in, PrintWriter out) throws IOException
    {
        boolean go = true;

        String s = in.readLine();
        if (s == null)
        {
            return false;
        }

        StringTokenizer t = new StringTokenizer(s);
        // get the command
        String sCommand = "";
        if (t.hasMoreTokens())
            sCommand = t.nextToken();
        // get (only one) parameter
        String sParam = "";
        if (t.hasMoreTokens())
            sParam = t.nextToken();

        /*
         * The SUBMIT command retrieves a
         * multi-line submission (it currently
         * handles only *job* submissions).
         * The GET and POST commands handle web browsers
         * connecting. GET calls getWebPage
         * (passing the parameter, which is
         * the "file name" of the web page requested,
         * and the query string, which is the name=value
         * pairs after the question mark)
         * to build the HTML text.
         */
        if (sCommand.equalsIgnoreCase("submit"))
        {
            if (sParam.equalsIgnoreCase("job"))
            {
                int jobID = BulkEmailer.getBulkEmailer().getNextJobID();
                EmailBatchJobControl ec = new EmailBatchJobControl(jobID);
                File fileSpec = ec.getSpecFile();

                boolean ok = false;
                try
                {
                    readJobIntoXmlFile(in,fileSpec);
                    if (isQuitting())
                    {
                        return false;
                    }

                    URL url = getClass().getClassLoader().getResource("com/surveysampling/bulkemailer/job/job.xsd");
                    XMLUtil.parse(fileSpec, url.toString(), null);

                    ok = true;

                    // if all is OK, start the job
                    BulkEmailer.getBulkEmailer().addJob(jobID);

                    out.println("OK " + jobID);
                }
                catch (SocketException e)
                {
                    /*
                     * Assume that if we get a SocketException, then we
                     * won't try sending it back to the client; just re-throw it.
                     */
                    throw e;
                }
                catch (Exception e)
                {
                    /*
                     * Send any other exception back to the client
                     * as an "ERROR" message.
                     */
                    e.printStackTrace();
                    out.println(filterErrorMessage(e.toString()));
                }
                finally
                {
                    if (!ok)
                    {
                        fileSpec.delete();
                    }
                }
            }
            else
            {
                out.println(filterErrorMessage("unrecognized submission " + sParam));
            }
        }
        else if (sCommand.equalsIgnoreCase("get"))
        {
            // assume this is an HTTP GET request of
            // the form "GET <URI> HTTP/1.<v>"

            // read and ignore the rest of the request
            while (in.ready())
            {
                in.read();
            }

            if (sParam.length() == 0)
            {
                // oops, no parameter (probably means the
                // client is not really a browser)
                out.println(filterErrorMessage("unrecognized command get"));
            }
            else
            {
                // parse the URI part of the request
                // to separate it into the path (sParam)
                // and the query parameter string (sQuery)
                String sQuery = null;
                String sPage = null;
                try
                {
                    URI uri = new URI(sParam);
                    sQuery = uri.getQuery();
                    sPage = uri.getPath();
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
                if (sQuery == null)
                {
                    sQuery = "";
                }
                if (sPage == null)
                {
                    sPage = "";
                }

                // parse the query parameter string
                Map<String,String> mapParamToValue = new HashMap<String,String>();
                HttpUtil.parseQueryStringSimple(sQuery,mapParamToValue);

                // call subclass to build content (or redirect)
                StringBuffer content = new StringBuffer(8192);
                StringBuffer redirect = new StringBuffer();
                getWebPage(sPage, mapParamToValue, content, redirect);

                // build the response for the browser
                StringBuffer response = new StringBuffer(content.length()+256);

                if (redirect.length() > 0)
                {
                    appendRedirect(response, redirect);
                }
                else
                {
                    appendOK(response, content);
                }

                // send the response back to the client
                out.print(response.toString());
                out.flush();

                go = false;
            }
        }
        else if (sCommand.equalsIgnoreCase("post"))
        {
            // read and ignore the rest of the request
            while (in.ready())
            {
                in.read();
            }

            if (sParam.equalsIgnoreCase("/shutdown"))
            {
                StringBuffer content = new StringBuffer(8192);
                appendHeader(content, "Shut Down Server", false, false);
                content.append("The server has been shut down.\n");
                appendFooter(content);

                StringBuffer response = new StringBuffer(content.length()+256);
                appendOK(response, content);
                out.print(response.toString());
                out.flush();

                BulkEmailer.getBulkEmailer().serverShutdown(this.socket.getInetAddress().getHostAddress());
                go = false;
            }
        }
        else
        {
            out.println("unrecognized command " + sCommand + " " + sParam);
        }

        return go;
    }

    /**
     * Reads the job specs from the given reader and writes them
     * to the given xml file.
     * @param in BufferedReader to read the specs from
     * @param fileXML the (.XML) file to write the specs to
     * @throws IOException
     */
    private void readJobIntoXmlFile(BufferedReader in, File fileXML) throws IOException
    {
        BufferedWriter outSpec = null;
        try
        {
            outSpec = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileXML),"UTF-8"));
            outSpec.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            outSpec.newLine();

            // read until line with lone period
            String sub = in.readLine();
            while (sub != null && !sub.equalsIgnoreCase(".") && !isQuitting())
            {
                // add each line to the xml file
                outSpec.write(sub);
                outSpec.newLine();

                sub = in.readLine();
            }
            if (sub == null)
            {
                throw new SocketException("Client dropped connection while sending job specification.");
            }
        }
        finally
        {
            if (outSpec != null)
            {
                try
                {
                    outSpec.close();
                }
                catch (Throwable ignore)
                {
                    ignore.printStackTrace();
                }
            }
        }
    }

    private static void appendRedirect(StringBuffer response, StringBuffer redirect)
    {
        response.append("HTTP/1.0 307 Temporary Redirect\r\n");
        
        response.append("Location: ");
        response.append(redirect);
        response.append("\r\n");
        
        response.append("Connection: close\r\n");
        response.append("Pragma: no-cache\r\n");
        response.append("Cache-Control: no-cache\r\n");
        response.append("\r\n");
    }

    private static void appendOK(StringBuffer response, StringBuffer content)
    {
        response.append("HTTP/1.0 200 OK\r\n");
        
        response.append("Content-Length: " + Integer.toString(content.length()) + "\r\n");
        response.append("Content-Type: "+CONTENT_TYPE+"; charset=UTF-8\r\n");
        
        response.append("Connection: close\r\n");
        response.append("Pragma: no-cache\r\n");
        response.append("Cache-Control: no-cache\r\n");
        response.append("\r\n");
        
        response.append(content);
    }

    private static String filterErrorMessage(String s)
    {
        StringBuffer sb = new StringBuffer(256);

        sb.append("ERROR ");
        if (s != null)
        {
            for (int i = 0; i < s.length(); ++i)
            {
                char c = s.charAt(i);
                if (Character.isWhitespace(c))
                    sb.append(' ');
                else
                    sb.append(c);
            }
        }

        return sb.toString();
    }

    private void getWebPage(String sPage, Map<String,String> sParams, StringBuffer content, StringBuffer redirect)
    {
        SimpleDateFormat formatDHM = new SimpleDateFormat("dd-HH:mm");

        if (sPage.equalsIgnoreCase("/"))
        {
            appendHeader(content, "Jobs");

            content.append("\n<table ");
            content.append("border=\"0\" bgcolor=\"#CCCCCC\" cellpadding = \"1\" cellspacing=\"1\"");
            content.append(">\n");
            content.append("<tr bgcolor=\"white\">");
            content.append("<th>name</th>");
            content.append("<th>ID</th>");
            content.append("<th>size</th>");
            content.append("<th>sent</th>");
            content.append("<th>last sent</th>");
            content.append("<th>rate</th>");
            content.append("<th>window</th>");
            content.append("<th>pty</th>");
            content.append("<th>tier</th>");
            content.append("<th>state</th>");
            content.append("<th>delete</th>");
            content.append("<th>hold</th>");
            content.append("<th>start&nbsp;time</th>");
            content.append("<th>elap&nbsp;time</th>");
            content.append("</tr>\n");

            List<Job> rJob = new ArrayList<Job>(10);
            BulkEmailer.getBulkEmailer().getSchedule((List)rJob);
            Collections.<Job>sort(rJob,new JobDisplayComparator());

            for (final Job job : rJob)
            {
                String sColor;
                String sColorDk;
                switch (job.getSimpleState())
                {
                    case 0 :
                        sColor = "EEFFEE"; //green
                        sColorDk = "CCFFCC";
                        break;

                    case 1 :
                        sColor = "FFFFEE"; //yellow
                        sColorDk = "FFFFCC";
                        break;

                    default :
                        sColor = "FFEEEE"; //red
                        sColorDk = "FFCCCC";
                }

                content.append("<tr");
                if (sColor.length() > 0)
                {
                    content.append(" bgcolor=\"#");
                    content.append(sColor);
                    content.append("\"");
                }

                content.append("><td>");

                // begin: job name column
                content.append("<a href=\"editjob?id=");
                content.append(job.getJobID());
                content.append("\">");
                content.append(job.getJobName());
                content.append("</a>");

                content.append("<br />\n");

                // begin: progress bar
                content.append("<table cellspacing=\"0\" width=\"100%\">\n");
                content.append("<tr style=\"line-height:1px\">\n");
                content.append("<td bgcolor=\"#");
                content.append(sColorDk);
                content.append("\" width=\"");
                content.append(Math.max(1, job.getProcessedCount() * 100 / job.getSampleSize()));
                content.append("%\">&nbsp;</td>");
                content.append("<td>&nbsp;</td>\n");
                content.append("</tr></table>");
                // end: job name column, progress bar

                content.append("</td><td align=\"right\">");
                content.append(job.getJobID());
                content.append("</td><td align=\"right\">");
                content.append(job.getSampleSize());
                content.append("</td><td align=\"right\">");
                content.append(job.getProcessedCount());
                content.append("</td><td align=\"right\">");
                long lastsent = job.getLastSentTime();
                if (lastsent > 0)
                {
                    content.append(formatDHM.format(new Date(lastsent)));
                }
                else
                {
                    content.append("&nbsp;");
                }

                content.append("</td><td align=\"right\">");
                ThrottlePrecise throttle = job.getThrottle();
                int rate = throttle.getRate();
                if (rate > 0)
                {
                    int perMS = throttle.getRatePer();
                    String sPer;
                    if (perMS == 24*60*60*1000)
                    {
                        sPer = "dy";
                    }
                    else if (perMS == 60*60*1000)
                    {
                        sPer = "hr";
                    }
                    else
                    {
                        perMS /= 60*1000;
                        if (perMS == 1)
                        {
                            sPer = "min";
                        }
                        else
                        {
                            sPer = perMS+"mins";
                        }
                    }
                    content.append(rate+"/"+sPer);
                }
                else
                {
                    content.append("&nbsp;");
                }
                content.append("</td><td>");
                DailyWindow window = job.getSendWindow();
                if (window.isAlwaysOpen())
                {
                    content.append("&nbsp;");
                }
                else if (window.isNeverOpen())
                {
                    content.append("[never]");
                }
                else
                {
                    content.append(window.getStart());
                    content.append("-");
                    content.append(window.getEnd());
                }

                content.append("</td><td align=\"right\">");
                content.append(job.getPriority());
                content.append("</td><td align=\"right\">");
                content.append(job.getTier());
                content.append("</td><td align=\"center\">");
                content.append(job.getJobStatusText());
                content.append("</td><td>");
                content.append("<font size=\"-2\">&lt;<a href=\"delete?id=");
                content.append(job.getJobID());
                content.append("\">delete</a>&gt;</font>");
                content.append("</td><td>");
                String shold;
                if (job.isUserHold())
                    shold = "release";
                else
                    shold = "hold";
                content.append("<font size=\"-2\">&lt;<a href=\"");
                content.append(shold);
                content.append("?id=");
                content.append(job.getJobID());
                content.append("\">");
                content.append(shold);
                content.append("</a>&gt;</font>");
                content.append("</td><td align=\"right\">");
                content.append(formatDHM.format(new Date(job.getStartTime())));
                content.append("</td><td align=\"right\">");
                DeltaTime.format(System.currentTimeMillis()-job.getStartTime(), content);
                content.append("</td></tr>\n");
            }
            content.append("</table>\n");

            appendFooter(content);
        }
        else if (sPage.equalsIgnoreCase("/editjob"))
        {
            try
            {
                String jobid = sParams.get("id");

                Job job = BulkEmailer.getBulkEmailer().getJobByID(Integer.parseInt(jobid));

                appendHeader(content, "Edit Job", false, false);
                content.append("<form action=\"submitjob\" method=\"get\">");

                content.append("<table>\n");

                content.append("<tr><td align=\"right\">ID</td><td>");
                content.append(job.getJobID());
                content.append("<input type=\"hidden\" name=\"id\" value=\"");
                content.append(job.getJobID());
                content.append("\" /></td></tr>\n");

                content.append("<tr><td align=\"right\">Name</td><td>");
                content.append(job.getJobName());
                content.append("</td></tr>\n");

                ThrottlePrecise throttle = job.getThrottle();
                content.append("<tr><td align=\"right\">Max Thruput</td><td><input type=\"text\" name=\"rate\" value=\"");
                int rate = throttle.getRate();
                if (rate > 0)
                {
                    content.append(rate);
                }
                content.append("\" /> emails (blank = unlimited)</td></tr>\n");

                content.append("<tr><td align=\"right\">per</td><td><input type=\"text\" name=\"rateper\" value=\"");
                content.append(throttle.getRatePer()/60000);
                content.append("\" /> minutes (60=hour, 1440=day)</td></tr>\n");

                DailyWindow window = job.getSendWindow();
                content.append("<tr><td align=\"right\">Send-window</td><td><input type=\"text\" name=\"windowstart\" value=\"");
                if (!window.isAlwaysOpen())
                {
                    content.append(window.getStart());
                }
                content.append("\" /> (HH:MM) through ");
                content.append("<input type=\"text\" name=\"windowend\" value=\"");
                if (!window.isAlwaysOpen())
                {
                    content.append(window.getEnd());
                }
                content.append("\" /> (HH:MM)");
                content.append("</td></tr>\n");

                content.append("<tr><td align=\"right\">Priority</td><td><input type=\"text\" name=\"priority\" value=\"");
                content.append(job.getPriority());
                content.append("\" /> ("+Schedulable.PRIORITY_MIN+" is lowest, "+Schedulable.PRIORITY_MAX+" is highest)</td></tr>\n");

                content.append("<tr><td align=\"right\">Min MTA Tier</td><td><input type=\"text\" name=\"tier\" value=\"");
                content.append(job.getTier());
                content.append("\" /></td></tr>\n");

                content.append("</table>");

                content.append("<input type=\"submit\" value=\"Save\" />");

                content.append("</form>");

                content.append("<form action=\"/\" method=\"get\">");
                content.append("<input type=\"submit\" value=\"Cancel\" />");
                content.append("</form>");

                appendFooter(content);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: Job");
                content.append("Error editing job:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/submitjob"))
        {
            try
            {
                String jobid = sParams.get("id");

                Job job = BulkEmailer.getBulkEmailer().getJobByID(Integer.parseInt(jobid));

                String sRate = sParams.get("rate");
                int rateNew;
                if (sRate.length() == 0)
                {
                    rateNew = -1;
                }
                else
                {
                    rateNew = Integer.parseInt(sRate);
                }

                String sRatePer = sParams.get("rateper");
                int ratePerNew;
                if (sRatePer.length() == 0)
                {
                    ratePerNew = 0;
                }
                else
                {
                    ratePerNew = Integer.parseInt(sRatePer);
                }
                ratePerNew *= 60000;

                ThrottlePrecise throttle = job.getThrottle();
                int rateOld = throttle.getRate();
                int ratePerOld = throttle.getRatePer();
                if (rateNew != rateOld || ratePerNew != ratePerOld)
                {
                    if (rateNew <= 0 && rateNew != -1)
                    {
                        throw new Exception("Invalid rate \""+rateNew+"\"; must be greater than zero, (or blank or -1 for unlimited).");
                    }
                    if (ratePerNew < 1000)
                    {
                        throw new Exception("Invalid period \""+ratePerNew+"\" ms; must be at least 1000 milliseconds.");
                    }
                    job.setSendRate(rateNew,ratePerNew);
                }



                String start = sParams.get("windowstart");
                String end = sParams.get("windowend");
                DailyWindow window;
                if (start.length() == 0 || end.length() == 0)
                {
                    window = DailyWindow.createDailyWindowAlwaysOpen();
                }
                else
                {
                    TimeOfDay timeStart = new TimeOfDay(Calendar.getInstance(),new TimeOfDayParser(start));
                    TimeOfDay timeEnd = new TimeOfDay(Calendar.getInstance(),new TimeOfDayParser(end));
                    window = DailyWindow.createDailyWindow(timeStart,timeEnd);
                }

                job.setSendWindow(window);

                int priOld = job.getPriority();
                int priNew = Integer.parseInt(sParams.get("priority"));
                if (priNew != priOld)
                {
                    if (priNew < Schedulable.PRIORITY_MIN || Schedulable.PRIORITY_MAX < priNew)
                    {
                        throw new Exception("Invalid priority");
                    }
                    job.setPriority(priNew);
                }

                int tierOld = job.getTier();
                int tierNew = Integer.parseInt(sParams.get("tier"));
                if (tierNew != tierOld)
                {
                    if (tierNew < 1)
                    {
                        throw new Exception("Invalid tier");
                    }
                    job.setTier(tierNew);
                }

                // take the user (back) to the "Jobs" page
                redirect.append("/");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: Job Update");
                content.append("Error updating job:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/jobhistory"))
        {
            try
            {
                StringBuffer resp = new StringBuffer(1000);
                appendHeader(resp, "History");

                resp.append("\n<table ");
                resp.append("border=\"0\" bgcolor=\"#CCCCCC\" cellpadding = \"1\" cellspacing=\"1\"");
                resp.append(">\n");
                resp.append("<tr bgcolor=\"white\">");
                resp.append("<th>name</th>");
                resp.append("<th>ID</th>");
                resp.append("<th>size</th>");
                resp.append("<th>sent</th>");
                resp.append("<th>start&nbsp;time</th>");
                resp.append("<th>end&nbsp;time</th>");
                resp.append("</tr>\n");

                List<JobHistory.JobInfo> rJob = new ArrayList<JobHistory.JobInfo>();
                BulkEmailer.getBulkEmailer().getHistory(rJob);
                Collections.<JobHistory.JobInfo>sort(rJob,new Comparator<JobHistory.JobInfo>()
                {
                    // sort by end date, descending
                    public int compare(JobHistory.JobInfo i1, JobHistory.JobInfo i2)
                    {
                        return i2.end.compareTo(i1.end);
                    }
                });
                for (final JobHistory.JobInfo job : rJob)
                {
                    String sColor;
                    if (job.processed < job.sampleSize || job.bad == job.sampleSize)
                    {
                        sColor = "FFCCCC"; // red
                    }
                    else
                    {
                        sColor = "CCFFCC"; // green
                    }

                    resp.append("<tr");
                    if (sColor.length() > 0)
                    {
                        resp.append(" bgcolor=\"#");
                        resp.append(sColor);
                        resp.append("\"");
                    }
                    resp.append("><td align=\"left\">");
                    resp.append("<a href=\"log?id=");
                    resp.append(job.jobID);
                    resp.append("\">");
                    String nam = job.name;
                    if (nam.length() > 30)
                    {
                        nam = nam.substring(0, 30);
                    }
                    resp.append(nam);
                    resp.append("</a>");

                    resp.append("</td><td align=\"right\">");
                    resp.append(job.jobID);
                    resp.append("</td><td align=\"right\">");
                    resp.append(job.sampleSize);
                    resp.append("</td><td align=\"right\">");
                    resp.append(job.processed);
                    resp.append("</td><td align=\"right\">");
                    resp.append(formatDHM.format(job.start));
                    resp.append("</td><td align=\"center\">");
                    resp.append(formatDHM.format(job.end));
                    resp.append("</td></tr>\n");
                }
                resp.append("</table>\n");

                appendFooter(resp);

                content.append(resp);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: job history");
                content.append("Error trying to read job history file:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/mtas"))
        {
            appendHeader(content, "MTAs");

            content.append("\n<table ");
            content.append("border=\"0\" bgcolor=\"#CCCCCC\" cellpadding = \"1\" cellspacing=\"1\"");
            content.append(">\n");
            content.append("<tr bgcolor=\"white\">");
            content.append("<th colspan=\"3\">MTA</th>");
            content.append("<th colspan=\"8\">emails sent (minutes ago)</th>");
            content.append("<th>rate</th>");
            content.append("<th>status</th>");
            content.append("<th colspan=\"4\">commands</th>");
            content.append("</tr>\n");
            content.append("<tr bgcolor=\"white\">");
            content.append("<th>host</th>");
            content.append("<th>tier</th>");
            content.append("<th>max/hr</th>");
            content.append("<th align=\"right\">total<br />0-60</th>");
            content.append("<th align=\"right\">0-10</th>");
            content.append("<th align=\"right\">10-20</th>");
            content.append("<th align=\"right\">20-30</th>");
            content.append("<th align=\"right\">30-40</th>");
            content.append("<th align=\"right\">40-50</th>");
            content.append("<th align=\"right\">50-60</th>");
            content.append("<th align=\"right\">60+</th>");
            content.append("<th>E/m</th>");
            content.append("<th>&nbsp;</th>");
            content.append("<th>hold</th>");
            content.append("<th>up</th>");
            content.append("<th>dn</th>");
            content.append("<th>delete</th>");
            content.append("</tr>\n");

            boolean first = true;
            for (Iterator<Mta> i = BulkEmailer.getBulkEmailer().getMtaManager().iterator(); i.hasNext();)
            {
                Mta mta = i.next();
                MtaState mt = mta.getState();

                String sColor;
                if (mta.isSendable() && !mt.mOnHold && !mta.isBadIO())
                {
                    sColor = "EEFFEE"; //green
                }
                else
                {
                    sColor = "FFEEEE"; //red
                }
                content.append("<tr bgcolor=\"#");
                content.append(sColor);
                content.append("\">");

                content.append("<td>");
                if (mt.mOnHold)
                {
                    content.append("<a href=\"editmta?mta=");
                    content.append(mta.getID());
                    content.append("\">");
                    content.append(mt.mHost);
                    if (mt.mPort != 25)
                    {
                        content.append(":");
                        content.append(mt.mPort);
                    }
                    content.append("</a>");
                }
                else
                {
                    content.append(mt.mHost);
                    if (mt.mPort != 25)
                    {
                        content.append(":");
                        content.append(mt.mPort);
                    }
                }
                if (mta.isBadIO())
                {
                    content.append(" (I/O Error; ");
                    content.append("<a href=\"retrymta?mta="+mta.getID()+"\">retry</a>");
                    Date at = mta.getDateResetBad();
                    if (at != null)
                    {
                        content.append(" auto@");
                        content.append(new SimpleDateFormat("HH:mm:ss").format(at));
                    }
                    content.append(")");
                }
                content.append("</td>");

                content.append("<td align=\"right\">");
                content.append(mt.mTier);
                content.append("</td>");

                content.append("<td align=\"right\">");
                int rate = mt.mRate;
                if (rate <= 0)
                {
                    content.append("&nbsp;");
                }
                else
                {
                    content.append(rate);
                }
                content.append("</td>");



                List<Date> rSent = new ArrayList<Date>(10000);
                mta.appendTimes(rSent);
                int[] rBucket = new int[7];
                long now = System.currentTimeMillis();
                for (final Date d : rSent)
                {
                    int ago = (int)(now-d.getTime());
                    ago /= 10*60*1000;
                    if (0 <= ago && ago < 6)
                    {
                        ++rBucket[ago];
                    }
                    else
                    {
                        ++rBucket[6];
                    }
                }
                int totBucket = 0;
                for (int j = 0; j < 6; ++j)
                {
                    totBucket += rBucket[j];
                }
                content.append("<td align=\"right\">");
                content.append(totBucket);
                content.append("</td>");
                for (int j = 0; j < 7; ++j)
                {
                    content.append("<td align=\"right\">");
                    content.append(rBucket[j]);
                    content.append("</td>");
                }



                content.append("<td align=\"right\">");
                int epm = mta.getEmailsPerMinute();
                if (epm > 0)
                {
                    content.append(epm);
                }
                else
                {
                    content.append("&nbsp;");
                }
                content.append("</td>");



                if (mt.mOnHold)
                {
                    content.append("<td>closed</td>");
                    content.append("<td>");
                    content.append("<a href=\"releasemta?mta="+mta.getID()+"\">release</a>");
                    content.append("</td>");
                }
                else
                {
                    content.append("<td>running</td>");
                    content.append("<td>");
                    content.append("<a href=\"holdmta?mta="+mta.getID()+"\">hold</a>");
                    content.append("</td>");
                }
                content.append("<td>");
                if (first)
                {
                    first = false;
                    content.append("up");
                }
                else
                {
                    content.append("<a href=\"upmta?mta="+mta.getID()+"\">up</a>");
                }
                content.append("</td>");
                content.append("<td>");
                if (i.hasNext())
                {
                    content.append("<a href=\"downmta?mta="+mta.getID()+"\">dn</a>");
                }
                else
                {
                    content.append("dn");
                }
                content.append("</td>");
                content.append("<td>");
                content.append("<a href=\"deletemta?mta="+mta.getID()+"\">delete</a>");
                content.append("</td>");




                content.append("</tr>\n");
            }
            content.append("<tr bgcolor=\"#FFFFFF\"><td colspan=\"17\"><a href=\"addmta\">add</a></td></tr>\n");
            content.append("</table>\n");

            appendFooter(content);
        }
        else if (sPage.equalsIgnoreCase("/admin"))
        {
            appendHeader(content, "Admin", true, true);

            content.append("<table>");
            content.append("<tr><td align=\"right\">start&nbsp;time:</td><td align=\"right\">");
            long start = BulkEmailer.getBulkEmailer().getStartTime();
            String startDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(new Date(start));
            content.append(startDate);
            content.append("</td></tr>");

            content.append("<tr><td align=\"right\">up&nbsp;time:</td><td align=\"right\">");
            DeltaTime.format(System.currentTimeMillis()-start, content);
            content.append("</td></tr>");
            content.append("</table>\n");

            content.append("<form action=\"shutdown\" method=\"post\"><input type=\"submit\" value=\"Shut Down Server\"></form>\n");

            appendFooter(content);
        }
        else if (sPage.equalsIgnoreCase("/dumpthreads"))
        {
            appendHeader(content, "Thread Dump");

            Thread[] rt = ThreadUtil.findAllThreads();

            content.append("<table border=\"1\">\n");
            content.append("<tr><th>Name</th>");
            content.append("<th>Priority</th>");
            content.append("<th>Alive</th>");
            content.append("<th>Daemon</th>");
            content.append("<th>Intr.</th>");
            content.append("<th>Group</th>");
            content.append("<th>Parent Group</th></tr>");

            for (int i = 0; i < rt.length; i++)
            {
                Thread t = rt[i];

                content.append("<tr><td>");
                content.append(t.getName());
                content.append("</td><td>");
                content.append(t.getPriority());
                content.append("</td><td>");
                content.append(t.isAlive());
                content.append("</td><td>");
                content.append(t.isDaemon());
                content.append("</td><td>");
                content.append(t.isInterrupted());
                content.append("</td><td>");
                content.append(t.getThreadGroup().getName());
                content.append("</td><td>");

                ThreadGroup pg = t.getThreadGroup().getParent();
                String spg;
                if (pg == null)
                    spg = "null";
                else
                    spg = pg.getName();
                content.append(spg);

                content.append("</td></tr>");
            }
            content.append("</table>\n");

            appendFooter(content);
        }
        else if (sPage.equalsIgnoreCase("/delete"))
        {
            try
            {
                String jobid = sParams.get("id");

                final InetAddress ip = this.socket.getInetAddress();
                final String userID = ip.getCanonicalHostName()+" ("+ip.getHostAddress()+")";

                BulkEmailer.getBulkEmailer().getJobByID(Integer.parseInt(jobid)).delete(userID);
                // take the user (back) to the "Jobs" page
                redirect.append("/");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: delete job");
                content.append("Error trying to delete job:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/hold"))
        {
            try
            {
                String jobid = sParams.get("id");

                Job job = BulkEmailer.getBulkEmailer().getJobByID(Integer.parseInt(jobid));

                job.holdUser();

                // take the user (back) to the "Jobs" page
                redirect.append("/");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: hold job");
                content.append("Error trying to place job on hold:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/release"))
        {
            try
            {
                String jobid = sParams.get("id");

                BulkEmailer.getBulkEmailer().getJobByID(Integer.parseInt(jobid)).releaseUser();

                // take the user (back) to the "Jobs" page
                redirect.append("/");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: release job");
                content.append("Error trying to release job:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/log"))
        {
            try
            {
                String jobid = sParams.get("id");
                EmailBatchJobControl jc = new EmailBatchJobControl(Integer.parseInt(jobid));
                FileInputStream li = new FileInputStream(jc.getArchiveLogFile());
                InputStreamReader lis = new InputStreamReader(li);
                BufferedReader br = new BufferedReader(lis);

                appendHeader(content, "Log File");
                content.append("<pre>");
                String s = br.readLine();
                while (s != null)
                {
                    content.append(s);
                    content.append("\n");
                    s = br.readLine();
                }
                br.close();
                content.append("</pre>");
                appendFooter(content);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: log file");
                content.append("Error trying to open the job's log file:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/editmta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                Mta mta = BulkEmailer.getBulkEmailer().getMtaManager().getByID(Integer.parseInt(mtaid));
                MtaState mt = mta.getState();

                appendHeader(content, "Edit MTA", false, false);
                content.append("<form action=\"submitmta\" method=\"get\">");

                content.append("<table>\n");

                content.append("<tr><td align=\"right\">ID</td><td>");
                content.append(mta.getID());
                content.append("<input type=\"hidden\" name=\"mta\" value=\"");
                content.append(mta.getID());
                content.append("\" /></td></tr>\n");

                content.append("<tr><td align=\"right\">Scheme</td><td><input type=\"text\" name=\"scheme\" value=\"");
                content.append(mt.mScheme);
                content.append("\" /></td></tr>\n");

                content.append("<tr><td align=\"right\">Host</td><td><input type=\"text\" name=\"host\" value=\"");
                content.append(mt.mHost);
                content.append("\" /></td></tr>\n");

                content.append("<tr><td align=\"right\">Port</td><td><input type=\"text\" name=\"port\" value=\"");
                content.append(mt.mPort);
                content.append("\" /></td></tr>\n");

                content.append("<tr><td align=\"right\">Max/hour</td><td><input type=\"text\" name=\"rate\" value=\"");
                content.append(mt.mRate <= 0 ? 0 : mt.mRate);
                content.append("\" /> (0 = unlimited)</td></tr>\n");

                content.append("<tr><td align=\"right\">Tier</td><td><input type=\"text\" name=\"tier\" value=\"");
                content.append(mt.mTier);
                content.append("\" /></td></tr>\n");

                content.append("<tr><td align=\"right\">Timeout</td><td><input type=\"text\" name=\"timeout\" value=\"");
                content.append(mt.mTimeout);
                content.append("\" /> (milliseconds)</td></tr>\n");

                content.append("</table>");

                content.append("<input type=\"submit\" value=\"Save\" />");

                content.append("</form>");

                content.append("<form action=\"/mtas\" method=\"get\">");
                content.append("<input type=\"submit\" value=\"Cancel\" />");
                content.append("</form>");

                appendFooter(content);
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/submitmta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                Mta mta = BulkEmailer.getBulkEmailer().getMtaManager().getByID(Integer.parseInt(mtaid));

                mta.setScheme(sParams.get("scheme"));

                mta.setHost(sParams.get("host"));

                String sRate = sParams.get("rate");
                int rate;
                if (sRate.length() == 0)
                {
                    rate = -1;
                }
                else
                {
                    rate = Integer.parseInt(sRate);
                }
                mta.setRate(rate);

                int port = Integer.parseInt(sParams.get("port"));
                mta.setPort(port);

                int tier = Integer.parseInt(sParams.get("tier"));
                mta.setTier(tier);

                int timeout = Integer.parseInt(sParams.get("timeout"));
                mta.setTimeout(timeout);

                mta.save();

                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error updating MTA:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/upmta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                MtaManager mgr = BulkEmailer.getBulkEmailer().getMtaManager();
                mgr.upMta(Integer.parseInt(mtaid));
                mgr.save();
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/downmta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                MtaManager mgr = BulkEmailer.getBulkEmailer().getMtaManager();
                mgr.downMta(Integer.parseInt(mtaid));
                mgr.save();
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/releasemta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                Mta mta = BulkEmailer.getBulkEmailer().getMtaManager().getByID(Integer.parseInt(mtaid));
                mta.release();
                mta.save();
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/holdmta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                Mta mta = BulkEmailer.getBulkEmailer().getMtaManager().getByID(Integer.parseInt(mtaid));
                mta.hold();
                mta.save();
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/retrymta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                Mta mta = BulkEmailer.getBulkEmailer().getMtaManager().getByID(Integer.parseInt(mtaid));
                mta.setBadIO(false);
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/deletemta"))
        {
            try
            {
                String mtaid = sParams.get("mta");
                MtaManager mgr = BulkEmailer.getBulkEmailer().getMtaManager();
                mgr.deleteMta(Integer.parseInt(mtaid));
                mgr.save();
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else if (sPage.equalsIgnoreCase("/addmta"))
        {
            try
            {
                MtaManager mgr = BulkEmailer.getBulkEmailer().getMtaManager();
                Mta mta = mgr.addMta();
                mta.save();
                mgr.save();
                redirect.append("mtas");
            }
            catch (Throwable e)
            {
                e.printStackTrace();
                appendHeader(content, "Error: MTA");
                content.append("Error: MTA not found:<br /><br />\n");
                content.append("<pre>");
                content.append(e.getMessage());
                content.append("</pre>");
                appendFooter(content);
            }
        }
        else
        {
            appendHeader(content, "Error");
            content.append("Error: unrecognized command.");
            appendFooter(content);
        }
    }

    private static void appendHeader(StringBuffer shtml, String sPage)
    {
        appendHeader(shtml, sPage, true, false);
    }

    private static void appendHeader(StringBuffer shtml, String sPage, boolean showLinks, boolean admin)
    {
        shtml.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
        shtml.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">\n");
        shtml.append("<head>\n");
        shtml.append("<meta http-equiv=\"Content-Type\" content=\""+CONTENT_TYPE+"; charset=UTF-8\" />\n");

        shtml.append("<title>Bulk Emailer");
        if (sPage.length() > 0)
        {
            shtml.append("--");
            shtml.append(sPage);
        }
        shtml.append("</title>\n");

        shtml.append("<style>\n");
        shtml.append("body {font-family: verdana,arial,helvetica,sans-serif; font-size: x-small}\n");
        shtml.append("pre {font-size: small}\n");
        shtml.append("th {white-space: nowrap}\n");
        shtml.append("td {white-space: nowrap}\n");
        shtml.append("a {color: #0000FF}\n");
        shtml.append(".heading {font-size: large}\n");
        shtml.append("</style>\n");

        shtml.append("</head>\n<body>\n");

        if (showLinks)
        {
            shtml.append("&lt;<a href=\"javascript:history.go(-1)\">back</a>&gt;&nbsp;&nbsp;\n");
            shtml.append("&lt;<a href=\"/\">jobs</a>&gt;&nbsp;&nbsp;\n");
            shtml.append("&lt;<a href=\"jobhistory\">history</a>&gt;&nbsp;&nbsp;\n");
            if (admin)
            {
                shtml.append("&lt;<a href=\"mtas\">mtas</a>&gt;&nbsp;&nbsp;\n");
                shtml.append("&lt;<a href=\"dumpthreads\">dump threads</a>&gt;&nbsp;&nbsp;\n");
            }
        }

        if (sPage.length() > 0)
        {
            shtml.append("<span class=\"heading\">");
            shtml.append(sPage);
            shtml.append("</span><br />&nbsp;<br />\n");
        }
    }

    private static void appendFooter(StringBuffer shtml)
    {
        shtml.append("</body>\n</html>\n");
    }



    /**
     * Closes this client connection.
     */
    public synchronized void close()
    {
        /*
         * For most of the processing
         * that this thread could be doing, we will just let it finish
         * naturally, so we don't need to do anything special for those cases.
         * The one case we need to handle specially is a client sending up a
         * (big) new job, which we will need to abort (cleanly).
         */
        this.quit = true;
    }

    /**
     * Checks if this thread is currently shutting down.
     * @return true if this thread is shutting down
     */
    public synchronized boolean isQuitting()
    {
        return this.quit;
    }

    /**
     * Waits for this connection's thread to finish,
     * ignoring any attempt to interrupt this thread.
     * @return true if the current
     * thread gets interrupted while waiting for
     * this connection's thread to finish.
     */
    public boolean joinUninterruptable()
    {
        return ThreadUtil.joinUninterruptable(this.thread);
    }
}
