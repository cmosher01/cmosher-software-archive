package callcontrol.solution;

import java.util.HashMap;
import java.util.Map;

import callcontrol.support.CTIX;
import callcontrol.support.Packet;

public class CCRequestHandler
{
	private final CTIX ctix = new CTIX();
	private final Map<String,Command> cmds = new HashMap<String,Command>();
	public CCRequestHandler()
	{
		cmds.put("MAKE",new Make(ctix));
		cmds.put("JOIN",new JoinCall(ctix));
		cmds.put("HOLD",new Hold(ctix));
		// ...
	}

	public void TIBCOToButtonPress(Packet packet)
	{
		String cmd = packet.getCommand();
		Command c = cmds.get(cmd);
		c.execute(packet);
	}
}
