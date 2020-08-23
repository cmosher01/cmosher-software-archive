package nu.mine.mosher.example;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javafx.beans.binding.Bindings;

public final class Model {
    public final StringProperty line;

    private Model(final String line) {
        this.line = new SimpleStringProperty(this, "line", line);
    }

    public static Model create() {
        return new Model("Untitled document.");
    }

    public Model copy() {
        return new Model(this.line.get());
    }

    public BooleanBinding equal(final Model that) {
        return Bindings.equal(this.line, that.line);
    }
}
