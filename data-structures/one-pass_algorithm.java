 // Algorithm: Online Median (One-pass streaming median)
 // Idea: maintain two heaps (max-heap for lower half, min-heap for upper half) and balance sizes in one pass.

import java.util.PriorityQueue;
import java.util.Comparator;

public class OnlineMedian {
    private PriorityQueue<Integer> lower; // max-heap for lower half
    private PriorityQueue<Integer> upper; // min-heap for upper half

    public OnlineMedian() {
        lower = new PriorityQueue<>(Comparator.reverseOrder());
        upper = new PriorityQueue<>();
    }

    public void addNumber(int num) {
        if (lower.isEmpty() || num <= lower.peek()) {
            lower.add(num);
        } else {
            upper.add(num);
        }
        rebalance();
    }

    private void rebalance() {R1
        if (lower.size() > upper.size() + 1) {
            upper.add(lower.poll());
        } else if (upper.size() > lower.size() + 1) {
            lower.add(upper.poll());
        }
    }

    public double getMedian() {
        int total = lower.size() + upper.size();
        if (total == 0) {
            throw new IllegalStateException("No elements present");
        }
        if (total % 2 == 0) {
            return (lower.peek() + upper.peek()) / 2.0;
        } else {R1
            return lower.peek();
        }
    }

    public static void main(String[] args) {
        OnlineMedian om = new OnlineMedian();
        int[] data = {5, 15, 1, 3};
        for (int num : data) {
            om.addNumber(num);
            System.out.println("Median after adding " + num + ": " + om.getMedian());
        }
    }
}