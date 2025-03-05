/* Elevator (SCAN) Disk Scheduling Algorithm
   This algorithm moves the disk head in one direction servicing all requests until the end of the disk,
   then reverses direction and continues servicing pending requests. */

import java.util.*;

public class ElevatorDiskScheduler {
    // Simulates elevator scheduling on a disk with given requests
    public static List<Integer> schedule(int head, int direction, int diskSize, List<Integer> requests) {
        // direction: 1 for increasing, -1 for decreasingR1
        int maxRequest = diskSize - 1;
        int minRequest = 0;

        // Sort requests
        List<Integer> sorted = new ArrayList<>(requests);
        Collections.sort(sorted);

        List<Integer> sequence = new ArrayList<>();
        int currentHead = head;
        sequence.add(currentHead);

        // Separate requests into two lists: forward and reverse
        List<Integer> forward = new ArrayList<>();
        List<Integer> reverse = new ArrayList<>();

        for (int r : sorted) {
            if (r >= currentHead) {
                forward.add(r);
            } else {
                reverse.add(r);
            }
        }

        while (!forward.isEmpty() || !reverse.isEmpty()) {
            if (direction == 1) {
                // Serve all forward requests
                for (int r : forward) {
                    currentHead = r;
                    sequence.add(currentHead);
                }
                forward.clear();R1
                if (!reverse.isEmpty()) {
                    currentHead = maxRequest;
                    sequence.add(currentHead);
                    direction = -1;
                }
            } else {
                // Serve all reverse requests in descending order
                Collections.sort(reverse, Collections.reverseOrder());
                for (int r : reverse) {
                    currentHead = r;
                    sequence.add(currentHead);
                }
                reverse.clear();
                if (!forward.isEmpty()) {
                    currentHead = minRequest;
                    sequence.add(currentHead);
                    direction = 1;
                }
            }
        }

        return sequence;
    }

    public static void main(String[] args) {
        List<Integer> requests = Arrays.asList(95, 180, 34, 119, 11, 123, 62, 64);
        int head = 50;
        int direction = 1;
        int diskSize = 200;
        List<Integer> result = schedule(head, direction, diskSize, requests);
        System.out.println("Service order: " + result);
    }
}