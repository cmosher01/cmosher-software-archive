package com.surveysampling.util.logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LoggerUtil
{
    private LoggerUtil()
    {
    }

    public static Logger getLogger(Object obj)
    {
        return getLogger(obj.getClass());
    }

    public static Logger getLogger(Class cl)
    {
        Logger logger = LogManager.getLogManager().getLogger(cl.getName());

        if (logger == null)
            logger = Logger.global;

        return logger;
    }

    public static void initPropertiesFile() throws Exception
    {
        System.setProperty("java.util.logging.config.file","logging.properties");
        LogManager.getLogManager().readConfiguration();
    }

    public static void createDefinedLoggers() throws Exception
    {
        String fname = System.getProperty("java.util.logging.config.file");
        if (fname == null)
        {
            fname = System.getProperty("java.home");
            if (fname == null)
            {
                throw new Error("Can't find java.home ??");
            }
            File f = new File(fname, "lib");
            f = new File(f, "logging.properties");
            fname = f.getCanonicalPath();
        }
        Properties propLogging = new Properties();
        propLogging.load(new FileInputStream(fname));

        Enumeration iProp = propLogging.propertyNames();
        while (iProp.hasMoreElements())
        {
            String prop = (String)iProp.nextElement();
            int dot = prop.lastIndexOf('.');
            if (dot >= 0)
            {
                String cn = prop.substring(0,dot);
                if (cn.length() > 0)
                    try
                    {
                        if (!Handler.class.isAssignableFrom(Class.forName(cn)))
                            Logger.getLogger(cn);
                    }
                    catch (ClassNotFoundException e)
                    {
                    }
            }
        }
    }
}
