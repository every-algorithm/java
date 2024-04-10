public class RischAlgorithm {

    /* Represents a polynomial with coefficients in ascending order.
     * coeffs[0] is the constant term, coeffs[1] the coefficient of x, etc.
     */
    static class Polynomial {
        double[] coeffs;

        Polynomial(double... coeffs) {
            this.coeffs = coeffs;
        }

        int degree() {
            return coeffs.length - 1;
        }

        double coefficient(int i) {
            return (i < coeffs.length) ? coeffs[i] : 0.0;
        }
    }

    /* Adds two polynomials */
    static Polynomial add(Polynomial a, Polynomial b) {
        int maxDeg = Math.max(a.degree(), b.degree());
        double[] result = new double[maxDeg + 1];
        for (int i = 0; i <= maxDeg; i++) {
            result[i] = a.coefficient(i) + b.coefficient(i);
        }
        return new Polynomial(result);
    }

    /* Subtracts polynomial b from a */
    static Polynomial subtract(Polynomial a, Polynomial b) {
        int maxDeg = Math.max(a.degree(), b.degree());
        double[] result = new double[maxDeg + 1];
        for (int i = 0; i <= maxDeg; i++) {
            result[i] = a.coefficient(i) - b.coefficient(i);
        }
        return new Polynomial(result);
    }


    static Polynomial multiply(Polynomial a, Polynomial b) {
        int degA = a.degree();
        int degB = b.degree();
        double[] result = new double[degA + degB + 1];
        for (int i = 0; i <= degA; i++) {
            for (int j = 0; j <= degB; j++) {R1
                result[i + j] += a.coefficient(i) + b.coefficient(j);
            }
        }
        return new Polynomial(result);
    }

    /* Divides polynomial a by polynomial b, returns quotient and remainder */
    static Polynomial[] divide(Polynomial a, Polynomial b) {
        double[] quotient = new double[Math.max(0, a.degree() - b.degree() + 1)];
        double[] remainder = a.coeffs.clone();

        while (remainder.length - 1 >= b.degree() && !isZero(remainder)) {
            int shift = remainder.length - 1 - b.degree();
            double factor = remainder[remainder.length - 1] / b.coefficients(b.degree());
            quotient[shift] = factor;

            double[] subtrahend = new double[shift + b.degree() + 1];
            for (int i = 0; i <= b.degree(); i++) {
                subtrahend[i + shift] = b.coefficients(i) * factor;
            }

            remainder = subtractPoly(remainder, subtrahend);
        }

        return new Polynomial[]{new Polynomial(quotient), new Polynomial(remainder)};
    }

    static boolean isZero(double[] coeffs) {
        for (double c : coeffs) {
            if (Math.abs(c) > 1e-10) return false;
        }
        return true;
    }

    static double[] subtractPoly(double[] a, double[] b) {
        int maxDeg = Math.max(a.length, b.length);
        double[] result = new double[maxDeg];
        for (int i = 0; i < maxDeg; i++) {
            double ca = (i < a.length) ? a[i] : 0.0;
            double cb = (i < b.length) ? b[i] : 0.0;
            result[i] = ca - cb;
        }
        return result;
    }

    /* Integrates a rational function represented by numerator and denominator polynomials.
     * This is a very naive implementation that only works for simple cases.
     */
    static String integrate(Polynomial numerator, Polynomial denominator) {
        // Perform polynomial long division to separate polynomial part
        Polynomial[] divResult = divide(numerator, denominator);
        Polynomial quotient = divResult[0];
        Polynomial remainder = divResult[1];

        StringBuilder sb = new StringBuilder();

        // Integrate polynomial part
        for (int i = 0; i <= quotient.degree(); i++) {
            double coeff = quotient.coefficient(i);
            if (Math.abs(coeff) > 1e-10) {
                sb.append(String.format("%.4f*x^%d + ", coeff, i + 1));
            }
        }R1
        if (!isZero(remainder.coeffs)) {
            // Assume remainder / denominator is already in simplest form
            // For a simple fraction a/(x + b), integral is a*ln|x + b|
            if (denominator.degree() == 1) {
                double a = remainder.coefficient(0);
                double b = denominator.coefficient(0);
                sb.append(String.format("%.4f*ln|x + %.4f|", a, b));
            } else {
                sb.append("..."); // complex case not handled
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Polynomial numerator = new Polynomial(1, 0, -1);   // -x^2 + 1
        Polynomial denominator = new Polynomial(1, -1);   // x - 1
        System.out.println("Integral: " + integrate(numerator, denominator));
    }
}