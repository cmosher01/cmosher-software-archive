/**
 * @(#)ServerTest.java April 3, 1998 I MAKE NO WARRANTIES ABOUT THE SUITABILITY
 * OF THIS SOFTWARE, EITHER EXPRESS OR IMPLIED AND SHALL NOT BE LIABLE FOR ANY
 * DAMAGES THIS SOFTWARE MAY BRING TO YOUR SYSTEM. USE IT AT YOUR OWN RISK.
 * Author : Steve Yeong-Ching Hsueh
 */

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



/**
 * ServerTest is an application the works as an HTTP client
 */
public class ServerTest
{
    String host = "127.0.0.1";

    int port = 2004;

    int MAX_TXN = 25;



    public ServerTest(int txn)
    {
        Socket sock;
        DataOutputStream dout;
        //String msg = "GET / HTTP/1.0\n\n";
        String msg = "Hello\n\n";
        int sleeptime = 200;
        if (txn > 0)
            MAX_TXN = txn;
        for (int i = 0; i < MAX_TXN; i++)
        {
            try
            {
                Thread.sleep(sleeptime);
                sock = new Socket(host,port);
                dout = new DataOutputStream(sock.getOutputStream());
                dout.writeBytes(msg);
                dout.flush();
                // System.out.println(i + " sending " + msg);
                sock.close();
            }
            catch (IOException e)
            {
                //System.exit(0);
                sleeptime += 10;
                System.out.println("sleeptime=" + sleeptime + "\nI/O error : " + e);
            }
            catch (InterruptedException e)
            {
            }

            //System.gc();
        }
    }

    /**
     * main, argv is the number of requests to send
     */
    public static void main(String argv[])
    {
        int txn = 0;
        System.out.println(argv.length + ":" + argv[0]);
        if (argv.length >= 1 && argv[0] != null)
            txn = Integer.parseInt(argv[0]);

        new ServerTest(txn);
    }

}

