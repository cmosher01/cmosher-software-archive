/*
 * Created on May 21, 2004
 */
package com.surveysampling.util.logging;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.ErrorManager;



/**
 * An <code>ErrorManager</code> that writes all messages as
 * <code>Exception</code> s to standard error, with stack traces, and
 * translates the error codes into meaningful strings. If an
 * <code>Exception</code> is being written, this class nests it in the
 * printed <code>Exception</code>.
 * 
 * @author Chris Mosher
 */
public class StandardErrorManager extends ErrorManager
{
    private static final String[] rCode =
    {
        "in logger",
        "trying to write to log file",
        "trying to flush log file",
        "trying to close log file",
        "trying to open log file",
        "trying to format a log message"
    };

    /**
     * Writes the given message, exception, and code to standard error (even
     * if <code>System.err</code> had been redirected elsewhere).
     * @param msg the error message to print
     * @param ex the exception to print or null if no exception
     * @param code the code as defined in <code>java.util.logging.ErrorManager</code>
     */
    public synchronized void error(String msg, Exception ex, int code)
    {
        if (code < 0 || rCode.length <= code)
        {
            code = 0;
        }
        String s = "Error "+rCode[code];

        if (msg != null && msg.length() > 0)
        {
            s += ": "+msg;
        }

        new Exception(s).fillInStackTrace().initCause(ex).printStackTrace(new PrintStream(new FileOutputStream(FileDescriptor.err)));
    }
}
