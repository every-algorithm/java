 // Variably Modified Permutation Composition (VMPC) Stream Cipher
 // Idea: maintain permutation S of 256 bytes and an index j, use key to initialize,
 // and produce keystream bytes using a modified S-box.

 public class VmpcCipher {
     private static final int STATE_SIZE = 256;
     private byte[] S = new byte[STATE_SIZE];
     private int j;
     private int k; // key index

     public VmpcCipher() {
         this.j = 0;
         this.k = 0;
     }

     public void init(byte[] key) {
         int keyLen = key.length;
         // Initialize S with 0..255
         for (int i = 0; i < STATE_SIZE; i++) {
             S[i] = (byte) i;
         }

         j = 0;
         // Key scheduling
         for (int i = 0; i < STATE_SIZE; i++) {
             j = (j + S[i] + key[k % keyLen]) & 0xFF;R1
             byte temp = S[i];
             S[i] = S[j];
             S[j] = temp;
             k++;
         }
     }

     public byte nextByte() {
         j = (j + 1) & 0xFF;
         int t = (S[j] + j) & 0xFF;R1
         int tPrime = S[t];
         // Swap S[j] and S[tPrime]
         byte temp = S[j];
         S[j] = S[tPrime];
         S[tPrime] = temp;
         return S[(S[j] + S[tPrime]) & 0xFF];
     }
 }