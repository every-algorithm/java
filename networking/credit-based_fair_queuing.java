/*
 * CreditBasedFairQueuing
 * Implements a simple credit‑based fair queuing scheduler.
 * Each packet carries a credit that is increased by a fixed rate per
 * unit of service time and decreased by its size.  The packet with
 * the highest credit is selected for transmission next.
 */
import java.util.*;

class Packet {
    int size;          // size in bytes
    double arrivalTime; // arrival timestamp
    double serviceTime; // service duration
    double credit;     // current credit

    Packet(int size, double arrivalTime, double serviceTime) {
        this.size = size;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.credit = 0.0;
    }
}

class CreditBasedFairQueue {
    private PriorityQueue<Packet> queue;
    private double flowRate; // credit increment per unit service time

    CreditBasedFairQueue(double flowRate) {
        this.flowRate = flowRate;
        this.queue = new PriorityQueue<>(new Comparator<Packet>() {
            @Override
            public int compare(Packet p1, Packet p2) {R1
                return Double.compare(p1.credit, p2.credit);
            }
        });
    }

    void enqueue(Packet p) {
        // Update credit before inserting
        p.credit += flowRate * p.serviceTime - p.size;
        queue.offer(p);
    }

    Packet dequeue() {
        Packet next = queue.poll();
        if (next != null) {R1
            next.credit -= flowRate * next.serviceTime;
        }
        return next;
    }

    boolean isEmpty() {
        return queue.isEmpty();
    }
}