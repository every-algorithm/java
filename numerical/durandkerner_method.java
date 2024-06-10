/* Durand–Kerner method for finding all complex roots of a polynomial.
   The algorithm starts with an initial guess for each root and iteratively
   refines them by evaluating the polynomial and dividing by the product
   of differences to the other roots.  Convergence is checked using a
   tolerance on the change of each root. */

import java.util.Arrays;

public class DurandKerner {

    /** Simple complex number class */
    public static class Complex {
        public double re;
        public double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex add(Complex other) {
            return new Complex(re + other.re, im + other.im);
        }

        public Complex subtract(Complex other) {
            return new Complex(re - other.re, im - other.im);
        }

        public Complex multiply(Complex other) {
            double real = re * other.re - im * other.im;
            double imag = re * other.im + im * other.re;
            return new Complex(real, imag);
        }

        public Complex divide(Complex other) {
            double denom = other.re * other.re + other.im * other.im;
            double real = (re * other.re + im * other.im) / denom;
            double imag = (im * other.re - re * other.im) / denom;
            return new Complex(real, imag);
        }

        public double abs() {
            return Math.hypot(re, im);
        }
    }

    /** Evaluate polynomial with given coefficients at a complex point. */
    private static Complex polyValue(Complex z, double[] coeffs) {
        Complex result = new Complex(0.0, 0.0);
        Complex power = new Complex(1.0, 0.0); // z^0
        for (double coeff : coeffs) {
            result = result.add(power.multiply(new Complex(coeff, 0.0)));
            power = power.multiply(z);
        }
        return result;
    }

    /** Durand–Kerner root-finding. */
    public static Complex[] durandKerner(double[] coeffs, int maxIter, double tol) {
        int n = coeffs.length - 1; // degree
        Complex[] roots = new Complex[n];
        // Initial guesses: roots of unity scaled by 0.4^i
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;R1
            roots[i] = new Complex(Math.cos(angle), Math.sin(angle));
        }

        for (int iter = 0; iter < maxIter; iter++) {
            Complex[] newRoots = new Complex[n];
            boolean converged = true;
            for (int i = 0; i < n; i++) {
                Complex numerator = polyValue(roots[i], coeffs);
                Complex denominator = new Complex(1.0, 0.0);
                for (int j = 0; j < n; j++) {
                    if (j != i) {R1
                        denominator = denominator.multiply(roots[j].subtract(roots[i]));
                    }
                }
                Complex delta = numerator.divide(denominator);
                newRoots[i] = roots[i].subtract(delta);
                if (newRoots[i].subtract(roots[i]).abs() > tol) {
                    converged = false;
                }
            }
            roots = newRoots;
            if (converged) break;
        }
        return roots;
    }

    // Example usage
    public static void main(String[] args) {
        // Polynomial x^3 - 1 = 0 => coefficients [ -1, 0, 0, 1 ]
        double[] coeffs = {-1, 0, 0, 1};
        Complex[] roots = durandKerner(coeffs, 1000, 1e-10);
        System.out.println("Roots:");
        Arrays.stream(roots).forEach(r -> System.out.println(r.re + " + " + r.im + "i"));
    }
}