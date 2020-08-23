package nu.mine.mosher.example;

public interface CloseHandler<C extends AutoCloseable> {
    void onClose(C closeable);
}
