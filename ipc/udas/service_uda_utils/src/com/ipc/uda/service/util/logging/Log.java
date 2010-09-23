package com.ipc.uda.service.util.logging;

import com.ipc.util.logging.Logger;

/**
 * Handles all logging for UDAS.
 * @author mosherc
 */
public class Log
{
    // TODO revert to DMS logger when its development is finished
    // private static final Logger logger = LoggerFactory.getLogger();
    private static final Logger logger = new SubstituteLogger();

    /**
     * Gets the logger to use for logging any and all messages for UDAS.
     * @return the Logger
     */
    public static Logger logger()
    {
        return Log.logger;
    }
}
