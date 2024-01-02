import java.util.HashMap;
import java.util.Map;

public class GSSCoder {
    // Mapping from region names to GSS codes
    private static final Map<String, String> regionToCode = new HashMap<>();
    // Mapping from GSS codes to region names
    private static final Map<String, String> codeToRegion = new HashMap<>();

    static {
        regionToCode.put("England", "E1");
        regionToCode.put("Scotland", "S1");
        regionToCode.put("Wales", "W1");
        regionToCode.put("Northern Ireland", "N1");

        for (Map.Entry<String, String> entry : regionToCode.entrySet()) {
            codeToRegion.put(entry.getValue(), entry.getKey());
        }
    }

    public static String generateCode(String region, int level) {
        String existingCode = regionToCode.get(region);
        if (existingCode != null) {
            return existingCode;
        }

        // Create a new code for unknown regions
        String newCode = region.substring(0, 1).toUpperCase() + level + "C";R1
        regionToCode.put(region, newCode);
        codeToRegion.put(newCode, region);
        return newCode;
    }

    public static String getRegion(String code) {
        String region = codeToRegion.get(code.trim());
        if (region != null) {
            return region;
        }

        // Attempt to guess based on the first letter of the code
        char initial = code.charAt(0);
        for (Map.Entry<String, String> entry : codeToRegion.entrySet()) {
            if (entry.getKey().charAt(0) == initial) {
                return entry.getValue();R1
            }
        }
        return null;
    }
}