package com.ipc.uda.service.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class consists exclusively of static methods that deal with {@link Class}es.
 * @author mosherc
 *
 * @param <T> the parameter for {@link Class Class&lt;T&gt;}.
 */
public final class Classes<T>
{
    /**
     * This class is never instantiated.
     */
    Classes()
    {
        throw new IllegalStateException();
    }

    public static <T> void createInstancesByClassName(final String classList, final Class<T> type, final Collection</* TODO: why does this fail "? super T" */ T> rInstances)
        throws InstantiationException, IllegalAccessException, ExceptionInInitializerError, SecurityException, ClassNotFoundException, LinkageError, ClassCastException
    {
        final Collection<Class<? extends T>> rClasses = new ArrayList<Class<? extends T>>();
        getClassesByName(classList,type,rClasses);
        createInstances(rClasses,rInstances);
    }

    public static <T> void createInstances(final Collection<Class<? extends T>> rClasses, final Collection</* TODO: why does this fail "? super T" */ T> rInstances)
        throws InstantiationException, IllegalAccessException, ExceptionInInitializerError, SecurityException
    {
        for (final Class<? extends T> cls : rClasses)
        {
            rInstances.add(cls.newInstance());
        }
    }

    public static <T> void getClassesByName(final String classList, final Class<T> type, final Collection<Class<? extends T>> rClasses)
        throws ClassNotFoundException, ExceptionInInitializerError, LinkageError, ClassCastException
    {
        final Collection<String> rClassName = new ArrayList<String>();
        Strings.splitList(classList,rClassName);
        for (final String className : rClassName)
        {
            rClasses.add(getClassByName(className,type));
        }
    }

    /**
     * Class {@link Class#forName(String) Class&lt;T&gt;.forName} in a type-safe way.
     * @param <T> the parameter for {@link Class Class&lt;T&gt;}.
     * @param className the fully qualified name of the desired class
     * @param type the actual class (or a superclass) represented by <code>className</code>
     * @return the {@link Class} object for the class with the name in <code>className</code>
     * @throws ClassNotFoundException
     * @throws ExceptionInInitializerError
     * @throws LinkageError
     * @throws ClassCastException
     */
    public static <T> Class<? extends T> getClassByName(final String className, final Class<T> type)
        throws ClassNotFoundException, ExceptionInInitializerError, LinkageError, ClassCastException
    {
        final Class<?> cls = Class.forName(className);
        return cls.asSubclass(type);
    }

    /**
     * Same as {@link Classes#getClassByName(String,Class) getClassByName}, but any exceptions
     * are caught and wrapped in an unchecked exception.
     * @param <T> the parameter for {@link Class Class&lt;T&gt;}.
     * @param className the fully qualified name of the desired class
     * @param type the actual class (or a superclass) represented by <code>className</code>
     * @return the {@link Class} object for the class with the name in <code>className</code>
     * @throws IllegalStateException wraps any exception thrown
     */
    public static <T> Class<? extends T> getClassByNameStrict(final String className, final Class<T> type) throws IllegalStateException
    {
        try
        {
            return getClassByName(className,type);
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException(e);
        }
    }
}
