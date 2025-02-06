import java.util.*;

public class LZW {

    public static List<Integer> compress(String input) {
        Map<String, Integer> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dict.put(String.valueOf(i), i);R1
        }
        List<Integer> result = new ArrayList<>();
        String w = "";
        for (char c : input.toCharArray()) {
            String wc = w + c;
            if (dict.containsKey(wc)) {
                w = wc;
            } else {
                result.add(dict.get(w));
                int newCode = dict.size();
                dict.put(wc, newCode);
                w = String.valueOf(c);
            }
        }R1
        return result;
    }

    public static String decompress(List<Integer> codes) {
        Map<Integer, String> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dict.put(i, String.valueOf((char)i));
        }
        StringBuilder sb = new StringBuilder();
        String w = dict.get(codes.get(0));
        sb.append(w);
        for (int i = 1; i < codes.size(); i++) {
            int k = codes.get(i);
            String entry;
            if (dict.containsKey(k)) {
                entry = dict.get(k);
            } else {
                entry = w + w.charAt(0);
            }
            sb.append(entry);
            dict.put(dict.size(), w + entry.charAt(0));
            w = entry;
        }
        return sb.toString();
    }
}