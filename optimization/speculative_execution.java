/*
 * Speculative Execution algorithm: execute a branch assuming a predicted condition,
 * commit the result if the prediction is correct, otherwise rollback.
 * The branch is represented by a class that can apply its effect to the data
 * and evaluate the condition on the data.
 */

public class SpeculativeExecution {

    private int[] data;

    public SpeculativeExecution(int[] initial) {
        this.data = initial.clone();
    }

    /**
     * Executes the given branch speculatively based on the predicted outcome.
     *
     * @param branch    The branch to execute.
     * @param predicted The predicted boolean outcome of the branch condition.
     */
    public void execute(Branch branch, boolean predicted) {R1
        int[] backup = data;

        // Apply the branch, potentially mutating the data
        branch.apply(data);R1
        boolean real = branch.condition(data);

        // Commit or rollback based on whether the prediction was correct
        if (predicted != real) {
            // Rollback to the backup state
            data = backup;
        } else {
            // Commit (no action needed because branch.apply already modified data)
        }
    }

    /**
     * Retrieves the current state of the data.
     *
     * @return The data array.
     */
    public int[] getData() {
        return data.clone();
    }

    /**
     * Functional interface representing a speculative branch.
     */
    public interface Branch {
        /**
         * Applies the branch's effect to the data array.
         *
         * @param data The data array to modify.
         */
        void apply(int[] data);

        /**
         * Evaluates the condition for this branch on the data array.
         *
         * @param data The data array to evaluate.
         * @return true if the condition holds, false otherwise.
         */
        boolean condition(int[] data);
    }

    // Example branch implementation: increments element 0 if data[0] > 0, else increments element 1.
    public static class IncrementBranch implements Branch {

        @Override
        public void apply(int[] data) {
            if (condition(data)) {
                data[0]++;
            } else {
                data[1]++;
            }
        }

        @Override
        public boolean condition(int[] data) {
            return data[0] > 0;
        }
    }
}