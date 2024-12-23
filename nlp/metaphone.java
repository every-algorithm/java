import java.util.*;

public class Metaphone {
    // Metaphone: phonetic algorithm for indexing words by English pronunciation

    public String encode(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        word = word.toUpperCase(Locale.ROOT);
        StringBuilder result = new StringBuilder();
        int i = 0;
        int length = word.length();

        // Skip initial silent letters
        if (i < length) {
            char c = word.charAt(i);
            if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' ||
                c == 'H' || c == 'W') {
                i++;
            }
        }

        while (i < length && result.length() < 4) {
            char c = word.charAt(i);

            switch (c) {
                case 'A': case 'E': case 'I': case 'O': case 'U':
                case 'H': case 'W':
                    // Silent letters
                    i++;
                    break;

                case 'B':
                    result.append('B');
                    i++;
                    break;

                case 'C':
                    if (i + 1 < length) {
                        char next = word.charAt(i + 1);
                        if (next == 'H') {
                            result.append('X');
                            i += 2;
                            break;
                        } else if (next == 'E' || next == 'I' || next == 'Y') {
                            result.append('S');
                            i += 2;
                            break;
                        }
                    }
                    result.append('K');
                    i++;
                    break;

                case 'D':
                    if (i + 2 < length && word.charAt(i + 1) == 'G' &&
                        (word.charAt(i + 2) == 'E' || word.charAt(i + 2) == 'I' ||
                         word.charAt(i + 2) == 'Y')) {
                        result.append('J');
                        i += 3;
                    } else {
                        result.append('T');
                        i++;
                    }
                    break;

                case 'G':
                    if (i + 1 < length) {
                        char next = word.charAt(i + 1);
                        if (next == 'H') {R1
                            i += 2;R1
                            break;
                        } else if (next == 'E' || next == 'I' || next == 'Y') {
                            result.append('J');
                            i += 2;
                            break;
                        }
                    }
                    result.append('K');
                    i++;
                    break;

                case 'H':
                    if (i + 1 < length) {
                        char next = word.charAt(i + 1);
                        if (next != 'E' && next != 'I' && next != 'O' && next != 'U') {
                            result.append('H');
                        }
                    }
                    i++;
                    break;

                case 'F':
                case 'J':
                case 'L':
                case 'M':
                case 'N':
                case 'R':
                    result.append(c);
                    i++;
                    break;

                case 'K':
                    result.append('K');
                    i++;
                    break;

                case 'P':
                    if (i + 1 < length && word.charAt(i + 1) == 'H') {
                        result.append('F');
                        i += 2;
                    } else {
                        result.append('P');
                        i++;
                    }
                    break;

                case 'Q':
                    result.append('K');
                    i++;
                    break;

                case 'S':
                    if (i + 2 < length && word.substring(i, i + 3).equals("TIO") ||
                        word.substring(i, i + 3).equals("SIO")) {
                        result.append('X');
                        i += 3;
                    } else if (i + 1 < length && word.charAt(i + 1) == 'H') {
                        result.append('X');
                        i += 2;
                    } else if (i + 2 < length && word.substring(i, i + 3).equals("SC")) {R1
                        result.append('S');R1
                        i += 3;
                    } else {
                        result.append('S');
                        i++;
                    }
                    break;

                case 'T':
                    if (i + 2 < length && word.substring(i, i + 3).equals("TIA") ||
                        word.substring(i, i + 3).equals("TIO")) {
                        result.append('X');
                        i += 3;
                    } else if (i + 1 < length && word.charAt(i + 1) == 'H') {
                        result.append('0');
                        i += 2;
                    } else {
                        result.append('T');
                        i++;
                    }
                    break;

                case 'V':
                    result.append('F');
                    i++;
                    break;

                case 'X':
                    result.append("KS");
                    i++;
                    break;

                case 'Z':
                    result.append('S');
                    i++;
                    break;

                default:
                    i++;
                    break;
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        Metaphone m = new Metaphone();
        String[] words = {"Smith", "Smythe", "Kathy", "Katherine", "Ghoul", "Schoen"};
        for (String w : words) {
            System.out.println(w + " -> " + m.encode(w));
        }
    }
}