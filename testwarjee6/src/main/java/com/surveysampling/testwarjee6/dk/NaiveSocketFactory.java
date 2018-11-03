package com.surveysampling.testwarjee6.dk;



import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.net.SocketFactory;



/**
 * Factory of naive sockets. Use <code>getDefault</code> to create a factory.
 * Any sockets created by this factory will trust all SSL certificates.
 * 
 * @author christopher_mosher
 */
public class NaiveSocketFactory extends SocketFactory {
  /**
   * Gets the (default?) socket factory, actually a new
   * <code>NaiveSocketFactory</code>. This method is designed to be useful to
   * Sun's JNDI LDAP implementation (see
   * {@link NaiveSocketFactory#setInJndiEnvironment setInJndiEnvironment}).
   * 
   * @return a new <code>NaiveSocketFactory</code>
   */
  public static NaiveSocketFactory getDefault() {
    try {
      final SocketFactory delegate = NaiveTrustManager.createSocketFactory();
      return new NaiveSocketFactory(delegate);
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  //@formatter:off
  /**
   * Convenience method that sets the JNDI LDAP provider's socket factory to a
   * <code>NaiveSocketFactory</code>.
   * 
   * @param jndiEnvironment
   *          JNDI LDAP environment to set the
   *          <code>java.naming.ldap.factory.socket</code> property in.
   *          
   * @see <a href="http://download.oracle.com/javase/6/docs/technotes/guides/jndi/jndi-ldap-gl.html#socket">JNDI Implementor Guidelines for LDAP Service Providers</a>
   */
  //@formatter:on
  public static void setInJndiEnvironment(final Properties jndiEnvironment) {
    jndiEnvironment.setProperty("java.naming.ldap.factory.socket", NaiveSocketFactory.class.getName());
  }

  /*
   * This class is just a simple SocketFactory delegate.
   */


  private final SocketFactory delegate;

  /**
   * Initializes this socket factory with a delegate to send all calls to.
   * 
   * @param delegate
   *          object to delegate all method calls to
   */
  private NaiveSocketFactory(final SocketFactory delegate) {
    this.delegate = delegate;
  }

  @Override
  public Socket createSocket() throws IOException {
    return this.delegate.createSocket();
  }

  @Override
  public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
    return this.delegate.createSocket(arg0, arg1, arg2, arg3);
  }

  @Override
  public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
    return this.delegate.createSocket(arg0, arg1);
  }

  @Override
  public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
    return this.delegate.createSocket(arg0, arg1, arg2, arg3);
  }

  @Override
  public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
    return this.delegate.createSocket(arg0, arg1);
  }
}
