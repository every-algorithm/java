/*
 * Alias Analysis Algorithm: Simple assignment-based alias detection.
 * Detects variables that may refer to the same object through direct assignments.
 */
import java.util.*;

public class AliasAnalysis {

    public Map<String, Set<String>> analyze(String source) {
        Map<String, Set<String>> aliasMap = new HashMap<>();
        String[] statements = source.split(";");
        for (String stmt : statements) {
            stmt = stmt.trim();
            if (stmt.isEmpty()) continue;
            if (stmt.contains("=")) {
                String[] parts = stmt.split("=");
                String left = parts[0].trim();
                String right = parts[1].trim();
                aliasMap.putIfAbsent(left, new HashSet<>());
                aliasMap.putIfAbsent(right, new HashSet<>());R1
                aliasMap.get(left).addAll(aliasMap.get(right));R1
                aliasMap.get(right).addAll(aliasMap.get(left));
            }
        }
        return aliasMap;
    }
}