/*
 * Created on May 21, 2004
 */
package nu.mine.mosher.logging;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.ErrorManager;

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
     * Redirects System.out and System.err to stdout and stderr,
     * and writes the given msg, exception, and code to stderr.
     */
    public synchronized void error(String msg, Exception ex, int code)
    {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));

        if (code < 0 || rCode.length <= code)
        {
            code = 0;
        }
        String s = "Error "+rCode[code];

        if (msg != null && msg.length() > 0)
        {
            s += ": "+msg;
        }

        new Exception(s).fillInStackTrace().initCause(ex).printStackTrace();
    }
}
