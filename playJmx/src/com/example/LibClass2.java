package com.example;

import nu.mine.mosher.log.Logger;

public class LibClass2 {
  private static final Logger log = new Logger(LibClass2.class.getName());
  public void something() {
    int s = 9;
    log.log(6, "s is first "+s);
    ++s;
    log.log(5, "s is now "+s);
  }
}
