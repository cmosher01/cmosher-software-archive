/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

public class Assertion
{
	private final SourceID idSource;

	/**
	 * true means affirmed, false means denied
	 */
	private final boolean affirmed;
	private final String rationale;

	// either
	private final RoleID idRole;
	// or
	private final PersonaID idPersonaSameAsA;
	private final PersonaID idPersonaSameAsB;
	private final Surety suretySameAs;
	// end

	/**
	 * @param source
	 * @param affirmed
	 * @param rationale
	 * @param role
	 * @param sameA
	 * @param sameB
	 * @param suretySame
	 */
	public Assertion(
		final SourceID source,
		final boolean affirmed,
		final String rationale,
		final RoleID role,
		final PersonaID sameA,
		final PersonaID sameB,
		final Surety suretySame)
	{
		this.idSource = source;
		this.affirmed = affirmed;
		this.rationale = rationale;
		this.idRole = role;
		this.idPersonaSameAsA = sameA;
		this.idPersonaSameAsB = sameB;
		this.suretySameAs = suretySame;
	}

	/**
	 * @return Returns the idSource.
	 */
	public SourceID getSource()
	{
		return this.idSource;
	}

	/**
	 * @return Returns the affirmed.
	 */
	public boolean isAffirmed()
	{
		return this.affirmed;
	}

	/**
	 * @return Returns the rationale.
	 */
	public String getRationale()
	{
		return this.rationale;
	}

	/**
	 * @return Returns the idRole.
	 */
	public RoleID getRole()
	{
		return this.idRole;
	}

	/**
	 * @return Returns the idPersonaSameAsA.
	 */
	public PersonaID getPersonaSameAsA()
	{
		return this.idPersonaSameAsA;
	}

	/**
	 * @return Returns the idPersonaSameAsB.
	 */
	public PersonaID getPersonaSameAsB()
	{
		return this.idPersonaSameAsB;
	}

	/**
	 * @return Returns the suretySameAs.
	 */
	public Surety getSuretySameAs()
	{
		return this.suretySameAs;
	}
}
