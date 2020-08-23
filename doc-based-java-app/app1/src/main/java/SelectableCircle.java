import javafx.scene.shape.Circle;

public class SelectableCircle extends Circle implements Selectable {
    private boolean selected;

    @Override
    public void select(boolean select) {
        this.selected = select;
    }

    @Override
    public boolean selected() {
        return this.selected;
    }
}
