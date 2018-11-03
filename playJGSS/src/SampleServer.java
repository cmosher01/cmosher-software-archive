/*
 * @(#)SampleServer.java
 *
 * Copyright (c) 2001, 2002, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following 
 * conditions are met:
 * 
 * -Redistributions of source code must retain the above copyright  
 * notice, this  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduct the above copyright 
 * notice, this list of conditions and the following disclaimer in 
 * the documentation and/or other materials provided with the 
 * distribution.
 * 
 * Neither the name of Oracle or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY 
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY 
 * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF  OR 
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR 
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, 
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER 
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that Software is not designed, licensed or 
 * intended for use in the design, construction, operation or 
 * maintenance of any nuclear facility. 
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.MessageProp;

/**
 * A sample server application that uses JGSS to do mutual authentication with a
 * client using Kerberos as the underlying mechanism. It then exchanges data
 * securely with the client.
 * 
 * Every message exchanged with the client includes a 4-byte application- level
 * header that contains the big-endian integer value for the number of bytes
 * that will follow as part of the JGSS token.
 * 
 * The protocol is: 1. Context establishment loop: a. client sends init sec
 * context token to server b. server sends accept sec context token to client
 * .... 2. client sends a wrap token to the server. 3. server sends a mic token
 * to the client for the application message that was contained in the wrap
 * token.
 */
public class SampleServer {
  public static void main(String[] args) throws IOException, GSSException {
    // Obtain the command-line arguments and parse the port number
    if (args.length != 1) {
      System.err.println("Usage: java <options> Login SampleServer <localPort>");
      System.exit(-1);
    }
    int localPort = Integer.parseInt(args[0]);
    ServerSocket ss = new ServerSocket(localPort);
    GSSManager manager = GSSManager.getInstance();
    while (true) {
      System.out.println("Waiting for incoming connection...");
      Socket socket = ss.accept();
      DataInputStream inStream = new DataInputStream(socket.getInputStream());
      DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
      System.out.println("Got connection from client " + socket.getInetAddress());
      /*
       * Create a GSSContext to receive the incoming request from the client.
       * Use null for the server credentials passed in. This tells the
       * underlying mechanism to use whatever credentials it has available that
       * can be used to accept this connection.
       */
      GSSContext context = manager.createContext((GSSCredential) null);
      // Do the context eastablishment loop
      byte[] token = null;
      while (!context.isEstablished()) {
        token = new byte[inStream.readInt()];
        System.out.println("Will read input token of size " + token.length + " for processing by acceptSecContext");
        inStream.readFully(token);
        token = context.acceptSecContext(token, 0, token.length);
        // Send a token to the peer if one was generated by
        // acceptSecContext
        if (token != null) {
          System.out.println("Will send token of size " + token.length + " from acceptSecContext.");
          outStream.writeInt(token.length);
          outStream.write(token);
          outStream.flush();
        }
      }
      System.out.print("Context Established! ");
      System.out.println("Client is " + context.getSrcName());
      System.out.println("Server is " + context.getTargName());
      /*
       * If mutual authentication did not take place, then only the client was
       * authenticated to the server. Otherwise, both client and server were
       * authenticated to each other.
       */
      if (context.getMutualAuthState())
        System.out.println("Mutual authentication took place!");
      /*
       * Create a MessageProp which unwrap will use to return information such
       * as the Quality-of-Protection that was applied to the wrapped token,
       * whether or not it was encrypted, etc. Since the initial MessageProp
       * values are ignored, just set them to the defaults of 0 and false.
       */
      MessageProp prop = new MessageProp(0, false);
      /*
       * Read the token. This uses the same token byte array as that used during
       * context establishment.
       */
      token = new byte[inStream.readInt()];
      System.out.println("Will read token of size " + token.length);
      inStream.readFully(token);
      byte[] bytes = context.unwrap(token, 0, token.length, prop);
      String str = new String(bytes);
      System.out.println("Received data \"" + str + "\" of length " + str.length());
      System.out.println("Confidentiality applied: " + prop.getPrivacy());
      /*
       * Now generate a MIC and send it to the client. This is just for
       * illustration purposes. The integrity of the incoming wrapped message is
       * guaranteed irrespective of the confidentiality (encryption) that was
       * used.
       */
      /*
       * First reset the QOP of the MessageProp to 0 to ensure the default
       * Quality-of-Protection is applied.
       */
      prop.setQOP(0);
      token = context.getMIC(bytes, 0, bytes.length, prop);
      System.out.println("Will send MIC token of size " + token.length);
      outStream.writeInt(token.length);
      outStream.write(token);
      outStream.flush();
      System.out.println("Closing connection with client " + socket.getInetAddress());
      context.dispose();
      socket.close();
    }
  }
}
