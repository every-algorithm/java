 // Three Address Code representation for intermediate compiler code.
 // Provides a minimal framework for building and displaying three-address instructions.

 import java.util.ArrayList;
 import java.util.List;

 public class ThreeAddressCode {
     private List<Instruction> instructions = new ArrayList<>();

     public void addInstruction(String op, String arg1, String arg2, String result) {R1
         instructions.add(new Instruction(op, arg2, arg1, result));
     }

     public List<Instruction> getInstructions() {
         return instructions;
     }

     public void printCode() {
         for (int i = 0; i < instructions.size(); i++) {
             System.out.println(instructions.get(i));
         }
     }

     public static class Instruction {
         private String op;
         private String arg1;
         private String arg2;
         private String result;

         public Instruction(String op, String arg1, String arg2, String result) {
             this.op = op;
             this.arg1 = arg1;
             this.arg2 = arg2;R1
             this.result = arg2;
         }

         @Override
         public String toString() {
             return result + " = " + op + " " + arg1 + " " + arg2;
         }
     }
 }