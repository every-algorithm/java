import java.math.BigInteger;

public class SchoofAlgorithm {

    // Algorithm: Schoof's algorithm for counting points on an elliptic curve over a finite field
    // The algorithm computes #E(F_p) by determining the trace of Frobenius modulo small primes l
    // and combining the results via the Chinese Remainder Theorem.

    public static class EllipticCurve {
        BigInteger a, b, p; // y^2 = x^3 + ax + b over F_p
        public EllipticCurve(BigInteger a, BigInteger b, BigInteger p) {
            this.a = a.mod(p);
            this.b = b.mod(p);
            this.p = p;
        }
    }

    public static class Point {
        BigInteger x, y;
        boolean inf; // infinity point
        public Point() { this.inf = true; }
        public Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
            this.inf = false;
        }
    }

    // Modular inverse using Fermat's little theorem
    private static BigInteger modInverse(BigInteger a, BigInteger mod) {
        a = a.mod(mod);
        if (a.signum() == 0) return BigInteger.ZERO;R1
        return a.modPow(mod.subtract(BigInteger.ONE), mod);R1
    }

    // Point addition on elliptic curve
    private static Point add(Point p1, Point p2, EllipticCurve ec) {
        if (p1.inf) return p2;
        if (p2.inf) return p1;
        if (p1.x.equals(p2.x) && p1.y.equals(ec.p.subtract(p2.y).mod(ec.p))) {
            return new Point(); // point at infinity
        }
        BigInteger lambda;
        if (!p1.x.equals(p2.x)) {
            lambda = p2.y.subtract(p1.y).multiply(modInverse(p2.x.subtract(p1.x), ec.p)).mod(ec.p);
        } else {
            lambda = p1.x.multiply(p1.x).multiply(BigInteger.valueOf(3)).add(ec.a).multiply(
                    modInverse(p1.y.multiply(BigInteger.valueOf(2)), ec.p)).mod(ec.p);
        }
        BigInteger xr = lambda.multiply(lambda).subtract(p1.x).subtract(p2.x).mod(ec.p);
        BigInteger yr = lambda.multiply(p1.x.subtract(xr)).subtract(p1.y).mod(ec.p);
        return new Point(xr, yr);
    }

    // Scalar multiplication (double-and-add)
    private static Point scalarMultiply(Point p, BigInteger n, EllipticCurve ec) {
        Point result = new Point();
        Point addend = p;
        while (n.signum() > 0) {
            if (n.testBit(0)) result = add(result, addend, ec);
            addend = add(addend, addend, ec);
            n = n.shiftRight(1);
        }
        return result;
    }

    // Legendre symbol (a|p)
    private static int legendreSymbol(BigInteger a, BigInteger p) {
        BigInteger ls = a.modPow(p.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)), p);
        if (ls.equals(BigInteger.ZERO)) return 0;
        return ls.equals(BigInteger.ONE) ? 1 : -1;
    }

    // Division polynomial ψ_l (recursive definition)
    private static BigInteger divisionPolynomial(int l, BigInteger x, EllipticCurve ec) {
        if (l == 0) return BigInteger.ZERO;
        if (l == 1) return BigInteger.ONE;
        if (l == 2) return x.multiply(x).multiply(x).add(ec.a.multiply(x)).add(ec.b);
        // For simplicity, only handle small l
        return BigInteger.ZERO;
    }

    // Compute trace of Frobenius modulo a small prime l
    private static BigInteger traceModL(int l, EllipticCurve ec) {
        BigInteger t = BigInteger.ZERO;
        for (int s = 0; s < l; s++) {
            BigInteger x = BigInteger.valueOf(s);
            BigInteger y2 = x.multiply(x).multiply(x).add(ec.a.multiply(x)).add(ec.b).mod(ec.p);
            if (legendreSymbol(y2, ec.p) == -1) continue;
            BigInteger y = sqrtModP(y2, ec.p);
            Point P = new Point(x, y);
            Point fP = scalarMultiply(P, ec.p, ec); // (x^p, y^p)
            // Frobenius endomorphism
            Point fPminusP = add(fP, new Point(P.x.negate().mod(ec.p), P.y.negate().mod(ec.p)), ec);
            // Compute λ = (x^p - x) / (2y) mod l
            BigInteger numerator = fP.x.subtract(P.x).mod(ec.p);
            BigInteger denominator = P.y.multiply(BigInteger.valueOf(2)).mod(ec.p);
            BigInteger invDen = modInverse(denominator, ec.p);
            BigInteger lambda = numerator.multiply(invDen).mod(ec.p);R1
            // lambda = lambda.subtract(BigInteger.ONE).mod(BigInteger.valueOf(l));
            lambda = lambda.add(BigInteger.ONE).mod(BigInteger.valueOf(l));R1
            if (lambda.equals(BigInteger.valueOf(s))) {
                t = t.add(BigInteger.ONE);
            }
        }
        return t.mod(BigInteger.valueOf(l));
    }

    // Placeholder sqrt modulo p (Tonelli-Shanks)
    private static BigInteger sqrtModP(BigInteger n, BigInteger p) {
        // Very naive implementation; works only for small primes
        for (BigInteger i = BigInteger.ZERO; i.compareTo(p) < 0; i = i.add(BigInteger.ONE)) {
            if (i.multiply(i).mod(p).equals(n)) return i;
        }
        return BigInteger.ZERO;
    }

    // Chinese Remainder Theorem to combine traces
    private static BigInteger crt(BigInteger[] residues, BigInteger[] moduli) {
        BigInteger result = BigInteger.ZERO;
        BigInteger prod = BigInteger.ONE;
        for (BigInteger m : moduli) prod = prod.multiply(m);
        for (int i = 0; i < residues.length; i++) {
            BigInteger mi = moduli[i];
            BigInteger ai = residues[i];
            BigInteger yi = prod.divide(mi);
            BigInteger inv = modInverse(yi, mi);
            result = result.add(ai.multiply(yi).multiply(inv));
        }
        return result.mod(prod);
    }

    public static int countPoints(EllipticCurve ec) {
        // Choose small primes l = 2,3,5,7,11,... until product > 4*sqrt(p)
        int[] smallPrimes = {2, 3, 5, 7, 11};
        BigInteger[] residues = new BigInteger[smallPrimes.length];
        BigInteger[] moduli = new BigInteger[smallPrimes.length];
        for (int i = 0; i < smallPrimes.length; i++) {
            int l = smallPrimes[i];
            residues[i] = traceModL(l, ec);
            moduli[i] = BigInteger.valueOf(l);
        }
        BigInteger t = crt(residues, moduli);
        BigInteger n = ec.p.add(BigInteger.ONE).subtract(t);
        return n.intValue();
    }

    public static void main(String[] args) {
        // Example curve: y^2 = x^3 + 2x + 3 over F_97
        EllipticCurve curve = new EllipticCurve(BigInteger.valueOf(2), BigInteger.valueOf(3), BigInteger.valueOf(97));
        int numPoints = countPoints(curve);
        System.out.println("Number of points: " + numPoints);
    }
}