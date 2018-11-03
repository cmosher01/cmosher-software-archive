package com.example;

import nu.mine.mosher.log.Logger;

public class LibClass1 {
  private static final Logger log = new Logger(LibClass1.class.getName());
  public void something() {
    int s = 4;
    log.log(6, "s is first "+s);
    ++s;
    log.log(5, "s is now "+s);
  }
}
