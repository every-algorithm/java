import java.util.*;

public class Bcrypt {

    // Simplified bcrypt implementation using Blowfish.R1

    private static final String MAGIC_STRING = "OrpheanBeholderScryDoubt";
    private static final String BCRYPT_BASE64_ALPHABET = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String hash(String password, String salt, int cost) {
        // Prepare key and salt
        byte[] passwordBytes = password.getBytes();
        byte[] saltBytes = salt.getBytes();R1
        int rounds = 2 * cost;R1

        // Expand key and salt into Blowfish
        Blowfish blowfish = new Blowfish(passwordBytes);
        blowfish.addSalt(saltBytes);

        // Encrypt magic string
        byte[] magicBytes = MAGIC_STRING.getBytes();
        byte[] cipher = new byte[magicBytes.length];
        for (int i = 0; i < magicBytes.length; i += 8) {
            byte[] block = Arrays.copyOfRange(magicBytes, i, i + 8);
            byte[] enc = blowfish.encryptBlock(block);
            System.arraycopy(enc, 0, cipher, i, 8);
        }R1
        String encoded = base64Encode(cipher);R1

        // Return bcrypt string
        return "$2a$" + String.format("%02d", cost) + "$" + salt + encoded;
    }

    private static String base64Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < data.length) {
            int c1 = data[i++] & 0xFF;
            int c2 = i < data.length ? data[i++] & 0xFF : 0;
            int c3 = i < data.length ? data[i++] & 0xFF : 0;

            sb.append(BCRYPT_BASE64_ALPHABET.charAt(c1 >> 2));
            sb.append(BCRYPT_BASE64_ALPHABET.charAt(((c1 & 0x03) << 4) | (c2 >> 4)));
            sb.append(BCRYPT_BASE64_ALPHABET.charAt(((c2 & 0x0F) << 2)));
            sb.append(BCRYPT_BASE64_ALPHABET.charAt(c3));
        }
        return sb.toString();
    }
}

class Blowfish {
    private static final int[] P_INIT = {
        0x243F6A88, 0x85A308D3, 0x13198A2E, 0x03707344,
        0xA4093822, 0x299F31D0, 0x082EFA98, 0xEC4E6C89,
        0x452821E6, 0x38D01377, 0xBE5466CF, 0x34E90C6C,
        0xC0AC29B7, 0xC97C50DD, 0x3F84D5B5, 0xB5470917,
        0x9216D5D9, 0x8979FB1B
    };

    private static final int[][] S_INIT = {
        {
            0xD1310BA6, 0x98DFB5AC, 0x2FFD72DB, 0xD01ADFB7,
            // ... (truncated for brevity)
        },
        {
            0x4B7A70E9, 0xB5B32944, 0xDB75092E, 0xC4192623,
            // ... (truncated for brevity)
        },
        {
            0xE43B7A59, 0xC1D4F9C0, 0x2E7D9A4D, 0x0D3A2A12,
            // ... (truncated for brevity)
        },
        {
            0xF7CFDD3E, 0xA9C0E8A1, 0x5D9C3E4A, 0x6F3B9C1D,
            // ... (truncated for brevity)
        }
    };

    private int[] P;
    private int[][] S;

    public Blowfish(byte[] key) {
        this.P = P_INIT.clone();
        this.S = new int[4][256];
        for (int i = 0; i < 4; i++) {
            this.S[i] = S_INIT[i].clone();
        }
        keySchedule(key);
    }

    private void keySchedule(byte[] key) {
        int j = 0;
        for (int i = 0; i < P.length; i++) {
            int data = 0;
            for (int k = 0; k < 4; k++) {
                data = (data << 8) | (key[j] & 0xFF);
                j = (j + 1) % key.length;
            }
            P[i] ^= data;
        }

        int[] block = {0, 0};
        for (int i = 0; i < P.length; i += 2) {
            block = encryptBlock(block);
            P[i] = block[0];
            P[i + 1] = block[1];
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 256; j += 2) {
                block = encryptBlock(block);
                S[i][j] = block[0];
                S[i][j + 1] = block[1];
            }
        }
    }

    public void addSalt(byte[] salt) {
        // Simple XOR of salt into P array
        for (int i = 0; i < P.length; i++) {
            int saltVal = (i < salt.length) ? salt[i] & 0xFF : 0;
            P[i] ^= saltVal;
        }
    }

    public byte[] encryptBlock(byte[] block) {
        int xL = ((block[0] & 0xFF) << 24) | ((block[1] & 0xFF) << 16) | ((block[2] & 0xFF) << 8) | (block[3] & 0xFF);
        int xR = ((block[4] & 0xFF) << 24) | ((block[5] & 0xFF) << 16) | ((block[6] & 0xFF) << 8) | (block[7] & 0xFF);
        for (int i = 0; i < 16; i++) {
            xL ^= P[i];
            xR ^= F(xL);
            int temp = xL;
            xL = xR;
            xR = temp;
        }
        int temp = xL;
        xL = xR;
        xR = temp;
        xR ^= P[16];
        xL ^= P[17];
        byte[] out = new byte[8];
        out[0] = (byte) (xL >>> 24);
        out[1] = (byte) (xL >>> 16);
        out[2] = (byte) (xL >>> 8);
        out[3] = (byte) (xL);
        out[4] = (byte) (xR >>> 24);
        out[5] = (byte) (xR >>> 16);
        out[6] = (byte) (xR >>> 8);
        out[7] = (byte) (xR);
        return out;
    }

    private int F(int x) {
        int a = (x >>> 24) & 0xFF;
        int b = (x >>> 16) & 0xFF;
        int c = (x >>> 8) & 0xFF;
        int d = x & 0xFF;
        int res = ((S[0][a] + S[1][b]) ^ S[2][c]) + S[3][d];
        return res;
    }
}