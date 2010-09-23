/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Veena Makam
 * 
 */
public class UDAEnumerationsUtil
{
    /**
     * Map holding the String and integer equivalents of ContactCategoryType
     */
    public static final HashMap<Integer, String> contactCategoryTypeMap = new HashMap<Integer, String>();

    /**
     * Mapping the enumeration of contactCategoryType String with their IDs
     */
    static
    {
        contactCategoryTypeMap.put(0, "ENTERPRISE");
        contactCategoryTypeMap.put(1, "INSTANCE");
        contactCategoryTypeMap.put(2, "GROUP");
        contactCategoryTypeMap.put(3, "PERSONAL");
    }

    /**
     * Utility method to get the enum type with the given String equivalent
     * 
     * @param enumValueString String
     * @param udaEnumMap HashMap<Integer, String>
     * @return Integer enumKeyTypeID
     */
    public static Integer getIntKeyFromStringValue(String enumValueString,
            HashMap<Integer, String> udaEnumMap)
    {
        int enumKeyTypeID = 0;
        Set<?> enumMapSet = udaEnumMap.entrySet();

        Iterator<?> enumMapItr = enumMapSet.iterator();

        while (enumMapItr.hasNext())
        {
            Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) enumMapItr
                    .next();
            if (entry.getValue().equalsIgnoreCase(enumValueString))
            {
                return entry.getKey();
            }
            else
            {
                continue;
            }
        }

        return enumKeyTypeID;
    }

    /**
     * Utillity method to get the String equivalent of enum with the given integer input
     * 
     * @param enumKeyTypeID int
     * @return String
     */
    public static String getStringValueFromIntKey(int enumKeyTypeID, HashMap<Integer, String> udaEnumMap)
    {
        return udaEnumMap.get(enumKeyTypeID);
    }

}
