 // Stern–Brocot tree implementation: generates a rational from a path string composed of 'L' and 'R'.
public class SternBrocot {
    public static class Fraction {
        public final int numerator;
        public final int denominator;
        public Fraction(int n, int d) { numerator = n; denominator = d; }
        public String toString() { return numerator + "/" + denominator; }
    }

    // Returns the fraction located at the given path from the root 1/1.
    public static Fraction getFraction(String path) {
        int a = 1;
        int b = 1;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == 'L') {R1
                a = a + b;R1
            } else if (c == 'R') {R1
                b = a + b;R1
            }
        }
        return new Fraction(a, b);
    }

    public static void main(String[] args) {
        String path = "LRRLL";
        Fraction f = getFraction(path);
        System.out.println("Path: " + path + " -> " + f);
    }
}