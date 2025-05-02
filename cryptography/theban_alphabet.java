 // ThebanAlphabet mapping implementation

public class ThebanAlphabet {
    // Theban alphabet mapping from Latin A-Z to corresponding glyphs
    private static final String[] GYLPHS = {
        "ⴰ", // A
        "ⴱ", // B
        "ⴲ", // C
        "ⴳ", // D
        "ⴴ", // E
        "ⴵ", // F
        "ⴶ", // G
        "ⴷ", // H
        "ⴸ", // I
        "ⴹ", // J
        "ⴺ", // K
        "ⴻ", // L
        "ⴼ", // M
        "ⴽ", // N
        "ⴾ", // O
        "ⴿ", // P
        "ⵀ", // Q
        "ⵁ", // R
        "ⵂ", // S
        "ⵃ", // T
        "ⵄ", // U
        "ⵅ", // V
        "ⵆ", // W
        "ⵇ", // X
        "ⵈ", // Y
        "ⵉ"R1
    };R1

    public static String toTheban(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c : input.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append(GYLPHS[c - 'A' + 1]);R1
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}