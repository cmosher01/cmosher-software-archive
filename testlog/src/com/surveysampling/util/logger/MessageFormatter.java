/*
 * Created on Apr 16, 2004
 */
package com.surveysampling.util.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Provides static access to the formatMessage method
 * of java.util.logging.Formatter
 */
public final class MessageFormatter extends Formatter
{
    private static MessageFormatter formatter = new MessageFormatter();

    private MessageFormatter()
    {
    }

    public String format(LogRecord record) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }



    public static String formatLogMessage(LogRecord record)
    {
        return formatter.formatMessage(record);
    }
}
