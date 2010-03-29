import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import statemap.FSMContext;
import statemap.State;


public class StateChangeMonitorSimple
{
	private final FSMContext fsm;

	public StateChangeMonitorSimple(final FSMContext fsm)
	{
		this.fsm = fsm;
		this.fsm.setDebugFlag(false);
		this.fsm.enterStartState();
		this.fsm.addStateChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(final PropertyChangeEvent event)
			{
				final State s1 = (State)event.getOldValue();
				final State s2 = (State)event.getNewValue();
				System.err.println("["+s1+"]-->"+fsm.getTransition()+"-->["+s2+"]");
			}
		});
	}

	public void undefinedChange(@SuppressWarnings("unused") final State endState)
	{
		//
	}

	public void dispose()
	{
		//
	}
}
