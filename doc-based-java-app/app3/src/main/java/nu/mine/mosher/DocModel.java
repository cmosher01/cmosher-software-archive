package nu.mine.mosher;

public class DocModel {
    public final String title;

    public DocModel(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return this.title.toString();
    }
}
