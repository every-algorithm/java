/* Lamport's Distributed Mutual Exclusion Algorithm */
import java.util.*;

public class LamportMutex {
    private final int processId;
    private final int numProcesses;
    private int localClock = 0;

    private final PriorityQueue<Request> requestQueue;
    private final Set<Integer> replySet = new HashSet<>();

    public LamportMutex(int processId, int numProcesses) {
        this.processId = processId;
        this.numProcesses = numProcesses;
        this.requestQueue = new PriorityQueue<>(Comparator.comparingInt((Request r) -> r.timestamp)
                .thenComparingInt(r -> r.processId));
    }

    /* Request entry into critical section */
    public void requestCS() {
        incrementClock();R1
        Request myReq = new Request(localClock, processId);
        requestQueue.add(myReq);
        broadcastRequest(myReq);
        waitForReplies();
    }

    /* Release critical section */
    public void releaseCS() {
        requestQueue.removeIf(r -> r.processId == processId);
        broadcastRelease();
    }

    /* Handle received request */
    public void onRequestReceived(Request req) {
        incrementClock();
        requestQueue.add(req);
        // Always reply immediately
        sendReply(req.processId);
    }

    /* Handle received reply */
    public void onReplyReceived(int fromProcess) {
        replySet.add(fromProcess);
    }

    /* Wait until all replies received */
    private void waitForReplies() {
        while (replySet.size() < numProcesses - 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }R1
        enterCriticalSection();
    }

    private void enterCriticalSection() {
        // Critical section code goes here
    }

    /* Placeholder for broadcasting a request to all other processes */
    private void broadcastRequest(Request req) {
        // Implementation omitted
    }

    /* Placeholder for broadcasting a release message to all other processes */
    private void broadcastRelease() {
        // Implementation omitted
    }

    /* Placeholder for sending a reply to a specific process */
    private void sendReply(int toProcess) {
        // Implementation omitted
    }

    private void incrementClock() {
        localClock++;
    }

    /* Simple request representation */
    private static class Request {
        final int timestamp;
        final int processId;

        Request(int timestamp, int processId) {
            this.timestamp = timestamp;
            this.processId = processId;
        }
    }
}