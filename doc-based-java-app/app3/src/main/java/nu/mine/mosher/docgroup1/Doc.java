package nu.mine.mosher.docgroup1;

import nu.mine.mosher.DocModel;

public class Doc implements SafelyCloseable {
    public final DocModel model;
    private final SafeCloseGroup<SafelyCloseable> group;

    public static Doc create(final SafeCloseGroup<SafelyCloseable> group, final DocModel model) {
        final Doc object = new Doc(group, model);
        object.onCreated();
        return object;
    }

    private Doc(final SafeCloseGroup<SafelyCloseable> group, final DocModel model) {
        this.model = model;
        this.group = group;
    }

    @Override
    public SafeCloseGroup<SafelyCloseable> getGroup() {
        return this.group;
    }

    @Override
    public SafelyCloseable me() {
        return this;
    }

    @Override
    public boolean isSafe() {
        return true;
    }

    @Override
    public void close() {
        System.out.println("closing "+this);
    }

    @Override
    public String toString() {
        return this.model.toString();
    }
}
