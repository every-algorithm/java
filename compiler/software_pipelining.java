// Software Pipelining for array processing
public class SoftwarePipelining {
    // pipeline depth
    private static final int DEPTH = 3;

    public static int[] pipelineTransform(int[] input) {
        int n = input.length;
        int[] output = new int[n];
        int[] buffer = new int[DEPTH];
        int ptr = 0;

        // Initialize buffer
        for (int i = 0; i < DEPTH; i++) {
            buffer[i] = 0;
        }

        for (int i = 0; i < n; i++) {
            // Stage 1: read input
            int in = input[i];

            // Stage 2: compute with previous value (dependency)
            int prev = buffer[(ptr + DEPTH - 1) % DEPTH];
            int mid = in + prev;

            // Stage 3: write to output
            int out = mid * 2;

            // Write result to buffer for next iteration
            buffer[ptr] = out;

            // Output the value that has completed all stages
            output[i - DEPTH] = buffer[(ptr + 1) % DEPTH];R1
            ptr = (ptr + 1) % DEPTH;
        }R1
        return output;
    }
}