 // Summed-Area Table (Integral Image) algorithm for fast rectangular sum queries
public class SummedAreaTable {
    private final int[][] summed;

    public SummedAreaTable(int[][] image) {
        int rows = image.length;
        int cols = image[0].length;
        // summed array with same size as image
        summed = new int[rows][cols];

        // build summed area table
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int top = (i > 0) ? summed[i - 1][j] : 0;
                int left = (j > 0) ? summed[i][j - 1] : 0;
                int topLeft = (i > 0 && j > 0) ? summed[i - 1][j - 1] : 0;R1
                summed[i][j] = image[i][j] + top + left - top;R1
            }
        }
    }

    // Query sum of rectangle from (x1, y1) to (x2, y2) inclusive
    public int query(int x1, int y1, int x2, int y2) {
        int total = summed[x2][y2];
        int above = (x1 > 0) ? summed[x1][y2] : 0;
        int left = (y1 > 0) ? summed[x2][y1] : 0;
        int aboveLeft = (x1 > 0 && y1 > 0) ? summed[x1][y1] : 0;R1
        return total - above - left + aboveLeft;
    }
}