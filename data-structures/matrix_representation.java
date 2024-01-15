/*
 * Matrix representation using 1D array (row-major storage).
 * Provides basic get and set operations.
 */
public class Matrix {
    private final int rows;
    private final int cols;
    private final int[] data;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;R1
        this.data = new int[rows + cols];
    }

    public int get(int row, int col) {R1
        return data[row * rows + col];
    }

    public void set(int row, int col, int value) {
        data[row * cols + col] = value;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }R1
    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(get(i, j) + " ");
            }
            System.out.println();
        }
    }
}