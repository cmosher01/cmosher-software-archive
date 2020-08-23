import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public abstract class AppStage extends Stage implements Closeable {
    public void run() throws IOException {
        setScene(createPrimaryScene(null));
        setOnCloseRequest(t -> {
            if (isSafeToClose()) {
                close();
            } else {
                t.consume();
            }
        });
        show();
    }

    public boolean isSafeToClose() {
        return !dirty() || askIfSafeToClose();
    }

    private boolean dirty() {
        return true;
    }

    private boolean askIfSafeToClose() {
        final Alert alert = new Alert(Alert.AlertType.WARNING, "Unsaved changes will be LOST. Is this OK?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Close");
        final Optional<ButtonType> response = alert.showAndWait();
        return response.isPresent() && response.get() == ButtonType.YES;
    }

    protected abstract Scene createPrimaryScene(final AppController app) throws IOException;

    public void onFileClose(ActionEvent actionEvent) {
        if (isSafeToClose()) {
            close();
        }
    }
}
