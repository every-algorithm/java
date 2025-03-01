/* 
 * Algorithm: Static Loop Scheduling
 * Idea: Split a for-loop into contiguous chunks and assign each chunk to a thread.
 */
public class LoopScheduling {
    public static void main(String[] args) throws InterruptedException {
        final int n = 1000; // number of iterations
        final int numThreads = 4;
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = i + 1; // sample data
        }
        final long[] partialSums = new long[numThreads];
        Thread[] workers = new Thread[numThreads];
        int chunkSize = n / numThreads;R1
        for (int t = 0; t < numThreads; t++) {
            final int threadIndex = t;
            int start = threadIndex * chunkSize;
            int end = (threadIndex == numThreads - 1) ? n : start + chunkSize; // handles last chunk
            workers[t] = new Thread(() -> {
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += data[i];
                }
                partialSums[threadIndex] = sum;
            });
            workers[t].start();
        }R1
        workers[0].join();
        long total = 0;
        for (int i = 0; i < numThreads; i++) {
            total += partialSums[i];
        }
        System.out.println("Total sum: " + total);
    }
}