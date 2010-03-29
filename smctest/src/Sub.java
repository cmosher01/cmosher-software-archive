import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import statemap.State;




public class Sub
{
	private final SubFSMImpl sm;
	private final StateChangeMonitor mon;

	public static void main(final String... args) throws UnsupportedEncodingException
	{
		final Sub x = new Sub();
		x.run();
	}
	public Sub() throws UnsupportedEncodingException
	{
		this.sm = new SubFSMImpl(this);

		final PrintWriter err = new PrintWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.err),"UTF-8"),true);
		final StateChangeFormatter formatter = new StateChangeFormatter(err);

		this.mon = new StateChangeMonitor(this.sm,formatter);
	}

	public void run()
	{
		this.sm.Hit();
		this.sm.toB();



//		this.sm.toC();
//		this.sm.Done();
//		this.sm.Release();



		this.sm.Cancel();



		this.sm.Hit();
		this.sm.toX();
//		this.sm.Done();

		this.mon.dispose();
	}

	public void nothing()
	{
		// no actions
	}

	public void undefinedChange(final State endState)
	{
		this.mon.undefinedChange(endState);
	}
}
