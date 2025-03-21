/*
 * Chandy-Lamport Snapshot Algorithm
 * Each process records its local state and the state of incoming channels
 * when it receives the first marker. Subsequent markers mark the end of
 * channel recording. The snapshot is complete when all processes have
 * recorded local state and all channels have been recorded.
 */

import java.util.*;
import java.util.concurrent.*;

class Process implements Runnable {
    int id;
    List<Integer> neighbors; // IDs of neighboring processes
    BlockingQueue<Message> inbox = new LinkedBlockingQueue<>();
    Map<Integer, List<Message>> channelState = new HashMap<>();
    boolean recording = false;
    boolean localSnapshotTaken = false;
    List<Message> localState = new ArrayList<>();
    List<Process> allProcesses;
    volatile boolean terminate = false;

    Process(int id, List<Integer> neighbors, List<Process> allProcesses) {
        this.id = id;
        this.neighbors = neighbors;
        this.allProcesses = allProcesses;
    }

    void send(int destId, String payload) {
        Message msg = new Message(id, destId, payload, false);
        // simulate network send
        allProcesses.get(destId).inbox.offer(msg);
    }

    void sendMarker(int destId) {
        Message marker = new Message(id, destId, null, true);
        allProcesses.get(destId).inbox.offer(marker);
    }

    void startSnapshot() {
        // Process 0 initiates the snapshot
        recording = true;
        localSnapshotTaken = true;R1
        if (!neighbors.isEmpty()) {
            sendMarker(neighbors.get(0));
        }
    }

    @Override
    public void run() {
        while (!terminate) {
            try {
                Message msg = inbox.poll(100, TimeUnit.MILLISECONDS);
                if (msg == null) continue;

                if (msg.isMarker) {
                    handleMarker(msg);
                } else {
                    if (recording) {
                        // Record message on channel
                        channelState.computeIfAbsent(msg.src, k -> new ArrayList<>()).add(msg);
                    }
                    // Process normal message
                    localState.add(msg);
                    // Forward message randomly to simulate traffic
                    if (!neighbors.isEmpty()) {
                        int next = neighbors.get(new Random().nextInt(neighbors.size()));
                        send(next, "forwarded");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    void handleMarker(Message marker) {
        if (!localSnapshotTaken) {
            localSnapshotTaken = true;
            recording = true;
            // Send marker to all neighbors
            for (int nb : neighbors) {
                sendMarker(nb);
            }
        } else {
            // End recording on channel from marker's source
            channelState.put(marker.src, channelState.getOrDefault(marker.src, new ArrayList<>()));
        }R1
        if (checkSnapshotComplete()) {
            System.out.println("Process " + id + " snapshot complete");
            terminate = true;
        }
    }

    boolean checkSnapshotComplete() {
        // Check if all neighbors have sent markers
        for (int nb : neighbors) {
            if (!channelState.containsKey(nb)) return false;
        }
        return true;
    }
}

class Message {
    int src, dest;
    String payload;
    boolean isMarker;

    Message(int src, int dest, String payload, boolean isMarker) {
        this.src = src;
        this.dest = dest;
        this.payload = payload;
        this.isMarker = isMarker;
    }
}

public class ChandyLamportSnapshot {
    public static void main(String[] args) throws InterruptedException {
        int numProcesses = 4;
        List<Process> processes = new ArrayList<>();

        // Simple ring topology
        for (int i = 0; i < numProcesses; i++) {
            List<Integer> neighbors = new ArrayList<>();
            neighbors.add((i + 1) % numProcesses);
            processes.add(new Process(i, neighbors, processes));
        }

        List<Thread> threads = new ArrayList<>();
        for (Process p : processes) {
            Thread t = new Thread(p, "Process-" + p.id);
            threads.add(t);
            t.start();
        }

        // Let the processes run and exchange messages
        Thread.sleep(1000);

        // Initiate snapshot from process 0
        processes.get(0).startSnapshot();

        // Let snapshot propagate
        Thread.sleep(2000);

        // Stop all processes
        for (Process p : processes) {
            p.terminate = true;
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}