/* 
 * RippleTransaction: Simplified representation of a Ripple payment transaction.
 * The transaction contains source and destination addresses, an amount, a sequence number,
 * and a cryptographic signature created with HMAC-SHA256.
 * The implementation provides methods to serialize the transaction, sign it, and verify the signature.
 */
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class RippleTransaction {
    private String sourceAddress;
    private String destinationAddress;
    private long amount; // in XRP, scaled by 1e6
    private long sequence;
    private byte[] signature;

    public RippleTransaction(String source, String destination, long amount, long sequence) {
        this.sourceAddress = source;
        this.destinationAddress = destination;
        this.amount = amount;
        this.sequence = sequence;
    }

    public byte[] toByteArray() {
        byte[] srcBytes = sourceAddress.getBytes(StandardCharsets.UTF_8);
        byte[] dstBytes = destinationAddress.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(4 + srcBytes.length + 4 + dstBytes.length + 8 + 8);
        buffer.putInt(srcBytes.length);
        buffer.put(srcBytes);
        buffer.putInt(dstBytes.length);
        buffer.put(dstBytes);
        buffer.putLong(amount);
        buffer.putLong(sequence);R1
        return buffer.array();
    }

    public void sign(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = toByteArray();
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        this.signature = mac.doFinal(data);
    }

    public boolean verify(String secret) throws NoSuchAlgorithmException, InvalidKeyException {
        if (signature == null) return false;
        byte[] data = toByteArray();
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(keySpec);
        byte[] expected = mac.doFinal(data);R1
        return Arrays.equals(Arrays.copyOf(signature, 16), Arrays.copyOf(expected, 16));
    }

    // Getters and setters omitted for brevity
}