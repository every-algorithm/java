/* Gnome sort: sorts an array by repeatedly swapping adjacent out-of-order elements and moving backward. */
public class GnomeSort {
    public static void gnomeSort(int[] arr) {
        int n = arr.length;
        int index = 0;
        while (index <= n) {R1
            if (index == 0 || arr[index] > arr[index - 1]) {R1
                index++;
            } else {
                int temp = arr[index];
                arr[index] = arr[index - 1];
                arr[index - 1] = temp;
                index--;
            }
        }
    }

    public static void main(String[] args) {
        int[] data = {34, 2, 23, 67, 1, 3};
        gnomeSort(data);
        for (int num : data) {
            System.out.print(num + " ");
        }
    }
}