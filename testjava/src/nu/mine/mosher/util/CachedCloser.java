package com.surveysampling.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UniversalCloser3
{
    /**
     * Calls the given object's "close()" method, if it has one.
     * Any exceptions are ignored.
     * 
     * For example:
     * <pre>
     * OutputStream os = null;
     * try
     * {
     *     os = new FileOutputStream(new File("test.txt"));
     *     os.write(65);
     * }
     * finally
     * {
     *     UniversalCloser.close(os);
     * }
     * </pre>
     * 
     * Performance considerations: on an Intel-based Windows PC
     * with Java 1.4.2, where calling close on an object took
     * about 4 nanoseconds, calling UniversalCloser2.close on
     * that object took about 384 nanoseconds.
     * 
     * @param obj the Object whose close() method is to be called.
     */
    private static final Map /*<Class,Method>*/ mClasses = new HashMap();
    public static void close(Object obj)
    {
        try
        {
            Class cl = obj.getClass();
            Method methodClose = (Method)mClasses.get(cl);
            if (methodClose == null)
            {
                methodClose = cl.getMethod("close",null);
                mClasses.put(cl,methodClose);
            }
            methodClose.invoke(obj,null);
        }
        catch (Throwable ignore)
        {
            ignore.printStackTrace();
        }
    }
}
