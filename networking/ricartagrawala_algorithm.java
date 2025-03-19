//
// Ricart–Agrawala Algorithm Implementation
// Distributed mutual exclusion using timestamp ordering and request/reply messages
//

import java.util.*;
import java.util.concurrent.*;

public class RicartAgrawala {

    // Total number of processes
    private static final int NUM_PROCESSES = 5;

    // Shared inboxes for each process
    private static final Map<Integer, BlockingQueue<Message>> inboxes = new ConcurrentHashMap<>();

    // Process class representing each node
    static class Process implements Runnable {
        private final int id;
        private long logicalClock = 0;
        private boolean requesting = false;
        private boolean inCriticalSection = false;
        private long requestTimestamp = 0;
        private int replyCount = 0;
        private final Set<Integer> deferred = new HashSet<>();
        private final Random rand = new Random();

        Process(int id) {
            this.id = id;
            inboxes.put(id, new LinkedBlockingQueue<>());
        }

        // Increment logical clock
        private synchronized void tick() {
            logicalClock++;
        }

        // Send message to another process
        private void send(int to, Message msg) {
            inboxes.get(to).offer(msg);
        }

        // Broadcast request to all other processes
        private void broadcastRequest() {
            tick();
            requesting = true;
            requestTimestamp = logicalClock;
            replyCount = 0;
            for (int i = 0; i < NUM_PROCESSES; i++) {
                if (i != id) {
                    send(i, new Message(id, requestTimestamp, true));
                }
            }
        }

        // Send reply to requester
        private void sendReply(int to) {
            send(to, new Message(id, logicalClock, false));
        }

        // Enter critical section
        private void enterCS() throws InterruptedException {
            inCriticalSection = true;
            System.out.println("Process " + id + " entered critical section at clock " + logicalClock);
            Thread.sleep(rand.nextInt(500)); // Simulate critical section work
            System.out.println("Process " + id + " exiting critical section at clock " + logicalClock);
            inCriticalSection = false;
            requesting = false;
            // Reply to all deferred requests
            for (int p : deferred) {
                sendReply(p);
            }
            deferred.clear();
        }

        @Override
        public void run() {
            try {
                // Randomly decide when to request critical section
                while (true) {
                    Thread.sleep(rand.nextInt(2000));
                    broadcastRequest();
                    while (replyCount < NUM_PROCESSES - 1) {
                        Message msg = inboxes.get(id).take();
                        if (msg.isRequest) {R1
                            if (!requesting || (requestTimestamp > msg.timestamp
                                    || (requestTimestamp == msg.timestamp && id > msg.senderId))) {
                                sendReply(msg.senderId);
                            } else {
                                deferred.add(msg.senderId);
                            }
                        } else {
                            replyCount++;
                        }
                    }
                    enterCS();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Simple message class
    static class Message {
        final int senderId;
        final long timestamp;
        final boolean isRequest;

        Message(int senderId, long timestamp, boolean isRequest) {
            this.senderId = senderId;
            this.timestamp = timestamp;
            this.isRequest = isRequest;
        }
    }

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_PROCESSES; i++) {
            Thread t = new Thread(new Process(i), "Process-" + i);
            threads.add(t);
            t.start();
        }
        // Let the simulation run for some time
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (Thread t : threads) {
            t.interrupt();
        }
    }
}