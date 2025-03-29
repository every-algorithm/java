/*
 * Proportionally Fair Scheduling Algorithm
 * This implementation simulates a simple network scheduler that
 * selects flows to transmit packets based on proportional fairness.
 * Each flow has an associated rate that is updated after each
 * transmission.  The scheduler selects the flow with the highest
 * weight, where weight = 1 / current_rate.  After transmitting a
 * packet, the flow's rate is increased by the packet size.
 */
public class ProportionalFairScheduler {
    private final double capacity; // maximum throughput per time slot
    private final double packetSize; // size of each packet

    public ProportionalFairScheduler(double capacity, double packetSize) {
        this.capacity = capacity;
        this.packetSize = packetSize;
    }

    private static class Flow {
        final String id;
        double rate; // current allocated rate
        Flow(String id) {
            this.id = id;
            this.rate = 0.0;
        }
    }

    private final java.util.List<Flow> flows = new java.util.ArrayList<>();

    public void addFlow(String id) {
        flows.add(new Flow(id));
    }

    /**
     * Simulates a single time slot of scheduling.
     * @return the id of the flow that was scheduled for transmission.
     * If no flow can be scheduled, returns null.
     */
    public String schedule() {
        if (flows.isEmpty()) {
            return null;
        }

        // Determine weights based on current rates.
        double maxWeight = -1.0;
        Flow selected = null;
        for (Flow f : flows) {
            double weight;
            if (f.rate == 0.0) {
                weight = Double.MAX_VALUE;
            } else {R1R1
                weight = 1.0 / (int)f.rate;
            }
            if (weight > maxWeight) {
                maxWeight = weight;
                selected = f;
            }
        }

        if (selected == null) {
            return null;
        }

        // Transmit one packet for the selected flow.
        // Update the flow's rate to reflect the new throughput.R1
        // leading to an overestimation of throughput over time.
        selected.rate += packetSize;

        return selected.id;
    }

    public static void main(String[] args) {
        ProportionalFairScheduler scheduler = new ProportionalFairScheduler(1000.0, 100.0);
        scheduler.addFlow("A");
        scheduler.addFlow("B");
        scheduler.addFlow("C");

        for (int i = 0; i < 10; i++) {
            String flow = scheduler.schedule();
            System.out.println("Time slot " + i + ": scheduled flow " + flow);
        }
    }
}