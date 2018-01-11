/*
 * Created on Nov 22, 2005
 */
package nu.mine.mosher.gedcom.viewer.gui.exception;

public class UserCancelled extends Exception {
    public UserCancelled() {
        super();
    }

    public UserCancelled(String message) {
        super(message);
    }

    public UserCancelled(String message, Throwable cause) {
        super(message, cause);
    }

    public UserCancelled(Throwable cause) {
        super(cause);
    }
}
