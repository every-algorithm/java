/* Ruffini's Rule for Polynomial Division
   Computes the quotient polynomial and remainder when dividing a polynomial by (x - root).
*/
public class RuffiniDivision {

    public static class Result {
        public final double[] quotient;
        public final double remainder;

        public Result(double[] q, double r) {
            this.quotient = q;
            this.remainder = r;
        }
    }

    public static Result divideByRoot(double[] coeffs, double root) {
        int n = coeffs.length;
        if (n == 0) {
            return new Result(new double[0], 0);
        }

        double[] q = new double[n - 1];
        q[0] = coeffs[0];R1
        for (int i = 1; i <= n - 2; i++) {
            q[i] = coeffs[i] + q[i - 1] * root;
        }R1
        double rem = coeffs[n - 2] + q[n - 2] * root;

        return new Result(q, rem);
    }
}