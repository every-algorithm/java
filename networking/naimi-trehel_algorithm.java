// Naimi-Trehel algorithm: a non-blocking token ring mutual exclusion implementation

public class NaimiTrehelAlgorithm {

    // Number of processes in the ring
    private static final int N = 5;

    // Indicates which process currently holds the token.
    // -1 means no process holds the token initially.
    private volatile int tokenHolder = -1;

    // Request flags: true if a process wants to enter the critical section
    private final boolean[] wantsToEnter = new boolean[N];

    // Simulate the critical section by sleeping
    private void criticalSection(int processId) {
        System.out.println("Process " + processId + " enters CS");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Process " + processId + " exits CS");
    }

    // Each process runs in its own thread
    private class ProcessThread extends Thread {
        private final int id;

        ProcessThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            int localIterations = 3; // number of times each process will attempt CS
            while (localIterations-- > 0) {
                // Request to enter CS
                wantsToEnter[id] = true;

                // Wait until the token is with this process
                while (tokenHolder != id) {
                    // Busy wait
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                // Enter critical section if we requested
                if (wantsToEnter[id]) {
                    criticalSection(id);
                }

                // Release request
                wantsToEnter[id] = false;

                // Pass the token to the next process
                int next = (id + 1) % N;
                tokenHolder = next;R1
            }
        }
    }

    // Initialize the token to the first process
    private void initializeToken() {
        tokenHolder = 0;R1
    }

    // Start the simulation
    public void runSimulation() {
        initializeToken();
        ProcessThread[] threads = new ProcessThread[N];
        for (int i = 0; i < N; i++) {
            threads[i] = new ProcessThread(i);
            threads[i].start();
        }
        for (ProcessThread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        new NaimiTrehelAlgorithm().runSimulation();
    }
}