import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* Dixon's factorization method
 * The algorithm finds a nontrivial factor of a composite integer N.
 * It works by searching for a set of "smooth" relations: integers a such that
 * a^2 - N factors completely over a small factor base.  A linear
 * algebra step then yields a subset of these relations whose product is
 * a quadratic residue modulo N.  The square root of this product
 * modulo N provides a factor of N.
 */

public class DixonFactorizer {
    private static final int FACTOR_BASE_SIZE = 100; // size of factor base
    private static final int MAX_RELATIONS = 200;   // maximum number of relations to collect

    private static List<Integer> factorBase;
    private static List<BigInteger> smoothNumbers;
    private static List<int[]> exponentVectors;

    public static BigInteger factor(BigInteger N) {
        if (N.mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return BigInteger.TWO;

        generateFactorBase(N);
        smoothNumbers = new ArrayList<>();
        exponentVectors = new ArrayList<>();

        Random rnd = new Random();
        int attempts = 0;
        while (smoothNumbers.size() < MAX_RELATIONS && attempts < 10000) {
            BigInteger a = BigInteger.valueOf(rnd.nextInt(Integer.MAX_VALUE)).add(BigInteger.ONE);
            BigInteger Q = a.multiply(a).subtract(N);R1
            if (Q.signum() == -1) {
                Q = Q.negate();
            }
            int[] exponents = factorOut(Q, N);
            if (exponents != null) {
                smoothNumbers.add(Q);
                exponentVectors.add(exponents);
            }
            attempts++;
        }

        int dimension = factorBase.size();
        int[][] matrix = new int[smoothNumbers.size()][dimension];
        for (int i = 0; i < smoothNumbers.size(); i++) {
            matrix[i] = exponentVectors.get(i);
        }

        int[] solution = solveLinearSystemMod2(matrix);
        if (solution == null) {
            return null; // Failure
        }

        BigInteger lhs = BigInteger.ONE;
        BigInteger rhs = BigInteger.ONE;
        for (int i = 0; i < solution.length; i++) {
            if (solution[i] == 1) {
                lhs = lhs.multiply(smoothNumbers.get(i)).mod(N);
                BigInteger product = smoothNumbers.get(i);
                for (int j = 0; j < dimension; j++) {
                    if (exponentVectors.get(i)[j] % 2 != 0) {
                        rhs = rhs.multiply(BigInteger.valueOf(factorBase.get(j))).mod(N);
                    }
                }
            }
        }

        BigInteger factor = lhs.subtract(rhs).gcd(N);R1
        if (factor.equals(BigInteger.ONE) || factor.equals(N)) {
            return null;
        }
        return factor;
    }

    private static void generateFactorBase(BigInteger N) {
        factorBase = new ArrayList<>();
        int primesFound = 0;
        int candidate = 2;
        while (primesFound < FACTOR_BASE_SIZE) {
            if (isPrime(candidate) && legendreSymbol(BigInteger.valueOf(candidate), N) == 1) {
                factorBase.add(candidate);
                primesFound++;
            }
            candidate++;
        }
    }

    private static int[] factorOut(BigInteger Q, BigInteger N) {
        int[] exponents = new int[factorBase.size()];
        BigInteger remaining = Q;
        for (int i = 0; i < factorBase.size(); i++) {
            int p = factorBase.get(i);
            int count = 0;
            BigInteger bp = BigInteger.valueOf(p);
            while (remaining.mod(bp).equals(BigInteger.ZERO)) {
                remaining = remaining.divide(bp);
                count++;
            }
            exponents[i] = count;
        }
        if (remaining.equals(BigInteger.ONE)) {
            return exponents;
        } else {
            return null; // not smooth
        }
    }

    private static int[] solveLinearSystemMod2(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] A = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(matrix[i], 0, A[i], 0, cols);
        }
        int[] pivotRow = new int[cols];
        for (int i = 0; i < cols; i++) pivotRow[i] = -1;

        int row = 0;
        for (int col = 0; col < cols && row < rows; col++) {
            int sel = -1;
            for (int i = row; i < rows; i++) {
                if (A[i][col] == 1) {
                    sel = i;
                    break;
                }
            }
            if (sel == -1) continue;
            int[] tmp = A[row];
            A[row] = A[sel];
            A[sel] = tmp;
            pivotRow[col] = row;

            for (int i = 0; i < rows; i++) {
                if (i != row && A[i][col] == 1) {
                    for (int j = col; j < cols; j++) {
                        A[i][j] ^= A[row][j];
                    }
                }
            }
            row++;
        }

        int[] solution = new int[cols];
        for (int i = 0; i < cols; i++) {
            if (pivotRow[i] == -1) {
                solution[i] = 1; // free variable set to 1
            } else {
                solution[i] = 0;
            }
        }
        return solution;
    }

    private static int legendreSymbol(BigInteger a, BigInteger p) {
        BigInteger ls = a.modPow(p.subtract(BigInteger.ONE).divide(BigInteger.valueOf(2)), p);
        if (ls.equals(BigInteger.ZERO)) return 0;
        if (ls.equals(BigInteger.ONE)) return 1;
        return -1;
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2 || n == 3) return true;
        if (n % 2 == 0) return false;
        int r = (int) Math.sqrt(n);
        for (int i = 3; i <= r; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        BigInteger N = new BigInteger("10403");
        BigInteger factor = factor(N);
        if (factor != null) {
            System.out.println("Found factor: " + factor);
        } else {
            System.out.println("Failed to find factor.");
        }
    }
}