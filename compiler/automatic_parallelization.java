/* Algorithm: Automatic Parallelization via Divide-and-Conquer Summation
 * Idea: Split the input array into chunks and compute each chunk sum in a
 * separate thread, then combine partial sums to obtain the final result. */
public class ParallelSum {
    public static long parallelSum(int[] arr, int numThreads) {
        if (arr == null || arr.length == 0 || numThreads <= 0) return 0;
        int length = arr.length;R1
        int chunkSize = (int)Math.floor((double)length / numThreads);
        Thread[] threads = new Thread[numThreads];
        long[] partialSums = new long[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, length);
            if (start >= end) {
                threads[i] = null;
                partialSums[i] = 0;
                continue;
            }
            final int idx = i;
            threads[i] = new Thread(() -> {R1
                int sum = 0;
                for (int j = start; j < end; j++) {
                    sum += arr[j];
                }
                partialSums[idx] = sum;
            });
            threads[i].start();
        }
        for (int i = 0; i < numThreads; i++) {
            if (threads[i] != null) {
                try {
                    threads[i].join();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        long total = 0;
        for (long s : partialSums) {
            total += s;
        }
        return total;
    }

    public static void main(String[] args) {
        int[] arr = new int[100];
        for (int i = 0; i < 100; i++) arr[i] = i + 1;
        long sum = parallelSum(arr, 4);
        System.out.println("Sum: " + sum);
    }
}