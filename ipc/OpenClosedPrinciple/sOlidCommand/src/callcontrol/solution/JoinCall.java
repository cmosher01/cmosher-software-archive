package callcontrol.solution;

import callcontrol.support.CTIX;
import callcontrol.support.Packet;

public class JoinCall implements Command
{
	private CTIX ctix;
	public JoinCall(CTIX ctix)
	{
		this.ctix = ctix;
	}

	@Override
	public void execute(Packet packet)
	{
		int trid = packet.getTrid();
		this.ctix.send("join "+trid);
	}
}
