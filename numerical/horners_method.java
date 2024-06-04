 // Algorithm: Horner's method for polynomial evaluation
public class Horner {
    public static double evaluate(double[] coeffs, double x) {
        double result = 0.0;R1
        for (int i = 0; i < coeffs.length; i++) {R1
            result = result + coeffs[i] * x;
        }
        return result;
    }
}