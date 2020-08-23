package nu.mine.mosher.example;

public class SessionController {
    private final Session session;

    public SessionController(final Session session) {
        this.session = session;
    }

    public void onFileQuit() {
        this.session.closeIfSafe();
    }
}
