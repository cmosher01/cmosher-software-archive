package com.surveysampling.someapp;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.surveysampling.util.logger.MessageFormatter;

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
                System.err.println(MessageFormatter.formatLogMessage(record));
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
}
