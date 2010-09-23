/**
 * 
 */
package com.ipc.uda.types.util;



import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ipc.uda.service.util.logging.Log;



/**
 * @author mordarsd
 * 
 */
public final class Converter<A, B>
{

    private static final String GET_PREFIX = "get";
    private static final String SET_PREFIX = "set";

    private final Set<MethodPair> methodPairs = new HashSet<MethodPair>(16,1.0f);
    private final Set<String> skippedPropertySet = new HashSet<String>(8,1.0f);
    private final Map<String, Method> enumMap = new HashMap<String, Method>();
    private final ConversionHandler<A, B> conversionHandler;
    private final Map<Class<? extends Enum<?>>, Class<? extends Enum<?>>> enumConversionMap;

    /**
     * 
     * @param src
     * @param dest
     */
    public Converter(Class<A> src, Class<B> dest)
    {
        this(src,dest,null,null,null);
    }

    /**
     * 
     * @param src
     * @param dest
     * @param conversionHandler
     */
    public Converter(Class<A> src, Class<B> dest, ConversionHandler<A, B> conversionHandler)
    {
        this(src,dest,Collections.<Class<?>> emptySet(),conversionHandler,null);
    }

    /**
     * 
     * @param src
     * @param dest
     * @param ignored
     */
    public Converter(Class<A> src, Class<B> dest, Set<Class<?>> ignored)
    {
        this(src,dest,ignored,null,null);
    }

    /**
     * 
     * @param srcClass
     * @param destClass
     * @param ignored
     * @param conversionHandler
     */
    public Converter(Class<A> srcClass, Class<B> destClass, Set<Class<?>> ignored, ConversionHandler<A, B> conversionHandler,
            Map<Class<? extends Enum<?>>, Class<? extends Enum<?>>> enumConversionMap)
    {
        if ((srcClass == null) || (destClass == null))
        {
            throw new NullPointerException("Unable map source type into destination type - "
                    + "neither source class nor destination class definition can be null");
        }

        this.enumConversionMap = enumConversionMap;
        this.conversionHandler = conversionHandler;

        Method[] declaredMethods = srcClass.getDeclaredMethods();
        for (Method m : declaredMethods)
        {
            if (m.getName().startsWith(GET_PREFIX))
            {
                if (ignored != null && !ignored.contains(m.getReturnType()))
                {
                    // Check for Enums
                    // In this case, the accessors return type will NOT match the modifiers
                    // paramter type
                    if (m.getReturnType().isEnum())
                    {
                        enumMap.put(getModifierNameFromAccessorName(m.getName()),m);
                    }
                    else
                    {
                        // find the matching setter in the dest
                        String modifierName = getModifierNameFromAccessorName(m.getName());

                        try
                        {
                            Method sm = destClass.getMethod(modifierName,m.getReturnType());
                            this.methodPairs.add(new MethodPair(m,sm));

                        }
                        catch (Exception e)
                        {
                            // TODO: possibly log this
                            skippedPropertySet.add(m.getName().substring(GET_PREFIX.length()));
                        }
                    }
                }
            }
        }
    }

    /**
     * 
     * @param src
     * @param target
     */
    @SuppressWarnings("unchecked")
    public void convert(A src, B target)
    {
        for (MethodPair pair : this.methodPairs)
        {
            try
            {
                Object res = pair.srcAccessor.invoke(src);
                System.out.println(pair.srcAccessor.getName() + " -> " + pair.destModifier.getName() + " : " + res);
                pair.destModifier.invoke(target,res);

            }
            catch (Exception e)
            {
                Log.logger().info("error converting DataServices entity",e);
            }
        }

        // now, perform any custom conversion
        if (this.conversionHandler != null)
        {
            for (String prop : skippedPropertySet)
            {
                conversionHandler.customConvert(src,target,prop);
            }

            // now, handle enums
            for (Map.Entry<String, Method> entry : this.enumMap.entrySet())
            {
                Method method = null;
                Enum<?> res = null;
                Class<? extends Enum<?>> c = null;
                try
                {
                    c = this.enumConversionMap.get(entry.getValue().getReturnType());
                    if (c != null)
                    {
                        res = (Enum<?>)entry.getValue().invoke(src);
                        if (res != null)
                        {
                            // get the mathching method on the target, and invoke it!
                            method = target.getClass().getMethod(entry.getKey(), c);
                            if (method != null)
                            {
                                method.invoke(target, Enum.valueOf(c.asSubclass(Enum.class), res.toString()));
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    // try invoking: fromValue(String)
                    if (method != null)
                    {
                        try {
                            Method fromValue = c.asSubclass(Enum.class).getMethod("fromValue", String.class);
                            if (fromValue != null)
                            {
                                Object eobj = fromValue.invoke(null, res.toString());
                                
                                method.invoke(target, eobj);
                            }
                            
                        } 
                        catch (Exception ex)
                        {
                            //ex.printStackTrace();
                            Log.logger().info("Unable to map Enum: " + c);
                        }
                    }
                    
                }

            }
        }
    }

    /**
     * 
     * @param accessor
     * @return
     */
    private String getModifierNameFromAccessorName(String accessor)
    {
        return SET_PREFIX + accessor.substring(GET_PREFIX.length());
    }

    /**
     * 
     * @author mordarsd
     * 
     */
    private static class MethodPair
    {
        public final Method srcAccessor;
        public final Method destModifier;

        public MethodPair(Method srcAccessor, Method destModifier)
        {
            this.srcAccessor = srcAccessor;
            this.destModifier = destModifier;
        }

        @Override
        public String toString()
        {
            return this.srcAccessor.getName() + "|" + this.destModifier.getName();
        }

    }

}
