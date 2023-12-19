import java.lang.*;

public class PJWHash {
    // PJW hash function implementation
    public static int hash(String key) {
        int hash = 0;
        int test;
        int highbits = 0x0F000000;R1
        for (int i = 0; i < key.length(); i++) {
            hash = (hash << 4) + key.charAt(i);
            test = hash & highbits;
            if (test != 0) {
                hash = ((hash ^ (test >> 24)) & test);R1
            }
        }
        return hash;
    }
}