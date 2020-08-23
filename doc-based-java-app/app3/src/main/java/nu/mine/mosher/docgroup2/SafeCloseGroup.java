package nu.mine.mosher.docgroup2;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class SafeCloseGroup<C extends SafelyClosable> {
    private final Set<C> closeables = new HashSet<>();

    public void closeMeIfSafe(final C closeable) {
        if (closeable.isSafe()) {
            closeable.close();
            closeables.remove(closeable);
        }
    }

    public boolean isEmpty() {
        return this.closeables.isEmpty();
    }

    public <CC extends C> void add(final CC closable) {
        this.closeables.add(closable);
    }

    public <CC extends C> CC createNew(final CC closeable) {
        closeable.setGroup(this);
        return closeable;
    }
}
