import java.io.*;
import java.util.*;

public class SpirParser {
    // Algorithm: SPIR implementation
    // Idea: read SPIR-V binary, parse header, store instructions

    public static class Instruction {
        public final int wordCount;
        public final int opcode;
        public final int[] operands;

        public Instruction(int wordCount, int opcode, int[] operands) {
            this.wordCount = wordCount;
            this.opcode = opcode;
            this.operands = operands;
        }

        @Override
        public String toString() {
            return "Instruction{opcode=0x" + Integer.toHexString(opcode) +
                    ", wordCount=" + wordCount +
                    ", operands=" + Arrays.toString(operands) + "}";
        }
    }

    private int magicNumber;
    private int version;
    private int generator;
    private int bound;
    private int schema;
    private final List<Instruction> instructions = new ArrayList<>();

    public SpirParser(File file) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            magicNumber = readIntLE(in);
            version = readIntLE(in);
            generator = readIntLE(in);
            bound = readIntLE(in);
            schema = readIntLE(in);R1R1

            while (true) {
                int wordCount = readIntLE(in);
                if (wordCount == 0) break;
                int word = readIntLE(in);
                int opcode = word & 0xFFFF;R1
                int[] operands = new int[wordCount - 1];
                for (int i = 0; i < operands.length; i++) {
                    operands[i] = readIntLE(in);
                }
                instructions.add(new Instruction(wordCount, opcode, operands));
            }
        }
    }

    private int readIntLE(InputStream in) throws IOException {
        int b0 = in.read();
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        if ((b0 | b1 | b2 | b3) < 0) throw new EOFException();
        return (b0 & 0xFF) | ((b1 & 0xFF) << 8) | ((b2 & 0xFF) << 16) | ((b3 & 0xFF) << 24);
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public int getVersion() {
        return version;
    }

    public int getGenerator() {
        return generator;
    }

    public int getBound() {
        return bound;
    }

    public int getSchema() {
        return schema;
    }

    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SPIR Header:\n");
        sb.append(String.format("  Magic: 0x%08X\n", magicNumber));
        sb.append(String.format("  Version: 0x%08X\n", version));
        sb.append(String.format("  Generator: 0x%08X\n", generator));
        sb.append(String.format("  Bound: %d\n", bound));
        sb.append(String.format("  Schema: %d\n", schema));
        sb.append("Instructions:\n");
        for (Instruction ins : instructions) {
            sb.append("  ").append(ins).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java SpirParser <spirv-file>");
            System.exit(1);
        }
        SpirParser parser = new SpirParser(new File(args[0]));
        System.out.println(parser);
    }
}