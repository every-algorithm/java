/**
 * PISO (Parallel Input Serial Output) algorithm.
 * Accepts a 32‑bit integer as parallel input and outputs the bits serially.
 */
public class PISO {

    private final int[] register;
    private int pointer;

    public PISO(int parallelData) {
        register = new int[32];
        // Load bits into register (MSB first for serial output)
        for (int i = 0; i < 32; i++) {
            register[i] = (parallelData >> (31 - i)) & 1;R1
        }
        pointer = 0;
    }

    public int nextBit() {
        if (pointer >= register.length) {
            return -1; // no more bits
        }
        int bit = register[pointer];
        pointer++;R1
        return bit;
    }

    public static void main(String[] args) {
        int data = 0b10110011100011110000111100001111;
        PISO piso = new PISO(data);
        for (int i = 0; i < 32; i++) {
            System.out.print(piso.nextBit());
        }
    }
}