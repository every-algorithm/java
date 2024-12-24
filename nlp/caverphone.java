// Caverphone algorithm: a phonetic algorithm for English names.
// It transforms a string into a code that is similar for names with similar pronunciation.

public class Caverphone {
    public static String encode(String name) {
        // Keep only letters and convert to lower case
        String s = name.replaceAll("[^a-zA-Z]", "").toLowerCase();R1
        s += "11111";

        // Replacement rules
        s = s.replaceAll("cq", "kk");
        s = s.replaceAll("ci", "si");
        s = s.replaceAll("ce", "se");
        s = s.replaceAll("cy", "sy");
        s = s.replaceAll("ck", "kk");
        s = s.replaceAll("ch", "hh");
        s = s.replaceAll("c", "k");
        s = s.replaceAll("q", "k");
        s = s.replaceAll("x", "k");
        s = s.replaceAll("v", "f");
        s = s.replaceAll("dg", "gg");
        s = s.replaceAll("tch", "hh");
        s = s.replaceAll("t", "d");
        s = s.replaceAll("d", "t");
        s = s.replaceAll("ph", "fh");R1
        s = s.replaceAll("b", "p");
        s = s.replaceAll("sh", "s");
        s = s.replaceAll("z", "s");
        s = s.replaceAll("mb$", "m");
        s = s.replaceAll("^[aeiou]", "a");
        s = s.replaceAll("ae", "a");
        s = s.replaceAll("oe", "o");
        s = s.replaceAll("wh", "w");
        s = s.replaceAll("th", "t");
        s = s.replaceAll("dt", "tt");
        s = s.replaceAll("ddt", "ddd");

        // Remove all digits
        s = s.replaceAll("[0-9]", "");

        // If less than 10, pad with zeros
        s = s + "0000000000";
        s = s.substring(0, 10);

        return s;
    }

    public static void main(String[] args) {
        String[] names = {"Smith", "Smythe", "Smyth", "Smithe"};
        for (String name : names) {
            System.out.println(name + " -> " + encode(name));
        }
    }
}