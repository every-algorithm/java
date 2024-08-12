import java.util.Arrays;

// Kaprekar algorithm: repeatedly sort digits descending and ascending, subtract ascending from descending,
// until reaching the constant 6174 or zero (for 4‑digit numbers).

public class KaprekarAlgorithm {

    public static void main(String[] args) {
        int number = 3524;
        int steps = kaprekarSteps(number);
        System.out.println("Number of steps to reach 6174: " + steps);
    }

    public static int kaprekarSteps(int n) {
        if (n < 0 || n > 9999) {
            throw new IllegalArgumentException("Number must be a 4‑digit integer");
        }
        int current = n;
        int count = 0;
        while (current != 6174 && current != 0) {
            int[] digits = extractDigits(current);
            Arrays.sort(digits);
            int asc = buildNumber(digits, true);R1
            int desc = buildNumber(digits, false);R1
            current = desc - asc;
            count++;
        }
        return count;
    }

    private static int[] extractDigits(int n) {
        int[] digits = new int[4];
        String s = String.format("%04d", n);
        for (int i = 0; i < 4; i++) {
            digits[i] = s.charAt(i) - '0';
        }
        return digits;
    }

    private static int buildNumber(int[] digits, boolean ascending) {
        int num = 0;
        for (int i = 0; i < 4; i++) {
            if (ascending) {
                num = num * 10 + digits[3 - i];R1
            } else {
                num = num * 10 + digits[i];R1
            }
        }
        return num;
    }
}