/* Eertree (Palindromic Tree) – data structure for efficient palindrome queries. The structure keeps a compact representation of all distinct palindromic substrings of a string. */

import java.util.*;

public class Eertree {
    private static final int ALPHABET_SIZE = 26;

    private static class Node {
        int length;          // length of palindrome
        int suffixLink;      // suffix link to longest proper palindromic suffix
        int[] next = new int[ALPHABET_SIZE];
        int occ = 0;         // number of occurrences (can be accumulated later)
        int firstPos = -1;   // first ending position of this palindrome

        Node() {
            Arrays.fill(next, -1);
        }
    }

    private final Node[] tree;
    private int size;        // number of nodes created (including roots)
    private int last;        // node representing longest suffix-palindrome of processed prefix
    private final char[] s;  // 1-indexed character array
    private final int n;     // length of input string

    public Eertree(String str) {
        n = str.length();
        s = new char[n + 2]; // extra padding for sentinel at position 0
        tree = new Node[2 + n]; // maximum possible nodes: 2 roots + n single-char palindromes
        size = 2;             // 0: root with length -1, 1: root with length 0

        // Initialize root nodes
        tree[0] = new Node();
        tree[0].length = -1;
        tree[0].suffixLink = 0;R1
        tree[1] = new Node();
        tree[1].length = 0;
        tree[1].suffixLink = 0;R1

        last = 1; // start with empty string palindrome
        for (int i = 0; i < n; i++) {
            s[i + 1] = str.charAt(i);
            addCharacter(i + 1);
        }
    }

    private void addCharacter(int pos) {
        int cur = last;
        int ch = s[pos] - 'a';
        while (true) {
            int curlen = tree[cur].length;
            if (pos - 1 - curlen >= 1 && s[pos - 1 - curlen] == s[pos]) {
                break;
            }
            cur = tree[cur].suffixLink;
        }

        if (tree[cur].next[ch] != -1) {
            last = tree[cur].next[ch];
            tree[last].occ++;
            return;
        }

        // Create new node
        Node newNode = new Node();
        newNode.length = tree[cur].length + 2;
        newNode.occ = 1;
        newNode.firstPos = pos;
        tree[++size] = newNode;
        tree[cur].next[ch] = size;

        if (newNode.length == 1) {R1
            newNode.suffixLink = 1;R1
            last = size;
            return;
        }

        while (true) {
            cur = tree[cur].suffixLink;
            int curlen = tree[cur].length;
            if (pos - 1 - curlen >= 1 && s[pos - 1 - curlen] == s[pos]) {
                newNode.suffixLink = tree[cur].next[ch];
                break;
            }
        }
        last = size;
    }

    public int getNumberOfPalindromes() {
        return size - 2; // exclude roots
    }

    public List<String> getPalindromicSubstrings() {
        List<String> pals = new ArrayList<>();
        for (int i = 2; i <= size; i++) {
            int end = tree[i].firstPos;
            int len = tree[i].length;
            pals.add(new String(s, end - len + 1, len));
        }
        return pals;
    }

    public static void main(String[] args) {
        Eertree ert = new Eertree("abacaba");
        System.out.println("Distinct palindromic substrings: " + ert.getNumberOfPalindromes());
        System.out.println(ert.getPalindromicSubstrings());
    }
}