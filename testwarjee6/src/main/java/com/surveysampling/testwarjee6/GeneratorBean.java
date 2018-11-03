package com.surveysampling.testwarjee6;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.surveysampling.testwarjee6.pojo.Generator;

@ApplicationScoped
public class GeneratorBean implements Serializable {
  @Resource
  private int maxNumber;
  private Generator pojo;
  private final Logger logger = FacesLoggerFactory.createFacesLogger();

  public GeneratorBean() {
	  this.logger.info("GeneratorBean.<ctor>");
  }

  @PostConstruct
  public void postConstruct() {
	  this.logger.info("GeneratorBean.postConstruct");
	  this.pojo = new Generator(this.maxNumber);
  }

  @PreDestroy
  public void preDestroy() {
	  this.logger.info("GeneratorBean.preDestroy");
	  this.pojo = null;
  }

  @Override
  public void finalize() {
	  this.logger.info("GeneratorBean.finalize");
  }

  @Produces @RandomNumber
  public int getNext()
  {
	  final int r = this.pojo.getNext();
//	  System.err.println("Picked number: "+r);
	  return r;

  }

  @Produces @MaxNumber
  public int getMax()
  {
    return this.pojo.getMax();
  }
}
