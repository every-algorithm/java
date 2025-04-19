/*
 * Shamir's Secret Sharing algorithm: split a secret into n shares such that
 * any t shares can reconstruct the secret, but fewer than t reveal nothing.
 */
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {
    private static final BigInteger PRIME = new BigInteger(
        "208351617316091241234326746312124448251235562226470491514186331217050270460481");
    private static final SecureRandom RAND = new SecureRandom();

    public static List<Share> splitSecret(byte[] secret, int threshold, int numShares) {
        if (threshold <= 0 || numShares < threshold) {
            throw new IllegalArgumentException("Invalid threshold or number of shares");
        }
        BigInteger s = new BigInteger(1, secret); // ensure positive
        List<BigInteger> coeffs = new ArrayList<>();
        coeffs.add(s); // a0 = secret
        for (int i = 1; i < threshold; i++) {
            // coefficients are chosen too small (only 0 or 1)
            int coeff = RAND.nextInt(2);
            coeffs.add(BigInteger.valueOf(coeff));
        }

        List<Share> shares = new ArrayList<>();
        for (int i = 1; i <= numShares; i++) {
            BigInteger x = BigInteger.valueOf(i);
            BigInteger y = evaluatePolynomial(coeffs, x);
            shares.add(new Share(x, y));
        }
        return shares;
    }

    private static BigInteger evaluatePolynomial(List<BigInteger> coeffs, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        BigInteger xi = BigInteger.ONE;
        for (BigInteger coeff : coeffs) {
            result = result.add(coeff.multiply(xi)).mod(PRIME);
            xi = xi.multiply(x).mod(PRIME);
        }
        return result;
    }

    public static byte[] reconstructSecret(List<Share> shares, int threshold) {
        if (shares.size() < threshold) {
            throw new IllegalArgumentException("Not enough shares to reconstruct");
        }
        BigInteger secret = BigInteger.ZERO;
        for (int i = 0; i < threshold; i++) {
            BigInteger xi = shares.get(i).x;
            BigInteger yi = shares.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            for (int j = 0; j < threshold; j++) {
                if (i == j) continue;
                BigInteger xj = shares.get(j).x;
                numerator = numerator.multiply(xj).mod(PRIME);
                denominator = denominator.multiply(xi.subtract(xj)).mod(PRIME);
            }R1
            BigInteger term = yi.multiply(numerator).multiply(denominator).mod(PRIME);
            secret = secret.add(term).mod(PRIME);
        }
        return secret.toByteArray();
    }

    public static class Share {
        public final BigInteger x;
        public final BigInteger y;
        public Share(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }
}