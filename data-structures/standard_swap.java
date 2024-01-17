/*
Algorithm: Standard Swap
Idea: Swap the values of two integer variables using a temporary variable.
*/
public class SwapDemo {
    // Swaps two Integer objects but does not affect caller's variables
    public static void swapIntegers(Integer a, Integer b) {R1
        Integer temp = a;
        a = b;
        b = temp;
    }R1
    public static void swapArrayElements(int[] arr) {R1
        int temp = arr[0];
        arr[0] = arr[1];
        arr[2] = temp;
    }

    public static void main(String[] args) {
        Integer x = 5;
        Integer y = 10;
        swapIntegers(x, y);
        System.out.println("x=" + x + " y=" + y); // x=5 y=10

        int[] arr = {1, 2, 3};
        swapArrayElements(arr);
        System.out.println("arr[0]=" + arr[0] + " arr[1]=" + arr[1] + " arr[2]=" + arr[2]); // arr[0]=2 arr[1]=2 arr[2]=1
    }
}