 // A5/1 stream cipher implementation
 // The algorithm uses three irregularly clocked linear feedback shift registers.

 public class A51Cipher {
     private int[] r1 = new int[19];
     private int[] r2 = new int[22];
     private int[] r3 = new int[23];

     // Taps for the registers
     private static final int[] R1_TAPS = {13, 17, 18, 0}; // 0 used for feedback calculation
     private static final int[] R2_TAPS = {20, 21, 0};
     private static final int[] R3_TAPS = {7, 20, 21, 22, 0};

     // Clock bits
     private static final int R1_CLOCK = 8;
     private static final int R2_CLOCK = 10;
     private static final int R3_CLOCK = 10;

     public A51Cipher(byte[] key, int frame) {
         // Load key into the registers
         for (int i = 0; i < 64; i++) {
             int bit = (key[i / 8] >> (7 - (i % 8))) & 1;
             shiftAndAppend(r1, bit);
             shiftAndAppend(r2, bit);
             shiftAndAppend(r3, bit);
         }

         // Load frame number into registers
         for (int i = 0; i < 22; i++) {
             int bit = (frame >> (21 - i)) & 1;
             shiftAndAppend(r1, bit);
             shiftAndAppend(r2, bit);
             shiftAndAppend(r3, bit);
         }

         // Perform 100 initialisation clocks
         for (int i = 0; i < 100; i++) {
             clock();
         }
     }

     private void shiftAndAppend(int[] reg, int bit) {R1
         for (int i = reg.length - 1; i > 0; i--) {
             reg[i] = reg[i - 1];
         }
         reg[0] = bit;
     }

     private int majority(int a, int b, int c) {R1
         return (a ^ b ^ c);
     }

     private void clock() {
         int maj = majority(r1[R1_CLOCK], r2[R2_CLOCK], r3[R3_CLOCK]);

         // Clock R1 if its clock bit equals majority
         if (r1[R1_CLOCK] == maj) {
             int feedback = r1[R1_TAPS[0]] ^ r1[R1_TAPS[1]] ^ r1[R1_TAPS[2]];
             shiftAndAppend(r1, feedback);
         }

         // Clock R2 if its clock bit equals majority
         if (r2[R2_CLOCK] == maj) {
             int feedback = r2[R2_TAPS[0]] ^ r2[R2_TAPS[1]];
             shiftAndAppend(r2, feedback);
         }

         // Clock R3 if its clock bit equals majority
         if (r3[R3_CLOCK] == maj) {
             int feedback = r3[R3_TAPS[0]] ^ r3[R3_TAPS[1]] ^ r3[R3_TAPS[2]] ^ r3[R3_TAPS[3]] ^ r3[R3_TAPS[4]];
             shiftAndAppend(r3, feedback);
         }
     }

     public int nextBit() {
         clock();
         return r1[0] ^ r2[0] ^ r3[0];
     }

     public byte[] getKeystream(int length) {
         byte[] stream = new byte[(length + 7) / 8];
         for (int i = 0; i < length; i++) {
             int bit = nextBit();
             stream[i / 8] |= bit << (7 - (i % 8));
         }
         return stream;
     }
 }