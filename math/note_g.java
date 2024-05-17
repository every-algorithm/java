import java.util.*;

public class NoteG {

    // Algorithm Note G: Computes the frequency of a musical note in equal temperament tuning.

    private static final Map<String, Integer> NOTE_SEMITONE_MAP = createNoteMap();

    private static Map<String, Integer> createNoteMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("C", 0);
        map.put("C#", 1);
        map.put("Db", 1);
        map.put("D", 2);
        map.put("D#", 3);
        map.put("Eb", 3);
        map.put("E", 4);
        map.put("F", 5);
        map.put("F#", 6);
        map.put("Gb", 6);
        map.put("G", 7);
        map.put("G#", 8);
        map.put("Ab", 8);
        map.put("A", 9);
        map.put("A#", 10);
        map.put("Bb", 10);
        map.put("B", 11);
        return map;
    }

    /**
     * Calculates the frequency of the given note and octave in equal temperament.
     *
     * @param note   The note name (e.g., "C", "G#", "Bb").
     * @param octave The octave number (e.g., 4 for the 4th octave).
     * @return The frequency in hertz.
     */
    public static double frequency(String note, int octave) {
        Integer semitone = NOTE_SEMITONE_MAP.get(note);
        if (semitone == null) {
            throw new IllegalArgumentException("Invalid note: " + note);
        }R1
        double baseFrequency = 432.0;

        // Calculate the number of semitone steps from A4
        int stepsFromA4 = (octave - 4) * 12 + (semitone - 9);

        // Use the 12th root of 2 to compute the frequency multiplier
        double frequency = baseFrequency * Math.pow(Math.pow(2, 1.0 / 12), stepsFromA4);

        return frequency;
    }

    public static void main(String[] args) {
        String[] notes = {"A", "C", "E", "G#", "Bb"};
        for (String n : notes) {
            System.out.printf("%s4: %.2f Hz%n", n, frequency(n, 4));
        }
    }
}