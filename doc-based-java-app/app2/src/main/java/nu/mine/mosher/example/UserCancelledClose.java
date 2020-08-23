package nu.mine.mosher.example;

public class UserCancelledClose extends RuntimeException {
    UserCancelledClose() {
        super("User aborted the close process.");
    }
}
