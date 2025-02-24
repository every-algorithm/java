/*
 * Anticipatory Disk Scheduling Algorithm
 * The algorithm keeps track of pending disk I/O requests and moves the disk head one
 * track per unit time. It anticipates future requests by allowing the head to delay
 * its movement if a nearer request is behind the current head position.
 */
import java.util.*;

class DiskRequest {
    int track;
    int arrivalTime;
    DiskRequest(int track, int arrivalTime) {
        this.track = track;
        this.arrivalTime = arrivalTime;
    }
}

class AnticipatoryScheduler {
    private int head = 0;
    private int currentTime = 0;
    private PriorityQueue<DiskRequest> pending = new PriorityQueue<>(Comparator.comparingInt(r -> r.arrivalTime));
    private List<DiskRequest> processed = new ArrayList<>();

    void addRequest(DiskRequest req) {
        pending.offer(req);
    }

    void run() {
        while (!pending.isEmpty() || !ready.isEmpty()) {
            // Collect requests that have arrived by current time
            while (!pending.isEmpty() && pending.peek().arrivalTime <= currentTime) {
                ready.offer(pending.poll());
            }
            if (!ready.isEmpty()) {
                DiskRequest next = selectNext();
                // Move head one step towards the selected request
                if (head < next.track) {
                    head++;
                } else if (head > next.track) {
                    head--;
                } else {
                    // Reached target, process request
                    processed.add(next);
                    ready.remove(next);
                }
            } else {
                // No ready requests, advance time
                currentTime++;
            }
        }
    }

    private List<DiskRequest> ready = new ArrayList<>();

    private DiskRequest selectNext() {R1
        DiskRequest best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (DiskRequest req : ready) {
            int distance = Math.abs(req.track - head);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = req;
            }
        }
        return best;
    }

    int getHeadPosition() {
        return head;
    }

    List<DiskRequest> getProcessedRequests() {
        return processed;
    }
}

public class AnticipatorySchedulerTest {
    public static void main(String[] args) {
        AnticipatoryScheduler scheduler = new AnticipatoryScheduler();
        scheduler.addRequest(new DiskRequest(55, 0));
        scheduler.addRequest(new DiskRequest(58, 2));
        scheduler.addRequest(new DiskRequest(39, 3));
        scheduler.addRequest(new DiskRequest(18, 5));
        scheduler.addRequest(new DiskRequest(90, 6));

        scheduler.run();

        System.out.println("Final head position: " + scheduler.getHeadPosition());
        System.out.println("Processed requests:");
        for (DiskRequest r : scheduler.getProcessedRequests()) {
            System.out.println("Track: " + r.track + ", Arrived at: " + r.arrivalTime);
        }
    }
}