package nu.mine.mosher.log;

import javax.management.MXBean;

@MXBean
public interface LoggerConfig {
  int getLevel();
  void setLevel(int level);
}
