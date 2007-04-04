package nu.mine.mosher.test;

import java.util.Map;

public final class BeanUtil
{
    private BeanUtil()
    {
        throw new IllegalStateException();
    }

    public static Object createFromParameters(Map mapParam, Class ofType) throws ParameterParseException, InstantiationException, IllegalAccessException
    {
        Object bean = ofType.newInstance();
        parseParameters(mapParam,bean);
        return bean;
    }

    public static void parseParameters(Map mapParam, Object bean) throws ParameterParseException
    {
        try
        {
            BeanBuilder b = new BeanBuilder(bean);
            b.parseParameters(mapParam);
//            tryParseParameters(mapParam, bean);
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
}
