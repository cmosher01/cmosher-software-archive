package com.ipc.uda.entity.internal.manager.base ;

import com.ipc.ds.base.annotation.DunkinTransient ;
import java.util.Calendar ;
import java.text.SimpleDateFormat ;
import com.ipc.ds.base.dto.DTOImpl ;
import com.ipc.ds.base.exception.StorageFailureException ;
import com.ipc.ds.base.exception.CrossOperationException ;
import com.ipc.ds.base.exception.EntityDoesNotExistException ;
import com.ipc.ds.base.exception.RepeatedSaveInTransactionException ;
import com.ipc.ds.base.security.SecurityContext ;
import java.util.List ;
import com.ipc.ds.notification.Publisher;
import com.ipc.uda.entity.cachebuilder.UserContextTransientCacheBuilder ;
import com.ipc.ds.base.exception.ItemNotFoundException ;
import com.ipc.ds.base.exception.CacheInitializationException ;
import com.ipc.uda.entity.dto.UserContextTransient ;
import com.ipc.uda.entity.internal.dto.impl.UserContextTransientImpl ;

/**
 * UserContextTransientBaseManager is the base manager class for the UserContextTransient entity.
 * It provides the base functionality of the API portal for operations and storage management over UserContextTransient objects while also providing factory methods for UserContextTransient creation.
 * 
 * @author DS Elves
 * 
 * @see UserContextTransient
 * 
 */

@DunkinTransient
public class UserContextTransientBaseManager
{
	/**
	 * This is the security context used to access entities.
	 */
	protected SecurityContext context ;
	protected Publisher publisher;
	protected UserContextTransientCacheBuilder cacheBuilder ;

	/**
	 * This is the primary constructor.
	 *
	 * @param context The security context to use for entity access.
	 */
	public UserContextTransientBaseManager(SecurityContext context)
	{
		this.context=context ;
		this.cacheBuilder=new UserContextTransientCacheBuilder() ;
		this.publisher = Publisher.GetPublisher();
	}

	/**
	 * This is the factory method for creating UserContextTransient entities.
	 * 
	 * @return a new UserContextTransient
	 * 
	 */ 
	public static UserContextTransient NewUserContextTransient()
	{
		return new UserContextTransientImpl() ;
	}

	/**
	 * This method will delete the UserContextTransient entity. Since this type of entity is transient, the entity will be deleted in an internal cache.
	 * 
	 * @param theUserContextTransient The UserContextTransient to delete
	 * @throws InvalidContextException thrown when the Security Context with the credentials is not valid.
	 * @throws StorageFailureException thrown when the operation is not completed because of some sql error.
	 * @throws EntityDoesNotExistException. It is thrown when the entity passed for delete does not exist in the DB.
	 * 
	 */ 
	public void delete(UserContextTransient theUserContextTransient) throws StorageFailureException
	{
		// Has this entity been saved before?
		if (theUserContextTransient.getTransientId()==null || theUserContextTransient.getTransientId().trim().length()==0)
		{ // no, so somebody is in trouble
			throw new StorageFailureException("Invalid UserContextTransient entity during delete.") ;
		}

		// Delete the entity in the cache
		cacheBuilder.delete((UserContextTransientImpl)theUserContextTransient) ;


		//Call the Notification API for publishing the delete.
		publisher.publish("ds.UserContextTransient."+theUserContextTransient.getTransientId()+".deleted", (UserContextTransientImpl)theUserContextTransient);
	}

	/**
	 * This method will get the UserContextTransient entity associated with the id. Since this type of entity is transient, the entity will be read from an internal cache.
	 * 
	 * @param id The id of the UserContextTransient to get
	 * @throws InvalidContextException thrown when the Security Context with the credentials is not valid.
	 * @throws StorageFailureException thrown when the operation is not completed because of some sql error.
	 * 
	 * @return The UserContextTransient associated with the id or null if it doesn't exist.
	 * 
	 */ 
	public UserContextTransient getById(String id) throws StorageFailureException
	{
		// Get the entity from the cache
		try
		{
			return cacheBuilder.getById(id) ;
		}
		catch (ItemNotFoundException e)
		{
			throw new StorageFailureException(e) ;
		}
		catch (CacheInitializationException e)
		{
			throw new StorageFailureException(e) ;
		}
	}

	/**
	 * This method will save UserContextTransient entities. Since this type of entity is transient, the entity will be saved in an internal cache.
	 * 
	 * @param theUserContextTransient The UserContextTransient to save
	 * @throws StorageFailureException thrown when the operation is not completed because of an error.
	 * @throws InvalidContextException thrown when the Security Context with the credentials is not valid.
	 * @throws EntityDoesNotExistException thrown when the UserContextTransient passed for update does not exist in the Database
	 * @throws RepeatedSaveInTransactionException thrown when the UserContextTransient is tried to be saved more than in the same transaction
	 * 
	 */ 
	public void save(UserContextTransient theUserContextTransient) throws StorageFailureException
	{

		// Save the entity in the cache, since it is Transient 
		cacheBuilder.put((UserContextTransientImpl)theUserContextTransient);

		//Call the Notification API for publishing the change.
		publisher.publish("ds.UserContextTransient."+theUserContextTransient.getTransientId()+".changed", (UserContextTransientImpl)theUserContextTransient);
	}

	/**
	 * This method will save a list of the UserContextTransient entities. Since this type of entity is transient, the entities will be saved in an internal cache.
	 * 
	 * @param theUserContextTransientList The UserContextTransient entities to save
	 * @throws InvalidContextException thrown when the Security Context with the credentials is not valid.
	 * @throws StorageFailureException thrown when the operation is not completed because of some sql error.
	 * @throws CrossOperationException thrown when theUserContextTransientList is mixed up with the entities for insert and update
	 * @throws EntityDoesNotExistException thrown when a UserContextTransient passed for update does not exist in the Database
	 * @throws RepeatedSaveInTransactionException thrown when the UserContextTransient is tried to be saved more than in the same transaction
	 * 
	 */ 
	public void saveAll(List<? extends UserContextTransient> theUserContextTransientList) throws StorageFailureException, CrossOperationException
	{
		boolean foundId = false;
		boolean foundNoId = false;
		boolean id = false;

		for (UserContextTransient item: theUserContextTransientList)
		{
			id = ( (item.getTransientId() != null) && (item.getTransientId().trim().length() > 0) ) ;
			foundId |= id ;
			foundNoId |= !id ;

			if(!(foundId ^ foundNoId)) 
			{
				throw new CrossOperationException("Cannot mix insert and update operations");
			}
		}

		// Save the entity List in the cache, since it is transient 
		cacheBuilder.saveList(theUserContextTransientList);

		for(UserContextTransient theUserContextTransient : theUserContextTransientList)
		{
			//Call the Notification API for publishing the change.
			publisher.publish("ds.UserContextTransient."+theUserContextTransient.getTransientId()+".changed", (UserContextTransientImpl)theUserContextTransient);
		}

	}

	/**
	 * This method will delete a List of the UserContextTransient entities. Since this type of entity is transient, the entities will be deleted in an internal cache.
	 * 
	 * @param theUserContextTransientList The UserContextTransient List to be deleted
	 * @throws InvalidContextException thrown when the Security Context with the credentials is not valid.
	 * @throws StorageFailureException thrown when the operation is not completed because of some sql error.
	 * @throws EntityDoesNotExistException. It is thrown when the entity passed for delete does not exist in the DB.
	 * 
	 */ 
	public void deleteAll(List<? extends UserContextTransient> theUserContextTransientList) throws StorageFailureException, EntityDoesNotExistException
	{
		//See if any of the UserContextTransient in the List are unsaved entities
		for(UserContextTransient theUserContextTransient : theUserContextTransientList)
		{
			if (theUserContextTransient.getTransientId()==null || theUserContextTransient.getTransientId().trim().length()==0)
			{ // no, so somebody is in trouble
				throw new StorageFailureException("Invalid UserContextTransient entity during delete.") ;
			}
		}

		// Delete the UserContextTransient in the cache
		cacheBuilder.deleteListByIds(theUserContextTransientList) ;

		//Call the Notification API for publishing the delete.
		for(UserContextTransient theUserContextTransient : theUserContextTransientList)
		{
			publisher.publish("ds.UserContextTransient."+theUserContextTransient.getTransientId()+".deleted", (UserContextTransientImpl)theUserContextTransient);
		}

	}

	/**
	 * This method will get the List of UserContextTransient entities associated with the given list of ids. Since this type of entity is transient, the entities will be read from an internal cache.
	 * 
	 * @param idList The List of the UserContextTransient ids
	 * @throws InvalidContextException thrown when the Security Context with the credentials is not valid.
	 * @throws StorageFailureException thrown when the operation is not completed because of some sql error.
	 * 
	 * @return The List of UserContextTransient associated with the given list of ids .
	 * 
	 */ 
	public List<UserContextTransient> getByIds(List<String> idList) throws StorageFailureException
	{
		// Get the entity from the cache
		try
		{
			return cacheBuilder.getByIds(idList) ;
		}
		catch (ItemNotFoundException e)
		{
			throw new StorageFailureException(e) ;
		}
		catch (CacheInitializationException e)
		{
			throw new StorageFailureException(e) ;
		}
	}

	/**
	 * This method will map the given UserContextTransient instance with the given name.
	 */ 
	public void setEntityName(String name, UserContextTransient theUserContextTransient) throws StorageFailureException
	{
		if(theUserContextTransient.getTransientId() == null)
		{
			throw new StorageFailureException("Cannot setEntityName for an Unsaved Entity.");
		}
		cacheBuilder.setEntityName(name, theUserContextTransient) ;
	}

	/**
	 * This method will return the UserContextTransient mapped to the given name.
	 */ 
	public UserContextTransient getEntityNamed(String name) throws StorageFailureException
	{
		return cacheBuilder.getEntityNamed(name) ;
	}

	/**
	 * This method will return the List all the UserContextTransients in the cache.
	 */ 
	public List<UserContextTransient> getAllEntitiesWithName() throws StorageFailureException
	{
		return cacheBuilder.getAllEntitiesWithName() ;
	}

	/**
	 * This method will return the List all the UserContextTransients in the cache.
	 */ 
	public List<UserContextTransient> getAll() throws StorageFailureException
	{
		try
		{
			return cacheBuilder.getByIds(getAllIds()) ;
		}
		catch (ItemNotFoundException e)
		{
			throw new StorageFailureException(e) ;
		}
		catch (CacheInitializationException e)
		{
			throw new StorageFailureException(e) ;
		}
	}

	/**
	 * This method will return the List of id's of all the UserContextTransients in the cache.
	 */ 
	public List<String> getAllIds() throws StorageFailureException
	{
		return cacheBuilder.getAllIds() ;
	}

	/**
	 * This method will return the total number of the UserContextTransient entities stored in the cache.
	 */ 
	public int getCount() throws StorageFailureException
	{
		return cacheBuilder.getAllIds().size() ;
	}

}
