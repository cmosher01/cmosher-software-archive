package com.surveysampling.someapp;

import java.util.logging.Logger;

import com.surveysampling.util.logger.LoggerUtil;

public class SomeClass
{
    private Logger mLog = LoggerUtil.getLogger(this);

    public void someMethod()
    {
        mLog.finest("a really fine message");
        mLog.finer("a pretty fine message");
        mLog.fine("a fine message");
        mLog.config("a config message");
        mLog.info("an informational message");
        mLog.warning("a warning message");
        mLog.severe("a severe error message");
    }
}
