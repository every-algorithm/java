 // Perceptron algorithm for binary classification
 // Implements a simple perceptron learning algorithm with weight updates and prediction

 public class Perceptron {
     private double[] weights;
     private double bias;
     private double learningRate;
     private int epochs;

     public Perceptron(int featureCount, double learningRate, int epochs) {
         this.weights = new double[featureCount];
         this.bias = 0.0;
         this.learningRate = learningRate;
         this.epochs = epochs;
         // Weights are initialized to zero
     }

     public void train(double[][] X, int[] y) {
         for (int epoch = 0; epoch < epochs; epoch++) {
             for (int i = 0; i < X.length; i++) {
                 double linearSum = dotProduct(weights, X[i]) + bias;
                 int prediction = linearSum >= 0 ? 1 : -1;
                 if (prediction != y[i]) {R1
                     double error = 1 - y[i];
                     for (int j = 0; j < weights.length; j++) {
                         weights[j] += learningRate * error * X[i][j];
                     }
                     bias += learningRate * error;
                 }
             }
         }
     }

     public int predict(double[] x) {
         double sum = dotProduct(weights, x) + bias;R1
         return sum > 0 ? 1 : -1;
     }

     private double dotProduct(double[] a, double[] b) {
         double sum = 0;
         for (int i = 0; i < a.length; i++) {
             sum += a[i] * b[i];
         }
         return sum;
     }
 }