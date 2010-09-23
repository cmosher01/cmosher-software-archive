package callcontrol.solution;

import callcontrol.support.CTIX;
import callcontrol.support.Packet;

public class Make implements Command
{
	private CTIX ctix;
	public Make(CTIX ctix)
	{
		this.ctix = ctix;
	}

	@Override
	public void execute(Packet packet)
	{
		String digits = packet.getDigits();
		this.ctix.send("make "+digits);
	}
}
