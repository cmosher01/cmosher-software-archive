package nu.mine.mosher.log;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

public class Logger implements LoggerConfig {
  private final String name;
  private int currentLevel = 6;

  public Logger(final String name) {
    this.name = name;
    final MBeanServer mbs = Logger.getMBeanServer();
    ObjectName mbeanName;
    try {
      mbeanName = new ObjectName(this.name+":type=Logger");
      mbs.registerMBean(this, mbeanName);
    } catch (final Throwable wrap) {
      throw new IllegalStateException(wrap);
    }
  }

  /*
   * 6=trace, 5=debug, 4=info, 3=warn, 2=error, 1=fatal, 0=(none)
   */
  public void log(final int level, final String message) {
    if (level <= this.currentLevel) {
      System.out.println(this.name+",L" + level + "," + message);
    }
  }

  @Override
  public int getLevel() {
    return this.currentLevel;
  }

  @Override
  public void setLevel(final int level) {
    this.currentLevel = level;
  }








  private static MBeanServer getMBeanServer() {
    final ArrayList<MBeanServer> rBeanServer = MBeanServerFactory.findMBeanServer(null);
    if (rBeanServer.isEmpty()) {
      return ManagementFactory.getPlatformMBeanServer();
    }
    return rBeanServer.get(0);
  }
}
