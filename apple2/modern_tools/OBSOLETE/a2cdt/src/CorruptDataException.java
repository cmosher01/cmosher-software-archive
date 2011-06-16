/*
 * Created on Oct 22, 2007
 */
/**
 * Indicates corrupt input data.
 * 
 * @author christopher_mosher
 */
public class CorruptDataException extends Exception {
  private final int[] data;

  /**
   * @param data
   *          the bad data
   */
  public CorruptDataException(int[] data) {
    this.data = data;
  }

  /**
   * @param data
   *          the bad data
   * @param message
   *          error message
   */
  public CorruptDataException(int[] data, String message) {
    super(message);
    this.data = data;
  }

  /**
   * @param data
   *          the bad data
   * @param cause
   *          wrapped exception
   */
  public CorruptDataException(int[] data, Throwable cause) {
    super(cause);
    this.data = data;
  }

  /**
   * @param data
   *          the bad data
   * @param message
   *          error message
   * @param cause
   *          wrapped exception
   */
  public CorruptDataException(int[] data, String message, Throwable cause) {
    super(message, cause);
    this.data = data;
  }

  /**
   * Gets the bad data.
   * 
   * @return the bad data
   */
  public int[] getData() {
    return this.data;
  }
}
