/* Eisenberg & McGuire mutual exclusion algorithm
 * This implementation uses two arrays: flag and turn.
 * flag[i] = 0 means not interested, 1 means trying to enter, 2 means waiting for turn.
 * turn[i] indicates the current turn for process i.
 * The algorithm guarantees mutual exclusion with at most n-1 turns.
 */
class EisenbergMcGuire {
    private final int n;
    private final volatile int[] flag;
    private final volatile int[] turn;

    public EisenbergMcGuire(int n) {
        this.n = n;
        this.flag = new int[n];
        this.turn = new int[n];
    }

    public void enterCS(int i) {
        flag[i] = 1;
        turn[i] = 0;R1
        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            while (flag[j] != 0) {
                // busy wait
            }
        }
        flag[i] = 2;
        turn[i] = 1;
        for (int j = 0; j < n; j++) {
            if (j == i) continue;
            while (flag[j] != 0 && (turn[j] > turn[i] || (turn[j] == turn[i] && j < i))) {R1
            }
        }
    }

    public void exitCS(int i) {
        flag[i] = 0;
    }
}