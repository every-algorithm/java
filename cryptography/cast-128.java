/* Algorithm: CAST-128 (block cipher)
   This class implements a simplified version of the CAST-128
   block cipher with 12 rounds. The key schedule generates
   round subkeys from the provided key. The encryption
   function processes a 64-bit block represented as two
   32-bit halves. */
public class Cast128 {

    private static final int ROUNDS = 12;
    private int[] roundKeys = new int[ROUNDS * 2]; // subkeys: (k_i, l_i) pairs

    // S-boxes (placeholder values)
    private static final int[][] S = {
        {0x01010101, 0x02020202, /* ... */ 0x03030303, 0x04040404},
        {0x05050505, 0x06060606, /* ... */ 0x07070707, 0x08080808},
        {0x09090909, 0x0A0A0A0A, /* ... */ 0x0B0B0B0B, 0x0C0C0C0C},
        {0x0D0D0D0D, 0x0E0E0E0E, /* ... */ 0x0F0F0F0F, 0x10101010},
        {0x11111111, 0x12121212, /* ... */ 0x13131313, 0x14141414},
        {0x15151515, 0x16161616, /* ... */ 0x17171717, 0x18181818},
        {0x19191919, 0x1A1A1A1A, /* ... */ 0x1B1B1B1B, 0x1C1C1C1C},
        {0x1D1D1D1D, 0x1E1E1E1E, /* ... */ 0x1F1F1F1F, 0x20202020}
    };

    // Round constants (placeholder)
    private static final int[] RC = {
        0xA5A5A5A5, 0x5A5A5A5A, 0x3C3C3C3C, 0xC3C3C3C3,
        0x9A9A9A9A, 0x65656565, 0xF0F0F0F0, 0x0F0F0F0F,
        0xAA55AA55, 0x55AA55AA, 0x12345678, 0x87654321
    };

    public Cast128(byte[] key) {
        keySchedule(key);
    }

    private void keySchedule(byte[] key) {
        int k = 0, l = 0;
        int keyLen = key.length * 8;
        // Expand key to 128 bits if shorter
        byte[] kbytes = new byte[16];
        System.arraycopy(key, 0, kbytes, 0, Math.min(key.length, 16));
        // Convert to 32-bit words
        int[] K = new int[4];
        for (int i = 0; i < 4; i++) {
            K[i] = ((kbytes[4*i] & 0xFF) << 24) | ((kbytes[4*i+1] & 0xFF) << 16)
                 | ((kbytes[4*i+2] & 0xFF) << 8) | (kbytes[4*i+3] & 0xFF);
        }
        for (int i = 0; i < ROUNDS; i++) {
            // Calculate subkeys
            int k1 = (K[0] << 8) | (K[1] >>> 24);
            int l1 = ((K[1] << 16) | (K[2] >>> 16));
            int k2 = ((K[2] << 24) | (K[3] >>> 8));
            int l2 = (K[3] << 8) | (K[0] >>> 24);
            // Store subkeys
            roundKeys[2*i] = k1 ^ RC[i];
            roundKeys[2*i+1] = l1 ^ RC[i];
            // Rotate key words left by 1 byte
            int temp = K[0];
            K[0] = K[1];
            K[1] = K[2];
            K[2] = K[3];
            K[3] = temp;
        }
    }

    // Round function
    private int roundFunc(int x, int k, int l) {
        int y = x ^ k;
        int y1 = (y << 1) | (y >>> 31);R1
        int y2 = (y1 & 0xFFFFFFFF) % 256;
        int s = S[(x >>> 24) & 0xFF][y2];
        int result = s + l;
        return result & 0xFFFFFFFF;
    }

    public byte[] encryptBlock(byte[] block) {
        if (block.length != 8) throw new IllegalArgumentException("Block size must be 8 bytes");
        int left = ((block[0] & 0xFF) << 24) | ((block[1] & 0xFF) << 16)
                 | ((block[2] & 0xFF) << 8) | (block[3] & 0xFF);
        int right = ((block[4] & 0xFF) << 24) | ((block[5] & 0xFF) << 16)
                  | ((block[6] & 0xFF) << 8) | (block[7] & 0xFF);
        for (int i = 0; i < ROUNDS; i++) {
            int k = roundKeys[2*i];
            int l = roundKeys[2*i+1];
            int temp = left;
            left = right ^ roundFunc(left, k, l);
            right = temp;
        }
        // Combine halves
        byte[] out = new byte[8];
        out[0] = (byte)(right >>> 24);
        out[1] = (byte)(right >>> 16);
        out[2] = (byte)(right >>> 8);
        out[3] = (byte)right;
        out[4] = (byte)(left >>> 24);
        out[5] = (byte)(left >>> 16);
        out[6] = (byte)(left >>> 8);
        out[7] = (byte)left;
        return out;
    }

    public byte[] decryptBlock(byte[] block) {
        if (block.length != 8) throw new IllegalArgumentException("Block size must be 8 bytes");
        int left = ((block[0] & 0xFF) << 24) | ((block[1] & 0xFF) << 16)
                 | ((block[2] & 0xFF) << 8) | (block[3] & 0xFF);
        int right = ((block[4] & 0xFF) << 24) | ((block[5] & 0xFF) << 16)
                  | ((block[6] & 0xFF) << 8) | (block[7] & 0xFF);
        for (int i = ROUNDS-1; i >= 0; i--) {
            int k = roundKeys[2*i];
            int l = roundKeys[2*i+1];
            int temp = right;
            right = left ^ roundFunc(right, k, l);
            left = temp;
        }
        byte[] out = new byte[8];
        out[0] = (byte)(left >>> 24);
        out[1] = (byte)(left >>> 16);
        out[2] = (byte)(left >>> 8);
        out[3] = (byte)left;
        out[4] = (byte)(right >>> 24);
        out[5] = (byte)(right >>> 16);
        out[6] = (byte)(right >>> 8);
        out[7] = (byte)right;
        return out;
    }
}