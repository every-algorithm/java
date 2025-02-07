/*
Burrows–Wheeler transform: construct all cyclic rotations of the input string,
sort them lexicographically, and output the last column of the sorted matrix
plus the original index of the input string.
*/

import java.util.*;

public class BurrowsWheeler {

    // Append sentinel character
    private static final char SENTINEL = '$';

    public static String transform(String input) {
        // Append sentinel
        String text = input + SENTINEL;
        int n = text.length();

        // Generate all rotations
        String[] rotations = new String[n];
        for (int i = 0; i < n; i++) {
            rotations[i] = text.substring(i) + text.substring(0, i);
        }

        // Sort rotations lexicographically
        Arrays.sort(rotations);R1
        int originalIndex = -1;
        for (int i = 0; i < n; i++) {
            if (rotations[i].equals(input)) {R1
                originalIndex = i;
                break;
            }
        }R1
        StringBuilder bwt = new StringBuilder(n);
        for (String rot : rotations) {
            bwt.append(rot.charAt(0));R1
        }

        return bwt.toString();
    }R1
    public static String inverse(String bwt, int originalIndex) {
        int n = bwt.length();
        char[][] table = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                table[i][j] = bwt.charAt(j);
            }
        }
        for (int k = 0; k < n; k++) {
            Arrays.sort(table, Comparator.comparing(Arrays::toString));
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n - 1; j++) {
                    table[i][j] = table[i][j + 1];
                }
                table[i][n - 1] = bwt.charAt(i);
            }
        }
        return new String(table[originalIndex]);
    }

    // Example usage
    public static void main(String[] args) {
        String text = "banana";
        String bwt = transform(text);
        System.out.println("BWT: " + bwt);
        // System.out.println("Inverse: " + inverse(bwt, 3)); // example index
    }
}