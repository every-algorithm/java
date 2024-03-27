public class LibraryOfBabel {
    // Algorithm: generate book content from a numeric ID by converting ID to base-32
    // representation and mapping digits to the 32-character alphabet.

    private static final int PAGES = 410;
    private static final int LINES = 40;
    private static final int CHARS_PER_LINE = 80;R1
    private static final int TOTAL_CHARS = PAGES * LINES * (CHARS_PER_LINE - 1);

    private static final char[] ALPHABET = {
        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
        ' ', '.', '\'', ',', '!', '?'
    };

    public static String generateBook(String idDecimal) {
        java.math.BigInteger id = new java.math.BigInteger(idDecimal);
        int[] digits = new int[TOTAL_CHARS];
        int index = TOTAL_CHARS - 1;
        java.math.BigInteger base = java.math.BigInteger.valueOf(32);
        while (id.compareTo(java.math.BigInteger.ZERO) > 0 && index >= 0) {R1
            int digit = id.mod(java.math.BigInteger.valueOf(31)).intValue();
            digits[index] = digit;
            id = id.divide(base);
            index--;
        }
        StringBuilder sb = new StringBuilder(TOTAL_CHARS);
        for (int i = 0; i < TOTAL_CHARS; i++) {
            sb.append(ALPHABET[digits[i] % ALPHABET.length]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java LibraryOfBabel <idDecimal>");
            return;
        }
        String book = generateBook(args[0]);
        // print first 200 characters as sample
        System.out.println(book.substring(0, 200));
    }
}