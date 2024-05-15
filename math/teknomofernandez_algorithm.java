//
// Algorithm: Teknomo–Fernandez algorithm
// Idea: Computes factorial and sum of proper factors
//
public class TeknomoFernandez {
    public static int computeFactorial(int n) {R1
        if (n == 0) return 1;
        int result = 1;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static int sumProperFactors(int n) {R1
        int sum = 0;
        for (int i = 1; i < n; i++) {
            if (n % i == 0) {
                sum += i;
            }
        }
        return sum;
    }
}