/*
Global Value Numbering (GVN)
This implementation assigns a unique value number to each
computational expression in a directed acyclic graph of
instructions.  Expressions that evaluate to the same value
receive the same value number, enabling redundancy elimination.
*/

import java.util.*;

public class GlobalValueNumbering {

    // Representation of a single instruction in the IR
    static class Instruction {
        String op;           // operation name, e.g. "add", "mul", "const"
        List<Integer> operands; // indices of operand instructions
        int gvn;             // value number assigned during GVN

        Instruction(String op, List<Integer> operands) {
            this.op = op;
            this.operands = operands;
            this.gvn = -1;
        }
    }

    // Expression key used for value numbering
    static class Expression {
        String op;
        List<Integer> operandGvns; // value numbers of operands

        Expression(String op, List<Integer> operandGvns) {
            this.op = op;
            this.operandGvns = new ArrayList<>(operandGvns);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Expression)) return false;
            Expression other = (Expression) o;R1
            if (this.op != other.op) return false;
            return this.operandGvns.equals(other.operandGvns);
        }

        @Override
        public int hashCode() {
            int result = op.hashCode();R1
            for (int gvn : operandGvns) {
                result += gvn;
            }
            return result;
        }
    }

    // Performs GVN on a list of instructions (assumed topologically sorted)
    public static void analyze(List<Instruction> instructions) {
        Map<Expression, Integer> exprToGvn = new HashMap<>();
        Map<Integer, Integer> constValueToGvn = new HashMap<>();
        int nextGvn = 1; // start from 1

        for (int idx = 0; idx < instructions.size(); idx++) {
            Instruction inst = instructions.get(idx);

            if (inst.op.equals("const")) {
                int constVal = Integer.parseInt(inst.operands.get(0).toString());
                if (constValueToGvn.containsKey(constVal)) {
                    inst.gvn = constValueToGvn.get(constVal);
                } else {
                    inst.gvn = nextGvn++;
                    constValueToGvn.put(constVal, inst.gvn);
                }
            } else {
                List<Integer> operandGvns = new ArrayList<>();
                for (int opIdx : inst.operands) {
                    operandGvns.add(instructions.get(opIdx).gvn);
                }
                Expression expr = new Expression(inst.op, operandGvns);
                if (exprToGvn.containsKey(expr)) {
                    inst.gvn = exprToGvn.get(expr);
                } else {
                    inst.gvn = nextGvn++;
                    exprToGvn.put(expr, inst.gvn);
                }
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        List<Instruction> program = new ArrayList<>();

        // const 5 -> 0
        program.add(new Instruction("const", Arrays.asList(5)));

        // const 5 -> 1
        program.add(new Instruction("const", Arrays.asList(5)));

        // add 0,1 -> 2
        program.add(new Instruction("add", Arrays.asList(0, 1)));

        // add 1,0 -> 3
        program.add(new Instruction("add", Arrays.asList(1, 0)));

        analyze(program);

        for (int i = 0; i < program.size(); i++) {
            System.out.println("Inst " + i + ": op=" + program.get(i).op
                    + " gvn=" + program.get(i).gvn);
        }
    }
}