package nu.mosher.mine.a2diskedit;

import java.awt.Component;

public class CommandNew extends Command
{
	public CommandNew()
	{
		super("new");
	}

	/**
	 * @see nu.mosher.mine.a2diskedit.Command#dispatch()
	 */
	public void dispatch()
	{
		A2DiskEdit.getApp().fileNew();
	}

	public Component getRightPane()
	{
		return null;
	}
}
