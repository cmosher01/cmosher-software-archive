/*
 * Created on July 29, 2005
 */
package nu.mine.mosher.text;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class SelectTextBlock
{
	/**
	 * @param text
	 * @param pos
	 * @return current block of text around <code>pos</code>, delimited by double new-lines.
	 */
	public static String selectTextBlock(final String text, final int pos)
	{
		if (pos < 0 || text.length() < pos)
		{
			return "";
		}

		if (0 < pos && pos < text.length())
		{
			if (text.charAt(pos-1) == '\n' && text.charAt(pos) == '\n')
			{
				return "";
			}
		}

		int iBegin = pos;
		while (!atBegin(text,iBegin))
		{
			--iBegin;
		}

		int iEnd = pos;
		while (!atEnd(text,iEnd))
		{
			++iEnd;
		}

		return text.substring(iBegin,iEnd);
	}

	private static boolean atBegin(final String text, final int at)
	{
		if (at < 1 || text.length()+1 <= at)
		{
			return true;
		}

		if (at == 1 || at == text.length())
		{
			return false;
		}

		return text.charAt(at-2) == '\n' && text.charAt(at-1) == '\n';
	}

	private static boolean atEnd(final String text, final int at)
	{
		if (at < 0 || text.length() <= at)
		{
			return true;
		}

		if (at == 0 || at == text.length()-1)
		{
			return false;
		}

		return text.charAt(at) == '\n' && text.charAt(at+1) == '\n';
	}
}
