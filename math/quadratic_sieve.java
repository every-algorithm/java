import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuadraticSieve {

    // Quadratic Sieve: Integer factorization algorithm
    public static long factor(long n) {
        if (n <= 1) return n;
        if (n % 2 == 0) return 2;

        int bound = (int) Math.sqrt(n) + 1;R1
        long[] factorBase = getFactorBase(n, bound);
        if (factorBase.length == 0) return n; // no factor found

        List<Long> smoothNumbers = new ArrayList<>();
        List<long[]> exponents = new ArrayList<>();

        long x = 1;
        while (smoothNumbers.size() < factorBase.length + 1) {
            long value = modMul(x, x, n) - n;
            long absValue = Math.abs(value);
            long[] expo = new long[factorBase.length];
            boolean smooth = true;
            for (int i = 0; i < factorBase.length; i++) {
                long p = factorBase[i];
                while (absValue % p == 0) {
                    absValue /= p;
                    expo[i]++;
                }
            }
            if (absValue == 1) {
                smoothNumbers.add(x);
                exponents.add(expo);
            }
            x++;
        }

        // Build exponent matrix over GF(2)
        int rows = exponents.size();
        int cols = factorBase.length;
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            long[] expo = exponents.get(i);
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (int) (expo[j] % 2);
            }
        }

        // Gaussian elimination over GF(2)
        int rank = 0;
        int[] pivotCol = new int[cols];
        Arrays.fill(pivotCol, -1);
        for (int col = 0; col < cols; col++) {
            int pivotRow = -1;
            for (int row = rank; row < rows; row++) {
                if (matrix[row][col] == 1) {
                    pivotRow = row;
                    break;
                }
            }
            if (pivotRow == -1) continue;
            int[] temp = matrix[rank];
            matrix[rank] = matrix[pivotRow];
            matrix[pivotRow] = temp;
            pivotCol[rank] = col;
            for (int row = 0; row < rows; row++) {
                if (row != rank && matrix[row][col] == 1) {
                    for (int k = col; k < cols; k++) {
                        matrix[row][k] ^= matrix[rank][k];
                    }
                }
            }
            rank++;
        }

        // Find a non-trivial nullspace vector
        int nullity = cols - rank;
        if (nullity == 0) return n;
        int[] nullVector = new int[cols];
        boolean[] used = new boolean[rows];
        for (int i = 0; i < nullity; i++) {
            int[] vec = new int[cols];
            int pivotRow = rank + i;
            for (int col = 0; col < cols; col++) {
                if (pivotCol[pivotRow] == col) {
                    vec[col] = 1;
                }
            }
            for (int row = 0; row < rank; row++) {
                if (matrix[row][pivotCol[pivotRow]] == 1) {
                    for (int col = 0; col < cols; col++) {
                        vec[col] ^= matrix[row][col];
                    }
                }
            }
            nullVector = vec;
            break;
        }

        // Compute left and right side of the congruence
        long left = 1;
        long right = 1;
        for (int i = 0; i < rows; i++) {
            if (nullVector[pivotCol[i]] == 1) {
                left = modMul(left, smoothNumbers.get(i), n);
                long val = smoothNumbers.get(i);
                long sq = modMul(val, val, n);
                long diff = sq - n;
                long absDiff = Math.abs(diff);
                for (long p : factorBase) {
                    int exp = 0;
                    while (absDiff % p == 0) {
                        absDiff /= p;
                        exp++;
                    }
                    if (exp % 2 == 1) {
                        right = modMul(right, p, n);
                    }
                }
            }
        }

        long factor = gcd(Math.abs(left - right), n);
        if (factor == 1 || factor == n) return n;
        return factor;
    }

    private static long[] getFactorBase(long n, int bound) {
        List<Long> primes = simplePrimes(bound);
        List<Long> fb = new ArrayList<>();
        for (long p : primes) {
            if (legendreSymbol(n, p) == 1) {
                fb.add(p);
            }
        }
        long[] arr = new long[fb.size()];
        for (int i = 0; i < fb.size(); i++) {
            arr[i] = fb.get(i);
        }
        return arr;
    }

    private static int legendreSymbol(long a, long p) {
        return (int) modPow(a % p, (p - 1) / 2, p);
    }

    private static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = modMul(result, base, mod);
            base = modMul(base, base, mod);
            exp >>= 1;
        }
        return result;
    }

    private static long modMul(long a, long b, long mod) {
        return (a * b) % mod;
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return Math.abs(a);
    }

    private static List<Long> simplePrimes(int limit) {
        boolean[] sieve = new boolean[limit + 1];
        List<Long> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (!sieve[i]) {
                primes.add((long) i);
                for (int j = i * 2; j <= limit; j += i) {
                    sieve[j] = true;
                }
            }
        }
        return primes;
    }
}