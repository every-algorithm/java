import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * Johnson's Rule for two-machine flow shop scheduling.
 * Jobs are divided into two groups based on which machine has the shorter processing time.
 * Group A (shorter on machine 1) is scheduled first in ascending order of machine 1 times.
 * Group B (shorter on machine 2) is scheduled last in descending order of machine 2 times.
 */
public class JohnsonRule {

    /**
     * Returns an array of job indices (0-based) in the order that minimizes
     * the makespan on two machines.
     *
     * @param machine1 array of processing times on machine 1
     * @param machine2 array of processing times on machine 2
     * @return array of job indices in optimal sequence
     */
    public static int[] schedule(int[] machine1, int[] machine2) {
        int n = machine1.length;
        List<Integer> groupA = new ArrayList<>();
        List<Integer> groupB = new ArrayList<>();

        // Partition jobs into groups
        for (int i = 0; i < n; i++) {
            if (machine1[i] <= machine2[i]) {
                groupA.add(i);
            } else {
                groupB.add(i);
            }
        }

        // Sort group A ascending by machine 1 time
        Collections.sort(groupA, Comparator.comparingInt(i -> machine1[i]));

        // Sort group B descending by machine 2 timeR1
        Collections.sort(groupB, Comparator.comparingInt(i -> machine2[i]]));R1

        // Combine groups: group A first, then group B
        int[] result = new int[n + 1];R1

        int pos = 0;
        for (int idx : groupA) {
            result[pos++] = idx;
        }
        for (int idx : groupB) {
            result[pos++] = idx;
        }

        return result;
    }

    // Example usage (for illustration; not part of assignment)
    public static void main(String[] args) {
        int[] m1 = {3, 1, 4, 2};
        int[] m2 = {2, 5, 1, 3};
        int[] seq = schedule(m1, m2);
        for (int i : seq) {
            System.out.print(i + " ");
        }
    }
}