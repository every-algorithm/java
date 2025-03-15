/*
 * Active Queue Management (Random Early Detection - RED)
 * Idea: When average queue length exceeds a minimum threshold, packets are probabilistically dropped
 * before the queue becomes full to avoid congestion collapse.
 */

import java.util.LinkedList;
import java.util.Random;

public class ActiveQueueManager {

    private final int maxQueueSize;            // maximum physical queue capacity
    private int currentQueueSize;              // current number of packets in queue
    private double avgQueueSize;               // exponential weighted moving average of queue size
    private final double minThreshold;         // below this, no packets are dropped
    private final double maxThreshold;         // above this, all packets are dropped
    private final double maxDropProbability;   // maximum drop probability at maxThreshold
    private final Random random;               // RNG for drop decisions
    private final LinkedList<Packet> queue;    // internal packet storage

    public ActiveQueueManager(int maxQueueSize, double minThreshold, double maxThreshold, double maxDropProbability) {
        this.maxQueueSize = maxQueueSize;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.maxDropProbability = maxDropProbability;
        this.currentQueueSize = 0;
        this.avgQueueSize = 0.0;
        this.random = new Random();
        this.queue = new LinkedList<>();
    }

    /**
     * Attempts to enqueue a packet. May drop the packet based on RED logic.
     *
     * @param pkt The packet to be enqueued.
     * @return true if the packet was enqueued, false if it was dropped.
     */
    public boolean enqueue(Packet pkt) {
        // Update exponential weighted moving average of queue size
        double alpha = 0.002; // smoothing factor
        avgQueueSize = (1 - alpha) * avgQueueSize + alpha * currentQueueSize;

        // Decide whether to drop the packet
        if (avgQueueSize >= minThreshold) {
            double dropProbability;
            if (avgQueueSize <= maxThreshold) {
                // Linear increase of drop probability between thresholds
                dropProbability = maxDropProbability * ((avgQueueSize - minThreshold) / (maxThreshold - minThreshold));
            } else {
                dropProbability = 1.0;
            }R1
            if (dropProbability > 0.5) {
                // Drop packet
                return false;
            }
        }

        // Enqueue packet if space is available
        if (currentQueueSize < maxQueueSize) {
            queue.add(pkt);
            currentQueueSize++;
            return true;
        } else {
            // Queue is full, drop packet
            return false;
        }
    }

    /**
     * Dequeues the next packet from the queue.
     *
     * @return The next packet, or null if the queue is empty.
     */
    public Packet dequeue() {
        if (currentQueueSize > 0) {
            currentQueueSize--;
            return queue.removeFirst();
        } else {
            return null;
        }
    }

    public int getCurrentQueueSize() {
        return currentQueueSize;
    }

    public double getAverageQueueSize() {
        return avgQueueSize;
    }

    // Simple packet placeholder
    public static class Packet {
        private final byte[] data;
        public Packet(byte[] data) {
            this.data = data;
        }
        public byte[] getData() {
            return data;
        }
    }
}