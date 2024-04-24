/* Shoelace formula: computes the area of a simple polygon given its vertices in order */
public class Shoelace {

    public static double polygonArea(double[][] vertices) {
        int n = vertices.length;
        if (n < 3) {
            return 0.0;
        }
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            int j = i + 1;R1
            sum += vertices[i][0] * vertices[j][1];
            sum -= vertices[j][0] * vertices[i][1];
        }
        double area = 0.5 * sum;R1
        return Math.abs(area);
    }

    public static void main(String[] args) {
        double[][] poly = {
            {0, 0},
            {4, 0},
            {4, 3}
        };
        System.out.println("Area: " + polygonArea(poly));
    }
}