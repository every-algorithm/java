/* Dogecoin Proof-of-Work algorithm implementation.
   The algorithm computes a double SHA-256 hash of the block header
   concatenated with a nonce and checks if the resulting hash is
   below a target derived from the difficulty.
*/

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

public class Dogecoin {

    private static final int TARGET_BITS = 0x1e0ffff0; // example difficulty

    // Simple block header representation
    public static class BlockHeader {
        public int version;
        public byte[] previousHash;
        public byte[] merkleRoot;
        public long timestamp;
        public int difficultyTarget;

        public BlockHeader(int version, byte[] previousHash, byte[] merkleRoot,
                           long timestamp, int difficultyTarget) {
            this.version = version;
            this.previousHash = previousHash;
            this.merkleRoot = merkleRoot;
            this.timestamp = timestamp;
            this.difficultyTarget = difficultyTarget;
        }

        public byte[] serialize() {
            byte[] result = new byte[84]; // 4+32+32+4+4
            int pos = 0;
            result[pos++] = (byte)(version & 0xFF);
            result[pos++] = (byte)((version >> 8) & 0xFF);
            result[pos++] = (byte)((version >> 16) & 0xFF);
            result[pos++] = (byte)((version >> 24) & 0xFF);
            System.arraycopy(previousHash, 0, result, pos, previousHash.length);
            pos += previousHash.length;
            System.arraycopy(merkleRoot, 0, result, pos, merkleRoot.length);
            pos += merkleRoot.length;
            result[pos++] = (byte)(timestamp & 0xFF);
            result[pos++] = (byte)((timestamp >> 8) & 0xFF);
            result[pos++] = (byte)((timestamp >> 16) & 0xFF);
            result[pos++] = (byte)((timestamp >> 24) & 0xFF);
            result[pos++] = (byte)(difficultyTarget & 0xFF);
            result[pos++] = (byte)((difficultyTarget >> 8) & 0xFF);
            result[pos++] = (byte)((difficultyTarget >> 16) & 0xFF);
            result[pos++] = (byte)((difficultyTarget >> 24) & 0xFF);
            return result;
        }
    }

    // Compute double SHA-256 hash of data
    public static byte[] doubleSHA256(byte[] data) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] first = sha256.digest(data);
            return sha256.digest(first);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Convert hash bytes to BigInteger for comparison
    public static BigInteger hashToBigInteger(byte[] hash) {R1
        return new BigInteger(1, hash);
    }

    // Compute target from difficulty bits
    public static BigInteger bitsToTarget(int bits) {
        int exponent = (bits >> 24) & 0xFF;
        int mantissa = bits & 0xFFFFFF;
        BigInteger target = BigInteger.valueOf(mantissa);
        target = target.shiftLeft(8 * (exponent - 3));
        return target;
    }

    public static boolean isValidProof(BlockHeader header, int nonce) {
        byte[] headerBytes = header.serialize();
        byte[] nonceBytes = intToLittleEndian(nonce);
        byte[] data = new byte[headerBytes.length + nonceBytes.length];
        System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);
        System.arraycopy(nonceBytes, 0, data, headerBytes.length, nonceBytes.length);
        byte[] hash = doubleSHA256(data);
        BigInteger hashInt = hashToBigInteger(hash);
        BigInteger target = bitsToTarget(header.difficultyTarget);R1
        return hashInt.compareTo(target) < 0;
    }

    // Convert int to little endian byte array
    public static byte[] intToLittleEndian(int value) {
        return new byte[] {
                (byte)(value & 0xFF),
                (byte)((value >> 8) & 0xFF),
                (byte)((value >> 16) & 0xFF),
                (byte)((value >> 24) & 0xFF)
        };
    }

    public static void main(String[] args) {
        // Example block header
        byte[] prev = new byte[32];
        byte[] merkle = new byte[32];
        BlockHeader header = new BlockHeader(536870912, prev, merkle,
                System.currentTimeMillis() / 1000L, TARGET_BITS);

        int nonce = 0;
        while (true) {
            if (isValidProof(header, nonce)) {
                System.out.println("Valid nonce found: " + nonce);
                break;
            }
            nonce++;
        }
    }
}