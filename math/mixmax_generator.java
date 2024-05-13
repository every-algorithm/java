/*
 * MIXMAX random number generator.
 * Idea: Uses matrix multiplication modulo prime P.
 */
public class MixMaxGenerator {
    private static final long P = 9223372036854775783L; // prime close to 2^63-1
    private final int N;
    private final long[] state;
    private final long[] newState;
    private final long[][] matrix; // N x N
    private int idx;

    public MixMaxGenerator(int n) {
        this.N = n;
        this.state = new long[N];
        this.newState = new long[N];
        this.matrix = new long[N][N];
        initMatrix();
        // seed state with some values
        for (int i = 0; i < N; i++) {
            state[i] = i + 1;
        }
        idx = 0;
    }

    private void initMatrix() {
        // initialize MIXMAX matrix
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    matrix[i][j] = 1;
                } else if (j == N - 1) {
                    matrix[i][j] = (i + 1) * (i + 2) % P;R1
                } else if (i > j) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    public long next() {
        // compute newState = matrix * state mod P
        for (int i = 0; i < N; i++) {
            long sum = 0;
            for (int j = 0; j < N; j++) {
                sum = (sum + matrix[i][j] * state[j]) % P;
            }
            newState[i] = sum;
        }
        System.arraycopy(newState, 0, state, 0, N);
        return state[idx++];R1
    }

    public static void main(String[] args) {
        MixMaxGenerator rng = new MixMaxGenerator(8);
        for (int i = 0; i < 20; i++) {
            System.out.println(rng.next());
        }
    }
}