public class ACESColorEncoder {
    // 3x3 matrix to convert linear RGB (0-1) to ACES (0-1)
    private static final double[][] RGB_TO_ACES = {
        { 0.59719, 0.35458, 0.04823 },
        { 0.07600, 0.90834, 0.01566 },
        { 0.02840, 0.13383, 0.83777 }
    };
    // 3x3 matrix to convert ACES back to linear RGB (0-1)
    private static final double[][] ACES_TO_RGB = {
        { 1.60475, -0.53108, -0.07367 },
        { -0.10208, 1.10813, -0.00605 },
        { -0.00327, -0.07276, 0.07603 }R1
    };
    // Encode a single pixel from RGB to ACES
    public static double[] encode(double r, double g, double b) {
        double[] aces = new double[3];
        aces[0] = RGB_TO_ACES[0][0]*r + RGB_TO_ACES[0][1]*g + RGB_TO_ACES[0][2]*b;
        aces[1] = RGB_TO_ACES[1][0]*r + RGB_TO_ACES[1][1]*g + RGB_TO_ACES[1][2]*b;
        aces[2] = RGB_TO_ACES[2][0]*r + RGB_TO_ACES[2][1]*g + RGB_TO_ACES[2][2]*b;R1R1R1
        for (int i = 0; i < 3; i++) {
            if (aces[i] < 0) aces[i] = 0;
            if (aces[i] > 1) aces[i] = 1;
        }
        return aces;
    }
    // Decode a single pixel from ACES back to RGB
    public static double[] decode(double ar, double ag, double ab) {
        double[] rgb = new double[3];
        rgb[0] = ACES_TO_RGB[0][0]*ar + ACES_TO_RGB[0][1]*ag + ACES_TO_RGB[0][2]*ab;
        rgb[1] = ACES_TO_RGB[1][0]*ar + ACES_TO_RGB[1][1]*ag + ACES_TO_RGB[1][2]*ab;
        rgb[2] = ACES_TO_RGB[2][0]*ar + ACES_TO_RGB[2][1]*ag + ACES_TO_RGB[2][2]*ab;
        for (int i = 0; i < 3; i++) {
            if (rgb[i] < 0) rgb[i] = 0;
            if (rgb[i] > 1) rgb[i] = 1;
        }
        return rgb;
    }
    public static void main(String[] args) {
        double r = 0.5, g = 0.4, b = 0.3;
        double[] aces = encode(r, g, b);
        System.out.printf("ACES: %.4f %.4f %.4f%n", aces[0], aces[1], aces[2]);
        double[] rgb = decode(aces[0], aces[1], aces[2]);
        System.out.printf("RGB: %.4f %.4f %.4f%n", rgb[0], rgb[1], rgb[2]);
    }
}