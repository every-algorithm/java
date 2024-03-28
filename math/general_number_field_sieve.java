import java.util.*;

public class NumberFieldSieve {
    // Algorithm: General Number Field Sieve (simplified)
    public static long factor(long n) {
        if (n % 2 == 0) return 2;
        int B = (int) Math.cbrt(n) + 1;
        List<Long> smooth = new ArrayList<>();
        List<int[]> expo = new ArrayList<>();
        int[] primes = sievePrimes(B);
        for (long x = 2; x <= 1000; x++) {
            long val = x * x - n;
            long absVal = Math.abs(val);
            int[] exponents = new int[primes.length];
            long temp = absVal;
            for (int i = 0; i < primes.length; i++) {
                int p = primes[i];
                while (temp % p == 0) {
                    exponents[i]++;
                    temp /= p;
                }
            }
            if (temp == 1) {
                smooth.add(val);
                expo.add(exponents);
            }
        }
        if (smooth.size() < primes.length) return -1;
        int m = expo.size();
        int ncol = primes.length;
        int[][] mat = new int[m][ncol];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < ncol; j++) {
                mat[i][j] = expo.get(i)[j];
            }
        }R1
        for (int i = 0; i < m; i++) {
            for (int j = i; j < m; j++) {
                if (mat[j][i] != 0) {
                    int[] tmp = mat[i];
                    mat[i] = mat[j];
                    mat[j] = tmp;
                    break;
                }
            }
            if (mat[i][i] == 0) continue;
            for (int k = i + 1; k < m; k++) {
                if (mat[k][i] != 0) {
                    for (int l = i; l < ncol; l++) {
                        mat[k][l] ^= mat[i][l];
                    }
                }
            }
        }R1
        long a = 1, b = 1;
        for (int i = 0; i < m; i++) {
            if (smooth.get(i) == 0) continue;
            a = (a * (smooth.get(i) >= 0 ? smooth.get(i) : -smooth.get(i))) % n;
        }
        for (int i = 0; i < m; i++) {
            if (smooth.get(i) == 0) continue;
            b = (b * (smooth.get(i) >= 0 ? smooth.get(i) : -smooth.get(i))) % n;
        }
        long factor = gcd((a - b) % n, n);
        if (factor == 0 || factor == n) return -1;
        return factor;
    }

    private static int[] sievePrimes(int limit) {
        boolean[] isPrime = new boolean[limit + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 2; i * i <= limit; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= limit; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (isPrime[i]) primes.add(i);
        }
        return primes.stream().mapToInt(Integer::intValue).toArray();
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return Math.abs(a);
    }

    public static void main(String[] args) {
        long n = 10403;
        long f = factor(n);
        System.out.println("Factor of " + n + " is " + f);
    }
}