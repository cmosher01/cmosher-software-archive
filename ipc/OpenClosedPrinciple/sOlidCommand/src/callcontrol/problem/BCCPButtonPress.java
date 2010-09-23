package callcontrol.problem;

import callcontrol.support.CTIX;
import callcontrol.support.Packet;

public class BCCPButtonPress
{
	private final CTIX ctix;
	public BCCPButtonPress(CTIX ctix)
	{
		this.ctix = ctix;
	}
	public void make(Packet packet)
	{
		String digits = packet.getDigits();
		ctix.send("make " + digits);
	}
	public void joinCall(Packet packet)
	{
		int trid = packet.getTrid();
		ctix.send("join " + trid);
	}
	public void hold(Packet packet)
	{
		int trid = packet.getTrid();
		ctix.send("hold " + trid);
	}
	// ...
}
