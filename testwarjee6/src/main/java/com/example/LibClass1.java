package com.example;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import nu.mine.mosher.log.LoggerManager;

public class LibClass1 {
  public void something() {
    int s = 4;
    LoggerManager.msg(6, "s is first "+s);
    ++s;
    LoggerManager.msg(5, "s is now "+s);
  }
}
