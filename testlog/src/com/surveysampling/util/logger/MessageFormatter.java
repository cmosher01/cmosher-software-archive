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
    private static MessageFormatter LOCAL = new MessageFormatter();

    private MessageFormatter()
    {
        throw new UnsupportedOperationException();
    }

    public static String formatMessage(LogRecord record)
    {
        return MessageFormatter.LOCAL.super.formatMessage(record);
    }

    /* (non-Javadoc)
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    public String format(LogRecord record)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
