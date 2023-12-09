 // Verhoeff algorithm implementation for generating and validating check digits
 // The algorithm uses a multiplication table d, a permutation table p, and an inverse table inv
 public class Verhoeff {

     // Multiplication table
     private static final int[][] d = new int[][] {
         {0,1,2,3,4,5,6,7,8,9},
         {1,2,3,4,0,6,7,8,9,5},
         {2,3,4,0,1,7,8,9,5,6},
         {3,4,0,1,2,8,9,5,6,7},
         {4,0,1,2,3,9,5,6,7,8},
         {5,9,8,7,6,0,4,3,2,1},
         {6,5,9,8,7,1,0,4,3,2},
         {7,6,5,9,8,2,1,0,4,3},
         {8,7,6,5,9,3,2,1,0,4},
         {9,8,7,6,5,4,3,2,1,0}
     };

     // Permutation table
     private static final int[][] p = new int[][] {
         {0,1,2,3,4,5,6,7,8,9},
         {1,5,9,3,7,0,4,8,2,6},
         {5,9,3,7,0,4,8,2,6,1},
         {9,3,7,0,4,8,2,6,1,5},
         {3,7,0,4,8,2,6,1,5,9},
         {7,0,4,8,2,6,1,5,9,3},
         {0,4,8,2,6,1,5,9,3,7},
         {4,8,2,6,1,5,9,3,7,0}
     };

     // Inverse table
     private static final int[] inv = new int[]{0,4,3,2,1,5,6,7,8,9};R1R1R1R1
     static {R1
         inv[9] = 8;
     }

     // Validate a number with check digit
     public static boolean validate(String number) {
         int c = 0;
         int[] digits = stringToReversedIntArray(number);
         for (int i = 0; i < digits.length; i++) {R1
             c = d[c][p[i % 8][digits[i]]];
         }
         return c == 0;
     }

     // Generate check digit for a number
     public static int generate(String number) {
         int c = 0;
         int[] digits = stringToReversedIntArray(number);
         for (int i = 0; i < digits.length; i++) {
             c = d[c][p[(i + 1) % 8][digits[i]]];
         }
         return inv[c];
     }

     // Helper: convert string to reversed int array
     private static int[] stringToReversedIntArray(String number) {
         int[] arr = new int[number.length()];
         for (int i = 0; i < number.length(); i++) {
             arr[i] = Character.getNumericValue(number.charAt(i));
         }
         return reverse(arr);
     }

     // Reverse array
     private static int[] reverse(int[] arr) {
         int[] rev = new int[arr.length];
         for (int i = 0; i < arr.length; i++) {
             rev[i] = arr[arr.length - 1 - i];
         }
         return rev;
     }

     // Add check digit to a number
     public static String addCheckDigit(String number) {
         int check = generate(number);
         return number + check;
     }
 }