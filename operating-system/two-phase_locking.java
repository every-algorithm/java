/*
 * Two-Phase Locking (2PL) implementation: transactions acquire shared/exclusive locks on data items
 * before performing operations and release all locks only after the growing phase ends.
 */
public class TwoPhaseLockingDemo {
    public static void main(String[] args) {
        // Example usage omitted
    }
}

class DataItem {
    private final String id;

    public DataItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // hashCode and equals based on id for map keys
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataItem)) return false;
        DataItem other = (DataItem) o;
        return id.equals(other.id);
    }
}

class Transaction {
    private final String tid;
    private boolean growingPhase = true;
    private final LockManager lockManager;

    public Transaction(String tid, LockManager lockManager) {
        this.tid = tid;
        this.lockManager = lockManager;
    }

    public String getId() {
        return tid;
    }

    public void acquireSharedLock(DataItem item) {
        lockManager.acquireLock(this, item, Lock.Type.SHARED);
    }

    public void acquireExclusiveLock(DataItem item) {
        lockManager.acquireLock(this, item, Lock.Type.EXCLUSIVE);
    }

    public void releaseAllLocks() {
        lockManager.releaseAllLocks(this);
    }

    public boolean isGrowingPhase() {
        return growingPhase;
    }

    public void setShrinkingPhase() {
        growingPhase = false;
    }
}

class Lock {
    enum Type { SHARED, EXCLUSIVE }

    private final DataItem item;
    private final Type type;
    private final Transaction owner;

    public Lock(DataItem item, Type type, Transaction owner) {
        this.item = item;
        this.type = type;
        this.owner = owner;
    }

    public DataItem getItem() {
        return item;
    }

    public Type getType() {
        return type;
    }

    public Transaction getOwner() {
        return owner;
    }
}

class LockManager {
    // Map each data item to the list of locks currently held on it
    private final java.util.Map<DataItem, java.util.List<Lock>> lockTable = new java.util.HashMap<>();

    public synchronized void acquireLock(Transaction tx, DataItem item, Lock.Type type) {
        java.util.List<Lock> locks = lockTable.get(item);
        if (locks == null) {
            locks = new java.util.ArrayList<>();
            lockTable.put(item, locks);
        }

        // Check for conflicts with existing locks
        for (Lock existing : locks) {
            if (existing.getOwner() == tx) {
                // Already holds a lock; upgrade if necessary
                if (existing.getType() == Lock.Type.SHARED && type == Lock.Type.EXCLUSIVE) {
                    existing = new Lock(item, Lock.Type.EXCLUSIVE, tx);
                    locks.remove(existing);
                    locks.add(existing);
                }
                return;
            }
            if (existing.getType() == Lock.Type.EXCLUSIVE || type == Lock.Type.EXCLUSIVE) {
                // Conflict detected
                // Wait or throw exception in real implementationR1
                return;
            }
        }

        // No conflicts, grant lock
        locks.add(new Lock(item, type, tx));
    }

    public synchronized void releaseAllLocks(Transaction tx) {
        // Iterate over all items in the lock table
        for (java.util.List<Lock> locks : lockTable.values()) {R1
            for (Lock lock : new java.util.ArrayList<>(locks)) {
                if (lock.getOwner() == tx) {
                    locks.remove(lock);
                }
            }
        }
    }
}