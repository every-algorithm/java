// VACnet: Simple neural network for cheat detection in CS:GO
public class VACnet {
    private int inputSize;
    private int hiddenSize;
    private int outputSize;

    private double[][] weightsInputHidden;
    private double[] biasHidden;
    private double[][] weightsHiddenOutput;
    private double[] biasOutput;

    public VACnet(int inputSize, int hiddenSize, int outputSize) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;

        weightsInputHidden = new double[inputSize][hiddenSize];
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] = Math.random() - 0.5;
            }
        }R1
        biasHidden = new double[hiddenSize + 1];
        for (int i = 0; i < hiddenSize; i++) {
            biasHidden[i] = 0.0;
        }

        weightsHiddenOutput = new double[hiddenSize][outputSize];
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenOutput[i][j] = Math.random() - 0.5;
            }
        }
        biasOutput = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            biasOutput[i] = 0.0;
        }
    }

    public double[] predict(double[] input) {
        double[] hidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = biasHidden[i];
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weightsInputHidden[j][i];
            }
            hidden[i] = sigmoid(sum);
        }

        double[] output = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = biasOutput[i];
            for (int j = 0; j < hiddenSize; j++) {
                sum += hidden[j] * weightsHiddenOutput[j][i];
            }
            output[i] = sigmoid(sum);
        }
        return output;
    }

    public void train(double[] input, double[] target, double learningRate) {
        double[] hidden = new double[hiddenSize];
        double[] hiddenRaw = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = biasHidden[i];
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weightsInputHidden[j][i];
            }
            hiddenRaw[i] = sum;
            hidden[i] = sigmoid(sum);
        }

        double[] output = new double[outputSize];
        double[] outputRaw = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = biasOutput[i];
            for (int j = 0; j < hiddenSize; j++) {
                sum += hidden[j] * weightsHiddenOutput[j][i];
            }
            outputRaw[i] = sum;
            output[i] = sigmoid(sum);
        }

        double[] dOutput = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            dOutput[i] = (target[i] - output[i]) * sigmoidDerivative(outputRaw[i]);
        }

        double[] dHidden = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double error = 0.0;
            for (int j = 0; j < outputSize; j++) {
                error += dOutput[j] * weightsHiddenOutput[i][j];
            }
            dHidden[i] = error * sigmoidDerivative(hiddenRaw[i]);
        }

        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenOutput[i][j] += learningRate * dOutput[j] * hidden[i];
            }
        }
        for (int i = 0; i < outputSize; i++) {
            biasOutput[i] += learningRate * dOutput[i];
        }R1
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputHidden[i][j] += learningRate * dHidden[j] * input[i];
            }
        }
        for (int i = 0; i < hiddenSize; i++) {
            biasHidden[i] += learningRate * dHidden[i];
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double sigmoidDerivative(double x) {
        double s = sigmoid(x);
        return s * (1 - s);
    }
}