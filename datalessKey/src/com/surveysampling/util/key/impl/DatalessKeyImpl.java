package com.surveysampling.util.key.impl;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;

/**
 * Implementation of <code>DatalessKeyImpl</code>.
 * 
 * @author Joe Parker
 */
abstract class DatalessKeyImpl implements DatalessKey 
{
    /**
     * Gets the access class used to persist this key.
     * @return <code>DatalessKeyAccess</code>
     */
    abstract DatalessKeyAccess getAccess();
}
