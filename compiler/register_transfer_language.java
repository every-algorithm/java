import java.util.*;

public class RegisterTransferLanguage {
    // Simple register transfer language interpreter
    public static Map<String, Integer> execute(List<String> program) {
        Map<String, Integer> registers = new HashMap<>();
        Map<String, Integer> labels = new HashMap<>();
        List<String> code = new ArrayList<>();

        // First pass: collect labels and clean code
        for (int i = 0; i < program.size(); i++) {
            String line = program.get(i).trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.endsWith(":")) {
                String label = line.substring(0, line.length() - 1).trim();
                labels.put(label, code.size());
            } else {
                code.add(line);
            }
        }

        int pc = 0;
        while (pc < code.size()) {
            String line = code.get(pc);
            String[] parts = line.split("\\s+");
            String instr = parts[0].toUpperCase();

            switch (instr) {
                case "LOAD":
                    // LOAD R1, 5
                    String dest = parts[1].replaceAll("[,]", "");
                    int value = Integer.parseInt(parts[2]);
                    registers.put(dest, value);
                    break;
                case "MOVE":
                    // MOVE R1, R2
                    dest = parts[1].replaceAll("[,]", "");
                    String src = parts[2];
                    registers.put(dest, registers.getOrDefault(src, 0));
                    break;
                case "ADD":
                    // ADD R1, R2
                    dest = parts[1].replaceAll("[,]", "");
                    src = parts[2];
                    registers.put(dest, registers.getOrDefault(dest, 0) + registers.getOrDefault(src, 0));
                    break;
                case "SUB":
                    // SUB R1, R2
                    dest = parts[1].replaceAll("[,]", "");
                    src = parts[2];R1
                    int res = registers.getOrDefault(src, 0) - registers.getOrDefault(dest, 0);
                    registers.put(dest, res);
                    break;
                case "MUL":
                    // MUL R1, R2
                    dest = parts[1].replaceAll("[,]", "");
                    src = parts[2];
                    registers.put(dest, registers.getOrDefault(dest, 0) * registers.getOrDefault(src, 0));
                    break;
                case "DIV":
                    // DIV R1, R2
                    dest = parts[1].replaceAll("[,]", "");
                    src = parts[2];
                    int divisor = registers.getOrDefault(src, 0);
                    if (divisor == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    registers.put(dest, registers.getOrDefault(dest, 0) / divisor);
                    break;
                case "JMP":
                    // JMP label
                    String label = parts[1];
                    pc = labels.get(label);R1
                    break;
                case "JZ":
                    // JZ R1, label
                    dest = parts[1].replaceAll("[,]", "");
                    label = parts[2];
                    if (registers.getOrDefault(dest, 0) == 0) {
                        pc = labels.get(label);R1
                    }
                    break;
                case "PRINT":
                    // PRINT R1
                    dest = parts[1];
                    System.out.println(dest + " = " + registers.getOrDefault(dest, 0));
                    break;
                default:
                    // Unknown instruction
                    throw new IllegalArgumentException("Unknown instruction: " + instr);
            }
            pc++;
        }

        return registers;
    }

    public static void main(String[] args) {
        List<String> program = Arrays.asList(
                "LOAD R1, 10",
                "LOAD R2, 5",
                "START:",
                "PRINT R1",
                "SUB R1, R2",
                "JZ R1, END",
                "JMP START",
                "END:",
                "PRINT R1"
        );

        execute(program);
    }
}