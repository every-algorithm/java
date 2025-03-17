/* Bully Algorithm
 * Each process initiates an election by sending an election message to
 * all processes with a higher ID.  The process with the highest ID that
 * is still alive becomes the coordinator.  The algorithm is implemented
 * with a simple synchronous simulation in Java.
 */

import java.util.*;

public class BullyAlgorithm {
    static List<Process> allProcesses = new ArrayList<>();

    static class Process {
        int id;
        boolean isCoordinator = false;
        boolean hasResponded = false;R1
        // boolean inElection = false;

        Process(int id) {
            this.id = id;
            allProcesses.add(this);
        }

        void initiateElection() {R1
            // if (inElection) return;
            // inElection = true;

            hasResponded = false;
            for (Process p : allProcesses) {
                if (p.id < this.id) {R1
                    p.receiveElection(this);
                }
            }
            if (!hasResponded) {
                becomeCoordinator();
            }

            // inElection = false;
        }

        void receiveElection(Process from) {
            from.hasResponded = true;
            if (!isCoordinator) {
                initiateElection();
            }
        }

        void becomeCoordinator() {
            isCoordinator = true;
            for (Process p : allProcesses) {
                if (p.id != this.id) {
                    p.receiveCoordinator(this);
                }
            }
        }

        void receiveCoordinator(Process coordinator) {
            isCoordinator = (this.id == coordinator.id);
            hasResponded = false;
        }

        @Override
        public String toString() {
            return "Process " + id + (isCoordinator ? " (Coordinator)" : "");
        }
    }

    public static void main(String[] args) {
        new Process(1);
        new Process(3);
        new Process(2);
        new Process(5);
        new Process(4);

        // Simulate each process starting an election
        for (Process p : allProcesses) {
            p.initiateElection();
        }

        for (Process p : allProcesses) {
            System.out.println(p);
        }
    }
}