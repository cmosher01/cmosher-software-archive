package com.surveysampling.bulkemailer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import com.surveysampling.bulkemailer.job.Job;
import com.surveysampling.bulkemailer.mta.MtaManager;
import com.surveysampling.util.Flag;
import com.surveysampling.util.SequenceNumberPersist;
import com.surveysampling.util.logging.DailyFileHandler;
import com.surveysampling.util.logging.SimplestFormatter;
import com.surveysampling.util.logging.StandardErrorManager;
import com.surveysampling.util.logging.StandardLog;

/**
 * Houses the main method and is responsible for the server startup
 * and shutdown.  Also, this class holds all of the references to the major
 * program modules and methods to access them.
 * 
 * @author Chris Mosher
 */
public class BulkEmailer
{
    /**
     * Reference to the BulkEmailer object.
     */
    private static final BulkEmailer mBulkEmailer = new BulkEmailer();

    /**
     * The Bulk Emailer version
     * TODO increment version number for each release
     */
    private static final String VERSION = "1.4";

    /**
     * The BulkEmailer protocol version number
     * 
     * NOTE: The protocol version was not used until
     * version 1.0.4 of the BulkEmailer.  The protocol 
     * version should change when the XML specification 
     * changes.
     * TODO increment PROTOCOL as necessary
     */
    private static final int PROTOCOL = 2;

    /**
     * Bulkemailer's main logger; writes to
     * the daily log file.
     */
    private Logger mLogger;

    /**
     * Reference to the BulkEmailer properties object.
     */
    private Properties mProperties = new Properties();
    private int mMaxEmailSizeBytes;
    private int mcWorkerThread;

    /**
     */
    private SimpleServer mServer;

    /**
     * Reference to the Scheduler object.
     */
    private Scheduler mScheduler;

    /**
     * Map of job IDs to actual Job objects
     */
    private final Map<Integer,Job> mrJob = new HashMap<Integer,Job>();

    /**
     * Handles the job history
     */
    private JobHistory mJobHistory;

    /**
     * Sequence for issuing job IDs
     */
    private SequenceNumberPersist mJobIDSeq;

    /**
     * Object that manages the connections to the
     * Mail Transfer Authorities.
     */
    private MtaManager mMtaManager;

    /**
     * Time when the program started running.
     */
    private final long mTimeStart = System.currentTimeMillis();

    private Flag mShutdown = new Flag();

    private static final SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    /**
     * Initializes the <code>BulkEmailer</code>.
     */
    protected BulkEmailer()
    {
        if (mBulkEmailer != null)
        {
            throw new IllegalStateException();
        }
    }



    /**
     * Creates an instance of the BulkEmailer class and starts it running.
     * 
     * @param argv command line parameters (none)
     * @throws Throwable
     */
    public static void main(String[] argv) throws Throwable
    {
        if (argv.length > 0)
        {
            System.err.println("command parameters ignored");
        }

        mBulkEmailer.mainApp();
    }

    /**
     * Returns a reference to the static <code>BulkEmailer</code> object.
     * @return the <code>BulkEmailer</code> reference
     */
    public static BulkEmailer getBulkEmailer()
    {
        return mBulkEmailer;
    }

    /**
     * Returns the herald, which typically is displayed to
     * clients that connect to this <code>BulkEmailer</code>'s
     * server socket.
     * @return the herald string
     */
    public static String getHerald()
    {
        StringBuffer sb = new StringBuffer(30);
        sb.append("Bulk E-Mailer version ");
        sb.append(VERSION);
        sb.append("/");
        sb.append(PROTOCOL);
        return sb.toString();
    }

    /**
     * Returns the date the server was started.
     * @return A string containing the date the server was started.
     */
    public long getStartTime()
    {
        return mTimeStart;
    }



    /**
     * Starts up, runs, and shuts down the Bulk E-Mailer server.
     * @throws Throwable
     */
    protected void mainApp() throws Throwable
    {
        boolean ok = false;
        try
        {
            startup();
            ok = true;
        }
        finally
        {
            /*
             * If we get an exception when trying to startup,
             * then we want to immediately shut down.
             */
            if (!ok)
            {
                quit();
            }

            /*
             * The main thread doesn't have any work of its own to
             * do so we just wait for the scheduler to finish.
             * This is where the main thread spends the majority
             * of its time.
             */
            if (mScheduler != null)
            {
                try
                {
                    mScheduler.join();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            shutdown();
        }
    }



    // STARTUP ------------------------------------------------------



    /**
     * @throws Throwable
     */
    protected void startup() throws Throwable
    {
        try
        {
            startup2();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @throws NumberFormatException
     * @throws ShuttingDownException
     * @throws IOException
     */
    protected void startup2() throws NumberFormatException, ShuttingDownException, IOException
    {
        Thread.currentThread().setName("BulkEmailer");
        installShutdownManager();

        System.out.println(formatDate.format(new Date())+": server startup");

        loadConfig();
        checkFolder(getLogFolder());
        createLogger();

        logStartup();

        checkConfig();

        initJobIDSeq();

        // Create the Scheduler
        mScheduler = new Scheduler();

        // Start listening on our port
        startServer();

        // Start the mail-server handler
        startMtaManager();

        // Initialize the job history manager
        initJobHistory();

        // Reload existing jobs
        reloadJobs();
    }

    /**
     * Initializes the MTA manager object
     */
    protected void startMtaManager()
    {
        mMtaManager = new MtaManager(getMtaFolder());
        try
        {
            mMtaManager.load();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @return the one MtaManager
     */
    public MtaManager getMtaManager()
    {
        return mMtaManager;
    }

    /**
     * When the server is started, this method is called to scan the "Job" directory
     * for spec files.  If any are found they are used to recreate job objects
     * which are then resubmitted to the scheduler.
     * @throws ShuttingDownException
     */
    protected void reloadJobs() throws ShuttingDownException
    {
        // get an array of the names of all "spec" files in the Job folder
        String[] rSpec = getJobFolder().list(new FilenameFilter()
        {
            public boolean accept(File dir, String fileName)
            {
                int dot = fileName.lastIndexOf('.');
                String ext;
                if (dot >= 0)
                    ext = fileName.substring(dot, fileName.length());
                else
                    ext = "";

                return ext.equalsIgnoreCase(".xml");
            }
        });

        if (rSpec == null)
        {
            return;
        }

        for (int i = 0; i < rSpec.length; ++i)
        {
            String sFileName = rSpec[i];
            String sJobID = sFileName.substring(0, sFileName.indexOf('.'));
            int jobID = Integer.parseInt(sJobID);
            try
            {
                addJob(jobID);
            }
            catch (ShuttingDownException shuttingdown)
            {
                throw shuttingdown;
            }
            catch (Exception e)
            {
                mLogger.warning("error reading specs for job ID " + jobID);
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates the listening socket.
     * @throws IOException
     * @throws NumberFormatException
     */
    protected void startServer() throws IOException, NumberFormatException
    {
        mServer = new SimpleServer(getPort());
    }

    /**
     * @return the port to listen on
     * @throws NumberFormatException
     */
    protected int getPort() throws NumberFormatException
    {
        int port = Integer.parseInt(mProperties.getProperty("ServerPort","60000"));
        if (port < 0x400 || 0x10000 <= port)
        {
            throw new NumberFormatException("Invalid value for ServerPort; must be: 1024 <= ServerPort < 65536");
        }
        return port;
    }



    // CONFIG -----------------------------------------------------------------------



    /**
     * Reads the configuration properties for this <code>BulkEmailer</code>.
     * The properties are read from <code>BulkEmailer.properties</code> in the current directory.
     * @throws IOException
     */
    protected void loadConfig() throws IOException
    {
        InputStream in = null;
        try
        {
            in = new FileInputStream(new File("BulkEmailer.properties"));
            mProperties.load(in);
        }
        catch (final FileNotFoundException e)
        {
            e.printStackTrace();
            System.err.println("Using default properties because the BulkEmailer.properties file was not found.");
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Throwable ignore)
                {
                    ignore.printStackTrace();
                }
            }
        }
    }

    /**
     * @throws IOException
     * @throws NumberFormatException
     */
    protected void checkConfig() throws IOException, NumberFormatException
    {
        checkFolder(getJobFolder());
        checkFolder(getJobArchiveFolder());
        checkFolder(getMtaFolder());
        checkFolder(getHistoryFolder());

        mcWorkerThread = getWorkerThreadCount();
        mMaxEmailSizeBytes = Integer.parseInt(mProperties.getProperty("MaxEmailSizeBytes","1024000"));
    }

    /**
     * @param f
     * @throws IOException
     */
    protected void checkFolder(File f) throws IOException
    {
        if (!f.exists())
        {
            f.mkdirs();
        }
        if (!f.isDirectory())
        {
            throw new IOException("Error in BulkEmailer properties: "+f.getAbsolutePath()+" directory does not exist.");
        }
    }

    /**
     * @return the number of worker threads to make per job
     * @throws NumberFormatException
     */
    protected int getWorkerThreadCount() throws NumberFormatException
    {
        String s = mProperties.getProperty("JobThreads","12");
        int r = Integer.parseInt(s);
        if (r < 0)
        {
            throw new NumberFormatException("parameter JobThreads cannot be negative");
        }
        return r;
    }

    /**
     * @return the log file directory
     */
    public File getLogFolder()
    {
        return new File(mProperties.getProperty("LogFileDir","log"));
    }

    /**
     * @return the job directory
     */
    public File getJobFolder()
    {
        return new File(mProperties.getProperty("JobDir","job"));
    }

    /**
     * @return the archive directory
     */
    public File getJobArchiveFolder()
    {
        return new File(mProperties.getProperty("JobArchiveDir","archive"));
    }

    /**
     * @return the MTA directory
     */
    public File getMtaFolder()
    {
        return new File(mProperties.getProperty("MtaDir","mta"));
    }

    /**
     * @return the history directory
     */
    public File getHistoryFolder()
    {
        return new File(mProperties.getProperty("HistoryDir","history"));
    }

    /**
     * @return the MTA server for notification emails
     */
    public String getNotifyServer()
    {
        return mProperties.getProperty("NotifyEmailServer", "");
    }



    /**
     * 
     */
    // SHUTDOWN -----------------------------------------------------------------------



    protected void shutdown()
    {
        /* Shut down the various pieces of the
         * server process. Some of these are actually
         * running threads, some are not. But don't
         * stop the logger thread yet, because we
         * still want to write to the log file.
         */

        closeServer();

        Thread.interrupted();

        if (mScheduler != null)
        {
            // close all jobs
            List<Schedulable> rJob = new ArrayList<Schedulable>(100);
            getSchedule(rJob);
            for (final Schedulable job : rJob)
            {
                job.close();
            }

            mScheduler = null;
        }

        if (mMtaManager != null)
        {
            mMtaManager.close();
        }

        Thread.interrupted();

        logShutdown();

        /* done writing to log file, so now
         * we can stop the logger thread
         */
        Thread.interrupted();
        closeLogger();

        signalCleanShutdown();

        System.out.println(formatDate.format(new Date())+": server shutdown");
    }



    /**
     * 
     */
    protected void installShutdownManager()
    {
        // Install a shutdown handler. Its run method
        // will be called when the process is terminated
        // via the "kill -15" command on Unix, or a control-C
        // in Windows.
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                Thread.currentThread().setName("Shutdown hook thread");
                // initiate the shutdown process
                quit();
                // wait for the shutdown to finish
                waitForCleanShutdown();
            }
        });
    }

    /**
     * 
     */
    protected void waitForCleanShutdown()
    {
        try
        {
            mShutdown.waitUntilTrue();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    protected void signalCleanShutdown()
    {
        try
        {
            mShutdown.waitToSetTrue();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    protected void logShutdown()
    {
        if (mLogger == null)
        {
            return;
        }

        // Write some startup info to the logger
        mLogger.info("Done.");
        mLogger.info("");
        mLogger.info("");
        mLogger.info("");
    }

    /**
     * 
     */
    protected void quit()
    {
        if (mScheduler == null)
        {
            return;
        }

        mScheduler.quit();
    }

    /**
     * Begins the shutdown sequence for the entire Bulk Emailer.
     * @param user String identifying who initiated the shutdown.
     */
    public void serverShutdown(String user)
    {
        mLogger.info("Server shutdown initiated by: "+user);
        mLogger.info("Shutting down server...");
        quit();
    }

    /**
     * 
     */
    protected void closeServer()
    {
        if (mServer == null)
        {
            return;
        }

        mServer.close();
        mServer.joinUninterruptable();
        mServer = null;
    }



    // LOGGING -----------------------------------------------------------------------



    /**
     * Creates the <code>BulkEmailer</code> logger to log to the daily BulkEmailer log files.
     */
    protected void createLogger()
    {
        /*
         * Remove all handlers from all loggers,
         * and turn off the root logger. This way,
         * we start with a clean slate.
         */
        LogManager.getLogManager().reset();
        Logger.getLogger("").setLevel(Level.OFF);

        /*
         * Create the main program's logger.
         * Set it to log ALL levels.
         */
        mLogger = Logger.getLogger(getClass().getPackage().getName());
        mLogger.setLevel(Level.ALL);

        /*
         * Create a daily log file handler,
         * initialize it with our CSV formatter,
         * and set our logger to use it.
         */
        Handler h = new DailyFileHandler(getLogFolder(),"BulkEmailer");
        h.setErrorManager(new StandardErrorManager());
        h.setLevel(Level.ALL);
        h.setFormatter(new SimplestFormatter());
        mLogger.addHandler(h);

        /*
         * Redirect System.out and err to the logger.
         */
        StandardLog.setErr(mLogger);
        StandardLog.setOut(mLogger);
    }

    /**
     * Writes some useful information to the log file when
     * the server is started.
     */
    protected void logStartup()
    {
        // Write some startup info to the logger
        mLogger.info("--------------------------------------------------------------");
        mLogger.config("Startup");
        mLogger.config("Version: "+VERSION);
        mLogger.config("Protocol: "+PROTOCOL);
        mLogger.config("user.dir: "+System.getProperty("user.dir"));

        logProperties();

        mLogger.config("java.home: "+System.getProperty("java.home"));
        mLogger.config("java.runtime.name: "+System.getProperty("java.runtime.name"));
        mLogger.config("java.vm.version: "+System.getProperty("java.vm.version"));

        mLogger.config("java.class.path:");
        logPath(System.getProperty("java.class.path"));
        mLogger.config("sun.boot.class.path:");
        logPath(System.getProperty("sun.boot.class.path"));
    }

    /**
     * 
     */
    protected void logProperties()
    {
        // get set of properties sorted by key name
        Collection<Map.Entry<Object,Object>> props = new TreeSet<Map.Entry<Object,Object>>(new Comparator<Map.Entry<Object,Object>>()
        {
            public int compare(Map.Entry<Object,Object> o1, Map.Entry<Object,Object> o2)
            {
                return o1.getKey().toString().compareToIgnoreCase(o2.getKey().toString());
            }
        });
        props.addAll(mProperties.entrySet());

        // print them to the log file
        mLogger.config("Properties:");
        for (final Map.Entry<Object,Object> entry : props)
        {
            mLogger.config("    "+entry.getKey()+": "+entry.getValue());
        }
    }

    /**
     * @param path
     */
    protected void logPath(String path)
    {
        StringTokenizer st = new StringTokenizer(path, System.getProperty("path.separator"));
        while (st.hasMoreTokens())
        {
            mLogger.config("    "+st.nextToken());
        }
    }

    /**
     * 
     */
    protected void closeLogger()
    {
        System.out.flush();
        System.err.flush();

        // reset System.out and err to standard output and standard error
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));

        if (mLogger != null)
        {
            Handler[] rh = mLogger.getHandlers();
            for (int i = 0; i < rh.length; ++i)
            {
                rh[i].close();
            }
            mLogger = null;
        }
    }



    // JOBS --------------------------------------------------------------------



    /**
     * Initializes the job ID sequence.
     * @throws IOException
     */
    protected void initJobIDSeq() throws IOException
    {
        File fileSeq = new File(mProperties.getProperty("JobIDSeqFilename","jobid.seq"));
        mJobIDSeq = new SequenceNumberPersist(fileSeq);
    }

    /**
     * Returns the next job ID and increments the JobIDSeq to the next number.  Also 
     * calls the writeJobIDSeq method to commit the new sequence value to disk.
     * 
     * @return the job ID
     * @throws IOException
     */
    public synchronized int getNextJobID() throws IOException
    {
        return mJobIDSeq.getNext();
    }

    /**
     * @param jobID
     * @throws ShuttingDownException
     * @throws SAXException
     * @throws IOException
     * @throws ParseException
     */
    public void addJob(int jobID) throws ShuttingDownException, SAXException, IOException, ParseException
    {
        Job job = new Job(jobID, mcWorkerThread, mMaxEmailSizeBytes);

        synchronized (mrJob)
        {
            mrJob.put(job.getJobID(),job);
        }

        mScheduler.add(job);
    }

    /**
     * @param rJob
     */
    public void getSchedule(List<Schedulable> rJob)
    {
        if (mScheduler != null)
        {
            rJob.addAll(Arrays.<Schedulable>asList(mScheduler.getList()));
        }
    }

    /**
     * @param jobID
     * @return the job
     * @throws JobNotFoundException
     */
    public Job getJobByID(int jobID) throws JobNotFoundException
    {
        Job job = null;
        synchronized (mrJob)
        {
            job = mrJob.get(jobID);
        }

        if (job == null)
            throw new JobNotFoundException(jobID);

        return job;
    }

    /**
     * 
     */
    protected void initJobHistory()
    {
        mJobHistory = new JobHistory(getHistoryFolder());
    }

    /**
     * Called when the job is finished processing. We
     * put the job on our history list.
     * @param j
     * @throws IOException
     */
    public void jobIsDone(Job j) throws IOException
    {
        synchronized (mJobHistory)
        {
            mJobHistory.put(j);
        }
        synchronized (mrJob)
        {
            mrJob.remove(j.getJobID());
        }
    }

    /**
     * @param rJob
     * @throws NoSuchElementException
     * @throws IOException
     * @throws ParseException
     */
    public void getHistory(Collection<JobHistory.JobInfo> rJob) throws NoSuchElementException, IOException, ParseException
    {
        synchronized (mJobHistory)
        {
            mJobHistory.getAsOf(new Date(System.currentTimeMillis()-48*60*60*1000),rJob);
        }
    }
}
