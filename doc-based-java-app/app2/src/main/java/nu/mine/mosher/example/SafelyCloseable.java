package nu.mine.mosher.example;

public interface SafelyCloseable extends AutoCloseable {
    @Override
    void close();

    boolean isSafe();

    SafeCloseGroup<SafelyCloseable> getGroup();

    SafelyCloseable me();

    default void onCreated() {
        getGroup().add(me());
    }

    default boolean closeIfSafe() {
         return getGroup().closeMeIfSafe(me());
    }
}
