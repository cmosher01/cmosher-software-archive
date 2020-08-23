package nu.mine.mosher.example;

import javafx.application.Platform;

import java.io.IOException;

public class Session implements AutoCloseable {
    private DocumentManager documentManager;

    private Session() {
    }

    public static Session create() {
        final Session session = new Session();
        session.documentManager = DocumentManager.create(session);
        return session;
    }

    public void run() throws IOException {
        // this is the start-up command:
        this.documentManager.createNewDocument();

        Platform.setImplicitExit(false);
    }

    public boolean closeIfSafe() {
        return this.documentManager.closeIfSafe();
    }

    @Override
    public void close() {
        Platform.exit();
    }
}
