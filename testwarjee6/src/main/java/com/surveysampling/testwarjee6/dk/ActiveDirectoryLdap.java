package com.surveysampling.testwarjee6.dk;



import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.sun.jndi.ldap.LdapCtxFactory;



/**
 * Connects to Active Directory via LDAP and authenticates a user.
 * 
 * @author christopher_mosher
 */
public class ActiveDirectoryLdap {
  private final Properties ldpaActiveDirectory;

  /**
   * Indicates that a problem occurred while trying to access the LDAP server.
   * This exception is not thrown as the result of simply an invalid username or
   * password.
   * 
   * @author christopher_mosher
   */
  public static class FailedToAccessServerException extends IOException {
    /**
     * Initializes this exception with the underlying cause.
     * 
     * @param cause
     *          underlying exception that caused thi connection exception
     * @param ldapServer
     *          name of LDAP server (just so we can display it in the error
     *          message)
     */
    public FailedToAccessServerException(final Throwable cause, final String ldapServer) {
      super("Error trying to access Active Directory LDAP server " + ldapServer, cause);
    }
  }

  /**
   * Initializes a new authenticator that will connect to the given LDAP AD
   * server with the given credentials.
   * 
   * @param urlLdapActiveDirectory
   *          the {@link URI} of the Active Directory LDAP server, such as
   *          <code>new URI("ldaps://ctdc03.surveysampling.com:636")</code>
   * @param domain
   *          the Active Directory Domain Name, such as
   *          <code>"surveysampling"</code> or <code>"surveysampling.com"</code>
   * @param username
   *          the username to authenticate with
   * @param password
   *          the password for the username to authenticate with
   */
  public ActiveDirectoryLdap(final URI urlLdapActiveDirectory, final String domain, final String username, final String password) {
    this(urlLdapActiveDirectory, domain, username, password, new Properties());
  }

  /**
   * Initializes a new authenticator that will connect to the given LDAP AD
   * server with the given credentials.
   * 
   * @param urlLdapActiveDirectory
   *          the {@link URI} of the Active Directory LDAP server, such as
   *          <code>new URI("ldaps://ctdc03.surveysampling.com")</code>
   * @param domain
   *          the Active Directory Domain Name, such as
   *          <code>"surveysampling"</code> or <code>"surveysampling.com"</code>
   * @param username
   *          the username to authenticate with
   * @param password
   *          the password for the username to authenticate with
   * @param ldpaActiveDirectory
   *          {@link Properties} containing any other properties for JNDI
   */
  public ActiveDirectoryLdap(final URI urlLdapActiveDirectory, final String domain, final String username, final String password, final Properties ldpaActiveDirectory) {
    this.ldpaActiveDirectory = ldpaActiveDirectory;

    this.ldpaActiveDirectory.setProperty(Context.INITIAL_CONTEXT_FACTORY, LdapCtxFactory.class.getName());
    this.ldpaActiveDirectory.setProperty(Context.PROVIDER_URL, urlLdapActiveDirectory.toString());
    setCredentials(domain, username, password);
  }

  private void setCredentials(final String domain, final String username, final String password) {
    this.ldpaActiveDirectory.setProperty(Context.SECURITY_PRINCIPAL, buildPrincipal(domain, username));
    this.ldpaActiveDirectory.setProperty(Context.SECURITY_CREDENTIALS, password);
  }



  /**
   * Authenticates the user.
   * 
   * @return <code>true</code> if the username/password authenticates OK;
   *         <code>false</code> if the username/password is invalid.
   * @throws FailedToAccessServerException
   *           if there is an error (other than an authentication error) while
   *           trying to access the Active Directory LDAP server
   */
  public boolean authenticate() throws ActiveDirectoryLdap.FailedToAccessServerException {
    final String pw = this.ldpaActiveDirectory.getProperty(Context.SECURITY_CREDENTIALS);
    if (pw == null || pw.isEmpty()) return false;
    Context ctx = null;
    try {
      /*
       * Try to connect to this Active Directory LDAP server using the given
       * domain, username, and password.
       */
      ctx = createInitialNamingContext();
    } catch (final AuthenticationException invalidCredentials) {
      /*
       * We expect an AuthenticationException to be thrown when the
       * username/password is invalid, so just return false.
       */
      return false;
    } catch (final Throwable someOtherProblem) {
      /*
       * Any other exception indicates a problem with the system, so let those
       * exceptions propagate.
       */
      throw new ActiveDirectoryLdap.FailedToAccessServerException(someOtherProblem, this.ldpaActiveDirectory.getProperty(Context.PROVIDER_URL));
    } finally {
      if (ctx != null) {
        try {
          ctx.close();
        } catch (final Throwable ignore) {
          // problem closing the connection; not critical so ignore it
        } finally {
          ctx = null;
        }
      }
    }
    /*
     * Nothing went wrong when we authenticated with Active Directory, so the
     * username/password must be OK.
     */
    return true;
  }



  /**
   * Creates a JNDI initial context for this Active Directory LDAP server.
   * 
   * @return the naming context
   * @throws NamingException
   *           if there is any error, authentication or otherwise, while trying
   *           to connect to the LDAP server
   */
  private InitialContext createInitialNamingContext() throws NamingException {
    return new InitialContext(this.ldpaActiveDirectory);
  }



  private final static String ARPA_ADDR_SPEC_DELIM = "@";

  //@formatter:off
  /**
   * Creates the user principal name UPN (for Active Directory). The UPN is in the form
   * username@domain, according to "addr-spec" specification in RFC 822, section 6.1.
   * 
   * @see <a href="http://technet.microsoft.com/en-us/library/cc739093%28v=ws.10%29.aspx">Active Directory naming</a>
   * 
   * @param domain
   *          Active Directory domain name
   * @param username
   *          SAM account name
   * @return UPN of given user
   */
  //@formatter:on
  private static String buildPrincipal(final String domain, final String username) {
    final StringBuilder sb = new StringBuilder();
    sb.append(username);
    sb.append(ARPA_ADDR_SPEC_DELIM);
    sb.append(domain);
    return sb.toString();
  }
}
