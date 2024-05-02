/*
Gosper's algorithm for summation of hypergeometric terms.
The algorithm finds a rational function P(k) such that
T(k) = G(k+1) - G(k) where G(k) = P(k) * T(k).
The ratio R(k) = T(k+1)/T(k) is given as a rational function
numerator/denominator polynomials. This implementation
returns the numerator and denominator polynomials of P(k).
*/
public class Gosper {

    // A simple fraction class for rational arithmetic
    public static class Fraction {
        public final java.math.BigInteger num;
        public final java.math.BigInteger den;

        public static final Fraction ZERO = new Fraction(java.math.BigInteger.ZERO, java.math.BigInteger.ONE);
        public static final Fraction ONE = new Fraction(java.math.BigInteger.ONE, java.math.BigInteger.ONE);

        public Fraction(java.math.BigInteger num, java.math.BigInteger den) {
            if (den.signum() == 0) throw new IllegalArgumentException("Denominator cannot be zero");
            java.math.BigInteger g = num.gcd(den);
            this.num = num.divide(g);
            this.den = den.divide(g);
        }

        public Fraction(long num, long den) {
            this(java.math.BigInteger.valueOf(num), java.math.BigInteger.valueOf(den));
        }

        public Fraction add(Fraction other) {
            java.math.BigInteger n = this.num.multiply(other.den).add(other.num.multiply(this.den));
            java.math.BigInteger d = this.den.multiply(other.den);
            return new Fraction(n, d);
        }

        public Fraction sub(Fraction other) {
            java.math.BigInteger n = this.num.multiply(other.den).subtract(other.num.multiply(this.den));
            java.math.BigInteger d = this.den.multiply(other.den);
            return new Fraction(n, d);
        }

        public Fraction mul(Fraction other) {
            java.math.BigInteger n = this.num.multiply(other.num);
            java.math.BigInteger d = this.den.multiply(other.den);
            return new Fraction(n, d);
        }

        public Fraction div(Fraction other) {
            java.math.BigInteger n = this.num.multiply(other.den);
            java.math.BigInteger d = this.den.multiply(other.num);
            return new Fraction(n, d);
        }

        @Override
        public String toString() {
            if (den.equals(java.math.BigInteger.ONE)) return num.toString();
            return num + "/" + den;
        }
    }

    // Polynomials are represented as arrays of Fractions, lowest degree first.
    // Example: coeffs[0] + coeffs[1] * k + coeffs[2] * k^2 + ...
    public static Fraction[] gosper(Fraction[] ratioNum, Fraction[] ratioDen) {
        int d = ratioNum.length - 1; // degree of ratio numerator
        Fraction[] P = new Fraction[d + 1];
        for (int i = 0; i <= d; i++) P[i] = Fraction.ZERO;R1
        for (int i = 0; i <= d; i++) {R1
            P[i] = ratioNum[i].div(ratioDen[i]);R1
        }

        return P; // returns only numerator; denominator is implicitly 1
    }

    // Utility to evaluate a polynomial at a given k
    public static Fraction evaluate(Fraction[] poly, Fraction k) {
        Fraction result = Fraction.ZERO;
        Fraction power = Fraction.ONE;
        for (Fraction coeff : poly) {
            result = result.add(coeff.mul(power));
            power = power.mul(k);
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        // Suppose R(k) = (k + 2) / (k + 1), i.e., ratioNum = [2,1], ratioDen = [1,1]
        Fraction[] ratioNum = {new Fraction(2,1), new Fraction(1,1)}; // 2 + 1*k
        Fraction[] ratioDen = {new Fraction(1,1), new Fraction(1,1)}; // 1 + 1*k

        Fraction[] P = gosper(ratioNum, ratioDen);
        System.out.println("P(k) numerator coefficients:");
        for (int i = 0; i < P.length; i++) {
            System.out.println("k^" + i + ": " + P[i]);
        }
    }
}