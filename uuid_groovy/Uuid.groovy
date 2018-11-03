package com.surveysampling.apps.newRelicWebHook

/**
 * Bridge to java.util.UUID, providing a toString method that returns
 * the UUID without dashes. Immutable.
 */
class Uuid {
    private final UUID uuid

    public Uuid() {
        this.uuid = UUID.randomUUID()
    }

    public Uuid(final UUID uuid) {
        this.uuid = uuid
    }

    public UUID getJavaUuid() {
        this.uuid
    }

    @Override
    public boolean equals(final Object that) {
        if (that instanceof Uuid) {
            this.uuid == that.uuid
        } else {
            false
        }
    }

    @Override
    public int hashCode() {
        this.uuid.hashCode()
    }

    @Override
    public String toString() {
        final String s = this.uuid.toString()
        "${s.substring(0, 8)}${s.substring(9, 13)}${s.substring(14, 18)}${s.substring(19, 23)}${s.substring(24)}"
    }
}
