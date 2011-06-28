/*
 * Created on Aug 10, 2004
 */


/**
 * @author Chris Mosher
 */
public class FourCC
{
    private final String fourCC;

    /**
     * @param fourCC
     */
    public FourCC(String fourCC)
    {
        this.fourCC = fourCC;
    }

    /**
     * @return Returns the fourCC.
     */
    public String getFourCC()
    {
        return fourCC;
    }

    /**
     * @return
     */
    public boolean isList()
    {
        return fourCC.equals("LIST") || fourCC.equals("RIFF");
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return fourCC;
    }
}
