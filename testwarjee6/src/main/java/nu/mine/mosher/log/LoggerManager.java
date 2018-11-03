package nu.mine.mosher.log;


import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public final class LoggerManager {

  private static final ConcurrentMap<String, Boolean> map = new ConcurrentHashMap<String, Boolean>(64,1.0f,64);

  private LoggerManager() {
    throw new IllegalStateException();
  }

  public static void msg(final String loggerName, final int level, final String message) {
    final Boolean exists = map.putIfAbsent(loggerName, Boolean.TRUE);
    final int currentLevel;
    if (exists != null) {
      currentLevel = getLevel(loggerName);
    } else {
      final Logger logger = new Logger();
      currentLevel = logger.getLevel();
      registerLogger(logger, loggerName);
    }
    if (level <= currentLevel) {
      log(loggerName, level, message);
    }
  }

  public static void msg(final int level, final String message, final int stackOffset) {
    final String name = Thread.currentThread().getStackTrace()[2 + stackOffset].getClassName();
    msg(name, level, message);
  }

  public static void msg(final int level, final String message) {
    msg(level, message, 1);
  }

  public static void release() {
    for (final String loggerName : map.keySet()) {
      unregisterLogger(loggerName);
    }
    map.clear();
  }

  // TODO how to handle logging Throwable?
  private static void log(final String loggerName, final int level, final String message) {
    // TODO what should we *really* do in order to log messages?
//    System.err.println(getNow() + "," + loggerName + ",L" + level + ",\"" + message + "\"");
    LogMessageSender.send(getNow() + "," + loggerName + ",L" + level + ",\"" + message + "\"");
  }

  private static int getLevel(final String name) {
    try {
      return ((Integer) jmx().getAttribute(beanName(name), LoggerMBean.LEVEL)).intValue();
    } catch (final Throwable e) {
//      log(LoggerManager.class.getName(), 1, "Could not get Logging Level for logger named: " + name + ". Defaulting to TRACE level.");
      return 6;
    }
  }

  private static String getNow() {
    final DateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    return f.format(new Date());
  }

  private static MBeanServer jmx() {
    return ManagementFactory.getPlatformMBeanServer();
  }

  private static void registerLogger(final Logger logger, final String loggerName) {
    try {
      jmx().registerMBean(logger, beanName(loggerName));
    } catch (final Throwable wrap) {
//      log(LoggerManager.class.getName(), 1, "Could not register Logger with JMX, for logger named: " + loggerName);
    }
  }

  private static void unregisterLogger(final String loggerName) {
    try {
      jmx().unregisterMBean(beanName(loggerName));
    } catch (final Throwable wrap) {
//      log(LoggerManager.class.getName(), 1, "Could not un-register Logger from JMX, for logger named: " + loggerName);
    }
  }

  private static ObjectName beanName(final String loggerName) {
    try {
      return new ObjectName(loggerName + ":type=Logger");
    } catch (final Throwable wrap) {
//      log(LoggerManager.class.getName(), 1, "Could not create JMX object name: " + loggerName);
      try {
        return new ObjectName("LoggerManager:type=Logger");
      } catch (final Throwable cannotHappen) {
        throw new IllegalStateException(cannotHappen);
      }
    }
  }
}
