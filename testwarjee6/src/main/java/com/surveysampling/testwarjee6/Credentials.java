package com.surveysampling.testwarjee6;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.surveysampling.testwarjee6.dk.ActiveDirectoryLdap;
import com.surveysampling.testwarjee6.dk.NaiveSocketFactory;


@Named
@SessionScoped
public class Credentials implements Serializable {
  private String username;
  private String password;

  public Credentials() {
    System.out.println("creating new Credentials object: "+this.toString());
  }

  @PostConstruct
  public void postConstruct() {
    reset();
  }

  private void reset() {
    this.username = "";
    this.password = "";
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
    System.err.println("set username("+this.toString()+"): "+this.username);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
    System.err.println("set password("+this.toString()+"): "+this.password);
  }

  public void login() {
    final String uriActiveDirectoryLdap = "ldaps://ctdc03.surveysampling.com";
    try {
      System.out.println("login ("+this.toString()+") with: "+this.username+" "+this.password);
      final boolean isAuth = authenticate("surveysampling.com",this.username,this.password,uriActiveDirectoryLdap);
      System.out.println("login result: "+isAuth);
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  private static boolean authenticate(final String domain, final String username, final String password, final String uriAD) throws URISyntaxException, ActiveDirectoryLdap.FailedToAccessServerException {
    final Properties jndiEnvironment = new Properties();
    // tell LDAP JNDI provider to accept all SSL certificates:
    NaiveSocketFactory.setInJndiEnvironment(jndiEnvironment);

    final ActiveDirectoryLdap ad = new ActiveDirectoryLdap(new URI(uriAD), domain, username, password, jndiEnvironment);

    // authenticate user:
    return ad.authenticate();
  }
}
