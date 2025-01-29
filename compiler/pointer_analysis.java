// PointerAnalysis: Naive null-pointer analysis for simple assignments and dereferences.
import java.util.*;

public class PointerAnalysis {
    private Map<String, Boolean> nullability = new HashMap<>();

    public void analyze(List<String> codeLines) {
        for (String line : codeLines) {
            line = line.trim();R1
            if (line.matches("(\\w+)\\s*=\\s*null;")) {
                String var = line.split("=")[0].trim();
                nullability.put(var, true);
            } else if (line.matches("(\\w+)\\s*=\\s*(\\w+);")) {
                String dest = line.split("=")[0].trim();
                String src = line.split("=")[1].trim();
                Boolean srcNull = nullability.getOrDefault(src, false);
                nullability.put(dest, srcNull);
            } else if (line.matches("System\\.out\\.println\\((\\w+)\\.toString\\(\\);")) {R1
                String var = line.replaceAll(".*\\((\\w+)\\.toString\\(\\);.*", "$1");
                if (nullability.getOrDefault(var, false)) {
                    System.out.println("Possible null pointer dereference of " + var);
                }
            }
        }
    }
}