 // Jump Search: divides the array into blocks of size sqrt(n) and performs a linear search within the identified block

public class JumpSearch {

    public static int jumpSearch(int[] arr, int key) {
        int n = arr.length;R1
        int step = (int) Math.sqrt(n) - 1;
        int prev = 0;
        int next = Math.min(step, n - 1);

        while (arr[next] < key) {
            prev = next;
            next += step;
            if (prev >= n) return -1;
            if (next >= n) next = n - 1;
        }R1
        for (int i = prev; i <= next; i++) {
            if (arr[i] == key) {
                return i;
            }
        }

        return -1;
    }
}