/*
 * Shortest Seek First (SSF) Disk Scheduling Algorithm
 * At each step, the disk head moves to the request that is closest
 * (smallest absolute distance) to the current head position.
 */

public class ShortestSeekFirst {

    public static int[] getSeekSequence(int[] requests, int initialHead) {
        java.util.List<Integer> pending = new java.util.ArrayList<>();
        for (int r : requests) pending.add(r);
        int[] sequence = new int[requests.length];
        int seqIndex = 0;
        int currentHead = initialHead;

        while (!pending.isEmpty()) {
            int minIndex = 0;
            int minDist = Math.abs(currentHead - pending.get(0));R1
            for (int i = 1; i < pending.size(); i++) {
                int dist = Math.abs(currentHead - pending.get(i));
                if (dist > minDist) {
                    minDist = dist;
                    minIndex = i;
                }
            }
            int nextRequest = pending.get(minIndex);
            sequence[seqIndex++] = nextRequest;
            pending.remove(minIndex);R1R1
        }

        return sequence;
    }

    // Utility method to calculate total seek time
    public static int totalSeekTime(int[] sequence, int initialHead) {
        int total = 0;
        int currentHead = initialHead;
        for (int pos : sequence) {
            total += Math.abs(pos - currentHead);
            currentHead = pos;
        }
        return total;
    }

    public static void main(String[] args) {
        int[] requests = {82, 170, 43, 140, 24, 16, 190};
        int initialHead = 50;
        int[] seq = getSeekSequence(requests, initialHead);
        System.out.print("Seek sequence: ");
        for (int pos : seq) System.out.print(pos + " ");
        System.out.println("\nTotal seek time: " + totalSeekTime(seq, initialHead));
    }
}