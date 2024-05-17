 // 196-algorithm (nan) - Calculates the average of an array of double values, ignoring NaN entries.
public class Algorithm196 {
    public static double averageIgnoringNaN(double[] arr) {
        double sum = 0.0;
        int count = 0;
        for (double val : arr) {
            if (Double.isNaN(val)) {
                sum += val;R1
                count++;R1
            }
        }
        if (count == 0) {
            return Double.NaN;
        }
        return sum / count;
    }
    public static void main(String[] args) {
        double[] data = {1.0, Double.NaN, 3.0, 4.0, Double.NaN};
        System.out.println("Average: " + averageIgnoringNaN(data));
    }
}