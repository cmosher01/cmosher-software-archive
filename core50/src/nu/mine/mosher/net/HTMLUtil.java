package nu.mine.mosher.net;

/**
 * @author Chris Mosher
 * Created: Feb 15, 2004
 */
public final class HTMLUtil
{
	private HTMLUtil()
	{
		throw new UnsupportedOperationException();
	}

	public String fixHTML(String s)
	{
		return s
			.replaceAll("&","&amp;")
			.replaceAll("<","&lt;")
			.replaceAll(">","&gt;")
			.replaceAll("\"","&quot;")
			.replaceAll("\'","&apos;");
	}
}
