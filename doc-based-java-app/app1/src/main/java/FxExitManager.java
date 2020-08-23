import javafx.application.Platform;
import javafx.stage.Window;

public class FxExitManager implements PlatformExit {
    public FxExitManager(final Window main, final AppController app) {
        main.setOnCloseRequest(closeRequest -> {
            if (app.isSafeToQuit()) {
                app.close();
                exit();
            } else {
                closeRequest.consume();
            }
        });
        Platform.setImplicitExit(false);
    }

    @Override
    public void exit() {
        Platform.exit();
    }
}
