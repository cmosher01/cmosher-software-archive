/*
 * TODO
 *
 * Created on Apr 16, 2004
 */
package com.surveysampling.util.logger;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * TODO
 */
public final class MessageFormatter extends Formatter
{
    private static MessageFormatter formatter = new MessageFormatter();

    private MessageFormatter() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
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
