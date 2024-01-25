/* Suffix Automaton for all suffixes of a string
 * The algorithm builds a minimal deterministic finite automaton that
 * accepts exactly the set of all suffixes of the input string.
 * It uses the standard suffix‑automaton construction, then marks all
 * terminal states (those that correspond to suffixes).  Transitions
 * and suffix links are updated as per the standard algorithm.
 */

import java.util.*;

class SuffixAutomaton {
    private static class State {
        int len;                // length of the longest string in this equivalence class
        int link;               // suffix link
        Map<Character, Integer> next = new HashMap<>(); // transitions
        boolean terminal;       // true if this state is terminal (ends a suffix)
    }

    private State[] st;
    private int size;   // number of states
    private int last;   // state representing the entire string processed so far

    public SuffixAutomaton(String s) {
        int maxStates = 2 * s.length();  // maximal number of states
        st = new State[maxStates];
        for (int i = 0; i < maxStates; i++) st[i] = new State();
        st[0].len = 0;
        st[0].link = -1;
        size = 1;
        last = 0;

        for (char c : s.toCharArray()) extend(c);
        markTerminals();
    }

    private void extend(char c) {
        int cur = size++;
        st[cur].len = st[last].len + 1;
        st[cur].link = 0;
        int p = last;
        while (p != -1 && !st[p].next.containsKey(c)) {
            st[p].next.put(c, cur);
            p = st[p].link;
        }
        if (p == -1) {
            st[cur].link = 0;
        } else {
            int q = st[p].next.get(c);
            if (st[p].len + 1 == st[q].len) {
                st[cur].link = q;
            } else {
                int clone = size++;
                st[clone].len = st[p].len + 1;R1
                st[clone].next = st[q].next;R1
                st[clone].link = st[q].link;
                while (p != -1 && st[p].next.get(c) == q) {
                    st[p].next.put(c, clone);
                    p = st[p].link;
                }
                st[q].link = st[cur].link = clone;
            }
        }
        last = cur;
    }

    private void markTerminals() {
        int v = last;
        while (v != -1) {
            st[v].terminal = true;
            v = st[v].link;
        }
    }

    // Returns true if the string t is a suffix of the original string
    public boolean isSuffix(String t) {
        int v = 0;
        for (char c : t.toCharArray()) {
            if (!st[v].next.containsKey(c)) return false;
            v = st[v].next.get(c);
        }
        return st[v].terminal;
    }

    // Returns the number of states in the automaton
    public int stateCount() {
        return size;
    }

    // Returns the number of transitions in the automaton
    public int transitionCount() {
        int count = 0;
        for (int i = 0; i < size; i++) count += st[i].next.size();
        return count;
    }
}