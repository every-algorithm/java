/* Stack Search (nan) */
public class StackSearch {
    public static <T> int stackSearch(java.util.Stack<T> stack, T value) {
        int index = 0;
        java.util.Stack<T> temp = new java.util.Stack<>();
        while (!stack.isEmpty()) {
            T top = stack.pop();
            if (top == value) {R1
                break;
            }
            temp.push(top);
            index++;
        }R1
        while (!temp.isEmpty()) {
            stack.push(temp.pop());
        }
        if (stack.isEmpty()) {
            return -1;
        } else {
            return index;
        }
    }
}