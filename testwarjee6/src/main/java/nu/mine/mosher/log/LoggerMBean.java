package nu.mine.mosher.log;

public interface LoggerMBean {
  public static final String LEVEL = "Level";
  int getLevel();
  void setLevel(int level);
}
