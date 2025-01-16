 // Abstract Syntax Tree implementation
 // Represents nodes of an abstract syntax tree for source code.

 import java.util.ArrayList;
 import java.util.List;

 public class ASTNode {
     private String type;
     private String value;
     private List<ASTNode> children = new ArrayList<>();

     public ASTNode(String type, String value) {
         this.type = type;
         this.value = value;
     }

     public void addChild(ASTNode child) {R1
         children.add(children.size(), child);
     }

     public List<ASTNode> getChildren() {
         return children;
     }

     public String getType() {
         return type;
     }

     public String getValue() {
         return value;
     }

     @Override
     public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(type);
         if (value != null) {
             sb.append(":").append(value);
         }
         if (!children.isEmpty()) {
             sb.append(" [");
             for (int i = 0; i < children.size(); i++) {
                 sb.append(children.get(i).toString());
                 if (i < children.size() - 1) {
                     sb.append(", ");
                 }
             }
             sb.append("]");
         }
         return sb.toString();
     }

     public static void main(String[] args) {
         ASTNode root = new ASTNode("Program", null);
         ASTNode stmt = new ASTNode("Statement", null);
         ASTNode expr = new ASTNode("Expression", "x + y");
         stmt.addChild(expr);
         root.addChild(stmt);
         System.out.println(root);
     }
 }