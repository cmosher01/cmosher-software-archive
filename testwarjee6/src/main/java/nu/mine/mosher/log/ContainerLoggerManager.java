package nu.mine.mosher.log;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContainerLoggerManager implements ServletContextListener {
  @Override
  public void contextInitialized(@SuppressWarnings("unused") final ServletContextEvent sce) {
    // we don't care about this notification
  }

  @Override
  public void contextDestroyed(@SuppressWarnings("unused") final ServletContextEvent sce) {
    LoggerManager.release();
  }
}
