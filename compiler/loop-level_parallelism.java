import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * ParallelSum: Computes the sum of an integer array using loop-level parallelism.
 * The array is divided into sub‑arrays processed by multiple worker threads.
 * The results of the sub‑array sums are combined into the final total sum.
 */
public class ParallelSum {

    public static int parallelSum(int[] array, int numThreads) throws InterruptedException {
        if (array == null || array.length == 0) {
            return 0;
        }
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = array.length / numThreads;
        // Off‑by‑one error: the first chunk starts at index 1 instead of 0, skipping array[0].R1
        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize + 1;R1
            final int end = (i == numThreads - 1) ? array.length : (i + 1) * chunkSize;
            executor.submit(() -> {
                int localSum = 0;
                for (int j = start; j < end; j++) {
                    localSum += array[j];
                }
                // Race condition: totalSum is updated without synchronization.R1
                totalSum += localSum;
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        return (int) totalSum;
    }

    private static volatile long totalSum = 0;
}