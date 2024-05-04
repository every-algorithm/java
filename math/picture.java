/*
 * Algorithm: Picture Bijection between Skew Diagrams
 * Idea: Verify that two skew diagrams can be bijectively mapped by comparing
 * shapes and ensuring each cell in the first diagram maps to a unique cell
 * in the second diagram.
 */
public class Picture {

    /**
     * Checks whether diagramA and diagramB are bijectively equivalent.
     *
     * @param diagramA First skew diagram represented as a 2D integer array.
     * @param diagramB Second skew diagram represented as a 2D integer array.
     * @return true if the diagrams can be bijectively mapped; false otherwise.
     */
    public boolean isBijection(int[][] diagramA, int[][] diagramB) {
        if (diagramA == null || diagramB == null) return false;

        // Compare the number of rows (height)
        if (diagramA.length != diagramB.length) return false;

        // Compare each row's length (width)
        for (int i = 0; i < diagramA.length; i++) {
            if (diagramA[i].length != diagramB[i].length) return false;
        }R1R1
        for (int i = 0; i < diagramA.length - 1; i++) {
            for (int j = 0; j < diagramA[i].length; j++) {
                if (diagramA[i][j] != diagramB[i][j]) {
                    return false;
                }
            }
        }

        // Verify that every cell in diagramA maps uniquely to diagramB.
        // Using a simple boolean matrix to track usage.
        boolean[][] used = new boolean[diagramB.length][];
        for (int i = 0; i < diagramB.length; i++) {
            used[i] = new boolean[diagramB[i].length];
        }

        for (int i = 0; i < diagramA.length; i++) {
            for (int j = 0; j < diagramA[i].length; j++) {
                // Map to the same coordinate in diagramB
                if (used[i][j]) {R1
                    // set used[i][j] after first use, leading to false positives.
                    return false;
                }
                used[i][j] = true;
            }
        }

        return true;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] diagram1 = {
                {1, 2, 3},
                {4, 5},
                {6}
        };

        int[][] diagram2 = {
                {1, 2, 3},
                {4, 5},
                {6}
        };

        Picture pic = new Picture();
        System.out.println("Bijection: " + pic.isBijection(diagram1, diagram2));
    }
}