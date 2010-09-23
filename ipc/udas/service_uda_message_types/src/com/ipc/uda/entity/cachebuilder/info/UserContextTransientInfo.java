package com.ipc.uda.entity.cachebuilder.info ;

import java.util.ArrayList ;
import java.util.List ;
import java.util.HashMap ;
import java.util.Iterator ;
import java.util.Map ;
import java.util.Set ;
import java.io.Serializable ;
import com.ipc.ds.base.dto.TransientDTOImpl ;
import com.ipc.uda.entity.dto.UserContextTransient ;

/**
 * UserContextTransientInfo is the info class for the UserContextTransient entity that holds some tracking data of its entities..
 * 
 * @author DS Elves
 * 
 * @see UserContextTransient
 * 
 */
public class UserContextTransientInfo extends TransientDTOImpl implements Serializable
{
	private static UserContextTransientInfo instance = null;

	/**
	 * Entities mapped to a name given by the user. Name given by the user is the key
	 */
	private static HashMap<String, UserContextTransient> entityNameMap = new HashMap<String, UserContextTransient>() ;

	/**
	 * All id's of the UserContextTransient entities in the cache.
	 */
	private static List<String> idList = new ArrayList<String>() ;

	/**
	 * Factory making sure that the object is a singleton
	 */
	public static UserContextTransientInfo getInstance()
	{
		if(instance == null)
		{
			instance = new UserContextTransientInfo();
		}
		return instance;
	}

	/**
	 * adds the given id of an UserContextTransient instance to the idList
	 */
	public static void addToIdList(String id)
	{
		idList.add(id) ;
	}

	/**
	 * Remove the given id of an UserContextTransient instance from the idList
	 */
	public static void removeFromIdList(String id)
	{
		idList.remove(id) ;
	}

	/**
	 * maps the given name with the given entity.
	 * @param name. name to be mapped to the entity.
	 * @param theUserContextTransient. UserContextTransient instance to be mapped with the given name.
	 */
	public static void setEntityWithName(String name, UserContextTransient theUserContextTransient)
	{
		entityNameMap.put(name, theUserContextTransient) ;
	}

	/**
	 * removes the given entity from the map.
	 * @param theUserContextTransient. entity to be removed.
	 */
	public static void removeEntityFromMap(UserContextTransient theUserContextTransient)
	{
		Set<?> mapSet=entityNameMap.entrySet() ;
		Iterator<?> mapItr=mapSet.iterator() ;
		while (mapItr.hasNext())
		{
			Map.Entry<String, UserContextTransient> entry=(Map.Entry<String, UserContextTransient>) mapItr.next() ;
			if (entry.getValue().equals(theUserContextTransient))
			{
				mapItr.remove();
			}
		}
	}

	/**
	 * removes the entity mapped to the given name, from the names map.
	 * @param name.
	 */
	public static UserContextTransient getEntityNamed(String name)
	{
		return entityNameMap.get(name) ;
	}

	public static List<UserContextTransient> getAllEntitiesInMap()
	{
		List<UserContextTransient> theUserContextTransients = new ArrayList<UserContextTransient>() ;

		for(String key : entityNameMap.keySet())
		{
			theUserContextTransients.add(entityNameMap.get(key)) ;
		}

		return theUserContextTransients ;
	}

	public static List<String> getIdList()
	{
		return idList ;
	}
}
