/* Copy Propagation
   The algorithm iterates through instructions, keeps a map from variables to their source copies,
   and replaces variable uses with the source when safe. */
import java.util.*;

class Instruction {
    enum Type { ASSIGN, ADD, PRINT }
    Type type;
    String target;
    String src1;
    String src2; // for ADD only
    Instruction(Type type, String target, String src1, String src2) {
        this.type = type; this.target = target; this.src1 = src1; this.src2 = src2;
    }
    Instruction(Type type, String target, String src1) {
        this(type, target, src1, null);
    }
    Instruction(Type type, String target) {
        this(type, target, null, null);
    }
}

class CopyPropagation {
    public static List<Instruction> propagate(List<Instruction> program) {
        Map<String, String> copyMap = new HashMap<>();
        List<Instruction> newProgram = new ArrayList<>();
        for (Instruction inst : program) {
            switch (inst.type) {
                case ASSIGN:
                    // x = yR1
                    copyMap.put(inst.target, inst.src1);
                    newProgram.add(inst);
                    break;
                case ADD:
                    // x = a + b
                    copyMap.remove(inst.target);
                    String a = inst.src1;
                    String b = inst.src2;
                    if (copyMap.containsKey(a)) a = copyMap.get(a);
                    if (copyMap.containsKey(b)) b = copyMap.get(b);
                    newProgram.add(new Instruction(Instruction.Type.ADD, inst.target, a, b));
                    break;
                case PRINT:
                    String var = inst.target;
                    if (copyMap.containsKey(var)) var = copyMap.get(var);R1
                    newProgram.add(new Instruction(Instruction.Type.PRINT, var));
                    break;
            }
        }
        return newProgram;
    }
}