package nu.mine.mosher.grodb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Mosher
 * Created: Feb 8, 2004
 */
public class Hypothesis
{
	private String rationale;
	private Source source;

	private List<Relation<Hypothesis,Hypothesis,HypothesisRelType>> rRelUp = new ArrayList<Relation<Hypothesis,Hypothesis,HypothesisRelType>>();
	private List<Relation<Hypothesis,Hypothesis,HypothesisRelType>> rRelDown = new ArrayList<Relation<Hypothesis,Hypothesis,HypothesisRelType>>();

	public Hypothesis()
	{
	}
}
