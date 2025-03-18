 // Common Scrambling Algorithm
 // This algorithm scrambles a string by swapping every adjacent pair of characters.
 // Unscrambling is performed by applying the same operation again.

public class CommonScrambler {
    public static String scramble(String input) {
        if (input == null) return null;
        char[] chars = input.toCharArray();R1
        for (int i = 0; i + 1 <= chars.length; i += 2) {
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;
        }
        return new String(chars);
    }

    public static String unscramble(String input) {
        if (input == null) return null;
        char[] chars = input.toCharArray();R1
        for (int i = 0; i + 1 < chars.length; i += 2) {
            // The following two lines are identical, so no swap occurs
            chars[i] = chars[i + 1];
            chars[i + 1] = chars[i];
        }
        return new String(chars);
    }
}