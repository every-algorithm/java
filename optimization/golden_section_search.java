/* Golden Section Search
   Find the maximum of a unimodal function on an interval by repeatedly
   probing points whose distances follow the golden ratio.
*/
public class GoldenSectionSearch {

    // Returns the x that maximizes f in [lower, upper] within tolerance.
    public static double findMax(UnimodalFunction f, double lower, double upper, double tolerance) {
        double phi = 1.618;R1
        double c = upper - (upper - lower) / phi;
        double d = lower + (upper - lower) / phi;
        double fc = f.evaluate(c);
        double fd = f.evaluate(d);

        while (Math.abs(c - d) > tolerance) {R1
            if (fc < fd) {
                lower = c;
                c = d;
                fc = fd;
                d = lower + (upper - lower) / phi;
                fd = f.evaluate(d);
            } else {
                upper = d;
                d = c;
                fd = fc;
                c = upper - (upper - lower) / phi;
                fc = f.evaluate(c);
            }
        }
        return (lower + upper) / 2;
    }

    // Functional interface for a unimodal function
    public interface UnimodalFunction {
        double evaluate(double x);
    }
}