package nu.mine.mosher.util;

/**
 * Indicates that an object is required or optional.
 * 
 * @author christopher_mosher
 * 
 * @param <T>
 *          the type of wrapped object
 */
public interface Requirement<T> {
  /**
   * @return true if the wrapped object exists, false if not
   */
  public abstract boolean exists();

  /**
   * Gets the wrapped object (passed into the constructor), as long as it
   * exists.
   * 
   * @return the wrapped object (never <code>null</code>)
   * @throws FieldDoesNotExist
   *           if the wrapped object does not exist
   */
  public abstract T get() throws FieldDoesNotExist;

  /**
   * Indicates an attempt to access a field that does not exist (that is, one
   * that is null).
   * 
   * @author christopher_mosher
   */
  public static class FieldDoesNotExist extends RuntimeException {
    private static final String MESSAGE = "Field does not exist.";

    FieldDoesNotExist() {
      super(MESSAGE);
    }

    FieldDoesNotExist(final Throwable cause) {
      super(MESSAGE, cause);
    }
  }
}
