package nu.mine.mosher.hiptopshell;

import danger.app.Application;
import danger.app.Event;
import danger.app.SocketRegistrar;
import danger.net.IPCServerSocket;



/**
 * Implements the main application class.
 */
public class HiptopShell extends Application
{
	private MainWindow win = new MainWindow();
	IPCServerSocket sock;

    /**
     * Creates the main application class.
     *
     */
    public HiptopShell()
    {
        win.setTitle("Shell");
    }

    public boolean receiveEvent(Event e)
    {
        switch (e.type)
        {
//            case Commands.kCmd_One :
//                // Todo: Insert code here...
//                DEBUG.p("Helloworld: Received kCmd_One");
//                break;
//            case Commands.kCmd_Two :
//                // Todo: Insert code here...
//                DEBUG.p("Helloworld: Received kCmd_Two");
//                break;
        }
        return (super.receiveEvent(e));
    }

    public void launch()
    {
    	sock = SocketRegistrar.makeServerSocket("theshell");
    	Thread th = new Thread(new Runnable()
    	{
            public void run()
            {
				try
				{
	            	while (true)
	            	{
                        new ClientConnection(sock.accept());
	            	}
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
            }
    	});
    	th.start();
    }

    public void resume()
    {
        win.show();
    }

    public void suspend()
    {
    }
}
