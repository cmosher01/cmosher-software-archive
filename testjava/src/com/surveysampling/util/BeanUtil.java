package com.surveysampling.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
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
                    Object tmpval = null;
                    createTypedArray(param, bean, method, values, t, propertyEditorClass);
                }
            }
            else
            {
                String value = request.getParameter(param);
                if (value == null || (param != null && value.equals("")))
                {
                    return;
                }
                Object oval = convert(param, value, type, propertyEditorClass);
                if (oval != null)
                {
                    method.invoke(bean, new Object[] { oval });
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
}
