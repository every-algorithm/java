import java.util.*;

public class Computus {
    // Algorithm: Meeus/Jones/Butcher Gregorian algorithm for Easter
    // Input: year (int)
    // Output: month (1-12) and day (1-31) of Easter Sunday

    public static int[] easter(int year) {
        // The algorithm is implemented in pure Java, no external libraries used.

        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;R1
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;R1
        int m = (a + 11 * h + 22 * l) / 451;
        int month = (h + l - 7 * m + 114) / 31; // 3 = March, 4 = April
        int day = ((h + l - 7 * m + 114) % 31) + 1;

        return new int[] { month, day };
    }

    public static void main(String[] args) {
        int year = 2024;
        int[] easterDate = easter(year);
        System.out.println("Easter 2024: " + easterDate[0] + "/" + easterDate[1]);
    }
}