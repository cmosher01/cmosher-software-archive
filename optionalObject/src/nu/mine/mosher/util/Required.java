package nu.mine.mosher.util;

/**
 * Like Optional, but required (that is, the wrapped object cannot be null).
 * 
 * @author christopher_mosher
 * 
 * @param <T>
 *          the type of wrapped object
 */
public class Required<T> implements Requirement<T>{
  private final T t;

  /**
   * Initializes an optional wrapped object.
   * 
   * @param object
   *          the object to wrap; cannot be <code>null</code>
   * @throws FieldDoesNotExist
   *           if object is <code>null</code>
   */
  public Required(final T object) throws FieldDoesNotExist {
    this.t = object;
    if (this.t == null) {
      throw new Required.FieldDoesNotExist();
    }
  }

  /**
   * @return always <code>true</code>
   */
  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public T get() {
    return this.t;
  }

  /**
   * @author christopher_mosher
   */
  public static class FieldDoesNotExist extends RuntimeException {
    FieldDoesNotExist() {
      super("Field does not exist.");
    }
  }

  /**
   * Indicates whether the given object's wrapped object is equal to this
   * object's wrapped object.
   * 
   * @param other
   *          the other Required instance
   */
  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != this.getClass()) {
      return false;
    }
    final Required<?> that = (Required<?>) other;
    return this.t.equals(that.t);
  }

  /**
   * Returns the hash code of the wrapped object.
   */
  @Override
  public int hashCode() {
    return this.t.hashCode();
  }

  /**
   * Returns the string representation of the wrapped object.
   */
  @Override
  public String toString() {
    return this.t.toString();
  }
}
