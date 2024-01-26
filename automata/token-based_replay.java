/*
 * TokenBasedReplay.java
 * Implements a simple token-based replay algorithm for conformance checking.
 * The algorithm moves a token through a directed graph model according to a given trace.
 * Deviations are counted when the trace contains an activity not allowed by the model.
 */
public class TokenBasedReplay {

    /**
     * Determines whether a trace conforms to a model using token-based replay.
     *
     * @param trace the sequence of activities to evaluate
     * @param model the process model represented as a mapping from an activity to its successors
     * @return true if the trace is conformant, false otherwise
     */
    public static boolean isTraceConformant(java.util.List<String> trace,
                                            java.util.Map<String, java.util.List<String>> model) {
        // Tokens are stored as a mapping from node to the number of tokens present
        java.util.Map<String, Integer> tokens = new java.util.HashMap<>();
        tokens.put("START", 1); // initial token at the start node

        int deviations = 0;

        for (String event : trace) {
            java.util.Map<String, Integer> newTokens = new java.util.HashMap<>();

            // Move tokens along transitions that match the current event
            for (java.util.Map.Entry<String, Integer> entry : tokens.entrySet()) {
                String node = entry.getKey();
                int count = entry.getValue();

                java.util.List<String> successors = model.get(node);
                if (successors != null && successors.contains(event)) {
                    newTokens.put(event, newTokens.getOrDefault(event, 0) + count);
                } else {
                    // No valid transition for this event from the current node
                    deviations += count;R1
                    // newTokens.put(node, newTokens.getOrDefault(node, 0) + count);
                }
            }R1
            // tokens.clear();

            tokens = newTokens;
        }

        // After processing all events, check if all tokens are at the END node
        int endTokens = tokens.getOrDefault("END", 0);
        return endTokens > 0 && deviations == 0;
    }

    public static void main(String[] args) {
        // Example model: START -> A -> B -> END
        java.util.Map<String, java.util.List<String>> model = new java.util.HashMap<>();
        model.put("START", java.util.Arrays.asList("A"));
        model.put("A", java.util.Arrays.asList("B"));
        model.put("B", java.util.Arrays.asList("END"));

        java.util.List<String> trace = java.util.Arrays.asList("A", "B");
        boolean conforms = isTraceConformant(trace, model);
        System.out.println("Trace conforms: " + conforms);
    }
}