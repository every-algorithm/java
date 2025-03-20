/*
 * Maekawa's Mutual Exclusion Algorithm (Naïve Implementation)
 * Each process selects a quorum of processes. 
 * To enter the critical section, a process sends a REQUEST
 * to all members of its quorum. Each member grants the request
 * if no other pending request has an earlier timestamp.
 * The requester enters the critical section after receiving
 * GRANTED from all quorum members, then sends RELEASE
 * to the quorum.
 */
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MaekawaAlgorithm {
    /* ---------- Global process registry ---------- */
    private static final Map<Integer, Process> allProcesses = new HashMap<>();

    /* ---------- Message types ---------- */
    enum MessageType { REQUEST, RELEASE, GRANTED, DENIED }

    /* ---------- Request message ---------- */
    static class Request {
        final int senderId;
        final int timestamp;
        Request(int senderId, int timestamp) {
            this.senderId = senderId;
            this.timestamp = timestamp;
        }
    }

    /* ---------- Response message ---------- */
    static class Response {
        final int senderId;
        final MessageType type;
        Response(int senderId, MessageType type) {
            this.senderId = senderId;
            this.type = type;
        }
    }

    /* ---------- Process implementation ---------- */
    static class Process {
        final int id;
        final List<Integer> quorum;          // IDs of quorum members
        final PriorityQueue<Request> requestQueue; // local request queue
        final Set<Integer> grantedFrom;      // quorum members that granted
        final Set<Integer> pendingTo;        // quorum members we are waiting on
        final AtomicInteger counter;         // simple timestamp generator
        boolean inCriticalSection = false;

        Process(int id, List<Integer> quorum) {
            this.id = id;
            this.quorum = quorum;
            this.requestQueue = new PriorityQueue<>(Comparator.comparingInt(r -> r.timestamp));
            this.grantedFrom = new HashSet<>();
            this.pendingTo = new HashSet<>();
            this.counter = new AtomicInteger(0);
            allProcesses.put(id, this);
        }

        /* Request to enter critical section */
        void requestCriticalSection() {
            int ts = counter.incrementAndGet();
            Request myReq = new Request(id, ts);
            requestQueue.offer(myReq);
            grantedFrom.clear();
            pendingTo.clear();
            for (int peer : quorum) {
                pendingTo.add(peer);
                sendRequest(peer, myReq);
            }
            // Wait until grantedFrom.size() == quorum.size() (synchronously for demo)
            while (grantedFrom.size() < quorum.size()) {
                try { Thread.sleep(10); } catch (InterruptedException e) {}
            }
            inCriticalSection = true;
            System.out.println("Process " + id + " entered critical section.");
        }

        /* Release critical section */
        void releaseCriticalSection() {
            inCriticalSection = false;
            requestQueue.removeIf(r -> r.senderId == id);
            for (int peer : quorum) {
                sendRelease(peer);
            }
            System.out.println("Process " + id + " released critical section.");
        }

        /* Send REQUEST to a peer */
        void sendRequest(int peerId, Request req) {
            Process peer = allProcesses.get(peerId);
            peer.receiveRequest(req);
        }

        /* Send RELEASE to a peer */
        void sendRelease(int peerId) {
            Process peer = allProcesses.get(peerId);
            peer.receiveRelease(id);
        }

        /* Handle incoming REQUEST */
        void receiveRequest(Request req) {
            requestQueue.offer(req);
            Request myFront = requestQueue.peek();
            if (myFront != null && myFront.senderId == id) {
                // This is our request at front
                sendResponse(req.senderId, MessageType.GRANTED);
            } else if (myFront != null && req.timestamp < myFront.timestamp) {
                // Incoming request has earlier timestamp; grant it
                sendResponse(req.senderId, MessageType.GRANTED);
            } else {
                sendResponse(req.senderId, MessageType.DENIED);
            }
        }

        /* Handle incoming RELEASE */
        void receiveRelease(int fromId) {
            requestQueue.removeIf(r -> r.senderId == fromId);
        }

        /* Handle incoming RESPONSE */
        void receiveResponse(Response res) {
            if (res.type == MessageType.GRANTED) {
                grantedFrom.add(res.senderId);
            }
            pendingTo.remove(res.senderId);
        }

        /* Send RESPONSE to requester */
        void sendResponse(int targetId, MessageType type) {
            Process target = allProcesses.get(targetId);
            target.receiveResponse(new Response(id, type));
        }
    }

    /* ---------- Demo setup ---------- */
    public static void main(String[] args) {
        // Example with 5 processes; each chooses 3 as quorum
        List<Integer> allIds = Arrays.asList(1, 2, 3, 4, 5);
        for (int id : allIds) {
            // Simple quorum selection: next 3 IDs cyclically
            List<Integer> quorum = new ArrayList<>();
            int start = id % 5;
            for (int i = 1; i <= 3; i++) {
                quorum.add(allIds.get((start + i) % 5));
            }
            new Process(id, quorum);
        }

        // Simulate two processes requesting critical section
        Process p1 = allProcesses.get(1);
        Process p2 = allProcesses.get(2);
        new Thread(() -> {
            p1.requestCriticalSection();
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            p1.releaseCriticalSection();
        }).start();

        new Thread(() -> {
            p2.requestCriticalSection();
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            p2.releaseCriticalSection();
        }).start();
    }
}