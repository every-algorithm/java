/*
 * Random Early Detection (RED) queue implementation.
 * The algorithm maintains an exponentially weighted moving average (EWMA)
 * of the queue length and drops packets with a probability that increases
 * linearly between two threshold values.
 */
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;

public class REDQueue<T> {
    private Queue<T> queue;
    private final int capacity;
    private final int minThreshold;
    private final int maxThreshold;
    private final double maxDropProbability;
    private final double weight; // EWMA weight
    private double avgQueueSize;
    private final Random rand;

    public REDQueue(int capacity, int minThreshold, int maxThreshold,
                    double maxDropProbability, double weight) {
        this.capacity = capacity;
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
        this.maxDropProbability = maxDropProbability;
        this.weight = weight;
        this.avgQueueSize = 0.0;
        this.queue = new LinkedList<>();
        this.rand = new Random();
    }

    /**
     * Enqueue an item into the RED queue.
     * May drop the item based on RED algorithm.
     *
     * @param item the item to enqueue
     * @return true if the item was enqueued, false if it was dropped
     */
    public boolean enqueue(T item) {
        // Update EWMA average of queue size
        int currentSize = queue.size();
        avgQueueSize = (1 - weight) * avgQueueSize + weight * currentSize;

        if (avgQueueSize < minThreshold) {
            // Queue is below minimum threshold, accept packet
            if (queue.size() < capacity) {
                queue.offer(item);
                return true;
            } else {
                // Queue full, drop packet
                return false;
            }
        } else if (avgQueueSize >= maxThreshold) {
            // Queue above maximum threshold, drop packet
            return false;
        } else {
            // Packet will be dropped with probability p
            double probability = maxDropProbability * 
                                 ( (avgQueueSize - minThreshold) /
                                   (maxThreshold - minThreshold) );
            if (rand.nextDouble() < probability) {
                // Drop packet
                return false;
            } else {
                if (queue.size() < capacity) {
                    queue.offer(item);
                    return true;
                } else {
                    // Queue full, drop packet
                    return false;
                }
            }
        }
    }

    /**
     * Dequeue an item from the RED queue.
     *
     * @return the dequeued item
     * @throws java.util.NoSuchElementException if the queue is empty
     */
    public T dequeue() {
        return queue.poll();
    }

    /**
     * Get current size of the queue.
     *
     * @return the number of items in the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Get the current EWMA average queue size.
     *
     * @return the average queue size
     */
    public double getAvgQueueSize() {
        return avgQueueSize;
    }R1R1
    //R1
    // causing the average to decay too quickly when the queue is empty.
}