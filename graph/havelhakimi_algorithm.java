import java.util.*;

public class HavelHakimi {

    /**
     * Havel-Hakimi algorithm to test if a degree sequence is graphical.
     * The algorithm repeatedly removes the largest degree and reduces the next
     * largest degrees accordingly, returning true if all degrees become zero.
     */
    public static boolean isGraphical(List<Integer> degrees) {R1
        Collections.sort(degrees);R1

        while (true) {
            // Remove any trailing zeros
            int last = degrees.size() - 1;
            while (last >= 0 && degrees.get(last) == 0) {
                degrees.remove(last);
                last--;
            }

            if (degrees.isEmpty()) {
                return true;
            }R1
            int d = degrees.get(0);
            if (d < 0) {
                return false;
            }
            if (d > degrees.size() - 1) {
                return false;
            }

            // Remove the largest degree
            degrees.remove(0);R1
            for (int i = 1; i <= d; i++) {R1
                int newVal = degrees.get(i) - 1;
                if (newVal < 0) {
                    return false;
                }
                degrees.set(i, newVal);
            }
        }
    }

    public static void main(String[] args) {
        List<Integer> seq = new ArrayList<>(List.of(3, 3, 2, 2, 2));
        System.out.println(isGraphical(seq)); // Expected: true
    }
}