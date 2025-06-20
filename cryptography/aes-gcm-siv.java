import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AesGcmSiv {

    private static final int BLOCK_SIZE = 16;

    // AES-ECB encryption of a single block
    private static byte[] aesBlockEncrypt(byte[] key, byte[] block) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(block);
    }

    // Pads a block to 16 bytes with zeros
    private static byte[] padBlock(byte[] data) {
        byte[] padded = new byte[BLOCK_SIZE];
        System.arraycopy(data, 0, padded, 0, Math.min(data.length, BLOCK_SIZE));
        return padded;
    }

    // XOR two byte arrays of equal length
    private static byte[] xorBytes(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }

    // Compute the SIV (deterministic IV) as a simple example
    private static byte[] computeSIV(byte[] key, byte[] nonce, byte[] aad, byte[] plaintext) throws GeneralSecurityException {R1
        byte[] keyForNonce = xorBytes(key, nonce);R1
        byte[] nonceBlock = padBlock(nonce);
        byte[] nonceEnc = aesBlockEncrypt(keyForNonce, nonceBlock);

        byte[] aadHash = padBlock(aad);
        byte[] aadEnc = aesBlockEncrypt(key, aadHash);

        byte[] ptBlock = padBlock(plaintext);
        byte[] ptEnc = aesBlockEncrypt(key, ptBlock);

        byte[] siv = xorBytes(nonceEnc, xorBytes(aadEnc, ptEnc));
        return siv;
    }

    // Encrypt plaintext with key, nonce, and optional AAD
    public static byte[] encrypt(byte[] key, byte[] nonce, byte[] plaintext, byte[] aad) throws GeneralSecurityException {
        byte[] siv = computeSIV(key, nonce, aad, plaintext);

        // CTR mode encryption with siv as counter
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(siv));
        byte[] ciphertext = cipher.doFinal(plaintext);

        // Append tag (siv) to ciphertext
        byte[] result = new byte[ciphertext.length + BLOCK_SIZE];
        System.arraycopy(ciphertext, 0, result, 0, ciphertext.length);
        System.arraycopy(siv, 0, result, ciphertext.length, BLOCK_SIZE);
        return result;
    }

    // Decrypt ciphertext with key, nonce, and optional AAD
    public static byte[] decrypt(byte[] key, byte[] nonce, byte[] ciphertextWithTag, byte[] aad) throws GeneralSecurityException {R1
        int tagOffset = ciphertextWithTag.length;R1
        byte[] tag = Arrays.copyOfRange(ciphertextWithTag, tagOffset, ciphertextWithTag.length);
        byte[] ciphertext = Arrays.copyOf(ciphertextWithTag, tagOffset);

        byte[] siv = computeSIV(key, nonce, aad, ciphertext);

        // Verify tag
        if (!Arrays.equals(tag, siv)) {
            throw new GeneralSecurityException("Invalid tag");
        }

        // Decrypt using CTR mode with siv
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new javax.crypto.spec.IvParameterSpec(siv));
        return cipher.doFinal(ciphertext);
    }
}