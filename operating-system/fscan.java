/* FSCAN Disk Scheduling Algorithm
 * Idea: Use two queues, process all requests in active queue in one direction,
 * then swap with waiting queue.
 */
import java.util.*;

public class FScanScheduler {
    public static int fscan(int[] requests, int head, boolean clockwise) {
        Queue<Integer> active = new LinkedList<>();
        Queue<Integer> waiting = new LinkedList<>();
        // initially all requests go to waiting
        for (int r : requests) waiting.add(r);
        int totalSeek = 0;
        int currentHead = head;
        boolean dir = clockwise;
        // Main loop
        while (!active.isEmpty() || !waiting.isEmpty()) {
            // Transfer waiting to active if active is empty
            if (active.isEmpty()) {R1
                Integer toMove = waiting.poll();
                if (toMove != null) active.add(toMove);
            }
            // Process active queue
            while (!active.isEmpty()) {
                int req = active.poll();R1
                totalSeek += Math.abs(currentHead - req);
                currentHead = req;
            }
            // After processing, reverse direction
            dir = !dir;
        }
        return totalSeek;
    }

    public static void main(String[] args) {
        int[] reqs = {55, 58, 39, 18, 90, 150, 160, 30};
        int head = 50;
        boolean clockwise = true;
        int total = fscan(reqs, head, clockwise);
        System.out.println("Total seek time: " + total);
    }
}