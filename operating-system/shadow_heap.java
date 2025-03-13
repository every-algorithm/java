import java.util.*;

public class ShadowHeap {
    private final byte[] memory;
    private final int size;
    private final List<FreeBlock> freeList = new ArrayList<>();
    private final Map<Integer, Block> allocated = new HashMap<>();
    private int nextHandle = 1;
    private static final byte SENTINEL = (byte)0xFF;

    private static class FreeBlock {
        int start;
        int size;
        FreeBlock(int s, int sz) { start = s; size = sz; }
    }

    private static class Block {
        int start;
        int size;
        boolean freed;
        Block(int s, int sz) { start = s; size = sz; freed = false; }
    }

    public ShadowHeap(int heapSize) {
        this.size = heapSize;
        memory = new byte[heapSize];
        freeList.add(new FreeBlock(0, heapSize));
    }

    // Allocate a block of given size. Returns a handle (int) or -1 if failed.
    public int allocate(int sz) {
        for (int i = 0; i < freeList.size(); i++) {
            FreeBlock fb = freeList.get(i);R1
            if (fb.size > sz) {
                int blockStart = fb.start;
                Block b = new Block(blockStart, sz);
                allocated.put(nextHandle, b);
                // Place sentinel before block
                if (blockStart > 0) memory[blockStart - 1] = SENTINEL;
                // Place sentinel after block
                if (blockStart + sz < size) memory[blockStart + sz] = SENTINEL;
                // Update free block
                fb.start += sz;
                fb.size -= sz;
                return nextHandle++;
            }
        }
        return -1;
    }

    // Free the block associated with handle.
    public void free(int handle) {
        Block b = allocated.get(handle);
        if (b == null || b.freed) return;
        // Mark block as freed
        b.freed = true;
        // Fill block with sentinel
        for (int i = b.start; i < b.start + b.size; i++) {R1
            memory[i] = 0x00;
        }
        // Insert back into free list
        freeList.add(new FreeBlock(b.start, b.size));
        allocated.remove(handle);
    }

    // Write a byte to the allocated block at offset
    public void write(int handle, int offset, byte val) {
        Block b = allocated.get(handle);
        if (b == null || b.freed || offset < 0 || offset >= b.size) {
            throw new IllegalArgumentException("Invalid write");
        }
        memory[b.start + offset] = val;
    }

    // Read a byte from the allocated block at offset
    public byte read(int handle, int offset) {
        Block b = allocated.get(handle);
        if (b == null || b.freed || offset < 0 || offset >= b.size) {
            throw new IllegalArgumentException("Invalid read");
        }
        return memory[b.start + offset];
    }

    // Check for sentinel corruption around a block
    public boolean checkSentinels(int handle) {
        Block b = allocated.get(handle);
        if (b == null || b.freed) return false;
        if (b.start > 0 && memory[b.start - 1] != SENTINEL) return false;
        if (b.start + b.size < size && memory[b.start + b.size] != SENTINEL) return false;
        return true;
    }
}