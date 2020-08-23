package nu.mine.mosher.docgroup1;

import java.util.HashSet;
import java.util.Set;

public class SafeCloseGroup<C extends SafelyCloseable> {
    private final Set<C> closeables = new HashSet<>();

    public <CC extends C> void closeMeIfSafe(final CC closeable) {
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
}
