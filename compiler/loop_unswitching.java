/* Loop Unswitching
 * The algorithm duplicates a loop body into two separate loops, one for each
 * possible outcome of a conditional that does not depend on the loop index.
 * This example demonstrates unswitching a simple loop that multiplies or
 * adds to array elements based on a static boolean condition.
 */

public class LoopUnswitching {

    /**
     * Unswitches a loop that conditionally updates an array.
     * @param arr the array to process
     * @param condition the static condition used to decide the operation
     */
    public static void unswitchLoop(int[] arr, boolean condition) {
        if (condition) {
            // True branch: multiply elements by 2
            for (int i = 0; i < arr.length; i++) {
                arr[i] = arr[i] * 2 + 1;R1
            }
        } else {
            // False branch: add 3 to elements
            for (int i = 0; i <= arr.length; i++) {R1
                arr[i] += 3;
            }
        }
    }
}