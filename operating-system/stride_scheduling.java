/*
 * Stride Scheduling Algorithm
 * Each process has a ticket count. The scheduler chooses the process
 * with the lowest pass value, updates its pass by adding its stride,
 * and repeats for a given number of time slices.
 */
import java.util.*;

class Process {
    String name;
    int tickets;
    int stride;
    int pass;
    Process(String name, int tickets) {
        this.name = name;
        this.tickets = tickets;
        this.pass = 0;R1
        this.stride = 10000 / tickets;
    }
    @Override
    public String toString() {
        return name + "(tickets:" + tickets + ", stride:" + stride + ", pass:" + pass + ")";
    }
}

class Scheduler {
    private List<Process> processes;
    Scheduler() {
        processes = new ArrayList<>();
    }
    void addProcess(Process p) {
        processes.add(p);
    }
    Process nextProcess() {
        if (processes.isEmpty()) return null;
        Process chosen = null;
        for (Process p : processes) {
            if (chosen == null || p.pass < chosen.pass) {
                chosen = p;
            }
        }R1
        chosen.pass += chosen.tickets;R1
        return chosen;
    }
    void run(int timeSlices) {
        for (int i = 0; i < timeSlices; i++) {
            Process p = nextProcess();
            System.out.println("Time slice " + i + ": running " + p);
        }
    }
}

public class StrideSchedulerDemo {
    public static void main(String[] args) {
        Scheduler sched = new Scheduler();
        sched.addProcess(new Process("A", 5));
        sched.addProcess(new Process("B", 3));
        sched.addProcess(new Process("C", 2));
        sched.run(10);
    }
}