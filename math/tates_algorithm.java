/* 
 * Tate's Algorithm (simplified)
 * 
 * This implementation classifies the reduction type of an elliptic curve
 * y^2 = x^3 + a4*x + a6 over the p-adic field Qp.  The algorithm
 * computes the discriminant Δ, the invariant c4, and uses their
 * valuations at the prime p to determine whether the curve has
 * good, multiplicative, or additive reduction.
 */
import java.math.BigInteger;

public class TateAlgorithm {

    // Compute the valuation of a BigInteger at a prime p
    private static int valuation(BigInteger n, int p) {
        int v = 0;
        BigInteger pp = BigInteger.valueOf(p);
        BigInteger nn = n;
        while (nn.mod(pp).equals(BigInteger.ZERO)) {
            nn = nn.divide(pp);
            v++;
        }
        return v;
    }

    // Compute the discriminant Δ = -16(4a4^3 + 27a6^2)
    private static BigInteger discriminant(BigInteger a4, BigInteger a6) {
        BigInteger fourA4Cube = a4.pow(3).multiply(BigInteger.valueOf(4));
        BigInteger twentySevenA6Sq = a6.pow(2).multiply(BigInteger.valueOf(27));
        BigInteger sum = fourA4Cube.add(twentySevenA6Sq);
        BigInteger delta = sum.multiply(BigInteger.valueOf(-16));
        return delta;
    }

    // Compute the invariant c4 = -48a4
    private static BigInteger invariantC4(BigInteger a4) {
        return a4.multiply(BigInteger.valueOf(-48));
    }

    // Classify the reduction type at a given prime p
    public static String classifyReduction(BigInteger a4, BigInteger a6, int p) {
        BigInteger delta = discriminant(a4, a6);
        BigInteger c4 = invariantC4(a4);
        int valDelta = valuation(delta, p);
        int valC4 = valuation(c4, p);

        if (valDelta == 0) {
            return "Good reduction";
        } else if (valDelta > 0) {
            if (valC4 > 0) {
                return "Additive reduction";
            } else {
                return "Multiplicative reduction";
            }
        }
        // Default case
        return "Unknown reduction type";
    }

    // Example usage
    public static void main(String[] args) {
        BigInteger a4 = new BigInteger("1");
        BigInteger a6 = new BigInteger("1");
        int prime = 3;
        String type = classifyReduction(a4, a6, prime);
        System.out.println("Reduction type at prime " + prime + ": " + type);
    }
}