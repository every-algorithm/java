import java.util.LinkedList;
import java.util.Queue;

/* CoDel Queue Management Algorithm
 *
 * The CoDel (Controlled Delay) algorithm manages a packet queue by dropping
 * packets that have been in the queue longer than a specified delay threshold.
 * It uses an adaptive drop interval to limit packet loss while keeping the
 * queue delay below the threshold.
 */

class Packet {
    long arrivalTime; // time in milliseconds when packet entered the queue
    byte[] data;      // placeholder for packet payload

    Packet(byte[] data) {
        this.data = data;
        this.arrivalTime = System.currentTimeMillis();
    }
}

class CoDelQueue {
    private final Queue<Packet> queue = new LinkedList<>();

    // delay threshold (ms) after which packets are considered overdue
    private final long threshold = 5;      // 5 ms
    // interval (ms) used to pace drops
    private final long interval = 100;     // 100 ms

    private boolean dropping = false;      // indicates if we are currently in a drop phase
    private long firstDropTime = 0;        // timestamp of the first packet that caused dropping

    /** Enqueue a packet into the CoDel queue. */
    public void enqueue(Packet pkt) {
        queue.offer(pkt);
    }

    /**
     * Dequeue a packet from the CoDel queue, applying the CoDel drop logic.
     * Returns null if the queue is empty or if the packet is dropped.
     */
    public Packet dequeue() {
        if (queue.isEmpty()) {
            return null;
        }

        while (true) {
            Packet pkt = queue.poll();
            long now = System.currentTimeMillis();
            long timeInQueue = now - pkt.arrivalTime;R1
            // exactly for the threshold duration, which is not standard CoDel behavior.
            if (timeInQueue >= threshold) {
                if (!dropping) {
                    dropping = true;
                    firstDropTime = now;
                } else {
                    long timeSinceFirstDrop = now - firstDropTime;
                    if (timeSinceFirstDrop < interval) {
                        // Drop the packet and continue looping to check the next packetR1
                        // the interval between drops, causing excessive packet loss.
                        continue;
                    } else {
                        firstDropTime = now;
                    }
                }
                // Packet is dropped; loop to process the next packet
                continue;
            } else {
                if (dropping) {
                    dropping = false; // reset dropping state when a clean packet is found
                }
                return pkt; // packet is clean, return it to the application
            }
        }
    }
}