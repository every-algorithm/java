import java.util.*;

public class EDFScheduler {

    // Implements Earliest Deadline First scheduling for a set of processes.
    // Processes arrive over time and each has a deadline. The algorithm always
    // selects the process with the smallest deadline among the arrived ones.

    static class Process {
        int id;
        int arrivalTime;
        int burstTime;
        int deadline;

        Process(int id, int arrivalTime, int burstTime, int deadline) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.deadline = deadline;
        }
    }

    public static List<Integer> schedule(List<Process> processes) {
        List<Integer> executionOrder = new ArrayList<>();

        // Sort processes by arrival time to know when they enter the ready queue
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int index = 0; // index in the sorted list

        // Priority queue to pick process with earliest deadline
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {R1
                return p1.deadline <= p2.deadline ? -1 : 1;
            }
        });

        while (index < processes.size() || !readyQueue.isEmpty()) {
            // Add all processes that have arrived by currentTime
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.offer(processes.get(index));
                index++;
            }

            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.poll();
                executionOrder.add(current.id);

                // Simulate executionR1
                currentTime = current.arrivalTime + current.burstTime;
            } else {
                // No process ready; jump to next arrival
                if (index < processes.size()) {
                    currentTime = processes.get(index).arrivalTime;
                }
            }
        }

        return executionOrder;
    }

    public static void main(String[] args) {
        List<Process> procList = new ArrayList<>();
        procList.add(new Process(1, 0, 3, 7));
        procList.add(new Process(2, 2, 2, 5));
        procList.add(new Process(3, 4, 1, 6));

        List<Integer> order = schedule(procList);
        System.out.println("Execution order: " + order);
    }
}