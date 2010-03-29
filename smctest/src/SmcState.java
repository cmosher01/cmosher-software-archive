import statemap.State;

public class SmcState
{
	private final boolean nul;
	private final String map;
	private final String state;

	public SmcState(final State state)
	{
		this.nul = (state == null);

		if (this.nul)
		{
			this.map = "";
			this.state = "";
		}
		else
		{
			final String[] s = state.getName().split("\\.");
			if (s == null || s.length != 2)
			{
				throw new IllegalStateException();
			}
			this.map = s[0];
			this.state = s[1];
		}
	}

	public boolean isNull()
	{
		return this.nul;
	}

	public String getMapName()
	{
		if (this.nul)
		{
			throw new IllegalStateException();
		}
		return this.map;
	}

	public String getStateName()
	{
		if (this.nul)
		{
			throw new IllegalStateException();
		}
		return this.state;
	}

	@Override
	public String toString()
	{
		if (this.nul)
		{
			return "null";
		}
		return this.map+"."+this.state;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (!(object instanceof SmcState))
		{
			return false;
		}
		final SmcState that = (SmcState)object;
		return
			this.nul==that.nul &&
			this.map.equals(that.map) &&
			this.state.equals(that.state);
	}

	@Override
	public int hashCode()
	{
		int hash = 17;
		hash *= 37;
		hash += (this.nul ? 0 : 1);
		hash *= 37;
		hash += this.map.hashCode();
		hash *= 37;
		hash += this.state.hashCode();
		return hash;
	}
}
