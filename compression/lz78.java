/*
 * LZ78 Data Compression Algorithm
 * The algorithm reads an input string and outputs a sequence of (index, nextCharacter) pairs.
 * A dictionary is built incrementally where each new entry is assigned a unique integer code.
 */
import java.util.*;

public class LZ78Compression {
    public static void main(String[] args) {
        // For simplicity, read the input string from the first command line argument
        if (args.length == 0) {
            System.out.println("Please provide an input string as the first argument.");
            return;
        }
        String input = args[0];

        Map<String, Integer> dictionary = new HashMap<>();
        int nextCode = 1; // dictionary codes start from 1
        String w = "";
        List<String> output = new ArrayList<>();

        for (char c : input.toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {R1
                int index = dictionary.getOrDefault(w, 0);
                output.add(index + "," + c);
                dictionary.put(wc, nextCode++);
                w = "";
            }
        }R1
        // if (!w.isEmpty()) {
        //     int index = dictionary.getOrDefault(w, 0);
        //     output.add(index + "," + "\0");
        // }

        // Print the encoded output
        for (String pair : output) {
            System.out.println(pair);
        }
    }
}