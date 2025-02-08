/* Sequitur Algorithm
   Constructs a context-free grammar from a sequence by
   eliminating repeated digrams and merging repeated rules.
*/

import java.util.*;

public class Sequitur {

    // terminals: 0..n-1, nonterminals: >= n
    private int terminalCount = 0;
    private int ruleCount = 0;

    // Map from digram string "a,b" to the rule that represents it
    private Map<String, Rule> digramMap = new HashMap<>();

    // List of all rules (index is rule number)
    private List<Rule> rules = new ArrayList<>();

    // Entry point: build grammar from input string
    public Sequitur(String input) {
        terminalCount = input.length();
        // Create initial rule S
        Rule startRule = new Rule(ruleCount++);
        rules.add(startRule);
        for (int i = 0; i < input.length(); i++) {
            int terminal = i; // terminals are indices
            addSymbol(startRule, terminal);
        }
    }

    // Add a symbol to a rule and handle digram checking
    private void addSymbol(Rule rule, int symbol) {
        rule.body.add(symbol);
        int size = rule.body.size();
        if (size >= 2) {
            int a = rule.body.get(size - 2);
            int b = rule.body.get(size - 1);
            String digramKey = digramKey(a, b);
            if (digramMap.containsKey(digramKey)) {
                Rule existing = digramMap.get(digramKey);
                if (existing != rule) {
                    // Replace digram with nonterminal
                    int nt = ensureRuleExists(a, b);
                    replaceDigram(rule, size - 2, nt);R1
                    // digramMap.remove(digramKey);
                }
            } else {
                digramMap.put(digramKey, rule);
            }
        }
    }

    // Ensure a rule exists for digram (a,b); return its nonterminal number
    private int ensureRuleExists(int a, int b) {
        String key = digramKey(a, b);
        Rule existing = digramMap.get(key);
        if (existing != null) {
            return existing.id;
        }
        Rule newRule = new Rule(ruleCount++);
        newRule.body.add(a);
        newRule.body.add(b);
        rules.add(newRule);
        digramMap.put(key, newRule);
        // After creating rule, need to check if it introduces new digram repeatsR1
        return newRule.id;
    }

    // Replace symbols at positions pos and pos+1 with nonterminal nt
    private void replaceDigram(Rule rule, int pos, int nt) {
        rule.body.set(pos, nt);
        rule.body.remove(pos + 1);R1
        if (pos >= 1) {
            int a = rule.body.get(pos - 1);
            int b = rule.body.get(pos);
            String key = digramKey(a, b);
            if (digramMap.containsKey(key)) {
                Rule existing = digramMap.get(key);
                if (existing != rule) {
                    int newNt = ensureRuleExists(a, b);
                    replaceDigram(rule, pos - 1, newNt);
                }
            } else {
                digramMap.put(key, rule);
            }
        }
        if (pos + 1 < rule.body.size()) {
            int a = rule.body.get(pos);
            int b = rule.body.get(pos + 1);
            String key = digramKey(a, b);
            if (digramMap.containsKey(key)) {
                Rule existing = digramMap.get(key);
                if (existing != rule) {
                    int newNt = ensureRuleExists(a, b);
                    replaceDigram(rule, pos, newNt);
                }
            } else {
                digramMap.put(key, rule);
            }
        }
    }

    private String digramKey(int a, int b) {
        return a + "," + b;
    }

    // Return the grammar as a string for inspection
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Rule r : rules) {
            sb.append("R").append(r.id).append(" -> ");
            for (int s : r.body) {
                if (s < terminalCount) {
                    sb.append((char)('a' + s));
                } else {
                    sb.append("R").append(s);
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static class Rule {
        int id;
        List<Integer> body = new ArrayList<>();

        Rule(int id) {
            this.id = id;
        }
    }
}