package nu.mine.mosher.log;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;



class LogMessageSender {

  @SuppressWarnings("unchecked")
  private static <T> T lookUpOrNull(final String name) {
    try {
      final Context ctx = new InitialContext();
      return (T)ctx.lookup(name);
    } catch (final Throwable e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void send(final String message) {
    final ConnectionFactory connectionFactory = lookUpOrNull("java:/ConnectionFactory");
    final Queue logMessages = lookUpOrNull("/queue/LogMessages");

    if (connectionFactory == null || logMessages == null) {
      System.err.println(message);
      return;
    }

    Connection connection = null;
    try {
      connection = connectionFactory.createConnection();
      final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

      final MessageProducer producer = session.createProducer(logMessages);
      final Message jmsmsg = session.createObjectMessage(message);
      producer.send(jmsmsg);
    } catch (final Throwable second) {
      second.printStackTrace();
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (final Throwable e) {
          e.printStackTrace();
        }
      }
    }
  }
}
