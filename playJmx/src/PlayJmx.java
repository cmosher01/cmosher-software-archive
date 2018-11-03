/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Main.java - main class for the Hello MBean and QueueSampler MXBean example.
 * Create the Hello MBean and QueueSampler MXBean, register them in the platform
 * MBean server, then wait forever (or until the program is interrupted).
 */

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import com.example.Hello;
import com.example.LibClass1;
import com.example.LibClass2;
import com.example.QueueSampler;

public class PlayJmx {
  private static void testLogging() {
    while (true) {
      final LibClass1 c1 = new LibClass1();
      c1.something();
      final LibClass2 c2 = new LibClass2();
      c2.something();
      final LibClass1 c1x = new LibClass1();
      c1x.something();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
  /*
   * For simplicity, we declare "throws Exception". Real programs will usually
   * want finer-grained exception handling.
   */
  public static void main(String[] args) throws Exception {
    testLogging();
  }

  private static void testJmx() throws Throwable {
    // Get the Platform MBean Server
    final MBeanServer mbs = PlayJmx.getMBeanServer();

    // Construct the ObjectName for the Hello MBean we will register
    final ObjectName mbeanName = new ObjectName("com.example:type=Logger");

    // Create the Hello World MBean
    final Hello mbean = new Hello();

    // Register the Hello World MBean
    mbs.registerMBean(mbean, mbeanName);

    // Construct the ObjectName for the QueueSampler MXBean we will register
    final ObjectName mxbeanName = new ObjectName("com.example:type=QueueSampler");

    // Create the Queue Sampler MXBean
    final Queue<String> queue = new ArrayBlockingQueue<String>(10);
    queue.add("Request-1");
    queue.add("Request-2");
    queue.add("Request-3");
    final QueueSampler mxbean = new QueueSampler(queue);

    // Register the Queue Sampler MXBean
    mbs.registerMBean(mxbean, mxbeanName);

    // Wait forever
    System.out.println("Waiting for incoming requests...");
    Thread.sleep(Long.MAX_VALUE);
  }

  private static MBeanServer getMBeanServer() {
    final ArrayList<MBeanServer> rBeanServer = MBeanServerFactory.findMBeanServer(null);
    if (rBeanServer.isEmpty()) {
      return ManagementFactory.getPlatformMBeanServer();
    }
    return rBeanServer.get(0);
  }
}
