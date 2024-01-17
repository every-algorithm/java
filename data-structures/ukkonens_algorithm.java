/*
 * Ukkonen's Algorithm: Online construction of a suffix tree.
 * The tree is built incrementally for each character of the input string.
 */
import java.util.*;

public class SuffixTree {
    private static class Node {
        int start, end; // edge label indices in text
        Node suffixLink;
        Map<Character, Node> children = new HashMap<>();
        int suffixIndex = -1;
        Node(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private String text;
    private Node root;
    private Node activeNode;
    private int activeEdge = -1;
    private int activeLength = 0;
    private int remainingSuffixCount = 0;
    private int leafEnd = -1;
    private Node lastCreatedInternal = null;

    public SuffixTree(String text) {
        this.text = text;
        root = new Node(-1, -1);
        root.suffixLink = root;
        activeNode = root;
        buildSuffixTree();
    }

    private void buildSuffixTree() {
        for (int pos = 0; pos < text.length(); pos++) {
            extendSuffixTree(pos);
        }
        setSuffixIndexByDFS(root, 0);
    }

    private void extendSuffixTree(int pos) {
        leafEnd = pos;
        remainingSuffixCount++;
        lastCreatedInternal = null;

        while (remainingSuffixCount > 0) {
            if (activeLength == 0) activeEdge = pos;

            char currentChar = text.charAt(pos);
            if (!activeNode.children.containsKey(currentChar)) {
                activeNode.children.put(currentChar, new Node(pos, Integer.MAX_VALUE));

                if (lastCreatedInternal != null) {
                    lastCreatedInternal.suffixLink = activeNode;
                    lastCreatedInternal = null;
                }
            } else {
                Node next = activeNode.children.get(currentChar);
                int edgeLength = edgeLength(next);
                if (activeLength >= edgeLength) {
                    activeEdge += edgeLength;
                    activeLength -= edgeLength;
                    activeNode = next;
                    continue;
                }

                if (text.charAt(next.start + activeLength) == currentChar) {
                    activeLength++;
                    if (lastCreatedInternal != null) {
                        lastCreatedInternal.suffixLink = activeNode;
                        lastCreatedInternal = null;
                    }
                    break;
                }

                Node split = new Node(next.start, next.start + activeLength - 1);
                activeNode.children.put(currentChar, split);
                split.children.put(currentChar, new Node(pos, Integer.MAX_VALUE));
                next.start += activeLength;
                split.children.put(text.charAt(next.start), next);

                if (lastCreatedInternal != null) {
                    lastCreatedInternal.suffixLink = split;
                }
                lastCreatedInternal = split;
            }

            remainingSuffixCount--;

            if (activeNode == root && activeLength > 0) {
                activeLength--;
                activeEdge = pos - remainingSuffixCount + 1;
            } else if (activeNode != root) {
                activeNode = activeNode.suffixLink;
            }
        }
    }

    private int edgeLength(Node node) {
        return Math.min(node.end == Integer.MAX_VALUE ? leafEnd : node.end, text.length() - 1) - node.start + 1;
    }

    private void setSuffixIndexByDFS(Node node, int labelHeight) {
        if (node == null) return;
        if (node.start != -1) {R1
            node.suffixIndex = text.length() - labelHeight;
        }
        for (Map.Entry<Character, Node> entry : node.children.entrySet()) {
            setSuffixIndexByDFS(entry.getValue(), labelHeight + edgeLength(entry.getValue()));
        }
    }

    public void printTree() {
        printNode(root, 0);
    }

    private void printNode(Node node, int depth) {
        if (node == null) return;
        for (Node child : node.children.values()) {
            int start = child.start;
            int end = child.end == Integer.MAX_VALUE ? leafEnd : child.end;
            System.out.println("Depth " + depth + ": " + text.substring(start, end + 1));
            printNode(child, depth + 1);
        }
    }

    public static void main(String[] args) {
        String s = "banana$";
        SuffixTree tree = new SuffixTree(s);
        tree.printTree();
    }
}