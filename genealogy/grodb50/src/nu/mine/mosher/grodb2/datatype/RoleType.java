/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb2.datatype;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public enum RoleType
{
	other,
	named, // person had a name (with a certain spelling)
	actor,
	enumeratee, // person counted in a census
	child,
	father,
	mother,
	husband,
	wife,
	recipient,
}
