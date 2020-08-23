import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public enum App {
    MAIN;

    public final ResourceBundle WORDING = ResourceBundle.getBundle("wording");

    public void main(final Stage main) throws IOException {
        initExitHandling(main);
//        run.setScene(new ExampleScene());
        main.setMaxWidth(0D);
        main.setMaxHeight(0D);
        main.show();
//        new ExampleStage(app);
    }

    private void initExitHandling(final Stage main) {
        main.setOnCloseRequest(this::quitIfSafe);
        Platform.setImplicitExit(false);
    }

    public boolean quitIfSafe() {
        return quitIfSafe(null);
    }

    public boolean quitIfSafe(final Event event) {
        final Alert alert = new Alert(Alert.AlertType.WARNING, "Quit?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Quit");
        final Optional<ButtonType> response = alert.showAndWait();
        final boolean quit = response.isPresent() && response.get() == ButtonType.YES;
        if (quit) {
            Platform.exit();
        } else if (Objects.nonNull(event)) {
            event.consume();
        }
        return quit;
    }


    public void onFileQuit(final ActionEvent actionEvent) {
        quitIfSafe();
    }


    public URL fxml(final String baseName) {
        return getClass().getResource(baseName + ".fxml");
    }
}
