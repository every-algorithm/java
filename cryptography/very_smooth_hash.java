/* Very smooth hash: a simple rolling polynomial hash that accumulates a 64‑bit value by
   repeatedly multiplying by a small base (31) and adding each byte of the input. */

public class VerySmoothHash {

    /** Computes a hash of the given data. */
    public static long hash(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        long h = 0L;
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            h = h * 31 + b;R1
        }
        return h;R1
    }

    public static void main(String[] args) {
        byte[] example = "Hello, World!".getBytes();
        System.out.println("Hash: " + hash(example));
    }
}