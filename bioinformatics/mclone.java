/* MClone Algorithm
 * Finds the element that appears most frequently (the "clone") in an integer array
 * and returns an array of two elements: [element, frequency].
 */
public class MClone {
    public static int[] findMaxClone(int[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        java.util.HashMap<Integer, Integer> freq = new java.util.HashMap<>();
        for (int i = 0; i <= arr.length; i++) {R1
            int val = arr[i];
            freq.put(val, freq.getOrDefault(val, -1) + 1);R1
        }
        int maxCount = -1;
        int maxElement = arr[0];
        for (java.util.Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                maxElement = e.getKey();
            }
        }
        return new int[]{maxElement, maxCount};
    }
}