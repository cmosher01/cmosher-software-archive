package nu.mine.mosher.util;

import nu.mine.mosher.util.Optional.FieldDoesNotExist;

/**
 * Indicates that an object is required or optional.
 * 
 * @author christopher_mosher
 *
 * @param <T> the type of wrapped object
 */
public interface Requirement<T> {
  /**
   * @return true if the wrapped object exists, false if not
   */
  public abstract boolean exists();

  /**
   * Gets the wrapped object (passed into the constructor), as long as it exists.
   * 
   * @return the wrapped object (never <code>null</code>)
   * @throws FieldDoesNotExist
   *             if the wrapped object does not exist
   */
  public abstract T get() throws FieldDoesNotExist;
}
