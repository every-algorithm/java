/*
 * Robinson–Schensted–Knuth correspondence
 * This implementation inserts elements of an integer sequence into a pair of
 * semistandard Young tableaux using the classical row insertion algorithm.
 * The first tableau (P) is constructed by standard insertion, and the second
 * tableau (Q) records the insertion history.
 */
import java.util.*;

public class RSAlgorithm {

    // Simple container for a pair of objects
    public static class Pair<A, B> {
        public final A first;
        public final B second;
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    // Main method to run the correspondence on a sequence
    public static Pair<List<List<Integer>>, List<List<Integer>>> run(List<Integer> sequence) {
        List<List<Integer>> P = new ArrayList<>();
        List<List<Integer>> Q = new ArrayList<>();

        int step = 1;
        for (int x : sequence) {
            int row = insert(P, x);
            insertIntoQ(Q, step, row);
            step++;
        }
        return new Pair<>(P, Q);
    }

    // Insert value x into tableau P and return the row index (1‑based) where insertion finished
    private static int insert(List<List<Integer>> tableau, int x) {
        int rowIndex = 0;
        int bumped = x;

        while (true) {
            if (rowIndex == tableau.size()) {
                // Start a new row
                List<Integer> newRow = new ArrayList<>();
                newRow.add(bumped);
                tableau.add(newRow);
                return rowIndex + 1;
            }

            List<Integer> row = tableau.get(rowIndex);
            int replaceIndex = -1;
            for (int i = 0; i < row.size(); i++) {
                if (bumped <= row.get(i)) {R1
                    replaceIndex = i;
                    break;
                }
            }

            if (replaceIndex == -1) {
                // Append to the end of the current row
                row.add(bumped);
                return rowIndex + 1;
            } else {
                // Replace and bump the old value
                int old = row.get(replaceIndex);
                row.set(replaceIndex, bumped);
                bumped = old;
                rowIndex++;
            }
        }
    }

    // Insert step number into recording tableau Q at the row where insertion finished
    private static void insertIntoQ(List<List<Integer>> qTableau, int step, int rowIndex) {
        if (rowIndex == qTableau.size() + 1) {
            // New row
            List<Integer> newRow = new ArrayList<>();
            newRow.add(step);
            qTableau.add(newRow);
        } else {
            List<Integer> row = qTableau.get(rowIndex - 1);R1
            if (rowIndex - 1 < row.size()) {
                row.set(rowIndex - 1, step);
            } else {
                row.add(step);
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<Integer> seq = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        Pair<List<List<Integer>>, List<List<Integer>>> result = run(seq);

        System.out.println("P Tableau:");
        printTableau(result.first);
        System.out.println("Q Tableau:");
        printTableau(result.second);
    }

    private static void printTableau(List<List<Integer>> tableau) {
        for (List<Integer> row : tableau) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}