package com.surveysampling.someapp;

import java.util.logging.Logger;

public class SomeClassSimple
{
    private Logger log = Logger.global;

    public void someMethod()
    {
        log.warning("Something strange happened.");
        log.severe("Something really bad happened.");
    }
}
