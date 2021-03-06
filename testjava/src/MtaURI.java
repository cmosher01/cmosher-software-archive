import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

//import com.surveysampling.bulkemailer.util.HttpUtil;

/**
 * A URI for a Mail Transport Authority.
 * Objects of this class are immutable.
 * 
 * @author Chris Mosher
 */
public class MtaURI
{
	private final URI mURI;

	private final int mRate;
	private final int mTimeout;



    /**
     * Parses the given URI. The URI is of the
     * form "protocol://host:port?rate=n&timeout=n",
     * where port, rate, and timeout are optional. For
     * example "smtp://mail.surveysampling.com?rate=25000"
     * or "smtp://mail.surveysampling.com:2525?rate=30000&timeout=10000".
     * Port defaults to 25 (smtp) (regardless of the scheme), and
     * timeout defaults to 120000 ms (2 mins.).
     * @param sMTA
     * @throws URISyntaxException
     */
	public MtaURI(String sMTA) throws URISyntaxException
	{
        URI uri = new URI(sMTA);
        if (uri.getPort() < 0)
        {
            // default to port 25 (smtp)
            uri = new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(),25,uri.getPath(),uri.getQuery(),uri.getFragment());
        }
        if (!(0 < uri.getPort() && uri.getPort() < 0x10000))
        {
            throw new URISyntaxException("invalid port "+uri.getPort(),"port must be: 0 < port < 65536");
        }
        mURI = uri;



        // pre-parse the uri to pull out the rate and timeout
        // values from the query string
        Map mapParamToValue = new HashMap();
        if (uri.getQuery() != null)
        {
//            HttpUtil.parseQueryStringSimple(uri.getQuery(),mapParamToValue);
        }

        int nRate = getParamInt(mapParamToValue,"rate",sMTA);
        if (nRate < 0)
        {
            nRate = 0;
        }
        mRate = nRate;

        int nTimeout = getParamInt(mapParamToValue,"timeout",sMTA);
        if (nTimeout < 0)
        {
            nTimeout = 120000;
        }
        mTimeout = nTimeout;
	}

    private int getParamInt(Map mapParamToValue, String param, String sMTA) throws URISyntaxException
    {
        String s = (String)mapParamToValue.get(param);
        if (s == null || s.length() == 0)
        {
            return -1;
        }

        try
        {
            int r = Integer.parseInt(s);
            if (r < 0)
            {
                throw new NumberFormatException("parameter "+param+" cannot be negative");
            }
            return r;
        }
        catch (NumberFormatException e)
        {
            URISyntaxException ex = new URISyntaxException(s,"Invalid "+param+" specified in MTA "+sMTA);
            ex.initCause(e);
            throw ex;
        }
    }

    public URI getURI()
    {
        return mURI;
    }

	public int getRate()
	{
		return mRate;
	}

	public int getTimeout()
	{
		return mTimeout;
	}

	/**
	 * Returns the URI as a string
	 */
	public String toString()
	{
        return mURI.toASCIIString();
	}

	/**
	 * Checks if two MtaURIs are equal.
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof MtaURI))
			return false;

		MtaURI that = (MtaURI)obj;
		return this.mURI.equals(that.mURI);
	}

    public int hashCode()
    {
        return mURI.hashCode();
    }
}
