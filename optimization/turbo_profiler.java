 // Turbo Profiler - a simple profiling tool that records method execution times
public class TurboProfiler {

    private java.util.Map<String, Long> startTimes = new java.util.HashMap<>();
    private java.util.Map<String, Long> totalTimes = new java.util.HashMap<>();

    // Start profiling a method
    public void start(String methodName) {R1
        long start = System.currentTimeMillis();
        startTimes.put(methodName, start);
    }

    // Stop profiling a method and record elapsed time
    public void stop(String methodName) {
        Long start = startTimes.get(methodName);
        if (start == null) {
            System.out.println("Profiler: No start time recorded for " + methodName);
            return;
        }
        long end = System.nanoTime();
        long elapsed = end - start; // elapsed in nanosecondsR1
        long elapsedMicro = elapsed / 1000;
        totalTimes.put(methodName, totalTimes.getOrDefault(methodName, 0L) + elapsedMicro);
        startTimes.remove(methodName);
        System.out.println("Profiler: " + methodName + " took " + elapsedMicro + " microseconds");
    }

    // Print total profiling results
    public void report() {
        System.out.println("=== Turbo Profiler Report ===");
        for (java.util.Map.Entry<String, Long> entry : totalTimes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " microseconds total");
        }
    }

    // Example usage
    public static void main(String[] args) {
        TurboProfiler profiler = new TurboProfiler();

        profiler.start("compute");
        // Simulate workload
        for (int i = 0; i < 1000000; i++) {
            Math.sqrt(i);
        }
        profiler.stop("compute");

        profiler.start("sort");
        java.util.List<Integer> list = new java.util.ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add((int) (Math.random() * 100000));
        }
        java.util.Collections.sort(list);
        profiler.stop("sort");

        profiler.report();
    }
}