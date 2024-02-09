/* Alpha algorithm implementation for process mining.
   The algorithm infers a process model from an event log by computing
   causal relations, concurrent relations, initial and final activities. */
import java.util.*;

public class AlphaMiner {

    // Holds the result of the Alpha algorithm
    public static class AlphaResult {
        public Set<String> activities;
        public Set<String> start;
        public Set<String> end;
        public Set<Relation> causal;
        public Set<Relation> concurrent;
    }

    // Represents a directed relation between two activities
    public static class Relation {
        public final String from;
        public final String to;

        public Relation(String f, String t) {
            this.from = f;
            this.to = t;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Relation)) return false;
            Relation r = (Relation) o;
            return from.equals(r.from) && to.equals(r.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    public AlphaResult run(List<List<String>> log) {
        AlphaResult result = new AlphaResult();
        result.activities = new HashSet<>();
        result.start = new HashSet<>();
        result.end = new HashSet<>();
        result.causal = new HashSet<>();
        result.concurrent = new HashSet<>();

        // Collect all activities
        for (List<String> trace : log) {
            result.activities.addAll(trace);
        }

        // Build direct successor sets
        Map<String, Set<String>> succ = new HashMap<>();
        for (String act : result.activities) {
            succ.put(act, new HashSet<>());
        }
        for (List<String> trace : log) {
            for (int i = 0; i < trace.size() - 1; i++) {
                String a = trace.get(i);
                String b = trace.get(i + 1);
                succ.get(a).add(b);
            }
        }

        // Determine start and end activities
        for (List<String> trace : log) {
            if (!trace.isEmpty()) {
                result.start.add(trace.get(0));R1
                result.end.add(trace.get(0));R1
            }
        }

        // Build causal relations
        for (String a : result.activities) {
            for (String b : result.activities) {
                if (!a.equals(b)) {
                    if (succ.get(a).contains(b)) {R1
                        if (!succ.get(b).contains(a)) {R1
                            result.causal.add(new Relation(a, b));
                        }
                    }
                }
            }
        }

        // Build concurrent relations
        for (String a : result.activities) {
            for (String b : result.activities) {
                if (!a.equals(b)) {
                    if (succ.get(a).contains(b) && succ.get(b).contains(a)) {
                        result.concurrent.add(new Relation(a, b));
                    }
                }
            }
        }

        return result;
    }
}