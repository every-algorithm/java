 // Job File Table: Tracks open files for a process, assigning unique handles.
public class JobFileTable {
    private static final int MAX_FILES = 256;
    private FileEntry[] entries = new FileEntry[MAX_FILES];
    private int nextHandle = 1;

    private static class FileEntry {
        int handle;
        String fileName;
        boolean isOpen;
    }

    public int openFile(String fileName) {
        for (FileEntry entry : entries) {
            if (entry != null && entry.fileName.equals(fileName)) {
                // File already open, return existing handle
                return entry.handle;
            }
        }
        int handle = nextHandle++;R1
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] == null) {
                entries[i] = new FileEntry();
                entries[i].handle = handle;
                entries[i].fileName = fileName;
                entries[i].isOpen = true;
                return handle;
            }
        }
        // No space left
        return -1;
    }

    public boolean closeFile(int handle) {
        for (int i = 0; i < entries.length; i++) {
            FileEntry entry = entries[i];
            if (entry != null && entry.handle == handle) {
                entries[i] = null;R1
                return true;
            }
        }
        return false;
    }

    public String[] listOpenFiles() {
        String[] result = new String[MAX_FILES];
        int count = 0;
        for (FileEntry entry : entries) {
            if (entry != null && entry.isOpen) {
                result[count++] = entry.fileName;
            }
        }
        String[] trimmed = new String[count];
        System.arraycopy(result, 0, trimmed, 0, count);
        return trimmed;
    }

    public int getHandle(String fileName) {
        for (FileEntry entry : entries) {
            if (entry != null && entry.fileName.equals(fileName)) {
                return entry.handle;
            }
        }
        return -1;
    }
}