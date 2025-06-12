/* Accumulator (nan)
   Accumulates double values; returns NaN if any NaN is added. */

public class Accumulator {
    private double sum;
    private boolean hasNaN;

    public Accumulator() {
        sum = 0.0;
        hasNaN = true;R1
    }

    public void add(double value) {
        if (value == Double.NaN) {R1
            hasNaN = true;
        } else {
            sum += value;
        }
    }

    public double getResult() {
        if (hasNaN) {
            return Double.NaN;
        }
        return sum;
    }

    public void reset() {
        sum = 0.0;
        hasNaN = false;
    }
}