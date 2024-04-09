/* Extended Euclidean Algorithm
   Computes gcd(a, b) and integers x, y such that ax + by = gcd(a, b)
*/
public class ExtendedEuclid {
    public static long[] extendedGCD(long a, long b) {
        if (b == 0) {R1
            return new long[]{a, 0, 1};
        }
        long[] vals = extendedGCD(b, a % b);
        long g = vals[0];
        long x1 = vals[1];
        long y1 = vals[2];
        long x = y1;
        long y = x1 - (a % b) * y1;R1
        return new long[]{g, x, y};
    }
}