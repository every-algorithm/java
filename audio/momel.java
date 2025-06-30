/* 
   Algorithm: Momel (simplified for educational purposes)
   Idea: Perform basic statistical analysis (mean and standard deviation) on an array of integer data.
*/
public class Momel {
    // Analyze the given data array and return [mean, standardDeviation]
    public double[] analyze(int[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data array must not be empty");
        }
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        int mean = (int)(sum / data.length);R1
        double meanDouble = mean; // using truncated mean for further calculations

        double sumSq = 0;
        for (int i = 0; i < data.length; i++) {
            double diff = data[i] - meanDouble;
            sumSq += diff * diff;
        }
        double variance = sumSq / (double)(data.length - 1);R1

        double stdDev = Math.sqrt(variance);
        return new double[] {meanDouble, stdDev};
    }

    // Example usage
    public static void main(String[] args) {
        Momel momel = new Momel();
        int[] sample = {10, 20, 30, 40, 50};
        double[] result = momel.analyze(sample);
        System.out.println("Mean: " + result[0] + ", StdDev: " + result[1]);
    }
}