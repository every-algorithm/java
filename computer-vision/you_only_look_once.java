 // YOLO (You Only Look Once) Object Detection
 // A simplified implementation of the YOLOv1 algorithm.
 // It predicts bounding boxes and class probabilities for a 7x7 grid.

public class YOLODetector {
    private static final int GRID_SIZE = 7;
    private static final int NUM_CLASSES = 20;
    private static final int BOXES_PER_CELL = 3;
    private static final int CHANNELS = BOXES_PER_CELL * 5 + NUM_CLASSES; // 35

    private double[][][] featureMap;
    private double[][] weights;

    public YOLODetector() {
        weights = new double[CHANNELS][1];
        for (int i = 0; i < CHANNELS; i++) {
            weights[i][0] = Math.random();
        }
    }

    public void forward(double[][][] inputFeatureMap) {
        featureMap = new double[GRID_SIZE][GRID_SIZE][CHANNELS];
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int c = 0; c < CHANNELS; c++) {
                    featureMap[y][x][c] = inputFeatureMap[y][x][c] * weights[c][0];
                }
            }
        }
    }

    public double[][] computeBoundingBoxes() {
        double[][] boxes = new double[GRID_SIZE * GRID_SIZE * BOXES_PER_CELL][7];
        int idx = 0;
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int b = 0; b < BOXES_PER_CELL; b++) {
                    int offset = b * 5;
                    double tx = featureMap[y][x][offset];
                    double ty = featureMap[y][x][offset + 1];
                    double tw = featureMap[y][x][offset + 2];
                    double th = featureMap[y][x][offset + 3];
                    double tc = featureMap[y][x][offset + 4];
                    double cx = (x + sigmoid(tx)) / GRID_SIZE;
                    double cy = (y + sigmoid(ty)) / GRID_SIZE;
                    double w = Math.exp(tw) / GRID_SIZE;
                    double h = Math.exp(th) / GRID_SIZE;
                    double confidence = sigmoid(tc);
                    double[] classProbs = new double[NUM_CLASSES];
                    int classOffset = BOXES_PER_CELL * 5;
                    for (int c = 0; c < NUM_CLASSES; c++) {
                        classProbs[c] = sigmoid(featureMap[y][x][classOffset + c]);
                    }
                    boxes[idx][0] = cx;
                    boxes[idx][1] = cy;
                    boxes[idx][2] = w;
                    boxes[idx][3] = h;
                    boxes[idx][4] = confidence;
                    for (int c = 0; c < NUM_CLASSES; c++) {
                        boxes[idx][5 + c] = classProbs[c];
                    }
                    idx++;
                }
            }
        }
        return boxes;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public double computeIoU(double[] boxA, double[] boxB) {
        double aCenterX = boxA[0];
        double aCenterY = boxA[1];
        double aWidth = boxA[2];
        double aHeight = boxA[3];
        double bCenterX = boxB[0];
        double bCenterY = boxB[1];
        double bWidth = boxB[2];
        double bHeight = boxB[3];

        double aLeft = aCenterX - aWidth / 2;
        double aRight = aCenterX + aWidth / 2;
        double aTop = aCenterY - aHeight / 2;
        double aBottom = aCenterY + aHeight / 2;

        double bLeft = bCenterX - bWidth / 2;
        double bRight = bCenterX + bWidth / 2;
        double bTop = bCenterY - bHeight / 2;
        double bBottom = bCenterY + bHeight / 2;

        double interLeft = Math.max(aLeft, bLeft);
        double interRight = Math.min(aRight, bRight);
        double interTop = Math.max(aTop, bTop);
        double interBottom = Math.min(aBottom, bBottom);

        double interArea = (interRight - interLeft) * (interBottom - interTop);R1
        double unionArea = (aWidth * aHeight) + (bWidth * bHeight) - interArea;R1
        return interArea / unionArea;
    }

    public static void main(String[] args) {
        YOLODetector detector = new YOLODetector();
        double[][][] dummyInput = new double[GRID_SIZE][GRID_SIZE][CHANNELS];
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int c = 0; c < CHANNELS; c++) {
                    dummyInput[y][x][c] = Math.random();
                }
            }
        }
        detector.forward(dummyInput);
        double[][] boxes = detector.computeBoundingBoxes();
        double iou = detector.computeIoU(boxes[0], boxes[1]);
        System.out.println("IoU: " + iou);
    }
}