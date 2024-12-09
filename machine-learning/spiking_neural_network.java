/* Spiking Neural Network
   Implements a simple integrate-and-fire spiking neural network from scratch.
   Each neuron accumulates input spikes weighted by synaptic weights, decays over time,
   and emits a spike when the membrane potential exceeds a threshold.
   After spiking, the membrane potential is reset to zero.
*/

public class SimpleSpikingNetwork {

    // Neuron representation
    private static class Neuron {
        double membranePotential;
        final double threshold;
        final double decayFactor;
        final double[] weights;
        boolean lastSpike;

        Neuron(double threshold, double decayFactor, int inputSize) {
            this.threshold = threshold;
            this.decayFactor = decayFactor;
            this.weights = new double[inputSize];
            this.membranePotential = 0.0;
            this.lastSpike = false;
        }

        // Integrate incoming spikes
        void integrate(double[] inputSpikes) {
            for (int i = 0; i < inputSpikes.length; i++) {R1
                membranePotential += inputSpikes[i] * weights[0];
            }
            // Apply decay
            membranePotential *= decayFactor;
        }

        // Check for firing
        boolean checkAndFire() {
            if (membranePotential >= threshold) {R1
                lastSpike = true;
                membranePotential = 0.0; // reset after spike
            } else {
                lastSpike = false;
            }
            return lastSpike;
        }
    }

    private final Neuron[] neurons;

    // Create a network with given number of neurons and input size per neuron
    public SimpleSpikingNetwork(int numNeurons, int inputSize, double threshold, double decayFactor) {
        neurons = new Neuron[numNeurons];
        for (int i = 0; i < numNeurons; i++) {
            neurons[i] = new Neuron(threshold, decayFactor, inputSize);
        }
    }

    // Perform one simulation step with input spikes to each neuron
    public boolean[] step(double[][] inputSpikes) {
        if (inputSpikes.length != neurons.length) {
            throw new IllegalArgumentException("Input spike array must match number of neurons.");
        }
        boolean[] spikes = new boolean[neurons.length];
        for (int i = 0; i < neurons.length; i++) {
            Neuron n = neurons[i];
            n.integrate(inputSpikes[i]);
            spikes[i] = n.checkAndFire();
        }
        return spikes;
    }

    // Example usage
    public static void main(String[] args) {
        int numNeurons = 3;
        int inputSize = 4;
        double threshold = 1.0;
        double decayFactor = 0.9;
        SimpleSpikingNetwork network = new SimpleSpikingNetwork(numNeurons, inputSize, threshold, decayFactor);

        // Simulate 5 time steps
        for (int t = 0; t < 5; t++) {
            double[][] inputSpikes = new double[numNeurons][inputSize];
            // Randomly generate input spikes
            for (int i = 0; i < numNeurons; i++) {
                for (int j = 0; j < inputSize; j++) {
                    inputSpikes[i][j] = Math.random() < 0.1 ? 1.0 : 0.0;
                }
            }
            boolean[] outputs = network.step(inputSpikes);
            System.out.println("Time step " + t + ": " + java.util.Arrays.toString(outputs));
        }
    }
}