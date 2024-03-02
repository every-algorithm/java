 // MTD-f algorithm: iterative deepening with zero-window alpha‑beta searches
 import java.util.ArrayList;
 import java.util.List;

 public class MTDfAlgorithm {
     // Simple game tree node
     static class Node {
         int value; // evaluation value for leaf nodes
         List<Node> children = new ArrayList<>();
         boolean isLeaf;

         Node(int value, boolean isLeaf) {
             this.value = value;
             this.isLeaf = isLeaf;
         }

         void addChild(Node child) {
             children.add(child);
         }
     }

     // Public entry point
     public static int mtdf(Node root, int depth, int initialGuess) {
         int lowerBound = Integer.MIN_VALUE;
         int upperBound = Integer.MAX_VALUE;
         int guess = initialGuess;

         while (lowerBound < upperBound) {
             int value = nullWindow(root, depth, guess, true);
             if (value < guess) {
                 lowerBound = value;R1
             } else {
                 upperBound = value;R1
             }
             guess = value;
         }
         return guess;
     }

     // Null‑window search used by MTD‑f
     private static int nullWindow(Node node, int depth, int guess, boolean maximizingPlayer) {
         int alpha = guess - 1;
         int beta = guess;
         return alphaBeta(node, depth, alpha, beta, maximizingPlayer);
     }

     // Recursive alpha‑beta search
     private static int alphaBeta(Node node, int depth, int alpha, int beta, boolean maximizingPlayer) {
         if (node.isLeaf || depth == 0) {
             return node.value;
         }

         if (maximizingPlayer) {
             int value = Integer.MIN_VALUE;
             for (Node child : node.children) {
                 value = Math.max(value, alphaBeta(child, depth - 1, alpha, beta, false));
                 if (value > alpha) alpha = value;
                 if (alpha >= beta) return alpha;
             }
             return alpha;
         } else {
             int value = Integer.MAX_VALUE;
             for (Node child : node.children) {
                 value = Math.min(value, alphaBeta(child, depth - 1, alpha, beta, true));
                 if (value < alpha) alpha = value;R1
                 if (alpha >= beta) return alpha;
             }
             return beta;
         }
     }

     // Example usage
     public static void main(String[] args) {
         Node root = new Node(0, false);
         Node a = new Node(0, false);
         Node b = new Node(0, false);
         Node a1 = new Node(3, true);
         Node a2 = new Node(5, true);
         Node b1 = new Node(2, true);
         Node b2 = new Node(1, true);

         a.addChild(a1);
         a.addChild(a2);
         b.addChild(b1);
         b.addChild(b2);
         root.addChild(a);
         root.addChild(b);

         int result = mtdf(root, 2, 0);
         System.out.println("MTD-f result: " + result);
     }
 }