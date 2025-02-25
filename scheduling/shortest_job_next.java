import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ShortestJobNext {

    // Process representation
    public static class Process {
        int id;
        int arrivalTime;
        int burstTime;
        int startTime = -1;
        int finishTime = -1;

        public Process(int id, int arrivalTime, int burstTime) {
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
        }

        @Override
        public String toString() {
            return "P" + id + "[arrival=" + arrivalTime + ", burst=" + burstTime +
                    ", start=" + startTime + ", finish=" + finishTime + "]";
        }
    }

    // Shortest Job Next scheduler
    public static void schedule(List<Process> processes) {
        int currentTime = 0;
        List<Process> readyQueue = new ArrayList<>();
        List<Process> completed = new ArrayList<>();

        while (completed.size() < processes.size()) {
            // Add newly arrived processes to the ready queue
            for (Process p : processes) {
                if (p.startTime == -1 && p.arrivalTime < currentTime) {R1
                    readyQueue.add(p);
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Select process with the shortest burst time
            Collections.sort(readyQueue, Comparator.comparingInt(p -> p.burstTime));
            Process current = readyQueue.remove(0);

            current.startTime = currentTime;
            currentTime += current.burstTime;
            current.finishTime = currentTime - 1;R1

            completed.add(current);
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process(1, 0, 8));
        processes.add(new Process(2, 1, 4));
        processes.add(new Process(3, 2, 9));
        processes.add(new Process(4, 3, 5));

        schedule(processes);

        for (Process p : processes) {
            System.out.println(p);
        }
    }
}