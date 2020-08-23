package nu.mine.mosher.docgroup2;

import nu.mine.mosher.DocModel;

public class Doc implements SafelyClosable {
    public final DocModel model;
    private SafeCloseGroup<? extends SafelyClosable> group;

    public Doc(final DocModel model) {
        this.model = model;
    }

    @Override
    public void setGroup(final SafeCloseGroup<? extends SafelyClosable> group) {
        this.group = group;
    }

    @Override
    public SafeCloseGroup<? extends SafelyClosable> getGroup() {
        return this.group;
    }

    @Override
    public SafelyClosable me() {
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
