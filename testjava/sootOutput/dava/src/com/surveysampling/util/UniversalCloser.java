package com.surveysampling.util;

import java.lang.reflect.*;

public class UniversalCloser
{

    public static void close(java.lang.Object obj)
    {
        label_0:
        {
            try
            {
                obj.getClass().getMethod("close", null).invoke(obj, null);
            }
            catch (Throwable ignore)
            {
                break label_0;
            }
        }

        return;
    }

    public UniversalCloser()
    {
        super();

        return;
    }
}
