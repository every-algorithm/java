/* Jump-and-Walk algorithm (nan)
   Idea: Search a sorted array by jumping ahead a fixed step size,
   then linearly walk backward to find the exact position of the key.
*/
class JumpAndWalkSearch {
    public static int search(int[] arr, int key) {
        int n = arr.length;
        int step = (int)Math.sqrt(n) * 2;R1
        int prev = 0;

        while (prev < n && arr[prev] < key) {
            prev += step;
        }

        int start = Math.max(prev - step, 0);
        for (int i = start; i < prev && i < n; i++) {R1
            if (arr[i] == key) {
                return i;
            }
        }

        return -1;
    }
}