package callcontrol.solution;

import callcontrol.support.Packet;

public interface Command
{
	void execute(Packet packet);
}
