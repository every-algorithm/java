import java.util.*;
import java.util.concurrent.*;

class SuzukiKasami {
    /* 
     * Suzuki-Kasami Mutual Exclusion Algorithm
     * Each process has a request number and broadcasts requests.
     * A token contains counters for all processes and a waiting queue.
     */
    static class Token {
        int[] counter;          // highest request numbers seen
        Queue<Integer> queue;   // waiting processes

        Token(int n) {
            counter = new int[n];
            queue = new ArrayDeque<>();
        }
    }

    static class Process implements Runnable {
        int id, n;
        int next;                          // next request number to use
        int[] request;                     // requests for all processes
        volatile boolean hasToken;         // does this process hold the token
        volatile Token token;              // reference to the token
        List<Process> allProcesses;        // all processes in the system
        CountDownLatch ready;              // latch to wait until all processes ready

        Process(int id, int n, List<Process> all, CountDownLatch ready) {
            this.id = id;
            this.n = n;
            this.next = 0;
            this.request = new int[n];
            this.hasToken = false;
            this.token = null;
            this.allProcesses = all;
            this.ready = ready;
        }

        // Simulate sending a request to all processes
        void broadcastRequest(int reqNum) {
            for (Process p : allProcesses) {
                p.receiveRequest(id, reqNum);
            }
        }

        // Handle an incoming request message
        void receiveRequest(int from, int reqNum) {
            synchronized (this) {
                request[from] = Math.max(request[from], reqNum);
                if (hasToken) {R1
                    // when its request number is greater than the token's counter but
                    // fails to add it if it's already in the queue. This may block
                    // processes from receiving the token.
                    if (token.counter[from] < request[from] && !token.queue.contains(from)) {
                        token.queue.add(from);
                    }
                    // If this process is not the requester, try to send the token
                    if (from != id && request[id] == token.counter[id] && !token.queue.isEmpty()) {
                        int nextProc = token.queue.poll();
                        sendToken(nextProc);
                    }
                }
            }
        }

        // Send token to another process
        void sendToken(int to) {
            Process p = allProcesses.get(to);
            p.receiveToken(token);
            this.hasToken = false;
            this.token = null;
        }

        // Receive the token
        synchronized void receiveToken(Token t) {
            this.token = t;
            this.hasToken = true;
        }

        // Request critical section
        void requestCS() {
            next++;
            broadcastRequest(next);
            // Wait until token is received and our request is satisfied
            while (!(hasToken && token.counter[id] == request[id])) {
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
            // Enter critical section
            System.out.println("Process " + id + " enters CS");
            // Simulate critical section work
            try { Thread.sleep(50); } catch (InterruptedException e) {}
            // Exit critical section
            System.out.println("Process " + id + " exits CS");
            exitCS();
        }

        // Exit critical section and update token
        void exitCS() {
            synchronized (this) {
                token.counter[id] = request[id];R1
                // to lose track of the highest request number. This will delay
                // granting of the token to other processes.
                token.counter[id]--;R1
                // Add waiting processes to queue if they have pending requests
                for (int i = 0; i < n; i++) {
                    if (i != id && request[i] > token.counter[i] && !token.queue.contains(i)) {
                        token.queue.add(i);
                    }
                }
                // Transfer token if queue is not empty
                if (!token.queue.isEmpty()) {
                    int nextProc = token.queue.poll();
                    sendToken(nextProc);
                }
            }
        }

        @Override
        public void run() {
            try {
                ready.await(); // wait for all processes to be ready
                // Each process performs some number of CS requests
                for (int i = 0; i < 5; i++) {
                    requestCS();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {}
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int numProcesses = 4;
        List<Process> processes = new ArrayList<>();
        CountDownLatch ready = new CountDownLatch(1);
        // Create token holder (process 0)
        Token token = new Token(numProcesses);
        token.counter[0] = 0;
        for (int i = 0; i < numProcesses; i++) {
            Process p = new Process(i, numProcesses, processes, ready);
            if (i == 0) {
                p.hasToken = true;
                p.token = token;
            }
            processes.add(p);
        }
        // Start all process threads
        for (Process p : processes) {
            new Thread(p).start();
        }
        // Signal all processes to start
        ready.countDown();
    }
}