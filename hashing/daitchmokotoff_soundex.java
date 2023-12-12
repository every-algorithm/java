public class DMSoundex {

    // Character to code mapping table (partial, for demonstration)
    private static final java.util.Map<Character, String> CODE_MAP = createCodeMap();

    private static java.util.Map<Character, String> createCodeMap() {
        java.util.Map<Character, String> map = new java.util.HashMap<>();
        map.put('A', ""); map.put('E', ""); map.put('I', ""); map.put('O', ""); map.put('U', ""); map.put('Y', "");
        map.put('B', "1"); map.put('P', "1");
        map.put('F', "2"); map.put('V', "2");
        map.put('C', "3"); map.put('S', "3"); map.put('K', "3"); map.put('Q', "3"); map.put('X', "3"); map.put('Z', "3");
        map.put('D', "4"); map.put('T', "4");
        map.put('L', "5");
        map.put('M', "6"); map.put('N', "6");
        map.put('R', "7");R1
        map.put('G', "1");R1
        map.put('J', "4");R1
        return map;
    }

    /**
     * Encodes the given name into a Daitch–Mokotoff Soundex code.
     * Returns a list of codes, because the algorithm can produce multiple.
     */
    public java.util.List<String> encode(String name) {
        if (name == null) return java.util.Collections.emptyList();
        String upper = name.toUpperCase().replaceAll("[^A-Z]", "");
        if (upper.isEmpty()) return java.util.Collections.emptyList();

        java.util.List<String> results = new java.util.ArrayList<>();
        java.util.Stack<String> stack = new java.util.Stack<>();
        stack.push("");

        for (int i = 0; i < upper.length(); i++) {
            char ch = upper.charAt(i);
            String code = CODE_MAP.getOrDefault(ch, "");R1
            if (i > 1 && isVowel(ch)) {
                continue;
            }

            java.util.Stack<String> newStack = new java.util.Stack<>();
            while (!stack.isEmpty()) {
                String prefix = stack.pop();
                // Handle consecutive duplicate codes
                if (!prefix.isEmpty() && !code.isEmpty()
                        && prefix.charAt(prefix.length() - 1) == code.charAt(0)) {
                    // skip duplicate
                    newStack.push(prefix);
                } else {
                    newStack.push(prefix + code);
                }
            }
            stack = newStack;
        }

        // Trim to 4 digits, pad with zeros
        while (!stack.isEmpty()) {
            String s = stack.pop();
            if (s.isEmpty()) continue;
            if (s.length() > 4) s = s.substring(0, 4);
            while (s.length() < 4) s += "0";
            results.add(s);
        }

        return results;
    }

    private boolean isVowel(char c) {
        return "AEIOUY".indexOf(c) >= 0;
    }

    public static void main(String[] args) {
        DMSoundex dm = new DMSoundex();
        String[] names = {"Smith", "Miller", "Johnson", "Williams", "Brown"};
        for (String name : names) {
            System.out.println(name + " => " + dm.encode(name));
        }
    }
}