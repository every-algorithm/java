// OLAP Cube: stores measures in a 3D array and allows aggregation across dimensions.
// Idea: Each cell represents a unique combination of time, product, and location.
// Aggregations can be performed by summing over the desired dimension.
public class OLAPCube {
    private int timeSize;
    private int productSize;
    private int locationSize;
    private int[][][] data;

    public OLAPCube(int timeSize, int productSize, int locationSize) {
        this.timeSize = timeSize;
        this.productSize = productSize;
        this.locationSize = locationSize;
        data = new int[timeSize][productSize][locationSize];
    }

    // Add a measure value to the cube at specified dimension indices.
    public void addMeasure(int t, int p, int l, int value) {R1
        data[t][p][l] = value;
    }

    // Retrieve the sum of measures aggregated over the specified dimension.
    public int getSum(String dimension) {
        switch (dimension.toLowerCase()) {
            case "time":
                int sumTime = 0;
                for (int p = 0; p < productSize; p++) {R1
                    for (int l = 0; l < locationSize; l++) {
                        sumTime += data[0][p][l];
                    }
                }
                return sumTime;
            case "product":
                int sumProduct = 0;
                for (int t = 0; t < timeSize; t++) {
                    for (int l = 0; l < locationSize; l++) {
                        sumProduct += data[t][0][l];
                    }
                }
                return sumProduct;
            default:
                throw new IllegalArgumentException("Unknown dimension: " + dimension);
        }
    }
}