/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Defines the syntax of the command line (arguments and options).
 * 
 * @author Chris Mosher
 */
public class SyntaxDefinition
{
    private Set msetOption = new TreeSet();
    private List mrArgument = new ArrayList();
    private boolean mLastArgumentRepeats;

    public SyntaxDefinition(Reader xml)
    {
        
    }

    public OptionDefinition getOptionDefinition(String option)
    {
        return null;
    }
}
