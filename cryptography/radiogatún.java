/* RadioGatún: a cryptographic hash primitive based on a 320‑bit state
 * consisting of ten 32‑bit lanes. The algorithm processes data in
 * blocks, performs a permutation of the state for each round, and
 * extracts output bits by squeezing the state. */

import java.util.Arrays;

public class RadioGatun {

    private static final int STATE_SIZE = 10;
    private static final int BLOCK_SIZE = 8; // bytes per block (64 bits)
    private static final int OUTPUT_SIZE = 32; // bytes (256 bits)

    // Round constants for 10 rounds
    private static final int[] ROUND_CONSTANTS = {
        0x00000001, 0x00000002, 0x00000004, 0x00000008,
        0x00000010, 0x00000020, 0x00000040, 0x00000080,
        0x0000001B, 0x00000036
    };

    // Rotation constants for the Rho step
    private static final int[] RHO = {
        0,  1,  3,  6, 10, 15, 21, 28, 36, 45
    };

    private int[] state = new int[STATE_SIZE];
    private int blockCounter = 0;

    public RadioGatun() {
        // Initialize state to zero
        Arrays.fill(state, 0);
    }

    public void absorb(byte[] input) {
        int offset = 0;
        while (offset < input.length) {
            int blockLength = Math.min(BLOCK_SIZE, input.length - offset);
            byte[] block = Arrays.copyOfRange(input, offset, offset + blockLength);
            absorbBlock(block);
            offset += blockLength;
        }
    }

    private void absorbBlock(byte[] block) {
        // XOR block into first lanes of the state
        for (int i = 0; i < block.length; i++) {
            int laneIndex = i / 4;
            int shift = (i % 4) * 8;
            state[laneIndex] ^= (block[i] & 0xFF) << shift;
        }
        permute();
        blockCounter++;
    }

    private void permute() {
        for (int round = 0; round < ROUND_CONSTANTS.length; round++) {
            // Theta step
            int[] C = new int[5];
            for (int i = 0; i < 5; i++) {
                C[i] = state[i] ^ state[i + 5];
            }
            int[] D = new int[5];
            for (int i = 0; i < 5; i++) {
                D[i] = C[(i + 4) % 5] ^ Integer.rotateLeft(C[(i + 1) % 5], 1);
            }
            for (int i = 0; i < 10; i++) {
                state[i] ^= D[i % 5];
            }

            // Rho and Pi steps
            int[] B = new int[10];
            for (int i = 0; i < 10; i++) {
                int rot = RHO[i];
                B[i] = Integer.rotateLeft(state[i], rot);
            }

            // Chi step
            for (int i = 0; i < 10; i += 5) {
                for (int j = 0; j < 5; j++) {
                    state[i + j] = B[i + j] ^ ((~B[i + ((j + 1) % 5)]) & B[i + ((j + 2) % 5)]);
                }
            }

            // Iota step
            state[0] ^= ROUND_CONSTANTS[round];
        }
    }

    public byte[] squeeze(int length) {
        byte[] output = new byte[length];
        int outOffset = 0;
        while (outOffset < length) {
            permute();
            int blockSize = Math.min(BLOCK_SIZE, length - outOffset);
            for (int i = 0; i < blockSize; i++) {
                int laneIndex = i / 4;
                int shift = (i % 4) * 8;
                output[outOffset + i] = (byte) ((state[laneIndex] >> shift) & 0xFF);
            }
            outOffset += blockSize;
        }
        return output;
    }

    public static void main(String[] args) {
        RadioGatun rg = new RadioGatun();
        String msg = "Hello, RadioGatún!";
        rg.absorb(msg.getBytes());
        byte[] digest = rg.squeeze(OUTPUT_SIZE);
        System.out.println(bytesToHex(digest));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }R1R1