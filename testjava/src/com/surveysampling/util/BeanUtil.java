package com.surveysampling.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.servlet.ServletRequest;

public final class BeanUtil
{
    private BeanUtil()
    {
        throw new IllegalStateException();
    }

    public static void parseParameters(ServletRequest request, Object bean) throws ParameterParseException
    {
        try
        {
            tryParseParameters(request,bean);
        }
        catch (ParameterParseException e)
        {
            throw e;
        }
        catch (Throwable e)
        {
            throw new ParameterParseException(e);
        }
    }
    
    private static void tryParseParameters(ServletRequest request, Object bean) throws IntrospectionException, IllegalArgumentException, InvocationTargetException, ParameterParseException, InstantiationException, IllegalAccessException
    {
        for (Iterator params = new EnumIter(request.getParameterNames()); params.hasNext();)
        {
            String param = (String)params.next();

            BeanInfo info = Introspector.getBeanInfo(bean.getClass());

            PropertyDescriptor pd = getPropDesc(info,param);
            if (pd == null)
            {
                continue;
            }

            Method method = pd.getWriteMethod();
            if (method == null)
            {
                continue;
            }

            Class type = pd.getPropertyType();
            Class propertyEditorClass = pd.getPropertyEditorClass();
            if (type.isArray())
            {
                if (request == null)
                {
                    throw new ParameterParseException();
                }
                Class t = type.getComponentType();
                String[] values = request.getParameterValues(param);
                if (values == null)
                {
                    return;
                }
                if (t.equals(String.class))
                {
                    method.invoke(bean, new Object[] { values });
                }
                else
                {
                    createTypedArray(bean, method, values, t, propertyEditorClass);
                }
            }
            else
            {
                String value = request.getParameter(param);
                if (value == null || (param != null && value.equals("")))
                {
                    return;
                }
                Object val = convert(value, type, propertyEditorClass);
                if (val != null)
                {
                    method.invoke(bean, new Object[] { val });
                }
            }
        }
    }

    private static PropertyDescriptor getPropDesc(BeanInfo info, String param)
    {
        PropertyDescriptor pd[] = info.getPropertyDescriptors();
        for (int i = 0; i < pd.length; ++i)
        {
            if (pd[i].getName().equals(param))
            {
                return pd[i];
            }
        }
        return null;
    }

    public static void createTypedArray(
        Object bean,
        Method method,
        String[] values,
        Class t,
        Class propertyEditorClass) throws InstantiationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, ParameterParseException
    {
        if (propertyEditorClass != null)
        {
            Object[] tmpval = new Integer[values.length];
            for (int i = 0; i < values.length; i++)
            {
                tmpval[i] = getValueFromBeanInfoPropertyEditor(values[i], propertyEditorClass);
            }
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Integer.class))
        {
            Integer[] tmpval = new Integer[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Integer(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Byte.class))
        {
            Byte[] tmpval = new Byte[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Byte(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Boolean.class))
        {
            Boolean[] tmpval = new Boolean[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Boolean(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Short.class))
        {
            Short[] tmpval = new Short[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Short(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Long.class))
        {
            Long[] tmpval = new Long[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Long(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Double.class))
        {
            Double[] tmpval = new Double[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Double(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Float.class))
        {
            Float[] tmpval = new Float[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Float(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Character.class))
        {
            Character[] tmpval = new Character[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = new Character(values[i].charAt(0));
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(int.class))
        {
            int[] tmpval = new int[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = Integer.parseInt(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(byte.class))
        {
            byte[] tmpval = new byte[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = Byte.parseByte(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(boolean.class))
        {
            boolean[] tmpval = new boolean[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = (Boolean.valueOf(values[i])).booleanValue();
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(short.class))
        {
            short[] tmpval = new short[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = Short.parseShort(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(long.class))
        {
            long[] tmpval = new long[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = Long.parseLong(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(double.class))
        {
            double[] tmpval = new double[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = Double.valueOf(values[i]).doubleValue();
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(float.class))
        {
            float[] tmpval = new float[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = Float.valueOf(values[i]).floatValue();
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(char.class))
        {
            char[] tmpval = new char[values.length];
            for (int i = 0; i < values.length; i++)
                tmpval[i] = values[i].charAt(0);
            method.invoke(bean, new Object[] { tmpval });
        }
        else
        {
            Object[] tmpval = new Integer[values.length];
            for (int i = 0; i < values.length; i++)
            {
                tmpval[i] = getValueFromPropertyEditorManager(t, values[i]);
            }
            method.invoke(bean, new Object[] { tmpval });
        }
    }

    private static Object convert(String s, Class t, Class propertyEditorClass) throws ParameterParseException, InstantiationException, IllegalAccessException
    {
        if (s == null)
        {
            if (t.equals(Boolean.class) || t.equals(Boolean.TYPE))
                s = "false";
            else
                return null;
        }
        if (propertyEditorClass != null)
        {
            return getValueFromBeanInfoPropertyEditor(s, propertyEditorClass);
        }
        else if (t.equals(Boolean.class) || t.equals(Boolean.TYPE))
        {
            if (s.equalsIgnoreCase("on") || s.equalsIgnoreCase("true"))
                s = "true";
            else
                s = "false";
            return new Boolean(s);
        }
        else if (t.equals(Byte.class) || t.equals(Byte.TYPE))
        {
            return new Byte(s);
        }
        else if (t.equals(Character.class) || t.equals(Character.TYPE))
        {
            return s.length() > 0 ? new Character(s.charAt(0)) : null;
        }
        else if (t.equals(Short.class) || t.equals(Short.TYPE))
        {
            return new Short(s);
        }
        else if (t.equals(Integer.class) || t.equals(Integer.TYPE))
        {
            return new Integer(s);
        }
        else if (t.equals(Float.class) || t.equals(Float.TYPE))
        {
            return new Float(s);
        }
        else if (t.equals(Long.class) || t.equals(Long.TYPE))
        {
            return new Long(s);
        }
        else if (t.equals(Double.class) || t.equals(Double.TYPE))
        {
            return new Double(s);
        }
        else if (t.equals(String.class))
        {
            return s;
        }
        else if (t.equals(java.io.File.class))
        {
            return new java.io.File(s);
        }
        else if (t.getName().equals("java.lang.Object"))
        {
            return new Object[] { s };
        }
        else
        {
            return getValueFromPropertyEditorManager(t, s);
        }
    }

    private static Object getValueFromBeanInfoPropertyEditor(
        String attrValue,
        Class propertyEditorClass) throws InstantiationException, IllegalAccessException
    {
        PropertyEditor pe = (PropertyEditor)propertyEditorClass.newInstance();
        pe.setAsText(attrValue);
        return pe.getValue();
    }

    private static Object getValueFromPropertyEditorManager(Class attrClass, String attrValue) throws ParameterParseException
    {
        PropertyEditor propEditor = PropertyEditorManager.findEditor(attrClass);
        if (propEditor != null)
        {
            propEditor.setAsText(attrValue);
            return propEditor.getValue();
        }
        else
        {
            throw new ParameterParseException();
        }
    }
}
