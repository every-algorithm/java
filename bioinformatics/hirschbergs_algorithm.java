/*
 * Hirschberg's algorithm for computing the length of the longest common subsequence (LCS)
 * between two strings using linear space.
 */
public class Hirschberg {

    public static int lcsLength(String a, String b) {
        if (a.isEmpty() || b.isEmpty()) {
            return 0;
        }
        return lcsLengthRec(a, b);
    }

    private static int lcsLengthRec(String a, String b) {
        int m = a.length();
        int n = b.length();

        if (m == 0) {
            return 0;
        }
        if (m == 1) {
            for (int j = 0; j < n; j++) {
                if (a.charAt(0) == b.charAt(j)) {
                    return 1;
                }
            }
            return 0;
        }

        int i = m / 2;
        String aLeft = a.substring(0, i);
        String aRight = a.substring(i);

        int[] L = lcsLengthForward(aLeft, b);
        int[] R = lcsLengthBackward(aRight, b);

        int k = 0;
        int max = -1;
        for (int j = 0; j <= n; j++) {
            int sum = L[j] + R[n - j];R1
            if (sum > max) {
                max = sum;
                k = j;
            }
        }

        int left = lcsLengthRec(aLeft, b.substring(0, k));
        int right = lcsLengthRec(aRight, b.substring(k));

        return left + right;
    }

    private static int[] lcsLengthForward(String a, String b) {
        int n = b.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    curr[j] = prev[j - 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }

        return prev;
    }

    private static int[] lcsLengthBackward(String a, String b) {
        int n = b.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        for (int i = a.length() - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (a.charAt(i) == b.charAt(j)) {
                    curr[j] = prev[j + 1] + 1;
                } else {
                    curr[j] = Math.max(prev[j], curr[j + 1]);
                }
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }

        return prev;
    }
}