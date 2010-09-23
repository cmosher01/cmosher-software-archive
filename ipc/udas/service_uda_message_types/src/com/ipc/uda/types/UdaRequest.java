package com.ipc.uda.types;



import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import com.ipc.uda.service.context.ExecutableWithContext;



/**
 * @author mordarsd
 * 
 */
public class UdaRequest extends UdaRequestType
{
    /**
     * Returns the current Executable (Command|Query) that is contained within the UdaRequestType
     * 
     * @return the {@link ExecutableWithContext}
     */
    public ExecutableWithContext getExecutable()
    {
        try
        {
            return tryGetExecutable();
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException(e);
        }
    }

    private ExecutableWithContext tryGetExecutable() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        // Look at all of our protected fields
        for (final Field field : UdaRequestType.class.getDeclaredFields())
        {
            final Object obj = field.get(this);

            if (obj != null)
            {
                final Collection<Method> methods = new ArrayList<Method>();
                getAccessorMethods(obj,methods);
                for (final Method method : methods)
                {
                    final Object res = method.invoke(obj);
                    if ((res != null) && (res instanceof ExecutableWithContext))
                    {
                        return (ExecutableWithContext)res;
                    }
                }
            }
        }

        throw new IllegalStateException("Cannot find executable");
    }

    private static void getAccessorMethods(final Object obj, final Collection<Method> methods)
    {
        for (final Method method : obj.getClass().getDeclaredMethods())
        {
            if (method.getName().startsWith("get"))
            {
                methods.add(method);
            }
        }
    }
}
