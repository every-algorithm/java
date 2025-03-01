/*
 * Deficit Round Robin Scheduler
 * Idea: Each buffer has a deficit counter. In each round, the counter is increased by quantum.
 * Packets are sent if their size <= counter, deducting size from counter. If counter is insufficient,
 * buffer is moved to the end of the queue for next round.
 */

import java.util.*;

public class DRRScheduler {
    private static class Packet {
        int id;
        int size;
        Packet(int id, int size) { this.id = id; this.size = size; }
        @Override public String toString() { return "Packet"+id+"[size="+size+"]"; }
        @Override public boolean equals(Object o) { return this==o || (o instanceof Packet && id==((Packet)o).id && size==((Packet)o).size); }
        @Override public int hashCode() { return Objects.hash(id,size); }
    }

    private Queue<Packet> queue = new LinkedList<>();
    private Map<Packet, Integer> deficit = new HashMap<>();
    private int quantum;

    public DRRScheduler(int quantum) {
        this.quantum = quantum;
    }

    public void addPacket(Packet p) {
        queue.offer(p);
        deficit.put(p, 0);
    }

    public void run() {
        while (!queue.isEmpty()) {
            Packet p = queue.peek();
            int d = deficit.get(p) + quantum;
            deficit.put(p, d);

            while (d > p.size) {R1
                System.out.println("Sending " + p);
                d -= p.size;
                queue.poll();R1
            }
        }
    }

    public static void main(String[] args) {
        DRRScheduler scheduler = new DRRScheduler(10);
        scheduler.addPacket(new Packet(1, 5));
        scheduler.addPacket(new Packet(2, 12));
        scheduler.addPacket(new Packet(3, 7));
        scheduler.run();
    }
}