import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class ExampleScene extends Scene {
    public ExampleScene(final AppController app) throws IOException {
        super(init(app));
    }

    private static Parent init(final AppController app) throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.MAIN.fxml("primaryScene"), App.MAIN.WORDING);
        final Parent root = loader.load();
        final Primary controller = loader.getController();
//        controller.setApp(app);
        return root;
    }
}
