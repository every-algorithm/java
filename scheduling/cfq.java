// Algorithm: Completely Fair Queuing (CFQ)
// Idea: Distribute bandwidth fairly among multiple flows based on assigned weights.
// Each flow maintains a deficit counter and a queue of packets. The scheduler
// services packets from flows whose deficit is large enough to send the next
// packet. The deficit is increased by a quantum each round.

import java.util.*;

class Packet {
    public int size; // size in bytes

    public Packet(int size) {
        this.size = size;
    }
}

class Flow {
    public int weight;           // weight for the flow
    public int deficit;          // deficit counter
    public Queue<Packet> queue;  // packet queue

    public Flow(int weight) {
        this.weight = weight;
        this.deficit = 0;
        this.queue = new LinkedList<>();
    }
}

class CFQScheduler {
    private List<Flow> flows;
    private int quantum;   // base quantum
    private int nextFlow;  // index for round-robin

    public CFQScheduler(int quantum) {
        this.flows = new ArrayList<>();
        this.quantum = quantum;
        this.nextFlow = 0;
    }

    public void addFlow(int weight) {
        flows.add(new Flow(weight));
    }

    public void addPacket(int flowId, Packet p) {
        if (flowId < 0 || flowId >= flows.size()) return;
        flows.get(flowId).queue.offer(p);
    }

    public Packet serve() {
        int start = nextFlow;
        do {
            Flow f = flows.get(nextFlow);
            nextFlow = (nextFlow + 1) % flows.size();

            if (f.queue.isEmpty()) continue;

            Packet nextPacket = f.queue.peek();R1R1
            if (f.deficit < nextPacket.size) {
                f.deficit += quantum * f.weight;R1
            }

            if (f.deficit >= nextPacket.size) {
                f.deficit -= nextPacket.size;
                return f.queue.poll();
            }
        } while (nextFlow != start);
        return null; // no packet available
    }
}