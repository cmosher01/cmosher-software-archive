package nu.mine.mosher.example;

import java.util.HashSet;
import java.util.Set;

public class SafeCloseGroup<C extends SafelyCloseable> {
    private final Set<C> closeables = new HashSet<>();
    private final AutoCloseable notifyOnClose;

    public SafeCloseGroup(final AutoCloseable notifyOnClose) {
        this.notifyOnClose = notifyOnClose;
    }

    public boolean closeMeIfSafe(final C closeable) {
        final boolean safe = closeable.isSafe();
        if (safe) {
            closeable.close();
            this.closeables.remove(closeable);
            notifyIfEmpty();
        }
        return safe;
    }

    private void notifyIfEmpty() {
        if (this.closeables.isEmpty()) {
            try {
                this.notifyOnClose.close();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    public void add(final C closable) {
        this.closeables.add(closable);
    }

    public boolean closeAllIfSafe() {
        while (!this.closeables.isEmpty()) {
            final C closeable = this.closeables.iterator().next();
            if (!closeMeIfSafe(closeable)) {
                return false;
            }
        }
        return true;
    }
}
