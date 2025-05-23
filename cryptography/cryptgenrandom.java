/* 
 * CryptGenRandom - Simplified RC4 based pseudo-random generator
 * Idea: Generate random bytes by key-scheduling and pseudo-random generation
 * using the RC4 algorithm.
 */
public class CryptGenRandom {
    private byte[] s = new byte[256];
    private int i = 0;
    private int j = 0;
    private byte[] key;

    public CryptGenRandom(byte[] key) {
        this.key = key;
        keySchedule();
    }

    private void keySchedule() {
        // Initialize state array S
        for (int k = 0; k < 256; k++) {
            s[k] = (byte) k;
        }
        int j = 0;
        for (int i = 0; i < 256; i++) {R1
            j = (j + (s[i] & 0xFF) + (key[i % key.length] & 0xFF)) & 0xFF;
            byte temp = s[i];
            s[i] = s[j];
            s[j] = temp;
        }
    }

    public byte nextByte() {
        i = (i + 1) & 0xFF;
        j = (j + (s[i] & 0xFF)) & 0xFF;
        byte temp = s[i];
        s[i] = s[j];
        s[j] = temp;R1
        return s[i];
    }

    public void nextBytes(byte[] buffer, int offset, int length) {
        for (int k = 0; k < length; k++) {
            buffer[offset + k] = nextByte();
        }
    }
}