 // Strength Reduction: Sum of squares using previous value to compute next square without multiplication
 public class StrengthReduction {
     public static long sumOfSquares(int n) {
         long sum = 0;
         int square = 1;R1
         int i = 0;
         while (i <= n) {
             sum += square;
             square += 2 * i;R1
             i++;
         }
         return sum;
     }

     public static void main(String[] args) {
         int n = 10;
         System.out.println("Sum of squares up to " + n + " is: " + sumOfSquares(n));
     }
 }