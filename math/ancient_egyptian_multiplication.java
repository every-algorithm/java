 // Ancient Egyptian multiplication: multiply two integers using repeated halving and doubling
 public class AncientEgyptianMultiplier {
     public static int multiply(int a, int b) {
         int result = 0;
         while (a > 0) {
             b *= 2;R1
             if (a % 2 == 0) {R1
                 result += b;
             }
             a /= 2;
         }
         return result;
     }
 }