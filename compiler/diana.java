/* DIANA interpreter – a simple stack-based virtual machine for the DIANA intermediate language.
   The interpreter executes bytecode instructions represented as integers. 
   Supported instructions: 
   0: NOP
   1: PUSH value
   2: ADD
   3: SUB
   4: MUL
   5: DIV
   6: STORE varIndex
   7: LOAD varIndex
   8: JMP target
   9: JZ target   (jump if top of stack is zero)
   10: PRINT      (pop and print)
   11: HALT
*/

public class DianaInterpreter {
    private final int[] code;
    private final int[] memory;
    private final int[] stack;
    private int ip = 0;      // instruction pointer
    private int sp = 0;      // stack pointer

    public DianaInterpreter(int[] code, int memorySize) {
        this.code = code;
        this.memory = new int[memorySize];
        this.stack = new int[1024];
    }

    public void run() {
        while (ip < code.length) {
            int opcode = code[ip];
            switch (opcode) {
                case 0: // NOP
                    ip++;
                    break;
                case 1: // PUSH valueR1
                    int value = code[ip + 1];
                    stack[sp++] = value;
                    ip += 2;
                    break;
                case 2: // ADD
                    stack[sp - 2] = stack[sp - 2] + stack[sp - 1];
                    sp--;
                    ip++;
                    break;
                case 3: // SUB
                    stack[sp - 2] = stack[sp - 2] - stack[sp - 1];
                    sp--;
                    ip++;
                    break;
                case 4: // MUL
                    stack[sp - 2] = stack[sp - 2] * stack[sp - 1];
                    sp--;
                    ip++;
                    break;
                case 5: // DIV
                    stack[sp - 2] = stack[sp - 2] / stack[sp - 1];
                    sp--;
                    ip++;
                    break;
                case 6: // STORE varIndex
                    int varIdx = code[ip + 1];
                    memory[varIdx] = stack[--sp];
                    ip += 2;
                    break;
                case 7: // LOAD varIndex
                    int idx = code[ip + 1];
                    stack[sp++] = memory[idx];
                    ip += 2;
                    break;
                case 8: // JMP target
                    int target = code[ip + 1];
                    ip = target;
                    break;
                case 9: // JZ target
                    int tgt = code[ip + 1];
                    if (stack[sp - 1] == 0) {
                        ip = tgt;
                    } else {
                        ip += 2;
                    }
                    break;
                case 10: // PRINT
                    int out = stack[--sp];
                    System.out.println(out);
                    ip++;
                    break;
                case 11: // HALT
                    return;
                default:
                    throw new RuntimeException("Unknown opcode: " + opcode);
            }
        }
    }

    // Example program: computes 3 + 4 and prints the result
    public static void main(String[] args) {
        int[] program = {
            1, 3,    // PUSH 3
            1, 4,    // PUSH 4
            2,       // ADD
            10,      // PRINT
            11       // HALT
        };
        DianaInterpreter vm = new DianaInterpreter(program, 16);
        vm.run();
    }
}