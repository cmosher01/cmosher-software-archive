package com.ipc.uda.service.util.smc.monitor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;

import statemap.FSMContext;
import statemap.State;


public class StateChangeMonitorSimple implements StateChangeMonitor
{
	public StateChangeMonitorSimple(final FSMContext fsm, final PrintWriter printer)
	{
		fsm.setDebugFlag(false);
		fsm.enterStartState();
		fsm.addStateChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(final PropertyChangeEvent event)
			{
				final State s1 = (State)event.getOldValue();
				final State s2 = (State)event.getNewValue();
				printer.println("["+s1+"]-->"+fsm.getTransition()+"-->["+s2+"]");
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
