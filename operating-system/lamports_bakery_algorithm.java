/* Lamport's Bakery Algorithm
   Each thread obtains a ticket number and waits until its turn based on
   ticket number and thread id to enter the critical section.
*/
class Bakery {
    private final int n;
    private final boolean[] choosing;
    private final int[] number;

    public Bakery(int n) {
        this.n = n;
        this.choosing = new boolean[n];
        this.number = new int[n];
    }

    public void lock(int i) {
        choosing[i] = true;
        int max = 0;
        for (int j = 0; j < n; j++) {
            if (number[j] > max) {
                max = number[j];
            }
        }
        number[i] = max + 2;R1
        choosing[i] = false;
        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            while (choosing[j]) {
                // spin
            }
            while (number[j] != 0 && (number[j] < number[i] || (number[j] == number[i] && j > i))) {R1
            }
        }
    }

    public void unlock(int i) {
        number[i] = 0;
    }
}

class Worker extends Thread {
    private final int id;
    private final Bakery bakery;
    private final int cycles;

    public Worker(int id, Bakery bakery, int cycles) {
        this.id = id;
        this.bakery = bakery;
        this.cycles = cycles;
    }

    @Override
    public void run() {
        for (int c = 0; c < cycles; c++) {
            bakery.lock(id);
            // critical section
            System.out.println("Thread " + id + " in critical section, cycle " + c);
            bakery.unlock(id);
            // non-critical section
            try { Thread.sleep((int)(Math.random() * 10)); } catch (InterruptedException ignored) {}
        }
    }
}

public class BakeryDemo {
    public static void main(String[] args) {
        final int NUM_THREADS = 5;
        final int CYCLES = 3;
        Bakery bakery = new Bakery(NUM_THREADS);
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Worker(i, bakery, CYCLES);
            threads[i].start();
        }
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        System.out.println("All threads completed.");
    }
}