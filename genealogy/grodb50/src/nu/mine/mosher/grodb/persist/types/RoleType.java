/*
 * Created on Nov 17, 2005
 */
package nu.mine.mosher.grodb.persist.types;

import com.sleepycat.persist.model.Persistent;

/**
 * TODO
 *
 * @author Chris Mosher
 */
@Persistent
public enum RoleType
{
	other,
	named, // person had a name (with a certain spelling)
	actor,
	enumeratee, // person counted in a census
	child,
	father,
	mother,
	spouse,
	recipient,
	subjectOfPhotograph,
}
