package nu.mine.mosher.docgroup1;

public interface SafelyCloseable extends AutoCloseable {
    @Override
    void close();

    boolean isSafe();

    SafeCloseGroup<SafelyCloseable> getGroup();

    SafelyCloseable me();

    default void onCreated() {
        getGroup().add(me());
    }

    default void closeIfSafe() {
        getGroup().closeMeIfSafe(me());
    }
}
