/*
 * Created on April 13, 2005
 */
package com.surveysampling.util.key;

import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;
import com.surveysampling.util.key.impl.DatalessKeyAccessFactoryImpl;


/**
 * Factory class for creating instances of <code>DatalessKeyAccess</code>
 * appropriate for the given underlying datatype.
 * 
 * @author Chris Mosher
 */
public final class DatalessKeyAccessFactory
{
    private DatalessKeyAccessFactory()
    {
        assert false : "cannot instantiate";
    }

    /**
     * Returns a new <code>DatalessKeyAccess</code> object appropriate for
     * persisting <code>DatalessKey</code> objects stored as the given underlying type.
     * For example:
     * <code>DatalessKeyAccess access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long")</code>,
     * which returns an object of type <code>com.surveysampling.util.key.impl.DatalessKeyAccessLong</code>
     * (initialized by calling its default constructor). The resulting <code>DatalessKeyAccess</code>
     * object would be used to persist a key as a <code>long</code> datatype.
     * @param datalessKeyUnderlyingTypeName name of underlying type, for example, "Long"
     * @return the new instance of <code>DatalessKeyAccess</code>
     * @throws DatalessKeyAccessCreationException
     */
    public static DatalessKeyAccess createDatalessKeyAccess(final String datalessKeyUnderlyingTypeName) throws DatalessKeyAccessCreationException
    {
        return DatalessKeyAccessFactoryImpl.createDatalessKeyAccess(datalessKeyUnderlyingTypeName);
    }
    
    /**
     * Returns a <code>DatalessKeyAccess</code> object appropriate for
     * persisting <code>DatalessKey</code> objects stored as the given underlying type.
     * For example:
     * <code>DatalessKeyAccess access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long")</code>,
     * which returns an object of type <code>com.surveysampling.util.key.impl.DatalessKeyAccessLong</code>
     * (initialized by calling its default constructor). The resulting <code>DatalessKeyAccess</code>
     * object would be used to persist a key as a <code>long</code> datatype.
     * @param datalessKeyUnderlyingTypeName name of underlying type, for example, "Long"
     * @return the new instance of <code>DatalessKeyAccess</code>
     */
    public static DatalessKeyAccess getDatalessKeyAccess(final DatalessKey key)
    {
        return DatalessKeyAccessFactoryImpl.getDatalessKeyAccess(key);
    }
}
