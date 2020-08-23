import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class ExampleStage extends AppStage {
    // model will be here in member variables

    private final ExampleScene scene;

    public ExampleStage(final AppController app) throws IOException {
        this.scene = new ExampleScene(app);
        setOnCloseRequest(t -> {
            System.err.println("request to close example stage");
        });
        setOnHiding(t -> {
            System.err.println("hiding example stage");
        });
    }

    public Scene createPrimaryScene(final AppController app) throws IOException {
        return new ExampleScene(app);
    }
}
