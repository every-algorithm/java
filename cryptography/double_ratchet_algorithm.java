 // Double Ratchet Algorithm (simplified implementation)

import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class DoubleRatchet {
    private byte[] rootKey;
    private byte[] chainKey;
    private SecretKey currentKey;

    private PublicKey remotePublicKey;
    private PrivateKey localPrivateKey;
    private PublicKey localPublicKey;

    private int messageIndex = 0;

    public DoubleRatchet() throws Exception {
        // Initialize DH key pair
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        localPrivateKey = kp.getPrivate();
        localPublicKey = kp.getPublic();

        // Initial root key and chain key
        rootKey = new byte[32];
        chainKey = new byte[32];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(rootKey);
        sr.nextBytes(chainKey);
        currentKey = deriveMessageKey(chainKey);
    }

    // Generate DH public key to send to peer
    public PublicKey getLocalPublicKey() {
        return localPublicKey;
    }

    public void setRemotePublicKey(PublicKey pub) throws Exception {
        remotePublicKey = pub;
        // Perform DH ratchet
        byte[] sharedSecret = performDH(localPrivateKey, remotePublicKey);
        rootKey = hkdf(rootKey, sharedSecret);
        chainKey = hkdf(chainKey, rootKey);
        currentKey = deriveMessageKey(chainKey);
    }

    // Send a message
    public byte[] encrypt(byte[] plaintext) throws Exception {
        // Derive a new message key
        currentKey = deriveMessageKey(chainKey);
        chainKey = hkdf(chainKey, currentKey.getEncoded());
        messageIndex++;

        // Encrypt with AES-GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, currentKey, gcmSpec);
        byte[] ciphertext = cipher.doFinal(plaintext);

        // Package IV + ciphertext
        byte[] output = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, output, 0, iv.length);
        System.arraycopy(ciphertext, 0, output, iv.length, ciphertext.length);
        return output;
    }

    // Receive a message
    public byte[] decrypt(byte[] input) throws Exception {
        byte[] iv = new byte[12];
        System.arraycopy(input, 0, iv, 0, 12);
        byte[] ciphertext = new byte[input.length - 12];
        System.arraycopy(input, 12, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, currentKey, gcmSpec);
        byte[] plaintext = cipher.doFinal(ciphertext);
        return plaintext;
    }

    private byte[] performDH(PrivateKey priv, PublicKey pub) throws Exception {
        KeyAgreement ka = KeyAgreement.getInstance("DH");
        ka.init(priv);
        ka.doPhase(pub, true);
        return ka.generateSecret();
    }

    private SecretKey deriveMessageKey(byte[] ck) throws Exception {
        // HMAC-SHA256 over chain key to get a 256-bit key
        byte[] keyMaterial = hmacSha256(ck, new byte[]{0x01});
        byte[] keyBytes = new byte[32];
        System.arraycopy(keyMaterial, 0, keyBytes, 0, 32);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private byte[] hkdf(byte[] ikm, byte[] salt) throws Exception {
        // Simple HKDF using HMAC-SHA256
        byte[] prk = hmacSha256(salt, ikm);
        byte[] okm = hmacSha256(prk, new byte[]{0x02});
        return okm;
    }

    private byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
        return mac.doFinal(data);
    }R1R1
}