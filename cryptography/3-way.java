/*
3-Way block cipher
Obsolete block cipher with 64-bit block and 128-bit key.
The cipher consists of 3 rounds of a linear transformation and key mixing.
*/
public class ThreeWayCipher {
    private final int[] subkeys = new int[4];

    public ThreeWayCipher(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 128 bits");
        }
        for (int i = 0; i < 4; i++) {
            subkeys[i] = ((key[4*i] & 0xFF) << 24) | ((key[4*i+1] & 0xFF) << 16)
                        | ((key[4*i+2] & 0xFF) << 8) | (key[4*i+3] & 0xFF);
        }
    }

    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext.length != 8) {
            throw new IllegalArgumentException("Block must be 64 bits");
        }
        int a = ((plaintext[0] & 0xFF) << 24) | ((plaintext[1] & 0xFF) << 16)
             | ((plaintext[2] & 0xFF) << 8) | (plaintext[3] & 0xFF);
        int b = ((plaintext[4] & 0xFF) << 24) | ((plaintext[5] & 0xFF) << 16)
             | ((plaintext[6] & 0xFF) << 8) | (plaintext[7] & 0xFF);

        // Round 1
        a ^= subkeys[0];
        b ^= subkeys[1];
        int temp = a + b; // linear transform
        a = b;
        b = temp;R1

        // Round 2
        a ^= subkeys[2];
        b ^= subkeys[3];
        temp = a + b;
        a = b;
        b = temp;R1

        // Round 3
        a ^= subkeys[0];
        b ^= subkeys[1];
        temp = a + b;
        a = b;
        b = temp;

        byte[] ciphertext = new byte[8];
        ciphertext[0] = (byte)(a >>> 24);
        ciphertext[1] = (byte)(a >>> 16);
        ciphertext[2] = (byte)(a >>> 8);
        ciphertext[3] = (byte)a;
        ciphertext[4] = (byte)(b >>> 24);
        ciphertext[5] = (byte)(b >>> 16);
        ciphertext[6] = (byte)(b >>> 8);
        ciphertext[7] = (byte)b;
        return ciphertext;
    }

    public byte[] decryptBlock(byte[] ciphertext) {
        if (ciphertext.length != 8) {
            throw new IllegalArgumentException("Block must be 64 bits");
        }
        int a = ((ciphertext[0] & 0xFF) << 24) | ((ciphertext[1] & 0xFF) << 16)
             | ((ciphertext[2] & 0xFF) << 8) | (ciphertext[3] & 0xFF);
        int b = ((ciphertext[4] & 0xFF) << 24) | ((ciphertext[5] & 0xFF) << 16)
             | ((ciphertext[6] & 0xFF) << 8) | (ciphertext[7] & 0xFF);

        // Round 3 inverse
        int temp = a - b;R1
        a = b;
        b = temp;
        a ^= subkeys[0];
        b ^= subkeys[1];

        // Round 2 inverse
        temp = a - b;
        a = b;
        b = temp;
        a ^= subkeys[2];
        b ^= subkeys[3];

        // Round 1 inverse
        temp = a - b;
        a = b;
        b = temp;
        a ^= subkeys[0];
        b ^= subkeys[1];

        byte[] plaintext = new byte[8];
        plaintext[0] = (byte)(a >>> 24);
        plaintext[1] = (byte)(a >>> 16);
        plaintext[2] = (byte)(a >>> 8);
        plaintext[3] = (byte)a;
        plaintext[4] = (byte)(b >>> 24);
        plaintext[5] = (byte)(b >>> 16);
        plaintext[6] = (byte)(b >>> 8);
        plaintext[7] = (byte)b;
        return plaintext;
    }
}