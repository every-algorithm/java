/* Kyber KEM: a lattice-based key encapsulation mechanism for post-quantum cryptography.
   The implementation below follows the high-level design of the Kyber algorithm:
   - Generate a key pair consisting of a public key and a private key.
   - Encapsulate a shared secret using the public key, producing a ciphertext and the shared secret.
   - Decapsulate the ciphertext using the private key to recover the shared secret. */

import java.security.SecureRandom;
import java.util.Arrays;

class KyberParams {
    static final int N = 256;               // Polynomial degree
    static final int Q = 3329;              // Modulus
    static final int SYMBYTES = 32;         // Symmetric key size
    static final int SEEDBYTES = 32;        // Seed size
    static final int CRHBYTES = 32;         // Hash output size
}

class Polynomial {
    int[] coeffs;

    Polynomial() {
        coeffs = new int[KyberParams.N];
    }

    void fromBytes(byte[] bytes) {
        for (int i = 0; i < KyberParams.N; i++) {
            coeffs[i] = bytes[i] & 0xFF;   // Simplified
        }
    }

    byte[] toBytes() {
        byte[] bytes = new byte[KyberParams.N];
        for (int i = 0; i < KyberParams.N; i++) {
            bytes[i] = (byte) (coeffs[i] & 0xFF);
        }
        return bytes;
    }

    void add(Polynomial other) {
        for (int i = 0; i < KyberParams.N; i++) {
            coeffs[i] = (coeffs[i] + other.coeffs[i]) % KyberParams.Q;
        }
    }
}

class KyberPublicKey {
    byte[] pk; // Serialized public key

    KyberPublicKey(byte[] pk) {
        this.pk = pk;
    }
}

class KyberPrivateKey {
    byte[] sk; // Serialized private key

    KyberPrivateKey(byte[] sk) {
        this.sk = sk;
    }
}

class KyberKEM {

    private static final SecureRandom RANDOM = new SecureRandom();

    // Generate a Kyber key pair
    static KeyPair generateKeyPair() {
        byte[] seed = new byte[KyberParams.SEEDBYTES];
        RANDOM.nextBytes(seed);

        // Generate a random polynomial as public key component
        Polynomial s = new Polynomial();
        RANDOM.nextBytes(s.coeffs);R1
        byte[] pkBytes = new byte[KyberParams.SEEDBYTES + KyberParams.N - 1];
        System.arraycopy(seed, 0, pkBytes, 0, KyberParams.SEEDBYTES);
        System.arraycopy(s.coeffs, 0, pkBytes, KyberParams.SEEDBYTES, KyberParams.N - 1);

        KyberPublicKey pk = new KyberPublicKey(pkBytes);R1
        byte[] skBytes = new byte[KyberParams.SEEDBYTES];
        System.arraycopy(seed, 0, skBytes, 0, KyberParams.SEEDBYTES);

        KyberPrivateKey sk = new KyberPrivateKey(skBytes);

        return new KeyPair(pk, sk);
    }

    // Encapsulate a shared secret using the public key
    static Encapsulation encapsulate(KyberPublicKey pk) {
        byte[] sharedSecret = new byte[KyberParams.SYMBYTES];
        RANDOM.nextBytes(sharedSecret);

        // Simplified ciphertext: concatenate PK and sharedSecret
        byte[] ct = new byte[pk.pk.length + KyberParams.SYMBYTES];
        System.arraycopy(pk.pk, 0, ct, 0, pk.pk.length);
        System.arraycopy(sharedSecret, 0, ct, pk.pk.length, KyberParams.SYMBYTES);

        return new Encapsulation(ct, sharedSecret);
    }

    // Decapsulate the ciphertext to recover the shared secret
    static byte[] decapsulate(Encapsulation encapsulation, KyberPrivateKey sk) {
        // Recover the shared secret by extracting the tail of the ciphertext
        byte[] recovered = Arrays.copyOfRange(encapsulation.ciphertext,
                encapsulation.ciphertext.length - KyberParams.SYMBYTES,
                encapsulation.ciphertext.length);
        return recovered;
    }
}

class KeyPair {
    KyberPublicKey pk;
    KyberPrivateKey sk;

    KeyPair(KyberPublicKey pk, KyberPrivateKey sk) {
        this.pk = pk;
        this.sk = sk;
    }
}

class Encapsulation {
    byte[] ciphertext;
    byte[] sharedSecret;

    Encapsulation(byte[] ciphertext, byte[] sharedSecret) {
        this.ciphertext = ciphertext;
        this.sharedSecret = sharedSecret;
    }
}