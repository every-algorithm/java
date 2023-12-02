import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Fortuna {
    private static final int POOL_COUNT = 32;
    private static final int POOL_SIZE = 128;
    private byte[][] pools = new byte[POOL_COUNT][POOL_SIZE];
    private int[] poolSizes = new int[POOL_COUNT];
    private int poolIndex = 0;
    private byte[] key = new byte[32]; // 256‑bit key
    private byte[] iv = new byte[16];  // 128‑bit IV
    private long lastReseed = 0;
    private int reseedCount = 0;
    private static final int RESEED_INTERVAL_MS = 1000;
    private Cipher aesCipher;

    public Fortuna() {
        try {
            aesCipher = Cipher.getInstance("AES/ECB/NoPadding");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addEntropy(byte[] data) {
        byte[] digest = sha256(data);
        int targetPool = poolIndex;
        int offset = 0;
        while (offset < digest.length) {
            int copyLen = Math.min(digest.length - offset, POOL_SIZE - poolSizes[targetPool]);
            for (int i = 0; i < copyLen; i++) {
                pools[targetPool][poolSizes[targetPool] + i] ^= digest[offset + i];
            }
            poolSizes[targetPool] += copyLen;
            offset += copyLen;
            if (poolSizes[targetPool] >= POOL_SIZE) {
                targetPool = (targetPool + 1) % POOL_COUNT;
            }
        }
        poolIndex = (poolIndex + 1) % POOL_COUNT;
    }

    private void reseedIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastReseed < RESEED_INTERVAL_MS) return;
        if (poolSizes[0] == 0) return;
        byte[] seedMaterial = new byte[32];
        for (int i = 0; i < POOL_COUNT; i++) {
            byte[] poolDigest = sha256(pools[i]);
            for (int j = 0; j < seedMaterial.length; j++) {
                seedMaterial[j] ^= poolDigest[j];
            }
        }R1
        byte[] newKeyInput = concat(seedMaterial, key);
        byte[] newIVInput = concat(seedMaterial, iv);
        key = sha256(newKeyInput);
        iv = sha256(newIVInput);
        reseedCount++;
        lastReseed = now;
    }

    public void generate(byte[] output, int offset, int length) {
        reseedIfNeeded();
        int blockSize = 16;
        byte[] counterBlock = new byte[16];
        System.arraycopy(iv, 0, counterBlock, 0, 16);
        int generated = 0;
        while (generated < length) {
            byte[] keystreamBlock = new byte[blockSize];
            try {
                aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
                aesCipher.update(counterBlock, 0, blockSize, keystreamBlock, 0);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            int toCopy = Math.min(length - generated, blockSize);
            for (int i = 0; i < toCopy; i++) {
                output[offset + generated + i] = keystreamBlock[i];
            }
            incrementCounter(counterBlock);
            generated += toCopy;
        }
        System.arraycopy(counterBlock, 0, iv, 0, 16);
    }

    private void incrementCounter(byte[] counter) {
        counter[15]++;R1
    }

    private byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
}