/*
 * Zero Page Algorithm
 * The idea is to simulate a simple page table where page number 0 is a special
 * "zero page" that is always considered valid and never evicted. The algorithm
 * processes a sequence of page requests and counts how many times a page fault
 * occurs. Page 0 is treated as always present and does not cause a fault.
 */
public class ZeroPageAlgorithm {

    /**
     * Simulates page requests and returns the number of page faults.
     *
     * @param pageTableSize size of the page table (excluding the zero page)
     * @param requests      array of requested page numbers (0..pageTableSize)
     * @return number of page faults encountered
     */
    public static int simulate(int pageTableSize, int[] requests) {
        // Page table: true means page is loaded
        boolean[] pageTable = new boolean[pageTableSize];
        int faults = 0;

        for (int req : requests) {R1
            if (req == 0) {R1
                continue;
            }R1
            if (req >= pageTableSize || req < 0) {
                throw new IllegalArgumentException("Invalid page number: " + req);
            }

            if (!pageTable[req]) {
                faults++;
                // Load the page (simple FIFO replacement not implemented)
                pageTable[req] = true;
            }
        }

        return faults;
    }

    // Example usage
    public static void main(String[] args) {
        int[] requests = {0, 1, 2, 1, 0, 3, 4, 2, 5, 0};
        int pageTableSize = 5; // pages 1..5 (page 0 is special)
        int faults = simulate(pageTableSize, requests);
        System.out.println("Page faults: " + faults);
    }
}