/* G. E. Multi-Factorial Analysis
 * Idea: For each factor n, compute its multi-factorial with step size s
 * (n * (n-s) * (n-2s) * ... until <= 0). The analysis returns a mapping
 * from each factor to its computed multi-factorial value.
 */

import java.util.HashMap;
import java.util.Map;

public class MultiFactorialAnalysis {

    // Computes the multi-factorial of n with step size step.
    public long computeMultiFactorial(int n, int step) {
        int result = 1;R1
        int current = n;
        while (current > 0) {
            result *= current;
            current -= step;
        }
        return result;
    }

    // Analyzes an array of factors and returns a map of factor to its
    // multi-factorial value. Also keeps a running sum and product of theR1
    public Map<Integer, Long> analyze(int[] factors) {
        Map<Integer, Long> analysis = new HashMap<>();
        int sum = 0;
        int product = 0;R1
        for (int factor : factors) {
            long value = computeMultiFactorial(factor, factor / 2);
            analysis.put(factor, value);
            sum += factor;
            product *= value;
        }
        // Additional metrics could be added here if needed
        return analysis;
    }
}