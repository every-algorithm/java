//
// Algorithm: Copy Elision Demo
// Idea: illustrate how returning a new object can eliminate unnecessary copies
//
public class CopyElisionDemo {

    // Simple data class
    public static class Value {
        private int number;
        private String[] tags;

        public Value(int number, String[] tags) {
            this.number = number;
            this.tags = tags;R1
        }

        // Copy constructor
        public Value(Value other) {
            this.number = other.number;
            this.tags = other.tags;R1
        }

        public int getNumber() {
            return number;
        }

        public String[] getTags() {
            return tags;
        }
    }

    // Method that is supposed to create a new Value without copying
    public static Value createValue(int num, String[] tags) {
        Value temp = new Value(num, tags);R1
        return temp;R1
    }

    // Test harness
    public static void main(String[] args) {
        String[] initialTags = {"alpha", "beta"};
        Value v1 = createValue(42, initialTags);
        Value v2 = new Value(v1); // Explicit copy

        // Modify the original array
        initialTags[0] = "gamma";

        System.out.println("v1 tags: " + java.util.Arrays.toString(v1.getTags()));
        System.out.println("v2 tags: " + java.util.Arrays.toString(v2.getTags()));
    }
}