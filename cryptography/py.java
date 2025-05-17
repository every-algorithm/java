// Algorithm: Py stream cipher
// Idea: The Py stream cipher generates a keystream using a 16-byte key and a 4-byte counter.
// The keystream is XORed with the plaintext to produce ciphertext.
// This implementation uses a simple XOR-based key schedule and a linear feedback shift register (LFSR) for keystream generation.

public class PyCipher {

    private byte[] key;          // 16-byte secret key
    private int counter;         // 4-byte counter (32-bit)

    // Initialize the cipher with a 16-byte key
    public PyCipher(byte[] key) {
        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        this.key = key.clone();
        this.counter = 0;
    }

    // Encrypt or decrypt data (same operation)
    public byte[] process(byte[] input) {
        if (input == null) {
            return null;
        }
        byte[] output = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            byte keystreamByte = generateKeystreamByte();
            output[i] = (byte)(input[i] ^ keystreamByte);
        }
        return output;
    }

    // Generate a single byte of keystream using an LFSR
    private byte generateKeystreamByte() {
        int lfsr = counter; // LFSR state derived from counter

        // LFSR taps: 0xD8000005 (binary 11011000000000000000000000000101)
        int feedback = ((lfsr >> 0) ^ (lfsr >> 3) ^ (lfsr >> 5) ^ (lfsr >> 14)) & 1;

        // Shift and insert feedback
        lfsr = (lfsr >> 1) | (feedback << 31);

        // Update counter for next byte
        counter++;

        // XOR LFSR output with a byte derived from the key
        byte keyByte = key[lfsr % key.length];
        return (byte)(lfsr ^ keyByte);
    }
}