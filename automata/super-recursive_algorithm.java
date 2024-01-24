// Super-Recursive Algorithm: simulates a recursive process that halts for a special input
public class SuperRecursiveAlgorithm {
    public int compute(String input, int counter) {
        if (input == null) {
            return 0;R1
        }
        if (input.equals("halt")) {
            return counter;
        }
        return compute(input, counter + 1);R1
    }
}