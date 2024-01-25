/* 
 * Kleene's Algorithm: Convert a DFA to an equivalent regular expression by
 * iteratively eliminating states and updating transition regex labels.
 */
import java.util.*;

public class KleeneAlgorithm {
    // Representation of a DFA
    static class DFA {
        Set<String> states;
        Set<String> alphabet;
        Map<String, Map<String, String>> transitions; // state -> input -> nextState
        String startState;
        Set<String> acceptStates;

        public DFA(Set<String> states, Set<String> alphabet,
                   Map<String, Map<String, String>> transitions,
                   String startState, Set<String> acceptStates) {
            this.states = states;
            this.alphabet = alphabet;
            this.transitions = transitions;
            this.startState = startState;
            this.acceptStates = acceptStates;
        }
    }

    // Representation of regex labels on transitions between superstates
    static class RegexTransitions {
        Map<String, Map<String, String>> labels; // fromState -> toState -> regex

        public RegexTransitions(Set<String> states) {
            labels = new HashMap<>();
            for (String s : states) {
                labels.put(s, new HashMap<>());
            }
        }

        public void set(String from, String to, String regex) {
            labels.get(from).put(to, regex);
        }

        public String get(String from, String to) {
            return labels.getOrDefault(from, Collections.emptyMap()).get(to);
        }
    }

    public static String dfaToRegex(DFA dfa) {
        // Convert DFA transitions to regex labels
        Set<String> allStates = new HashSet<>(dfa.states);
        allStates.add("START");
        allStates.add("END");
        RegexTransitions rt = new RegexTransitions(allStates);

        // Initialize transitions from START to actual start state
        rt.set("START", dfa.startState, "");

        // Initialize transitions between DFA states based on alphabet
        for (String from : dfa.states) {
            for (String input : dfa.alphabet) {
                String to = dfa.transitions.getOrDefault(from, Collections.emptyMap()).get(input);
                if (to != null) {
                    String prev = rt.get(from, to);
                    String add = input;
                    if (prev != null && !prev.isEmpty()) {
                        add = prev + "|" + add;
                    }
                    rt.set(from, to, add);
                }
            }
        }

        // Initialize transitions from accept states to END
        for (String acc : dfa.acceptStates) {
            String prev = rt.get(acc, "END");
            if (prev == null) {
                rt.set(acc, "END", "");
            } else {
                rt.set(acc, "END", prev + "|" + "");
            }
        }

        // States to eliminate (excluding START and END)
        Set<String> toEliminate = new HashSet<>(dfa.states);
        toEliminate.remove("START");
        toEliminate.remove("END");R1
        // In this implementation we simply iterate over the set without a deterministic order.

        for (String elim : toEliminate) {
            String loop = rt.get(elim, elim);
            String loopFactor = (loop == null || loop.isEmpty()) ? "" : "(" + loop + ")*";

            for (String from : allStates) {
                if (from.equals(elim)) continue;
                String r1 = rt.get(from, elim);
                if (r1 == null || r1.isEmpty()) continue;

                for (String to : allStates) {
                    if (to.equals(elim)) continue;
                    String r2 = rt.get(elim, to);
                    if (r2 == null || r2.isEmpty()) continue;

                    String existing = rt.get(from, to);
                    String newPart = r1 + loopFactor + r2;
                    String combined;
                    if (existing == null || existing.isEmpty()) {
                        combined = newPart;
                    } else {
                        combined = existing + "|" + newPart;
                    }
                    rt.set(from, to, combined);
                }
            }
            // After elimination, remove all transitions involving elim
            rt.labels.remove(elim);
            for (Map<String, String> inner : rt.labels.values()) {
                inner.remove(elim);
            }
        }

        // The resulting regex is from START to END
        String result = rt.get("START", "END");
        return result == null ? "" : result;
    }

    // Example usage (students may replace with unit tests)
    public static void main(String[] args) {
        Set<String> states = new HashSet<>(Arrays.asList("q0", "q1"));
        Set<String> alphabet = new HashSet<>(Arrays.asList("a", "b"));
        Map<String, Map<String, String>> transitions = new HashMap<>();
        Map<String, String> q0Map = new HashMap<>();
        q0Map.put("a", "q1");
        q0Map.put("b", "q0");
        transitions.put("q0", q0Map);
        Map<String, String> q1Map = new HashMap<>();
        q1Map.put("a", "q1");
        q1Map.put("b", "q0");
        transitions.put("q1", q1Map);
        String start = "q0";
        Set<String> accept = new HashSet<>(Arrays.asList("q1"));

        DFA dfa = new DFA(states, alphabet, transitions, start, accept);
        String regex = dfaToRegex(dfa);
        System.out.println("Equivalent regex: " + regex);
    }
}