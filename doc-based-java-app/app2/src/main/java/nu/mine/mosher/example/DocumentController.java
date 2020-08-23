package nu.mine.mosher.example;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class DocumentController {
    private final Document document;

    public Text model;
    public TextField modelEdit;
    public CheckBox dirty;
    public MenuItem save;

    public DocumentController(final Document document) {
        this.document = document;
    }

    public void setUp() {
        this.save.disableProperty().bind(Bindings.not(this.document.dirtyProperty()));
        this.modelEdit.textProperty().bindBidirectional(this.document.model().line);

        this.model.textProperty().bind(this.document.modelLastSaved().line);
        this.dirty.selectedProperty().bind(this.document.dirtyProperty());
    }

    public void onFileSave() {
        this.document.save();
    }

    public void onFileClose() {
        this.document.closeIfSafe();
    }
}
