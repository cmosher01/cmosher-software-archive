package nu.mine.mosher.docgroup2;

public interface SafelyClosable extends AutoCloseable {
    @Override
    void close();

    boolean isSafe();

    SafeCloseGroup<? extends SafelyClosable> getGroup();

    <C extends SafelyClosable> C me();

    default void onCreated() {
        getGroup().add(me());
    }

    default void closeIfSafe() {
        getGroup().closeMeIfSafe(me());
    }

    void setGroup(SafeCloseGroup<? extends SafelyClosable> closeable);
}
