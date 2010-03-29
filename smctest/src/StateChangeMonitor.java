import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Stack;

import statemap.FSMContext;
import statemap.State;


public class StateChangeMonitor
{
	private final FSMContext fsm;
	private final StateChangeListener listener;

	private final Stack<String> stackMap = new Stack<String>();
	private final Stack<String> stackState = new Stack<String>();
	private SmcState statePrev = new SmcState(null);



	public StateChangeMonitor(final FSMContext fsm, final StateChangeListener listener)
	{
		this.fsm = fsm;
		this.listener = listener;

		this.fsm.setDebugFlag(false);

		enterStartState();

		this.fsm.addStateChangeListener(new PropertyChangeListener()
		{
			@SuppressWarnings("synthetic-access")
			@Override
			public void propertyChange(final PropertyChangeEvent event)
			{
				final SmcState s1 = new SmcState((State)event.getOldValue());
				final SmcState s2 = new SmcState((State)event.getNewValue());
				change(s1,s2);
			}
			
		});
	}

	/**
	 * Indicates an undefined state change event has occurred.
	 * To use this method, define the following in every map
	 * of the FSM file:
	 * <code>
	 * <pre>
	 * Default
	 * {
	 *     Default nil { undefinedChange(endState); }
	 * }
	 * </pre>
	 * </code>
	 * Also define undefinedChange in your action class as simply
	 * a pass-through to this method.
	 * @param endState
	 */
	public void undefinedChange(final State endState)
	{
		this.listener.undefinedChange(
			this.statePrev.getStateName(),
			this.fsm.getTransition(),
			new SmcState(endState).getStateName());
	}

	/**
	 * Indicates that this monitor's FSM is being disposed. This
	 * method will cause the listener to be notified of transitions
	 * to the "final" state and returns from sub-maps, as necessary.
	 */
	public void dispose()
	{
		this.listener.stateChanged(this.statePrev.getStateName(),null,null);
		while (!this.stackMap.isEmpty())
		{
			final String map = this.stackMap.pop();
			final String state = this.stackState.pop();
			this.listener.mapChanged(map,false);
			if (state != null)
			{
				this.listener.stateChanged(state,null,null);
			}
		}
	}

	private void change(SmcState s1, SmcState s2)
	{
		if (s1.isNull())
		{
			this.listener.stateChanged(this.statePrev.getStateName(),this.fsm.getTransition(),null);
			final String map = this.stackMap.pop();
			this.stackState.pop();
			this.listener.mapChanged(map,false);
		}
		else if (!s1.getMapName().equals(s2.getMapName()))
		{
			enterSubState(s1,s2);
		}
		else
		{
			this.listener.stateChanged(s1.getStateName(),this.fsm.getTransition(),s2.getStateName());
		}
		this.statePrev = s2;
	}

	private void enterSubState(final SmcState s1, final SmcState s2)
	{
		this.listener.mapChanged(s2.getMapName(),true);

		/*
		 * Also trigger a pseudo-state-change event, to indicate
		 * the "entry" transition to the initial state within
		 * the new map we are entering
		 */
		this.listener.stateChanged(null,null,s2.getStateName());

		/*
		 * Push the name of the map we are entering, and
		 * the name of the state we are leaving.
		 */
		this.stackMap.push(s2.getMapName());
		this.stackState.push(s1==null?null:s1.getStateName());
	}

	private void enterStartState()
	{
		this.fsm.enterStartState();
		enterSubState(null,new SmcState(getState()));
	}

	/**
	 * Gets the current state of this monitor's FSM.
	 * @return the current state
	 */
	private State getState()
	{
		/*
		 * The getState method is defined in the generated FSM class, but
		 * not in the super class FSMContext, so we need to use
		 * reflection to call it.
		 */
		try
		{
			final Method method = this.fsm.getClass().getMethod("getState");
			return (State)method.invoke(this.fsm);
		}
		catch (final Throwable e)
		{
			throw new IllegalStateException(e);
		}
	}
}
