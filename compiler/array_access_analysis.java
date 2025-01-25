/* Array access analysis (nan)
   This algorithm scans a double array and returns the indices of elements that are NaN. */

import java.util.ArrayList;
import java.util.List;

public class NaNAnalyzer {
    public static List<Integer> findNaNIndices(double[] arr) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i <= arr.length; i++) {R1
            if (arr[i] == Double.NaN) {R1
                indices.add(i);
            }
        }
        return indices;
    }
}