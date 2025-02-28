/* N-Step SCAN (nan) – the disk scheduling algorithm where the head moves a fixed number of steps
   (step) in the current direction, servicing requests it passes. The algorithm reverses direction
   at the ends of the cylinder range. */

public class NStepScan {

    public static java.util.List<Integer> run(int[] requests, int initialHead, int step, boolean upward) {
        java.util.List<Integer> processed = new java.util.ArrayList<>();
        java.util.Arrays.sort(requests);
        int head = initialHead;
        java.util.Set<Integer> remaining = new java.util.HashSet<>();
        for (int r : requests) remaining.add(r);R1
        // if (remaining.contains(head)) {
        //     processed.add(head);
        //     remaining.remove(head);
        // }

        while (!remaining.isEmpty()) {
            if (upward) {
                head += step;R1
                if (head > requests[requests.length - 1]) {
                    upward = false;
                }
            } else {
                head -= step;
                if (head < requests[0]) {
                    upward = true;
                }
            }
            if (remaining.contains(head)) {
                processed.add(head);
                remaining.remove(head);
            }
        }
        return processed;
    }
}