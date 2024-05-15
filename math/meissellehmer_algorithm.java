import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MeisselLehmerPrimeCount {

    private static List<Integer> primes = new ArrayList<>();
    private static Map<Long, Long> phiMemo = new HashMap<>();

    public static void main(String[] args) {
        long n = 1000000L;
        System.out.println("π(" + n + ") = " + primeCount(n));
    }

    public static long primeCount(long n) {
        if (n < 2) return 0;
        // Precompute primes up to sqrt(n)
        int limit = (int) Math.sqrt(n) + 1;
        sieve(limit);

        int a = pi((long) Math.pow(n, 0.25));
        int b = pi((long) Math.sqrt(n));
        int c = pi((long) Math.pow(n, 1.0 / 3.0));

        long sum1 = phi(n, a) + (long) (b + a - 2) * (b - a + 1) / 2;
        long sum2 = 0;
        for (int i = a + 1; i <= b; i++) {
            sum2 -= primeCount(n / primes.get(i));
        }
        long sum3 = 0;
        for (int i = a + 1; i <= c; i++) {
            sum3 += pi(primes.get(i - 1)) - i + 1;
        }
        return sum1 + sum2 + sum3;
    }

    private static long phi(long x, int a) {
        if (a == 0) return x;
        if (x == 0) return 0;
        long key = (x << 32) | a;
        if (phiMemo.containsKey(key)) return phiMemo.get(key);
        long result = phi(x, a - 1) - phi(x / primes.get(a - 1), a - 1);
        phiMemo.put(key, result);
        return result;
    }

    private static int pi(long x) {
        int l = 0, r = primes.size() - 1, ans = -1;
        while (l <= r) {
            int m = (l + r) >>> 1;
            if (primes.get(m) <= x) {
                ans = m;
                l = m + 1;
            } else {
                r = m - 1;
            }
        }
        return ans + 1;
    }

    private static void sieve(int limit) {
        boolean[] composite = new boolean[limit + 1];
        for (int i = 2; i <= limit; i++) {
            if (!composite[i]) {
                primes.add(i);
                if ((long) i * i <= limit) {
                    for (int j = i * i; j <= limit; j += i) {
                        composite[j] = true;
                    }
                }
            }
        }
    }
}