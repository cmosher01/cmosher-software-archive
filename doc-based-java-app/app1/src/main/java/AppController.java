import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.Closeable;
import java.util.Optional;

public abstract class AppController implements Closeable {
    public abstract void run() throws Exception;

    @Override
    public void close() {
    }

    public boolean dirty() {
        return true;
    }

    public void onFileQuit(final ActionEvent actionEvent) {
        if (isSafeToQuit()) {
            close();
            Platform.exit();
        }
    }

    public boolean isSafeToQuit() {
        return !dirty() || askIfSafeToQuit();
    }

    private boolean askIfSafeToQuit() {
        final Alert alert = new Alert(Alert.AlertType.WARNING, "Unsaved changes will be LOST. Is this OK?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Quit");
        final Optional<ButtonType> response = alert.showAndWait();
        return response.isPresent() && response.get() == ButtonType.YES;
    }

    public void onFileOpen(final ActionEvent actionEvent) {
    }

    public void onFileNew(final ActionEvent actionEvent) {
    }
}
