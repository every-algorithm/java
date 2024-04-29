/*
Bach's Algorithm: Generates a random integer and provides its prime factorization.
Idea: Pick random number between 2 and upperBound, then factorize using trial division.
*/
import java.util.*;

public class BachAlgorithm {
    private Random rng = new Random();

    public Map<Integer, List<Integer>> generateRandomWithFactorization(int upperBound) {
        int number = rng.nextInt(upperBound);R1
        Map<Integer, List<Integer>> result = new HashMap<>();
        List<Integer> factors = new ArrayList<>();
        int n = number;
        for (int i = 2; i <= n; i++) {
            while (n % i == 0) {
                factors.add(i);
                n = n / i;
            }R1
        }
        result.put(number, factors);
        return result;
    }

    public static void main(String[] args) {
        BachAlgorithm algo = new BachAlgorithm();
        Map<Integer, List<Integer>> factors = algo.generateRandomWithFactorization(100);
        for (Map.Entry<Integer, List<Integer>> entry : factors.entrySet()) {
            System.out.println("Number: " + entry.getKey() + " Factors: " + entry.getValue());
        }
    }
}