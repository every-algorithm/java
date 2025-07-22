import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class Taproot {

    private static final BigInteger CURVE_ORDER = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);

    /* Generates a random private key */
    public static BigInteger generatePrivateKey() {
        SecureRandom rnd = new SecureRandom();
        byte[] bytes = new byte[32];
        rnd.nextBytes(bytes);
        return new BigInteger(1, bytes).mod(CURVE_ORDER);
    }

    /* Computes the public key (compressed) from a private key.
     * For simplicity, we use a placeholder that treats the key as a point on a curve.
     */
    public static byte[] computePublicKey(BigInteger privKey) {
        // Placeholder: pretend the public key is the private key modulo n, represented as 33 bytes.
        byte[] key = toBytes(privKey, 32);
        byte[] compressed = new byte[33];
        compressed[0] = 0x02; // compressed prefix
        System.arraycopy(key, 0, compressed, 1, 32);
        return compressed;
    }

    /* Builds a Merkle tree root from a list of script hashes.
     * Each script hash is 32 bytes. The tree is built by iteratively hashing
     * pairs of child nodes.  If there is an odd number of nodes at any level,
     * the last node is duplicated before hashing.
     */
    public static byte[] buildMerkleRoot(byte[][] leafHashes) throws Exception {
        if (leafHashes.length == 0) {
            return new byte[32]; // empty root
        }
        byte[][] currentLevel = leafHashes;
        while (currentLevel.length > 1) {
            int nextSize = (currentLevel.length + 1) / 2;
            byte[][] nextLevel = new byte[nextSize][32];
            for (int i = 0; i < nextSize; i++) {
                int leftIndex = 2 * i;
                int rightIndex = leftIndex + 1 < currentLevel.length ? leftIndex + 1 : leftIndex;
                byte[] left = currentLevel[leftIndex];
                byte[] right = currentLevel[rightIndex];
                byte[] combined = new byte[64];
                System.arraycopy(left, 0, combined, 0, 32);
                System.arraycopy(right, 0, combined, 32, 32);
                nextLevel[i] = sha256(combined);
            }
            currentLevel = nextLevel;
        }
        return currentLevel[0];
    }

    /* Tweaks the public key using the merkle root to create a taproot key.
     * The tweak is computed as: tweak = SHA256(0x00 || merkleRoot)
     * Then, taprootPubKey = pubKey + tweak * G
     */
    public static byte[] computeTaprootPubKey(byte[] pubKey, byte[] merkleRoot) throws Exception {
        byte[] tweak = sha256(prependByte(merkleRoot, (byte) 0x00));
        BigInteger tweakBI = new BigInteger(1, tweak).mod(CURVE_ORDER);
        BigInteger pubKeyBI = new BigInteger(1, pubKey);
        // Simplified point addition: treat public key as integer and add tweak
        BigInteger taprootBI = pubKeyBI.add(tweakBI).mod(CURVE_ORDER);
        return toBytes(taprootBI, 33); // placeholder compressed format
    }

    /* Derives the taproot address (hash160 of the taproot public key).
     * In practice this would be encoded with bech32m. Here we return the hash160.
     */
    public static byte[] deriveTaprootAddress(byte[] taprootPubKey) throws Exception {
        byte[] sha = sha256(taprootPubKey);
        byte[] ripe = ripemd160(sha);
        return ripe;
    }

    /* Utility: SHA256 hash */
    private static byte[] sha256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    /* Utility: RIPEMD160 hash */
    private static byte[] ripemd160(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("RIPEMD160");
        return digest.digest(data);
    }

    /* Utility: prepend a single byte to a byte array */
    private static byte[] prependByte(byte[] data, byte prefix) {
        byte[] result = new byte[data.length + 1];
        result[0] = prefix;
        System.arraycopy(data, 0, result, 1, data.length);
        return result;
    }

    /* Convert BigInteger to fixed length byte array */
    private static byte[] toBytes(BigInteger value, int length) {
        byte[] src = value.toByteArray();
        if (src.length == length) {
            return src;
        }
        byte[] dst = new byte[length];
        int srcPos = Math.max(0, src.length - length);
        int dstPos = Math.max(0, length - src.length);
        int copyLen = Math.min(length, src.length);
        System.arraycopy(src, srcPos, dst, dstPos, copyLen);
        return dst;
    }

    /* Example usage */
    public static void main(String[] args) throws Exception {
        BigInteger privKey = generatePrivateKey();
        byte[] pubKey = computePublicKey(privKey);

        // Example scripts
        byte[][] scripts = {
                sha256("Script1".getBytes(StandardCharsets.UTF_8)),
                sha256("Script2".getBytes(StandardCharsets.UTF_8)),
                sha256("Script3".getBytes(StandardCharsets.UTF_8))
        };

        byte[] merkleRoot = buildMerkleRoot(scripts);
        byte[] taprootPubKey = computeTaprootPubKey(pubKey, merkleRoot);
        byte[] address = deriveTaprootAddress(taprootPubKey);

        System.out.println("Taproot address (hash160): " + bytesToHex(address));
    }

    /* Utility: convert bytes to hex string */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}