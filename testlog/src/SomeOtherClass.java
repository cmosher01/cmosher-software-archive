import java.util.logging.Logger;

import com.surveysampling.util.logger.LoggerUtil;

public class SomeOtherClass
{
    public static void someStaticMethod()
    {
        Logger log = LoggerUtil.getLogger(SomeOtherClass.class);

        log.finest("another really fine message");
        log.finer("another pretty fine message");
        log.fine("another fine message");
        log.config("another config message");
        log.info("another informational message");
        log.warning("another warning message");
        log.severe("another severe error message");
    }
}
