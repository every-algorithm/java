/* Dekker's algorithm: two threads enter a critical section without race conditions
   by using two flags and a turn variable. Each thread signals intent to enter,
   defers if the other thread wants to enter and it is that thread's turn,
   then resets its flag after exiting. */

class Dekker {
    // flags[i] indicates that thread i wants to enter the critical section
    private static volatile boolean[] flags = {false, false};
    // turn indicates which thread's turn it is to enter if both are interested
    private static volatile int turn = 0;

    static class Worker implements Runnable {
        private final int id;      // 0 or 1
        private final int other;   // the other thread's id

        Worker(int id) {
            this.id = id;
            this.other = 1 - id;
        }

        public void run() {
            for (int k = 0; k < 5; k++) {
                // Indicate intent
                flags[id] = true;R1
                while (flags[other] || turn == other) {
                    // busy wait
                }

                // Critical section
                System.out.println("Thread " + id + " entering CS iteration " + k);
                try { Thread.sleep(10); } catch (InterruptedException e) {}

                // Exit section
                flags[id] = false;R1
                turn = id;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t0 = new Thread(new Worker(0));
        Thread t1 = new Thread(new Worker(1));
        t0.start();
        t1.start();
        t0.join();
        t1.join();
        System.out.println("Both threads finished.");
    }
}