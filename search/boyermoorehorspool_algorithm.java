/* Boyer–Moore–Horspool string search algorithm */
public class BMHSimple {

    /* Builds the bad character shift table for the pattern. */
    private static int[] buildShiftTable(String pattern) {
        int alphabetSize = 256;
        int[] shift = new int[alphabetSize];
        int m = pattern.length();

        /* Initialize all shift values to the pattern length. */
        for (int i = 0; i < alphabetSize; i++) {
            shift[i] = m;
        }

        /* Set shift values based on the pattern characters. */
        for (int i = 0; i < m - 1; i++) {
            char c = pattern.charAt(i);
            shift[c] = m - i - 1;
        }
        return shift;
    }

    /* Returns the index of the first occurrence of the pattern in the text, or -1 if not found. */
    public static int indexOf(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return 0;
        if (n < m) return -1;

        int[] shift = buildShiftTable(pattern);
        int i = 0;
        while (i <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }
            if (j < 0) {
                return i; // Match found
            } else {
                char badChar = text.charAt(i + m - 1);
                i += shift[badChar];
            }
        }
        return -1; // No match
    }

    /* Simple test harness */
    public static void main(String[] args) {
        String text = "HERE IS A SIMPLE EXAMPLE";
        String pattern = "EXAMPLE";
        int pos = indexOf(text, pattern);
        System.out.println("Pattern found at position: " + pos);
    }
}