package nu.mine.mosher.log;

class Logger implements LoggerMBean {
  /*
   * 6=trace, 5=debug, 4=info, 3=warn, 2=error, 1=fatal, 0=(none)
   */
  private int currentLevel = 4;

  @Override
  public int getLevel() {
    return this.currentLevel;
  }

  @Override
  public void setLevel(final int level) {
    this.currentLevel = level;
  }
}
