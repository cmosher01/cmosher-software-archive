package callcontrol.problem;

import callcontrol.support.CTIX;
import callcontrol.support.Packet;

public class CCRequestHandler
{
	private final CTIX ctix = new CTIX();
	private final BCCPButtonPress bp = new BCCPButtonPress(ctix);

	public void TIBCOToButtonPress(Packet packet)
	{
		String cmd = packet.getCommand();
		if (cmd.equals("MAKE"))
		{
			bp.make(packet);
		}
		else if (cmd.equals("JOIN"))
		{
			bp.joinCall(packet);
		}
		else if (cmd.equals("HOLD"))
		{
			bp.hold(packet);
		}
		// ...
	}
}
