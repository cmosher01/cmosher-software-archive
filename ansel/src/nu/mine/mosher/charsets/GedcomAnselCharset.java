package nu.mine.mosher.charsets;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Chris Mosher
 */
public class GedcomAnselCharset extends Charset
{
	public static final String name = "x-gedcom-ansel";

    public GedcomAnselCharset()
    {
        this(name, null);
    }

    public GedcomAnselCharset(String canonicalName, String[] aliases)
    {
        super(canonicalName,aliases);
    }

    public boolean contains(Charset cs)
    {
        return cs instanceof GedcomAnselCharset;
    }

    public CharsetDecoder newDecoder()
    {
        return new CharsetDecoder(this,1,1)
        {
        	private List listCombining = new ArrayList();
			boolean combining = false;
            protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out)
            {
				Map map = GedcomAnselTable.getDecoder();
				if (!combining)
				{
					while (out.position() < out.limit() && listCombining.size() > 0)
					{
						Integer cint = (Integer)listCombining.remove(0);
						out.put((char)cint.intValue());
					}
				}
                while (in.hasRemaining() && out.position() < out.limit())
                {
                    Byte b = new Byte(in.get());
					char c;
					int bint = b.intValue();
					bint &= 0xff;

					combining = false;
					if (map.containsKey(b))
					{
						c = ((Character)map.get(b)).charValue();
						if (bint >= 0xe0)
						{
							listCombining.add(new Integer(c));
							combining = true;
						}
					}
					else
					{
						c = (char)b.byteValue();
						c &= 0xff;
					}
					if (!combining)
					{
						out.put(c);
						while (out.position() < out.limit() && listCombining.size() > 0)
						{
							Integer cint = (Integer)listCombining.remove(0);
                            out.put((char)cint.intValue());
                        }
					}
                }
				return in.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
            }
        };
    }

    public CharsetEncoder newEncoder()
    {
        return new CharsetEncoder(this,1,1)
        {
        	// TODO fix ansel encoding (needs to reverse combining chars)
            protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out)
            {
				Map map = GedcomAnselTable.getEncoder();
				while (in.hasRemaining())
				{
					Character c = new Character(in.get());
					byte b;

					if (map.containsKey(c))
					{
						b = ((Byte)map.get(c)).byteValue();
					}
					else
					{
						char cn = c.charValue();
						if (cn >= 0x100)
							return CoderResult.unmappableForLength(1);
						b = (byte)cn;
					}

					out.put(b);
				}
				return in.hasRemaining() ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
            }
        };
    }
}
