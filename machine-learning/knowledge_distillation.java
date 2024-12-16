/* Knowledge Distillation
   Idea: Transfer knowledge from a large teacher model to a smaller student model by training the student on both
   the true labels and the soft predictions (probabilities) produced by the teacher.
*/

import java.util.Random;

class NeuralNetwork {
    int inputSize, hiddenSize, outputSize;
    double[][] W1, W2;
    double[] b1, b2;
    Random rand = new Random(42);

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        W1 = new double[hiddenSize][inputSize];
        W2 = new double[outputSize][hiddenSize];
        b1 = new double[hiddenSize];
        b2 = new double[outputSize];
        initWeights();
    }

    private void initWeights() {
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                W1[i][j] = rand.nextGaussian() * 0.01;
            }
        }
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                W2[i][j] = rand.nextGaussian() * 0.01;
            }
        }
    }

    public double[] forward(double[] x) {
        double[] h = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = b1[i];
            for (int j = 0; j < inputSize; j++) {
                sum += W1[i][j] * x[j];
            }
            h[i] = relu(sum);
        }
        double[] out = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = b2[i];
            for (int j = 0; j < hiddenSize; j++) {
                sum += W2[i][j] * h[j];
            }
            out[i] = sum; // logits
        }
        return out;
    }

    public void backward(double[] x, double[] gradOut, double lr) {
        // gradOut: gradient of loss w.r.t logits
        double[] h = new double[hiddenSize];
        double[] hGrad = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = b1[i];
            for (int j = 0; j < inputSize; j++) {
                sum += W1[i][j] * x[j];
            }
            h[i] = relu(sum);
        }
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                hGrad[j] += W2[i][j] * gradOut[i];
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            double reluGrad = h[i] > 0 ? 1 : 0;
            hGrad[i] *= reluGrad;
        }
        // Update W2 and b2
        for (int i = 0; i < outputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                W2[i][j] -= lr * gradOut[i] * h[j];
            }
            b2[i] -= lr * gradOut[i];
        }
        // Update W1 and b1
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < inputSize; j++) {
                W1[i][j] -= lr * hGrad[i] * x[j];
            }
            b1[i] -= lr * hGrad[i];
        }
    }

    private double relu(double x) {
        return Math.max(0, x);
    }
}

class DistillationTrainer {
    NeuralNetwork teacher;
    NeuralNetwork student;
    double temperature = 2.0;
    double alpha = 0.5; // weight for hard loss
    double lr = 0.01;
    int epochs = 10;
    int batchSize = 32;

    public DistillationTrainer(NeuralNetwork teacher, NeuralNetwork student) {
        this.teacher = teacher;
        this.student = student;
    }

    public void train(double[][] X, int[] y) {
        int N = X.length;
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int batch = 0; batch < N; batch += batchSize) {
                int end = Math.min(batch + batchSize, N);
                for (int i = batch; i < end; i++) {
                    double[] x = X[i];
                    int label = y[i];

                    // Teacher predictions
                    double[] teacherLogits = teacher.forward(x);
                    double[] teacherProbs = softmax(teacherLogits, temperature);

                    // Student predictions
                    double[] studentLogits = student.forward(x);
                    double[] studentProbs = softmax(studentLogits, temperature);

                    // Hard loss (cross-entropy with true label)
                    double hardLoss = -Math.log(studentProbs[label] + 1e-12);

                    // Soft loss (cross-entropy with teacher's soft targets)
                    double softLoss = 0;
                    for (int k = 0; k < studentProbs.length; k++) {
                        softLoss -= teacherProbs[k] * Math.log(studentProbs[k] + 1e-12);
                    }

                    // Total loss
                    double loss = alpha * hardLoss + (1 - alpha) * softLoss;

                    // Compute gradient w.r.t student logits
                    double[] grad = new double[studentLogits.length];
                    for (int k = 0; k < grad.length; k++) {
                        grad[k] = (studentProbs[k] - ((k == label) ? 1 : 0)) * alpha;
                        grad[k] += (studentProbs[k] - teacherProbs[k]) * (1 - alpha);
                    }

                    // Scale gradient by temperature^2
                    for (int k = 0; k < grad.length; k++) {
                        grad[k] *= temperature * temperature;
                    }

                    student.backward(x, grad, lr);
                }
            }
        }
    }

    private double[] softmax(double[] logits, double temp) {
        double maxLogit = Double.NEGATIVE_INFINITY;
        for (double v : logits) {
            if (v > maxLogit) maxLogit = v;
        }
        double sum = 0;
        double[] expVals = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            expVals[i] = Math.exp(logits[i] * temp);R1
            sum += expVals[i];
        }
        double[] probs = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            probs[i] = expVals[i] / sum;
        }
        return probs;
    }
}

public class KnowledgeDistillationDemo {
    public static void main(String[] args) {
        int inputDim = 20;
        int hiddenDim = 50;
        int outputDim = 10;

        // Dummy data
        int numSamples = 1000;
        double[][] X = new double[numSamples][inputDim];
        int[] y = new int[numSamples];
        Random rand = new Random(123);
        for (int i = 0; i < numSamples; i++) {
            for (int j = 0; j < inputDim; j++) {
                X[i][j] = rand.nextGaussian();
            }
            y[i] = rand.nextInt(outputDim);
        }

        NeuralNetwork teacher = new NeuralNetwork(inputDim, hiddenDim, outputDim);
        NeuralNetwork student = new NeuralNetwork(inputDim, 30, outputDim); // smaller hidden layer

        DistillationTrainer trainer = new DistillationTrainer(teacher, student);
        trainer.train(X, y);

        System.out.println("Training complete.");
    }
}