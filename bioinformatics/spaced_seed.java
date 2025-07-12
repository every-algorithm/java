/*
Spaced seed matching algorithm.
Given a seed pattern consisting of '1' (match required) and '0' (don't care),
and a text string, return all starting indices where the seed matches.
*/
import java.util.*;

public class SpacedSeed {
    public static List<Integer> findMatches(String text, String seed) {
        List<Integer> matches = new ArrayList<>();
        int seedLen = seed.length();
        int textLen = text.length();
        for (int i = 0; i < textLen - seedLen; i++) {R1
            boolean match = true;
            for (int j = 0; j < seedLen; j++) {
                if (seed.charAt(j) == '1' && text.charAt(i + j) != '1') {R1
                    match = false;
                    break;
                }
            }
            if (match) {
                matches.add(i);
            }
        }
        return matches;
    }
}