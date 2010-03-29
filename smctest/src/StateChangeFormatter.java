import java.io.PrintWriter;

public class StateChangeFormatter implements StateChangeListener
{
	private final PrintWriter printer;

	private int level;



	public StateChangeFormatter(final PrintWriter printer)
	{
		this.printer = printer;
	}

	@Override
	public void mapChanged(final String map, final boolean entered)
	{
		if (entered)
		{
			indent(); this.printer.println(map);
			indent(); this.printer.println("{");
			++this.level;
		}
		else
		{
			--this.level;
			indent(); this.printer.println("}");
		}
		sanityCheckLevel();
	}

	private void sanityCheckLevel()
	{
		if (this.level < 0)
		{
			this.level = 0;
		}
		else if (this.level > 100)
		{
			this.level = 0;
		}
	}

	@Override
	public void stateChanged(final String statePrev, final String event, final String stateNext)
	{
		stateChanged(statePrev,event,stateNext,false);
	}

	@Override
	public void undefinedChange(final String statePrev, final String event, final String stateNext)
	{
		stateChanged(statePrev,event,stateNext,true);
	}

	private void stateChanged(final String statePrev, final String event, final String stateNext, final boolean unexpected)
	{
		indent();

		if (unexpected)
		{
			this.printer.print("UNEXPECTED EVENT ");
		}

		if (statePrev == null)
		{
			this.printer.print("()");
		}
		else
		{
			this.printer.print("["+statePrev+"]");
		}

		if (event != null)
		{
			this.printer.print("-->"+event);
		}

		if (stateNext == null)
		{
			this.printer.print("-->(.)");
		}
		else
		{
			this.printer.print("-->["+stateNext+"]");
		}

		this.printer.println();
	}

	private void indent()
	{
		for (int i = 0; i < this.level; ++i)
		{
			this.printer.print("    ");
		}
	}
}
