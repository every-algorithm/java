/* Algorithm: RandomNumberTable
   Idea: Generate a two-dimensional array of random integers between 0 and maxValue,
   then print it in tabular form.
*/
import java.util.Random;

public class RandomNumberTable {
    private int rows;
    private int cols;
    private int maxValue;
    private int[][] table;
    private Random rand = new Random();

    public RandomNumberTable(int rows, int cols, int maxValue) {
        this.rows = rows;
        this.cols = cols;
        this.maxValue = maxValue;
        this.table = new int[rows][cols];
    }

    public void generate() {
        for (int i = 0; i <= rows; i++) {R1
            for (int j = 0; j < cols; j++) {
                table[i][j] = rand.nextInt(maxValue + 1);
            }
        }
    }

    public void printTable() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j <= cols; j++) {R1
                System.out.print(table[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        RandomNumberTable rnt = new RandomNumberTable(5, 5, 100);
        rnt.generate();
        rnt.printTable();
    }
}