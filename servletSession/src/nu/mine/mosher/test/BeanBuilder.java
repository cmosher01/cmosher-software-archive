package nu.mine.mosher.test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

public final class BeanBuilder
{
    private final Object bean;

    public BeanBuilder(Object bean)
        throws
            IntrospectionException,
            IllegalArgumentException,
            InvocationTargetException,
            ParameterParseException,
            InstantiationException,
            IllegalAccessException
    {
        this.bean = bean;
    }

    public void parseParameters(Map mapParam)
        throws
            IntrospectionException,
            IllegalArgumentException,
            InvocationTargetException,
            ParameterParseException,
            InstantiationException,
            IllegalAccessException
    {
        /*
         * Go through each param and set appropriate
         * bean property for that param.
         */
        for (Iterator i = mapParam.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry param = (Map.Entry)i.next();
            parseParam((String)param.getKey(), (String[])param.getValue());
        }
    }

    private void parseParam(String param, String[] rValue)
        throws
            IntrospectionException,
            IllegalArgumentException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException,
            ParameterParseException
    {
        PropertyDescriptor pd = getPropDesc(param);
        if (pd == null) // if bean doesn't have this param, skip it
        {
            return;
        }

        Method method = pd.getWriteMethod();
        if (method == null) // if cannot write this param to bean, skip it
        {
            return;
        }

        Class type = pd.getPropertyType();
        if (type.isArray())
        {
            type = type.getComponentType();
            createTypedArray(method, rValue, type, pd.getPropertyEditorClass());
        }
        else
        {
            String value = rValue[0];
            createTypedObject(method, value, type, pd.getPropertyEditorClass());
        }
    }

    private PropertyDescriptor getPropDesc(String param) throws IntrospectionException
    {
        BeanInfo info = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor rpd[] = info.getPropertyDescriptors();
        for (int i = 0; i < rpd.length; ++i)
        {
            if (rpd[i].getName().equals(param))
            {
                return rpd[i];
            }
        }
        return null;
    }

    private void createTypedObject(Method method, String value, Class t, Class propertyEditorClass)
        throws ParameterParseException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Object val = convert(value, t, propertyEditorClass);
        if (val != null)
        {
            method.invoke(bean, new Object[] { val });
        }
    }

    private void createTypedArray(Method method, String[] values, Class t, Class propertyEditorClass)
        throws
            InstantiationException,
            IllegalArgumentException,
            IllegalAccessException,
            InvocationTargetException,
            ParameterParseException
    {
        if (propertyEditorClass != null)
        {
            Object[] tmpval = new Integer[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = getValueFromBeanInfoPropertyEditor(values[i], propertyEditorClass);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(String.class))
        {
            method.invoke(bean, new Object[] { values });
        }
        else if (t.equals(Integer.class))
        {
            Integer[] tmpval = new Integer[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Integer(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Boolean.class))
        {
            Boolean[] tmpval = new Boolean[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Boolean(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Byte.class))
        {
            Byte[] tmpval = new Byte[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Byte(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Short.class))
        {
            Short[] tmpval = new Short[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Short(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Long.class))
        {
            Long[] tmpval = new Long[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Long(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Double.class))
        {
            Double[] tmpval = new Double[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Double(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Float.class))
        {
            Float[] tmpval = new Float[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Float(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(Character.class))
        {
            Character[] tmpval = new Character[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = new Character(values[i].charAt(0));
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(int.class))
        {
            int[] tmpval = new int[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = Integer.parseInt(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(byte.class))
        {
            byte[] tmpval = new byte[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = Byte.parseByte(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(boolean.class))
        {
            boolean[] tmpval = new boolean[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = (Boolean.valueOf(values[i])).booleanValue();
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(short.class))
        {
            short[] tmpval = new short[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = Short.parseShort(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(long.class))
        {
            long[] tmpval = new long[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = Long.parseLong(values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(double.class))
        {
            double[] tmpval = new double[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = Double.valueOf(values[i]).doubleValue();
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(float.class))
        {
            float[] tmpval = new float[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = Float.valueOf(values[i]).floatValue();
            method.invoke(bean, new Object[] { tmpval });
        }
        else if (t.equals(char.class))
        {
            char[] tmpval = new char[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = values[i].charAt(0);
            method.invoke(bean, new Object[] { tmpval });
        }
        else
        {
            Object[] tmpval = new Integer[values.length];
            for (int i = 0; i < values.length; ++i)
                tmpval[i] = getValueFromPropertyEditorManager(t, values[i]);
            method.invoke(bean, new Object[] { tmpval });
        }
    }

    private static Object convert(String s, Class t, Class propertyEditorClass)
        throws ParameterParseException, InstantiationException, IllegalAccessException
    {
        if (s == null)
        {
            if (t.equals(Boolean.class) || t.equals(Boolean.TYPE))
            {
                s = "false";
            }
            else
            {
                return null;
            }
        }
        if (propertyEditorClass != null)
        {
            return getValueFromBeanInfoPropertyEditor(s, propertyEditorClass);
        }
        return cvt(s,t);
    }

    private static Object cvt(String s, Class t) throws ParameterParseException
    {
        if (t.equals(String.class))
        {
            return s;
        }
        if (t.equals(Integer.class) || t.equals(Integer.TYPE))
        {
            return new Integer(s);
        }
        if (t.equals(Boolean.class) || t.equals(Boolean.TYPE))
        {
            if (s.equalsIgnoreCase("on") || s.equalsIgnoreCase("true"))
                s = "true";
            else
                s = "false";
            return new Boolean(s);
        }
        if (t.equals(Byte.class) || t.equals(Byte.TYPE))
        {
            return new Byte(s);
        }
        if (t.equals(Character.class) || t.equals(Character.TYPE))
        {
            return s.length() > 0 ? new Character(s.charAt(0)) : null;
        }
        if (t.equals(Short.class) || t.equals(Short.TYPE))
        {
            return new Short(s);
        }
        if (t.equals(Float.class) || t.equals(Float.TYPE))
        {
            return new Float(s);
        }
        if (t.equals(Long.class) || t.equals(Long.TYPE))
        {
            return new Long(s);
        }
        if (t.equals(Double.class) || t.equals(Double.TYPE))
        {
            return new Double(s);
        }
        if (t.equals(File.class))
        {
            return new File(s);
        }
        if (t.getName().equals("java.lang.Object"))
        {
            return new Object[] { s };
        }

        return getValueFromPropertyEditorManager(t, s);
    }

    private static Object getValueFromBeanInfoPropertyEditor(String attrValue, Class propertyEditorClass)
        throws InstantiationException, IllegalAccessException
    {
        PropertyEditor propEditor = (PropertyEditor)propertyEditorClass.newInstance();
        propEditor.setAsText(attrValue);
        return propEditor.getValue();
    }

    private static Object getValueFromPropertyEditorManager(Class attrClass, String attrValue)
        throws ParameterParseException
    {
        PropertyEditor propEditor = PropertyEditorManager.findEditor(attrClass);
        if (propEditor == null)
        {
            throw new ParameterParseException();
        }
        propEditor.setAsText(attrValue);
        return propEditor.getValue();
    }
}
