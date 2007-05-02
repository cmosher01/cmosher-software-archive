/*
 * Created on April 13, 2005
 */
package com.surveysampling.util.key.impl;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Implementation of <code>DatalessKeyAccessFactory</code>.
 * 
 * @author Chris Mosher
 */
public final class DatalessKeyAccessFactoryImpl
{
    private DatalessKeyAccessFactoryImpl()
    {
        assert false : "cannot instantiate";
    }

    /**
     * Returns a new <code>DatalessKeyAccess</code> object appropriate for
     * persisting <code>DatalessKey</code> objects of the given class.
     * For example:
     * <code>DatalessKeyAccess access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long")</code>
     * (which returns an object of type <code>com.surveysampling.util.key.impl.DatalessKeyAccessLong</code>
     * initialized by calling its default constructor.
     * @param datalessKeyUnderlyingTypeName name of underlying type, for example, "Long"
     * @return the new instance of <code>DatalessKeyAccess</code>
     * @throws DatalessKeyAccessCreationException
     */
    public static DatalessKeyAccess createDatalessKeyAccess(final String datalessKeyUnderlyingTypeName) throws DatalessKeyAccessCreationException
    {
        try
        {
            return tryCreateDatalessKeyAccess(datalessKeyUnderlyingTypeName);
        }
        catch (final Throwable e)
        {
            throw new DatalessKeyAccessCreationException(datalessKeyUnderlyingTypeName,e);
        }
    }

    /**
     * @param datalessKeyUnderlyingTypeName
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static DatalessKeyAccess tryCreateDatalessKeyAccess(final String datalessKeyUnderlyingTypeName)
        throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        final String packagenameOurs = DatalessKeyAccessFactoryImpl.class.getPackage().getName();
        final String classnameAccessXxx = packagenameOurs+".DatalessKeyAccess"+datalessKeyUnderlyingTypeName;
        final Class classAccessXxx = Class.forName(classnameAccessXxx);
        return (DatalessKeyAccess)classAccessXxx.newInstance();
    }
    
    /**
     * Returns the <code>DatalessKeyAccess</code> object appropriate for
     * persisting <code>DatalessKey</code> objects of the given class.
     * For example:
     * <code>DatalessKeyAccess access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long")</code>
     * (which returns an object of type <code>com.surveysampling.util.key.impl.DatalessKeyAccessLong</code>
     * initialized by calling its default constructor.
     * @param datalessKeyUnderlyingTypeName name of underlying type, for example, "Long"
     * @return the instance of <code>DatalessKeyAccess</code>
     */
    public static DatalessKeyAccess getDatalessKeyAccess(final DatalessKey key)
    {
        final DatalessKeyImpl keyImpl = (DatalessKeyImpl) key;
        return keyImpl.getAccess();
    }
}
