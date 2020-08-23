package nu.mine.mosher.example;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.Optional;

public class Document implements SafelyCloseable {
    private final SafeCloseGroup<SafelyCloseable> manager;

    private Optional<Path> path = Optional.empty();

    private Model modelLastSaved = Model.create();
    private Model model = this.modelLastSaved.copy();

    private final ReadOnlyBooleanWrapper dirty = new ReadOnlyBooleanWrapper();


    public static Document createNew(final SafeCloseGroup<SafelyCloseable> group) {
        final Document object = new Document(group);
        object.onCreated();
        return object;
    }

    private Document(final SafeCloseGroup<SafelyCloseable> manager) {
        this.manager = manager;
        this.dirty.bind(this.model.equal(this.modelLastSaved).not());
    }

    public ReadOnlyBooleanProperty dirtyProperty() {
        return this.dirty.getReadOnlyProperty();
    }

    public void save() {
        if (this.path.isPresent()) {
            // write to path
            this.modelLastSaved = this.model.copy();
        } else {
            saveAs();
        }
    }

    public void saveAs() {
        // TODO ask for new file
        // if OK
        // this.path = ...
        // save();
    }

    public void show(final Scene scene) {
        final Stage stage = new Stage();
        stage.setOnCloseRequest(event -> handleCloseWindowRequest(stage));
        stage.setScene(scene);
        stage.show();
    }

    private void handleCloseWindowRequest(final Stage stage) throws UserCancelledClose {
        stage.toFront();
        if (!closeIfSafe()) {
            // in case this is an application quit process, throw an exception
            // to prevent the framework from sending close requests to other windows
            throw new UserCancelledClose();
        }
    }

    private boolean askDiscard() {
        final Alert alert = new Alert(Alert.AlertType.WARNING, "DISCARD changes?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Discard Changes");
        final Optional<ButtonType> response = alert.showAndWait();
        return response.isPresent() && response.get() == ButtonType.YES;
    }

    @Override
    public void close() {
    }

    public Model modelLastSaved() {
        return this.modelLastSaved;
    }

    public Model model() {
        return this.model;
    }

    @Override
    public SafeCloseGroup<SafelyCloseable> getGroup() {
        return this.manager;
    }

    @Override
    public SafelyCloseable me() {
        return this;
    }

    @Override
    public boolean isSafe() {
        boolean safe = true;
        if (this.dirty.get()) {
            safe = askDiscard();
        }
        return safe;
    }
}
