package nu.mine.mosher.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DocumentManager {
    private final URL fxml = getClass().getResource("Document.fxml");
    private final Session session;
    private final SafeCloseGroup<SafelyCloseable> group;

    private DocumentManager(final Session session) {
        this.session = session;
        this.group = new SafeCloseGroup<>(session);
    }

    public static DocumentManager create(final Session session) {
        return new DocumentManager(session);
    }

    public void createNewDocument() throws IOException {
        final Document document = Document.createNew(this.group);
        final DocumentController documentController = new DocumentController(document);
        final Map<Class<?>, Object> registry = new HashMap<>();
        register(documentController, registry);
        register(new DocumentManagerController(this), registry);
        register(new SessionController(session), registry);

        final Scene scene = createScene(registry);
        documentController.setUp();

        document.show(scene);
    }

    private Scene createScene(final Map<Class<?>, Object> registry) throws IOException {
        return new Scene(FXMLLoader.load(this.fxml, null, null, registry::get));
    }

    private void register(final Object object, final Map<Class<?>, Object> into) {
        into.put(object.getClass(), object);
    }

    public boolean closeIfSafe() {
        return this.group.closeAllIfSafe();
    }
}
