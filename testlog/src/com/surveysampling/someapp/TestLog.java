package com.surveysampling.someapp;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class TestLog
{
    public static void main(String[] args) throws Exception
    {
//        LoggerUtil.initPropertiesFile();
//        LoggerUtil.createDefinedLoggers();
//
//        SomeClass sc = new SomeClass();
//        sc.someMethod();

        Handler[] rh = Logger.global.getHandlers();
        for (int i = 0; i < rh.length; ++i)
        {
            Handler h = rh[i];
            Logger.global.removeHandler(h);
        }
        Logger.global.addHandler(new Handler()
        {
            public void publish(LogRecord record)
            {
                System.err.println(getFormatter().format(record));
            }

            public void flush()
            {
                System.err.flush();
            }

            public void close() throws SecurityException
            {
            }
        });

        Logger.global.severe("bad bad bad");
    }

    private void format()
    {
        public synchronized String formatMessage(LogRecord record) {
        String format = record.getMessage();
        java.util.ResourceBundle catalog = record.getResourceBundle();
        if (catalog != null) {
//          // We cache catalog lookups.  This is mostly to avoid the
//          // cost of exceptions for keys that are not in the catalog.
//          if (catalogCache == null) {
//          catalogCache = new HashMap();
//          }
//          format = (String)catalogCache.get(record.essage);
//          if (format == null) {
                try {
                    format = catalog.getString(record.getMessage());
                } catch (java.util.MissingResourceException ex) {
                // Drop through.  Use record message as format
                format = record.getMessage();
            }
//          catalogCache.put(record.message, format);
//          }
        }
        // Do the formatting.
        try {
            Object parameters[] = record.getParameters();
            if (parameters == null || parameters.length == 0) {
            // No parameters.  Just return format string.
            return format;
            }
            // Is is a java.text style format?
            if (format.indexOf("{0") >= 0) {
                return java.text.MessageFormat.format(format, parameters);
            }
            return format;

        } catch (Exception ex) {
            // Formatting failed: use localized format string.
            return format;
        }
        }
    }
}
