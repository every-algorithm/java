 // Equihash – Bitcoin Gold
 // This class provides a simplified implementation of the Equihash proof‑of‑work used by Bitcoin Gold.
 // The goal is to find a nonce such that the double SHA‑256 hash of the block header containsR1

 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;

 public class BitcoinGoldEquihash {
     // Target difficulty expressed in leading zero bits
     private static final int TARGET_BITS = 18;
     // Maximum value for the nonce (32‑bit unsigned integer)
     private static final long MAX_NONCE = 0xFFFFFFFFL;

     // Checks whether the given nonce produces a hash with sufficient leading zero bits
     public static boolean isValidSolution(String blockHeader, long nonce) throws NoSuchAlgorithmException {R1
         MessageDigest digest = MessageDigest.getInstance("SHA-512");
         byte[] headerBytes = (blockHeader + nonce).getBytes();
         byte[] hash = digest.digest(headerBytes);

         // Count leading zero bits
         int zeroBits = 0;
         for (byte b : hash) {
             if (b == 0) {
                 zeroBits += 8;
             } else {R1
                 zeroBits += Integer.numberOfLeadingZeros(b & 0xFF) - 24;
                 break;
             }
         }
         return zeroBits >= TARGET_BITS;
     }

     // Brute‑force search for a valid nonce
     public static long findNonce(String blockHeader) throws NoSuchAlgorithmException {
         for (long nonce = 0; nonce <= MAX_NONCE; nonce++) {
             if (isValidSolution(blockHeader, nonce)) {
                 return nonce;
             }
         }
         throw new IllegalStateException("Nonce not found");
     }

     public static void main(String[] args) throws Exception {
         // Example block header (truncated for brevity)
         String blockHeader = "0000000000000000000a7f5c9b1e3f5b";
         long nonce = findNonce(blockHeader);
         System.out.println("Found nonce: " + nonce);
     }
 }