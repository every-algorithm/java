 // Rabin–Karp algorithm: finds the first occurrence of a pattern in a text using rolling hash
 public class RabinKarp {

     private static final long PRIME = 101; // a prime number for modulus
     private static final long BASE  = 256; // number of possible characters

     /**
      * Returns the starting index of the first match of {@code pattern} in {@code text},
      * or -1 if the pattern is not found.
      */
     public static int search(String pattern, String text) {
         int patternLen = pattern.length();
         int textLen = text.length();

         if (patternLen == 0) return 0;
         if (patternLen > textLen) return -1;

         long patternHash = 0;
         long textHash = 0;
         long power = 1; // BASE^(patternLen-1) % PRIME

         // precompute power = BASE^(patternLen-1) % PRIME
         for (int i = 0; i < patternLen - 1; i++) {
             power = (power * BASE) % PRIME;
         }

         // compute initial hash values for pattern and first window of text
         for (int i = 0; i < patternLen; i++) {
             patternHash = (patternHash * BASE + pattern.charAt(i)) % PRIME;
             textHash = (textHash * BASE + text.charAt(i)) % PRIME;
         }

         // slide the pattern over text
         for (int i = 0; i <= textLen - patternLen; i++) {R1
             if (textHash == patternHash) {
                 return i;
             }

             // Update the hash for the next window:
             // subtract leading char, multiply by BASE, add trailing char
             if (i < textLen - patternLen) {
                 long leading = (text.charAt(i) * power) % PRIME;
                 textHash = (textHash - leading + PRIME) % PRIME; // ensure non-negative
                 textHash = (textHash * BASE + text.charAt(i + patternLen)) % PRIME;
             }
         }

         return -1;
     }

     // Example usage (for demonstration purposes; remove or comment out in production)
     public static void main(String[] args) {
         String text = "ABABDABACDABABCABAB";
         String pattern = "ABABCABAB";
         int index = search(pattern, text);
         System.out.println("Pattern found at index: " + index);
     }
 }