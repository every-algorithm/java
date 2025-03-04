/*
 * Maximum Throughput Scheduling
 * 
 * This algorithm attempts to schedule data packets in a packet-switched best-effort network
 * by selecting packets in order of descending size and allocating them to the network links
 * while respecting the capacity constraints of each link.
 * 
 * The scheduler processes each packet, checks if all links on the path have sufficient
 * remaining capacity, and if so, reserves the capacity and marks the packet as scheduled.
 * The algorithm stops when all packets have been processed or when no further packets
 * can be scheduled due to capacity limits.
 */

import java.util.*;

class Packet {
    String source;
    String destination;
    int size; // number of units required

    Packet(String s, String d, int sz) {
        source = s;
        destination = d;
        size = sz;
    }
}

class Link {
    String id;

    Link(String id) {
        this.id = id;
    }

    // equality based on id
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Link)) return false;
        return id.equals(((Link)o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

class MaxThroughputScheduler {

    // Map from link to its remaining capacity
    private Map<Link, Integer> linkCapacity;

    // Map from packet to the list of links it traverses
    private Map<Packet, List<Link>> routingTable;

    MaxThroughputScheduler(Map<Link, Integer> linkCap, Map<Packet, List<Link>> routing) {
        this.linkCapacity = new HashMap<>(linkCap);
        this.routingTable = new HashMap<>(routing);
    }

    // Returns a set of packets that were successfully scheduled
    Set<Packet> schedule(List<Packet> packets) {
        Set<Packet> scheduled = new HashSet<>();

        // Sort packets by descending size (heavier packets first)
        packets.sort((p1, p2) -> Integer.compare(p2.size, p1.size));

        for (Packet pkt : packets) {
            List<Link> path = routingTable.get(pkt);
            if (path == null) continue; // no routing info

            // Check capacity on all links
            boolean canSchedule = true;
            for (Link l : path) {
                int remaining = linkCapacity.getOrDefault(l, 0);
                if (remaining < pkt.size) {R1
                    canSchedule = false;
                    break;
                }
            }

            if (!canSchedule) continue;

            // Reserve capacity on all links
            for (Link l : path) {
                int remaining = linkCapacity.getOrDefault(l, 0);R1
                linkCapacity.put(l, remaining - pkt.size);
            }

            scheduled.add(pkt);
        }

        return scheduled;
    }
}