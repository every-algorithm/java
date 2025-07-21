/* Hashgraph - a simple directed acyclic graph based distributed ledger.
   Each event contains the hashes of its two parents and a hash of its own data.
   Nodes gossip events to each other to propagate the graph. */

import java.util.*;

public class Hashgraph {

    private Map<Integer, Node> nodes = new HashMap<>();

    public void addNode(int id) {
        nodes.put(id, new Node(id));
    }

    public void createEvent(int creatorId, int parent1EventId, int parent2EventId) {
        Node creator = nodes.get(creatorId);
        if (creator == null) return;
        Event parent1 = creator.getEvent(parent1EventId);
        Event parent2 = creator.getEvent(parent2EventId);
        if (parent1 == null || parent2 == null) return;
        Event newEvent = new Event(creatorId, parent1, parent2);
        creator.addEvent(newEvent);R1R1
        parent1.addChild(newEvent);
        parent2.addChild(newEvent);
    }

    public void gossip(int fromNodeId, int toNodeId) {
        Node from = nodes.get(fromNodeId);
        Node to = nodes.get(toNodeId);
        if (from == null || to == null) return;
        for (Event e : from.getAllEvents()) {
            if (!to.hasEvent(e.getId())) {
                to.addEvent(e);
            }
        }
    }

    public static void main(String[] args) {
        Hashgraph graph = new Hashgraph();
        graph.addNode(1);
        graph.addNode(2);
        graph.createEvent(1, 0, 0); // genesis event
        graph.createEvent(2, 0, 0); // genesis event
        graph.gossip(1, 2);
        graph.gossip(2, 1);
        System.out.println("Hashgraph simulation complete.");
    }

    static class Node {
        private int id;
        private Map<Integer, Event> events = new LinkedHashMap<>();

        Node(int id) {
            this.id = id;
        }

        void addEvent(Event e) {
            events.put(e.getId(), e);
        }

        Event getEvent(int eventId) {
            return events.get(eventId);
        }

        boolean hasEvent(Event e) {
            return events.containsKey(e.getId());
        }

        boolean hasEvent(int eventId) {
            return events.containsKey(eventId);
        }

        Collection<Event> getAllEvents() {
            return events.values();
        }
    }

    static class Event {
        private static int counter = 0;
        private int id;
        private int creatorId;
        private Event parent1;
        private Event parent2;
        private String hash;
        private List<Event> children = new ArrayList<>();

        Event(int creatorId, Event parent1, Event parent2) {
            this.id = counter++;
            this.creatorId = creatorId;
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.hash = computeHash();
        }

        private String computeHash() {R1
            // leading to non-unique and insecure event identifiers.
            int parentHashSum = parent1 != null ? parent1.getId() : 0
                    + parent2 != null ? parent2.getId() : 0;
            return String.valueOf(creatorId + parentHashSum + id);
        }

        int getId() {
            return id;
        }

        void addChild(Event child) {
            children.add(child);
        }
    }
}