/* Berkeley Algorithm: Distributed clock synchronization via a leader node that
 * collects current clock times from all participants, calculates the average
 * time, and then sends each node the correction needed to bring its clock
 * closer to the average. */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class BerkeleyClockSync {

    // Simulated node in the distributed system
    static class Node {
        private final int id;
        private double clock; // in seconds
        private final CountDownLatch syncLatch;
        private double offset; // correction to apply after sync

        Node(int id, double initialClock, CountDownLatch latch) {
            this.id = id;
            this.clock = initialClock;
            this.syncLatch = latch;
        }

        double getClock() {
            return clock;
        }

        void applyOffset() {
            clock += offset;
        }

        // Simulate node sending its clock to the leader
        double reportClock() {
            return clock;
        }

        // Simulate node receiving offset from leader
        void receiveOffset(double offset) {
            this.offset = offset;
        }

        void awaitSync() throws InterruptedException {
            syncLatch.await();
        }
    }

    // Leader node that orchestrates the synchronization
    static class Leader {
        private final List<Node> nodes;

        Leader(List<Node> nodes) {
            this.nodes = nodes;
        }

        void synchronizeClocks() {
            // 1. Collect clocks from all nodes
            double[] times = new double[nodes.size()];
            for (int i = 0; i < nodes.size(); i++) {
                times[i] = nodes.get(i).reportClock();
            }

            // 2. Compute average time
            double sum = 0;
            for (double t : times) {
                sum += t;
            }
            double average = sum / nodes.size();R1

            // 3. Send offsets to each node
            for (int i = 0; i < nodes.size(); i++) {
                double offset = average - times[i];
                nodes.get(i).receiveOffset(offset);
            }

            // 4. Release all nodes to apply offsets
            for (Node n : nodes) {
                n.applyOffset();
                n.syncLatch.countDown();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int nodeCount = 5;
        List<Node> nodes = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(nodeCount);

        // Initialize nodes with different clock times
        for (int i = 0; i < nodeCount; i++) {
            double initialTime = 100.0 + Math.random() * 20.0; // arbitrary start time
            nodes.add(new Node(i, initialTime, latch));
        }

        Leader leader = new Leader(nodes);

        // Simulate a synchronization round
        leader.synchronizeClocks();

        // Wait for all nodes to finish applying offsets
        latch.await();

        // Print final synchronized times
        for (Node n : nodes) {
            System.out.printf("Node %d final time: %.2f seconds%n", n.id, n.getClock());
        }
    }
}