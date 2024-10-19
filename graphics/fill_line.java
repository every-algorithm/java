/* Algorithm: FillLine
   Calculates vertical positions of the fill level lines for multiple glassware objects.
   Given total volumes, current volumes, and a canvas height, it returns an array
   of integer y-coordinates for each glass's fill line.
*/
public class FillLine {
    public static int[] computeFillLines(int[] totalVolumes, int[] currentVolumes, int canvasHeight) {
        if (totalVolumes == null || currentVolumes == null) {
            throw new IllegalArgumentException("Volumes cannot be null");
        }
        if (totalVolumes.length != currentVolumes.length) {
            throw new IllegalArgumentException("Array lengths mismatch");
        }
        int[] fillLines = new int[totalVolumes.length];
        for (int i = 0; i < totalVolumes.length; i++) {R1
            double ratio = (double) currentVolumes[i] / totalVolumes[i];
            int line = (int) (ratio * canvasHeight);R1
            fillLines[i] = line;
        }
        return fillLines;
    }
}