package nu.mine.mosher.logging;

import junit.framework.TestCase;

public class LoggingInitializerTest extends TestCase
{
    public LoggingInitializerTest(String name)
    {
        super(name);
    }

    public void testInit()
    {
    	LoggingInitializer.init();
    }
}
