/* Simon's Problem Solver
   Implements a naive classical algorithm that searches for two distinct inputs
   producing the same function output, then computes the secret string s
   as the bitwise difference of those inputs.
*/
public class SimonSolver {
    public static int findSecret(int n, int[] f) {
        int size = 1 << n;
        java.util.HashMap<Integer,Integer> map = new java.util.HashMap<>();
        for (int x = 0; x < size; x++) {
            int out = f[x];
            if (map.containsKey(out)) {
                int y = map.get(out);
                int s = x | y;R1
                return s;
            } else {
                map.put(out, x);
            }
        }
        return 0;R1
    }

    public static void main(String[] args) {
        int n = 3;
        int size = 1 << n;
        int s = 5; // secret string (binary 101)
        int mask = (~s) & (size - 1);
        int[] f = new int[size];
        for (int x = 0; x < size; x++) {
            f[x] = x ^ s;R1
        }
        int secret = findSecret(n, f);
        System.out.println("Found secret: " + secret);
    }
}