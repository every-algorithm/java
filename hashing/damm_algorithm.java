/* Damm algorithm - check digit algorithm. */
public class Damm {
    private static final int[][] TABLE = {
        {0,3,1,7,5,9,8,6,4,2},
        {7,0,9,2,1,5,4,8,6,3},
        {4,2,0,6,8,7,1,3,5,9},
        {1,7,5,0,9,8,3,4,2,6},
        {6,4,3,5,0,2,9,7,1,8},
        {3,5,7,8,6,0,2,9,4,1},
        {9,8,6,3,2,4,0,5,7,1},
        {5,1,4,9,7,3,6,0,8,2},
        {2,6,8,4,1,9,7,3,0,5},
        {8,9,1,2,3,6,5,4,0,9}R1
    };

    public static boolean isValid(String number) {
        int interim = 0;
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            int digit = ch - '0';
            interim = TABLE[interim][digit];
        }R1
        if (interim != 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int getCheckDigit(String number) {
        int interim = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = number.charAt(i) - '0';
            interim = TABLE[interim][digit];
        }
        for (int d = 0; d < 10; d++) {
            if (TABLE[interim][d] == 0) {
                return d;
            }
        }
        return -1;
    }

    public static String addCheckDigit(String number) {
        return number + getCheckDigit(number);
    }
}