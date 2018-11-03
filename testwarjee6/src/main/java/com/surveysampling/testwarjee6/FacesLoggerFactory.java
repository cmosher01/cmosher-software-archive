package com.surveysampling.testwarjee6;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * @author christopher_mosher
 */
public final class FacesLoggerFactory {
  private FacesLoggerFactory() {
    /* never instantiated */
    throw new IllegalStateException();
  }





  /**
   * 
   * @return
   */
  public static Logger createFacesLogger() {
    final Logger logger = Logger.getAnonymousLogger();
    final FacesLogHandler handler = new FacesLogHandler(logger.getParent());
    logger.addHandler(handler);
    logger.setUseParentHandlers(false);
    return logger;
  }





  /**
   * 
   * @author christopher_mosher
   */
  private static class FacesLogHandler extends Handler {
    /**
     * Logger to fall-back to when the Faces context cannot be found. A
     * <code>null</code> value is legal, and indicates that no fallback logger
     * exists (in which case standard error stream is used, as a last resort).
     */
    private final Logger parentLoggerOrNull;



    /**
     * Initializes this handler. Sets the fallback parent logger.
     * 
     * @param parentLogger
     *          fallback logger, or null
     */
    public FacesLogHandler(final Logger parentLogger) {
      this.parentLoggerOrNull = parentLogger;
    }



    /**
     * Publishes (logs) the given <code>LogRecord</code> using the current
     * thread's Faces external context, if possible; otherwise logs it to this
     * handler's parent logger, if possible, otherwise writes the record to
     * standard error.
     */
    @Override
    public void publish(final LogRecord record) {
      if (!isLoggable(record)) {
        return;
      }
      synchronized (FacesLoggerFactory.class) {
        final ExternalContext externalContext = getExternalContext();
        if (externalContext == null) {
          parent(record);
        } else {
          log(record, externalContext);
        }
      }
    }

    @Override
    public void flush() { /* NoOp */
    }

    @Override
    public void close() { /* NoOp */
    }



    /**
     * Logs the given record using this handler's <em>parent</em> logger. If
     * this handler does not have a parent logger, then this method simply
     * prints the record to the current standard error stream.
     * 
     * @param record
     *          <code>LogRecord</code> to be logged
     */
    private void parent(final LogRecord record) {
      if (this.parentLoggerOrNull == null) {
        System.err.println(getMessage(record));
      } else {
        this.parentLoggerOrNull.log(record);
      }
    }





    /**
     * Gets the Faces external context (of the request that is being processed
     * by the current thread), if possible; otherwise returns <code>null</code>.
     * 
     * @return Faces context, or null
     */
    private static ExternalContext getExternalContext() {
      try {
        return FacesContext.getCurrentInstance().getExternalContext();
      } catch (final Throwable ignore) {
        return null;
      }
    }

    /**
     * Logs a the given record to the given external context, along with some
     * other useful information obtained from the context.
     * 
     * @param record
     *          <code>LogRecord</code> to log (can be null)
     * @param externalContext
     *          where to log the record to, and where to get other information
     *          from (cannot be null)
     */
    private static void log(final LogRecord record, final ExternalContext externalContext) {
      externalContext.log(getContextInfo(externalContext) + " " + getMessage(record));
    }

    /**
     * Gets the message from the given <code>LogRecord</code>. If the given
     * record is null, or contains a null or empty log message, then the string
     * <code>"[empty log message]"</code> is returned instead,
     * 
     * @param record
     *          <code>LogRecord</code> to extract the message from (or null)
     * @return message, or <code>"[empty log message]"</code> (never null or
     *         empty)
     */
    private static String getMessage(final LogRecord record) {
      if (record == null) {
        return "[empty log message]";
      }
      final String messageOrNull = record.getMessage();
      if (messageOrNull == null || messageOrNull.isEmpty()) {
        return "[empty log message]";
      }
      return messageOrNull;
    }

    /**
     * Gets some useful information from the given external context.
     * 
     * @param externalContext
     *          (cannot be null)
     * @return string with session ID, client hostname, and X-Forward-For value;
     *         never null
     */
    private static String getContextInfo(final ExternalContext externalContext) {
      final String sessionID = getSessionID(externalContext);
      final String client = getClient(externalContext);
      final String xfwd = getOriginalClient(externalContext);
      return "{" + sessionID + "} {" + client + "} {" + xfwd + "}";
    }

    /**
     * Returns the <code>HttpSession</code> ID from the given external context.
     * If not possible, returns an empty string.
     * 
     * @param externalContext
     *          (cannot be null)
     * @return session ID, or empty string
     */
    private static String getSessionID(final ExternalContext externalContext) {
      final Object session = externalContext.getSession(false);
      if (!(session instanceof HttpSession)) {
        return "";
      }
      final HttpSession httpSession = (HttpSession) session;
      return httpSession.getId();
    }

    /**
     * Returns the fully qualified name of the client or the last proxy that
     * sent the request. If not possible, returns an empty string.
     * 
     * @param externalContext
     *          (cannot be null)
     * @return client hostname, or empty string
     */
    private static String getClient(final ExternalContext externalContext) {
      final Object request = externalContext.getRequest();
      if (!(request instanceof ServletRequest)) {
        return "";
      }
      final ServletRequest servletRequest = (ServletRequest) request;
      return servletRequest.getRemoteHost();
    }

    /**
     * Gets the value(s) of the <code>X-Forwarded-For</code> headers in the
     * given <code>ExternalContext</code>, if that context has an associated
     * <code>HttpServletRequest</code>. Otherwise, returns an empty string.
     * 
     * @param externalContext
     *          (cannot be null)
     * @return <code>X-Forward-For</code> header value, or empty string
     */
    private static String getOriginalClient(final ExternalContext externalContext) {
      final Object request = externalContext.getRequest();
      if (!(request instanceof HttpServletRequest)) {
        return "";
      }
      final HttpServletRequest servletRequest = (HttpServletRequest) request;
      return getClientIP(servletRequest);
    }

    /**
     * Gets the value(s) of the <code>X-Forwarded-For</code> headers in the
     * given <code>HttpServletRequest</code>. Returns empty string if no such
     * headers are found.
     * 
     * @param request
     *          (cannot be null)
     * @return <code>X-Forward-For</code> header value, or empty string
     */
    private static String getClientIP(final HttpServletRequest request) {
      final StringBuilder sb = new StringBuilder();
      final Enumeration<String> headers = request.getHeaders("X-Forwarded-For");
      while (headers != null && headers.hasMoreElements()) {
        final String client = headers.nextElement();
        sb.append(" ");
        sb.append(client);
      }
      return sb.toString();
    }
  }
}
