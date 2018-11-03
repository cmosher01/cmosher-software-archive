package com.surveysampling.testwarjee6.dk;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collections;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//@formatter:off
/**
 * <p>
 * This Trust Manager is "naive" because it trusts everyone.
 * </p>
 * <p>
 * Copyright 2009, by Howard Abrams: CreativeCommons Attribution 2.5 Generic (CC
 * BY 2.5).
 * </p>
 * <p>
 * Modified, refactored, and reformatted, by Chris Mosher, 2011.
 * </p>
 * 
 * @see <a href="http://www.howardism.org/Technical/Java/SelfSignedCerts.html">Accepting Self-Signed SSL Certificates in Java (Approach 1)</a>
 * 
 * @author Howard Abrams
 * @author christopher_mosher
 */
//@formatter:on
class NaiveTrustManager implements X509TrustManager {


  //@formatter:off
  /**
   * @see <a href="http://download.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#SSLContext">SSLContext Algorithms</a>
   */
  //@formatter:on
  private static final String SSL_PROTOCOL = "SSL";

  /**
   * Gets an SSL socket factory that is naive regarding certificate problems
   * (such as self-signed)
   * 
   * @return factory that creates naive sockets
   * @throws NoSuchAlgorithmException
   *           if no SSL provider can be found
   * @throws KeyManagementException
   *           error initializing the SSL provider context
   */
  public static SocketFactory createSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
    final SSLContext context = SSLContext.getInstance(SSL_PROTOCOL);
    context.init(null, getTrustManagers(), null);
    return context.getSocketFactory();
  }

  private static TrustManager[] getTrustManagers() {
    return Collections.<TrustManager> singleton(new NaiveTrustManager()).toArray(new TrustManager[1]);
  }





  @SuppressWarnings("unused")
  @Override
  public void checkClientTrusted(X509Certificate[] cert, String authType) {
    return; // doing nothing means we trust the certificate
  }

  @SuppressWarnings("unused")
  @Override
  public void checkServerTrusted(X509Certificate[] cert, String authType) {
    return; // doing nothing means we trust the certificate
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return new X509Certificate[0];
  }
}
