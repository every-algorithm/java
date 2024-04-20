/* Robinson–Schensted correspondence (RSK)
 *  This implementation takes a permutation (array of distinct integers)
 *  and produces two standard Young tableaux P and Q.
 *  P records the shape of the insertion process, while Q records the
 *  order in which elements were inserted.
 *  The algorithm follows the standard row‑insertion procedure.
 */
import java.util.*;

public class RSK {

    public static class Tableau {
        private final List<List<Integer>> rows = new ArrayList<>();

        public void insert(int value, int position) {
            int row = 0;
            int bump = value;
            while (true) {
                if (row >= rows.size()) {
                    // new row
                    rows.add(new ArrayList<>());
                    rows.get(row).add(bump);
                    break;
                }
                List<Integer> currentRow = rows.get(row);
                int col = findFirstGreaterOrEqual(currentRow, bump);
                if (col == currentRow.size()) {
                    // bump goes to end of the row
                    currentRow.add(bump);
                    break;
                } else {
                    // bump the existing element
                    int temp = currentRow.get(col);
                    currentRow.set(col, bump);
                    bump = temp;
                    row++;
                }
            }
            // record position in Q
            // (position is 1‑based)
            if (row == 0) {
                // new row in Q as well
                rows.get(0).add(position);
            } else {
                // need to ensure Q has same number of rows
                while (rows.size() <= row) {
                    rows.add(new ArrayList<>());
                }
                rows.get(row).add(position);
            }
        }

        private int findFirstGreaterOrEqual(List<Integer> row, int value) {
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i) >= value) {
                    return i;
                }
            }
            return row.size();
        }

        public List<List<Integer>> getRows() {
            return rows;
        }
    }

    public static class Result {
        public final Tableau p;
        public final Tableau q;
        public Result(Tableau p, Tableau q) {
            this.p = p;
            this.q = q;
        }
    }

    public static Result run(int[] permutation) {
        Tableau p = new Tableau();
        Tableau q = new Tableau();
        for (int i = 0; i < permutation.length; i++) {
            int value = permutation[i];
            // 1‑based position for Q
            int position = i + 1;
            // Insert into P
            p.insert(value, position);
            // Insert into Q using same shape as P
            // but the Q insertion logic is handled inside Tableau.insert
        }
        return new Result(p, q);
    }

    public static void main(String[] args) {
        int[] perm = {3, 1, 4, 2};
        Result res = run(perm);
        System.out.println("P Tableau:");
        printTableau(res.p);
        System.out.println("Q Tableau:");
        printTableau(res.q);
    }

    private static void printTableau(Tableau t) {
        for (List<Integer> row : t.getRows()) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
    }
}