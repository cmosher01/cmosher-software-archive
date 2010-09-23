package com.ipc.uda.entity.cachebuilder ;

import java.util.ArrayList ;
import java.util.List ;
import com.ipc.ds.base.cache.CacheManager ;
import com.ipc.uda.entity.cachebuilder.info.UserContextTransientInfo ;
import com.ipc.ds.base.exception.StorageFailureException ;
import com.ipc.ds.base.exception.CacheInitializationException ;
import com.ipc.ds.base.exception.ItemNotFoundException ;
import com.ipc.uda.entity.dto.UserContextTransient ;
import com.ipc.uda.entity.internal.dto.impl.UserContextTransientImpl ;

/**
 * UserContextTransientCacheBuilder is the cache builder class for the UserContextTransient entity.
 * It provides the base functionality of the API portal for operations and storage management over UserContextTransient objects into cache through cache manager.
 * 
 * @author DS Elves
 * 
 * @see UserContextTransient
 * 
 */
public final class UserContextTransientCacheBuilder
{
	/**
	 * cacheManager instance
	 */
	 private static CacheManager<UserContextTransient> cacheManager = null;

	/**
	 * CacheManager instance for the UserContextTransientInfo
	 */
	 private static CacheManager<UserContextTransientInfo> infoCacheManager = null;

	/**
	 * The UserContextTransientInfo instance
	 */
	 private static UserContextTransientInfo theUserContextTransientInfo = UserContextTransientInfo.getInstance() ;

	/**
	 * This is the primary constructor.
	 *
	 */
	public UserContextTransientCacheBuilder()
	{
		try
		{
			cacheManager = (CacheManager<UserContextTransient>)CacheManager.getInstance(UserContextTransient.class);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Store the UserContextTransientInfo Singleton stored in the Cache.
	 */
	static
	{
		try
		{
			infoCacheManager = (CacheManager<UserContextTransientInfo>)CacheManager.getInstance(UserContextTransientInfo.class);
			infoCacheManager.putTransientItem(theUserContextTransientInfo) ;
		}
		catch(Exception e)
		{
			e.printStackTrace() ;
		}
	}

	/**
	 * This method will delete the UserContextTransient entity. Since this type of entity is transient, the entity will be deleted from an internal cache.
	 * 
	 * @param theUserContextTransientImpl The UserContextTransient to delete
	 * 
	 */ 
	public void delete(UserContextTransient theUserContextTransient) throws StorageFailureException
	{
		theUserContextTransientInfo = getUserContextTransientInfo() ;

		// Delete the entity in the cache
		try
		{
			cacheManager.removeItem(theUserContextTransient.getTransientId()) ;
			theUserContextTransientInfo.removeFromIdList(theUserContextTransient.getTransientId()) ;
			UserContextTransientInfo.removeEntityFromMap(theUserContextTransient);
			infoCacheManager.putTransientItem(theUserContextTransientInfo) ;
		}
		catch (Exception e)
		{
			throw new StorageFailureException() ;
		}
	}

	/**
	 * This method will delete the list of UserContextTransient entities. Since this type of entity is transient, the entity will be deleted in an internal cache.
	 * 
	 * @param theUserContextTransientList The list of UserContextTransient to delete
	 * 
	 */ 
	public void deleteListByIds(List<? extends UserContextTransient> theUserContextTransientList) throws StorageFailureException
	{
		theUserContextTransientInfo = getUserContextTransientInfo() ;

		// Delete the entity in the cache
		for(UserContextTransient theUserContextTransient:theUserContextTransientList)
		{
			try
			{
				cacheManager.removeItem(theUserContextTransient.getTransientId());
				theUserContextTransientInfo.removeFromIdList(theUserContextTransient.getTransientId()) ;
				UserContextTransientInfo.removeEntityFromMap(theUserContextTransient);
			}
			catch (Exception e)
			{
				throw new StorageFailureException();
			}
		}

		try
		{
			infoCacheManager.putTransientItem(theUserContextTransientInfo) ;
		}
		catch (Exception e)
		{
			throw new StorageFailureException(e) ;
		}
	}

	/**
	 * This method will get the UserContextTransient entity associated with the id. Since this type of entity is transient, the entity will be read from an internal cache.
	 * 
	 * @param id The id of the UserContextTransient to get
	 * 
	 * @return The UserContextTransient associated with the id or null if it doesn't exist.
	 * 
	 */ 
	public UserContextTransient getById(String id) throws ItemNotFoundException, CacheInitializationException
	{
		// Get the entity from the cache
		UserContextTransient theUserContextTransient = null;
		try
		{
			if(cacheManager != null)
			{
				theUserContextTransient = (UserContextTransient) cacheManager.getItem(id);
			}
			else
			{
				throw new CacheInitializationException();
			}
		}
		catch (Exception e)
		{
			throw new ItemNotFoundException();
		}
		return theUserContextTransient ;
	}

	/**
	 * This method will get the list of UserContextTransient entities associated with the id. Since this type of entity is Transient, the entity will be read from an internal cache.
	 * 
	 * @param idList The list of ids of the UserContextTransient to get
	 * 
	 * @return The list of UserContextTransient associated with the id or null if it doesn't exist.
	 * 
	 */ 
	public List<UserContextTransient> getByIds(List<String> idList) throws ItemNotFoundException, CacheInitializationException
	{
		// Get the entity from the cache
		List<UserContextTransient> theUserContextTransientList = new ArrayList<UserContextTransient>();
		for(String id:idList)
		{
			try
			{
				if(cacheManager != null)
				{
					if (cacheManager.getItem(id) != null )
					{
						theUserContextTransientList.add(((UserContextTransient)cacheManager.getItem(id)));
					}
				}
				else
				{
					throw new CacheInitializationException();
				}
			}
			catch (Exception e)
			{
				throw new ItemNotFoundException();
			}
		}
		return theUserContextTransientList ;
	}

	/**
	 * This method will save UserContextTransient entities. Since this type of entity is transient, the entity will be saved in an internal cache.
	 * 
	 * @param theUserContextTransient The UserContextTransient to save
	 * 
	 */ 
	public boolean put(UserContextTransient theUserContextTransient) throws StorageFailureException
	{
		boolean isSaved = false ;
		theUserContextTransientInfo = getUserContextTransientInfo() ;

		// Save the entity in the cache
		try
		{
			if(theUserContextTransient.getTransientId() == null || theUserContextTransient.getTransientId().trim().length() == 0)
			{
				isSaved = cacheManager.putTransientItem((UserContextTransientImpl)theUserContextTransient) ;
				theUserContextTransientInfo.addToIdList(theUserContextTransient.getTransientId()) ;
			}
			else
			{
				cacheManager.removeItem(theUserContextTransient.getTransientId());
				theUserContextTransientInfo.removeFromIdList(theUserContextTransient.getTransientId()) ;

				isSaved = cacheManager.putTransientItem((UserContextTransientImpl)theUserContextTransient) ;
				theUserContextTransientInfo.addToIdList(theUserContextTransient.getTransientId()) ;
			}

			infoCacheManager.putTransientItem(theUserContextTransientInfo) ;
		}
		catch (ItemNotFoundException e)
		{
			throw new StorageFailureException() ;
		}
		catch (Exception e)
		{
			throw new StorageFailureException() ;
		}

		return isSaved ;
	}

	/**
	 * This method will save the list of UserContextTransient entities. Since this type of entities are transient, the entities will be saved in an internal cache.
	 * 
	 * @param theUserContextTransientList The list of UserContextTransient to save
	 * 
	 */ 
	public boolean saveList(List<? extends UserContextTransient> theUserContextTransientList) throws StorageFailureException
	{
		boolean isSaved = false;
		theUserContextTransientInfo = getUserContextTransientInfo() ;

		// Save the list of entity in the cache
		for(UserContextTransient theUserContextTransient:theUserContextTransientList)
		{
			try
			{
				if(theUserContextTransient.getTransientId() == null || theUserContextTransient.getTransientId().trim().length() == 0)
				{
					isSaved = cacheManager.putTransientItem((UserContextTransientImpl)theUserContextTransient) ;
					theUserContextTransientInfo.addToIdList(theUserContextTransient.getTransientId()) ;
				}
				else
				{
					cacheManager.removeItem(theUserContextTransient.getTransientId()) ;
					theUserContextTransientInfo.removeFromIdList(theUserContextTransient.getTransientId()) ;

					isSaved = cacheManager.putTransientItem((UserContextTransientImpl)theUserContextTransient) ;
					theUserContextTransientInfo.addToIdList(theUserContextTransient.getTransientId()) ;
				}
			}
			catch (ItemNotFoundException e)
			{
				throw new StorageFailureException(e) ;
			}
			catch (Exception e)
			{
				throw new StorageFailureException(e) ;
			}
		}

		try
		{
			infoCacheManager.putTransientItem(theUserContextTransientInfo) ;
		}
		catch (Exception e)
		{
			throw new StorageFailureException(e) ;
		}

		return isSaved ;
	}

	/**
	 * Returns the UserContextTransientInfo Singleton stored in the Cache.
	 */
	public UserContextTransientInfo getUserContextTransientInfo() throws StorageFailureException
	{
		try
		{
			return (UserContextTransientInfo)infoCacheManager.getItem(theUserContextTransientInfo.getTransientId()) ;
		}
		catch(Exception e)
		{
			throw new StorageFailureException() ;
		}
	}

	/**
	 * This method will map the given UserContextTransient instance with the given name.
	 */ 
	public void setEntityName(String name, UserContextTransient theUserContextTransient) throws StorageFailureException
	{
		theUserContextTransientInfo = getUserContextTransientInfo() ;
		theUserContextTransientInfo.setEntityWithName(name, theUserContextTransient) ;

		try
		{
			infoCacheManager.putTransientItem(theUserContextTransientInfo) ;
		}
		catch(Exception e)
		{
			throw new StorageFailureException(e) ;
		}
	}

	/**
	 * This method will return the UserContextTransient mapped to the given name.
	 */ 
	public UserContextTransient getEntityNamed(String name) throws StorageFailureException
	{
		return getUserContextTransientInfo().getEntityNamed(name) ;
	}

	/**
	 * This method will return the List all the UserContextTransients in the cache.
	 */ 
	public List<UserContextTransient> getAllEntitiesWithName() throws StorageFailureException
	{
		return getUserContextTransientInfo().getAllEntitiesInMap() ;
	}

	/**
	 * This method will return the List of id's of all the UserContextTransients in the cache..
	 */ 
	public List<String> getAllIds() throws StorageFailureException
	{
		return getUserContextTransientInfo().getIdList() ;
	}

}
